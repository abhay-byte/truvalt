<?php

namespace App\Services\Firebase;

use Illuminate\Auth\AuthenticationException;
use Illuminate\Auth\GenericUser;
use Kreait\Firebase\Exception\Auth\FailedToVerifyToken;
use Kreait\Firebase\Exception\Auth\UserNotFound;

class FirebaseAuthService
{
    public function __construct(
        private readonly FirebaseAdmin $admin,
        private readonly IdentityToolkitClient $identityToolkit,
        private readonly TruvaltFirestoreRepository $repository,
        private readonly FirebaseProjectConfig $config,
    ) {
    }

    public function registerWithEmailPassword(string $email, string $password, ?string $authKeyHash): array
    {
        $result = $this->identityToolkit->signUpWithEmailPassword($email, $password);
        $user = $this->hydrateUserProfile($this->firebaseUid($result), $authKeyHash);

        return $this->buildAuthResponse($user, $result);
    }

    public function loginWithEmailPassword(string $email, string $password, ?string $authKeyHash = null): array
    {
        $result = $this->identityToolkit->signInWithEmailPassword($email, $password);
        $user = $this->hydrateUserProfile($this->firebaseUid($result), $authKeyHash);

        return $this->buildAuthResponse($user, $result);
    }

    public function loginWithGoogleIdToken(string $googleIdToken, ?string $authKeyHash = null): array
    {
        $result = $this->identityToolkit->signInWithGoogleIdToken($googleIdToken);
        $user = $this->hydrateUserProfile($this->firebaseUid($result), $authKeyHash);

        return $this->buildAuthResponse($user, $result);
    }

    public function authenticate(string $idToken): GenericUser
    {
        try {
            $token = $this->admin->auth()->verifyIdToken(
                $idToken,
                $this->config->checkRevokedTokens(),
            );
        } catch (FailedToVerifyToken) {
            throw new AuthenticationException('Unauthenticated.');
        }

        $uid = (string) $token->claims()->get('sub');

        try {
            $record = $this->admin->auth()->getUser($uid);
        } catch (UserNotFound) {
            throw new AuthenticationException('Unauthenticated.');
        }
        $profile = $this->repository->getUserProfile($uid)
            ?? $this->repository->saveUserProfile($uid, $this->profileFromRecord($record, []));

        return new GenericUser([
            'id' => $uid,
            'email' => $profile['email'],
            'providers' => $profile['providers'],
            'email_verified' => $profile['email_verified'],
        ]);
    }

    public function logout(string $uid): void
    {
        try {
            $this->admin->auth()->revokeRefreshTokens($uid);
        } catch (UserNotFound) {
            throw new AuthenticationException('Unauthenticated.');
        }
    }

    public function currentUserProfile(string $uid): array
    {
        return $this->repository->getUserProfile($uid)
            ?? $this->repository->saveUserProfile($uid, ['id' => $uid]);
    }

    private function hydrateUserProfile(string $uid, ?string $authKeyHash): array
    {
        $record = $this->admin->auth()->getUser($uid);
        $existing = $this->repository->getRawUserProfile($uid) ?? [];

        if ($authKeyHash !== null && isset($existing['auth_key_hash']) && !password_verify($authKeyHash, $existing['auth_key_hash'])) {
            throw new FirebaseRequestException('The provided vault authentication material is incorrect.', 422);
        }

        $attributes = $this->profileFromRecord($record, $existing);

        if ($authKeyHash !== null) {
            $attributes['auth_key_hash'] = isset($existing['auth_key_hash']) && password_verify($authKeyHash, $existing['auth_key_hash'])
                ? $existing['auth_key_hash']
                : password_hash($authKeyHash, PASSWORD_ARGON2ID);
        } elseif (isset($existing['auth_key_hash'])) {
            $attributes['auth_key_hash'] = $existing['auth_key_hash'];
        }

        return $this->repository->saveUserProfile($uid, $attributes);
    }

    private function profileFromRecord(object $record, array $existing): array
    {
        $providerIds = [];
        $providerData = $record->providerData ?? [];

        if (!is_iterable($providerData)) {
            $providerData = [];
        }

        foreach ($providerData as $provider) {
            if (isset($provider->providerId) && is_string($provider->providerId)) {
                $providerIds[] = $provider->providerId;
            }
        }

        if ($providerIds === []) {
            $providerIds[] = 'password';
        }

        return [
            'email' => (string) $record->email,
            'providers' => array_values(array_unique($providerIds)),
            'email_verified' => (bool) $record->emailVerified,
            'last_login_at' => now()->timestamp,
            'created_at' => $existing['created_at'] ?? now()->timestamp,
        ];
    }

    private function buildAuthResponse(array $user, array $result): array
    {
        return [
            'user' => $user,
            'token' => (string) ($result['idToken'] ?? ''),
            'refresh_token' => (string) ($result['refreshToken'] ?? ''),
            'expires_in' => (int) ($result['expiresIn'] ?? 0),
        ];
    }

    private function firebaseUid(array $result): string
    {
        $uid = $result['localId'] ?? null;

        if (!is_string($uid) || $uid === '') {
            throw new FirebaseRequestException('Firebase authentication returned an invalid user ID.', 502);
        }

        return $uid;
    }
}
