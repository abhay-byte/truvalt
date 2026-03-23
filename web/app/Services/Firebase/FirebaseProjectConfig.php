<?php

namespace App\Services\Firebase;

use RuntimeException;

class FirebaseProjectConfig
{
    private ?array $serviceAccount = null;

    public function projectId(): string
    {
        $projectId = config('services.firebase.project_id');

        if (is_string($projectId) && $projectId !== '') {
            return $projectId;
        }

        $serviceAccount = $this->serviceAccount();
        $resolvedProjectId = $serviceAccount['project_id'] ?? null;

        if (!is_string($resolvedProjectId) || $resolvedProjectId === '') {
            throw new RuntimeException('Firebase project ID is not configured.');
        }

        return $resolvedProjectId;
    }

    public function serviceAccount(): array
    {
        if ($this->serviceAccount !== null) {
            return $this->serviceAccount;
        }

        $inlineJson = config('services.firebase.credentials_json');

        if (is_string($inlineJson) && trim($inlineJson) !== '') {
            return $this->serviceAccount = $this->decodeJson($inlineJson, 'FIREBASE_CREDENTIALS_JSON');
        }

        $path = config('services.firebase.credentials');

        if (!is_string($path) || $path === '') {
            throw new RuntimeException('Firebase credentials are not configured.');
        }

        if (!is_file($path) || !is_readable($path)) {
            throw new RuntimeException('Firebase credentials file is missing or unreadable.');
        }

        $contents = file_get_contents($path);

        if ($contents === false) {
            throw new RuntimeException('Firebase credentials file could not be read.');
        }

        return $this->serviceAccount = $this->decodeJson($contents, 'FIREBASE_CREDENTIALS');
    }

    public function webApiKey(): string
    {
        $apiKey = config('services.firebase.web_api_key');

        if (!is_string($apiKey) || $apiKey === '') {
            throw new RuntimeException('Firebase web API key is not configured.');
        }

        return $apiKey;
    }

    public function authRedirectUri(): string
    {
        $redirectUri = config('services.firebase.auth_redirect_uri', 'http://localhost');

        if (!is_string($redirectUri) || $redirectUri === '') {
            return 'http://localhost';
        }

        return $redirectUri;
    }

    public function firestoreDatabase(): string
    {
        $database = config('services.firebase.firestore_database', '(default)');

        if (!is_string($database) || $database === '') {
            return '(default)';
        }

        return $database;
    }

    public function checkRevokedTokens(): bool
    {
        return (bool) config('services.firebase.check_revoked_tokens', true);
    }

    private function decodeJson(string $contents, string $source): array
    {
        $decoded = json_decode($contents, true);

        if (!is_array($decoded)) {
            throw new RuntimeException("{$source} does not contain valid Firebase service account JSON.");
        }

        return $decoded;
    }
}
