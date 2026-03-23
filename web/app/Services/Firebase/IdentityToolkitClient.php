<?php

namespace App\Services\Firebase;

use Illuminate\Support\Facades\Http;

class IdentityToolkitClient
{
    public function __construct(
        private readonly FirebaseProjectConfig $config,
    ) {
    }

    public function signUpWithEmailPassword(string $email, string $password): array
    {
        return $this->request('accounts:signUp', [
            'email' => $email,
            'password' => $password,
            'returnSecureToken' => true,
        ]);
    }

    public function signInWithEmailPassword(string $email, string $password): array
    {
        return $this->request('accounts:signInWithPassword', [
            'email' => $email,
            'password' => $password,
            'returnSecureToken' => true,
        ]);
    }

    public function signInWithGoogleIdToken(string $googleIdToken): array
    {
        return $this->request('accounts:signInWithIdp', [
            'postBody' => http_build_query([
                'id_token' => $googleIdToken,
                'providerId' => 'google.com',
            ]),
            'requestUri' => $this->config->authRedirectUri(),
            'returnSecureToken' => true,
            'returnIdpCredential' => true,
        ]);
    }

    private function request(string $endpoint, array $payload): array
    {
        $response = Http::asJson()
            ->timeout(20)
            ->connectTimeout(5)
            ->acceptJson()
            ->post($this->url($endpoint), $payload);

        if ($response->failed()) {
            $code = (string) ($response->json('error.message') ?? 'UNKNOWN');
            throw new FirebaseRequestException(
                $this->messageForCode($code),
                $response->status(),
                ['firebase_error' => $code],
            );
        }

        $json = $response->json();

        if (!is_array($json)) {
            throw new FirebaseRequestException('Firebase Auth returned an invalid response.', 502);
        }

        return $json;
    }

    private function url(string $endpoint): string
    {
        return sprintf(
            'https://identitytoolkit.googleapis.com/v1/%s?key=%s',
            $endpoint,
            $this->config->webApiKey(),
        );
    }

    private function messageForCode(string $code): string
    {
        return match ($code) {
            'EMAIL_EXISTS' => 'That email address is already registered.',
            'INVALID_EMAIL' => 'The email address is invalid.',
            'EMAIL_NOT_FOUND', 'INVALID_LOGIN_CREDENTIALS', 'INVALID_PASSWORD' => 'The provided credentials are incorrect.',
            'USER_DISABLED' => 'This account has been disabled.',
            'INVALID_IDP_RESPONSE', 'FEDERATED_USER_ID_ALREADY_LINKED' => 'Google sign-in could not be completed.',
            'TOO_MANY_ATTEMPTS_TRY_LATER' => 'Too many authentication attempts. Try again later.',
            default => 'Firebase authentication failed.',
        };
    }
}
