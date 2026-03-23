<?php

namespace Tests\Feature\Api;

use App\Services\Firebase\FirebaseAuthService;
use Illuminate\Auth\GenericUser;
use Mockery;
use Tests\TestCase;

class FirebaseAuthApiTest extends TestCase
{
    public function test_register_returns_firebase_auth_tokens(): void
    {
        $service = Mockery::mock(FirebaseAuthService::class);
        $service->shouldReceive('registerWithEmailPassword')
            ->once()
            ->with('user@example.com', 'secret12', 'vault-auth')
            ->andReturn([
                'user' => [
                    'id' => 'firebase-user-1',
                    'email' => 'user@example.com',
                    'providers' => ['password'],
                    'auth_key_hash_configured' => true,
                ],
                'token' => 'firebase-id-token',
                'refresh_token' => 'firebase-refresh-token',
                'expires_in' => 3600,
            ]);

        $this->app->instance(FirebaseAuthService::class, $service);

        $this->postJson('/api/register', [
            'email' => 'user@example.com',
            'password' => 'secret12',
            'auth_key_hash' => 'vault-auth',
        ])->assertCreated()
            ->assertJsonPath('token', 'firebase-id-token')
            ->assertJsonPath('refresh_token', 'firebase-refresh-token')
            ->assertJsonPath('user.id', 'firebase-user-1');
    }

    public function test_google_login_returns_firebase_tokens(): void
    {
        $service = Mockery::mock(FirebaseAuthService::class);
        $service->shouldReceive('loginWithGoogleIdToken')
            ->once()
            ->with('google-id-token', null)
            ->andReturn([
                'user' => [
                    'id' => 'firebase-user-2',
                    'email' => 'google@example.com',
                    'providers' => ['google.com'],
                    'auth_key_hash_configured' => false,
                ],
                'token' => 'firebase-google-token',
                'refresh_token' => 'firebase-google-refresh-token',
                'expires_in' => 3600,
            ]);

        $this->app->instance(FirebaseAuthService::class, $service);

        $this->postJson('/api/login/google', [
            'id_token' => 'google-id-token',
        ])->assertOk()
            ->assertJsonPath('token', 'firebase-google-token')
            ->assertJsonPath('user.providers.0', 'google.com');
    }

    public function test_logout_revokes_refresh_tokens_for_authenticated_user(): void
    {
        $service = Mockery::mock(FirebaseAuthService::class);
        $service->shouldReceive('authenticate')
            ->once()
            ->with('firebase-id-token')
            ->andReturn(new GenericUser([
                'id' => 'firebase-user-3',
                'email' => 'logout@example.com',
            ]));
        $service->shouldReceive('logout')
            ->once()
            ->with('firebase-user-3');

        $this->app->instance(FirebaseAuthService::class, $service);

        $this->withHeader('Authorization', 'Bearer firebase-id-token')
            ->postJson('/api/logout')
            ->assertOk()
            ->assertJsonPath('message', 'Logged out successfully. Firebase refresh tokens for this account were revoked.');
    }
}
