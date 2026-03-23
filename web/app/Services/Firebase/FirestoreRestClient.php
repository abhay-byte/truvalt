<?php

namespace App\Services\Firebase;

use Google\Auth\Credentials\ServiceAccountCredentials;
use Illuminate\Support\Facades\Http;

class FirestoreRestClient
{
    private const SCOPE = 'https://www.googleapis.com/auth/datastore';

    private ?string $accessToken = null;
    private int $accessTokenExpiresAt = 0;

    public function __construct(
        private readonly FirebaseProjectConfig $config,
    ) {
    }

    public function getDocument(string $documentPath): ?array
    {
        $response = $this->request('get', $this->documentUrl($documentPath));

        if ($response->status() === 404) {
            return null;
        }

        if ($response->failed()) {
            throw new FirebaseRequestException('Firestore document lookup failed.', $response->status());
        }

        $json = $response->json();

        return is_array($json) ? $this->decodeDocument($json) : null;
    }

    public function listDocuments(string $collectionPath): array
    {
        $documents = [];
        $pageToken = null;

        do {
            $response = $this->request('get', $this->collectionUrl($collectionPath, $pageToken));

            if ($response->failed()) {
                throw new FirebaseRequestException('Firestore collection lookup failed.', $response->status());
            }

            $json = $response->json();

            if (!is_array($json)) {
                throw new FirebaseRequestException('Firestore returned an invalid collection response.', 502);
            }

            foreach ($json['documents'] ?? [] as $document) {
                if (is_array($document)) {
                    $documents[] = $this->decodeDocument($document);
                }
            }

            $pageToken = $json['nextPageToken'] ?? null;
        } while (is_string($pageToken) && $pageToken !== '');

        return $documents;
    }

    public function setDocument(string $documentPath, array $data): array
    {
        $response = $this->request('patch', $this->documentUrl($documentPath), [
            'fields' => $this->encodeFields($data),
        ]);

        if ($response->failed()) {
            throw new FirebaseRequestException('Firestore document write failed.', $response->status());
        }

        $json = $response->json();

        if (!is_array($json)) {
            throw new FirebaseRequestException('Firestore returned an invalid write response.', 502);
        }

        return $this->decodeDocument($json);
    }

    public function deleteDocument(string $documentPath): void
    {
        $response = $this->request('delete', $this->documentUrl($documentPath));

        if ($response->status() === 404) {
            return;
        }

        if ($response->failed()) {
            throw new FirebaseRequestException('Firestore document delete failed.', $response->status());
        }
    }

    private function request(string $method, string $url, array $payload = [])
    {
        $request = Http::acceptJson()
            ->timeout(20)
            ->connectTimeout(5)
            ->withToken($this->accessToken());

        if ($method === 'get') {
            return $request->get($url);
        }

        if ($method === 'patch') {
            return $request->patch($url, $payload);
        }

        return $request->delete($url);
    }

    private function accessToken(): string
    {
        if ($this->accessToken !== null && time() < ($this->accessTokenExpiresAt - 60)) {
            return $this->accessToken;
        }

        $credentials = new ServiceAccountCredentials(self::SCOPE, $this->config->serviceAccount());
        $token = $credentials->fetchAuthToken();

        $accessToken = $token['access_token'] ?? null;

        if (!is_string($accessToken) || $accessToken === '') {
            throw new FirebaseRequestException('Unable to acquire a Firestore access token.', 500);
        }

        $this->accessToken = $accessToken;
        $this->accessTokenExpiresAt = time() + (int) ($token['expires_in'] ?? 3600);

        return $this->accessToken;
    }

    private function documentUrl(string $documentPath): string
    {
        return $this->baseUrl().'/'.$this->encodePath($documentPath);
    }

    private function collectionUrl(string $collectionPath, ?string $pageToken): string
    {
        $query = http_build_query(array_filter([
            'pageSize' => 500,
            'pageToken' => $pageToken,
        ]));

        $url = $this->baseUrl().'/'.$this->encodePath($collectionPath);

        return $query === '' ? $url : $url.'?'.$query;
    }

    private function baseUrl(): string
    {
        return sprintf(
            'https://firestore.googleapis.com/v1/projects/%s/databases/%s/documents',
            rawurlencode($this->config->projectId()),
            rawurlencode($this->config->firestoreDatabase()),
        );
    }

    private function encodePath(string $path): string
    {
        return implode('/', array_map('rawurlencode', explode('/', trim($path, '/'))));
    }

    private function encodeFields(array $data): array
    {
        $fields = [];

        foreach ($data as $key => $value) {
            $fields[$key] = $this->encodeValue($value);
        }

        return $fields;
    }

    private function encodeValue(mixed $value): array
    {
        if ($value === null) {
            return ['nullValue' => null];
        }

        if (is_bool($value)) {
            return ['booleanValue' => $value];
        }

        if (is_int($value)) {
            return ['integerValue' => (string) $value];
        }

        if (is_float($value)) {
            return ['doubleValue' => $value];
        }

        if (is_string($value)) {
            return ['stringValue' => $value];
        }

        if (is_array($value)) {
            if ($this->isList($value)) {
                return [
                    'arrayValue' => [
                        'values' => array_map(fn (mixed $item) => $this->encodeValue($item), $value),
                    ],
                ];
            }

            return [
                'mapValue' => [
                    'fields' => $this->encodeFields($value),
                ],
            ];
        }

        return ['stringValue' => (string) $value];
    }

    private function decodeDocument(array $document): array
    {
        $name = (string) ($document['name'] ?? '');
        $segments = explode('/', $name);
        $id = end($segments);
        $fields = $this->decodeFields($document['fields'] ?? []);

        if (is_string($id) && $id !== '' && !array_key_exists('id', $fields)) {
            $fields['id'] = $id;
        }

        return $fields;
    }

    private function decodeFields(array $fields): array
    {
        $decoded = [];

        foreach ($fields as $key => $value) {
            if (is_array($value)) {
                $decoded[$key] = $this->decodeValue($value);
            }
        }

        return $decoded;
    }

    private function decodeValue(array $value): mixed
    {
        if (array_key_exists('nullValue', $value)) {
            return null;
        }

        if (array_key_exists('booleanValue', $value)) {
            return (bool) $value['booleanValue'];
        }

        if (array_key_exists('integerValue', $value)) {
            return (int) $value['integerValue'];
        }

        if (array_key_exists('doubleValue', $value)) {
            return (float) $value['doubleValue'];
        }

        if (array_key_exists('stringValue', $value)) {
            return (string) $value['stringValue'];
        }

        if (array_key_exists('arrayValue', $value)) {
            $values = $value['arrayValue']['values'] ?? [];

            return array_map(
                fn (array $item) => $this->decodeValue($item),
                array_values(array_filter($values, 'is_array')),
            );
        }

        if (array_key_exists('mapValue', $value)) {
            return $this->decodeFields($value['mapValue']['fields'] ?? []);
        }

        return null;
    }

    private function isList(array $value): bool
    {
        if (function_exists('array_is_list')) {
            return array_is_list($value);
        }

        return array_keys($value) === range(0, count($value) - 1);
    }
}
