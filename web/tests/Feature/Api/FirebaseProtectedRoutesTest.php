<?php

namespace Tests\Feature\Api;

use App\Services\Firebase\FirebaseAuthService;
use App\Services\Firebase\TruvaltFirestoreRepository;
use Illuminate\Auth\GenericUser;
use Mockery;
use Tests\TestCase;

class FirebaseProtectedRoutesTest extends TestCase
{
    public function test_me_returns_the_authenticated_firebase_profile(): void
    {
        $service = Mockery::mock(FirebaseAuthService::class);
        $service->shouldReceive('authenticate')
            ->once()
            ->with('firebase-id-token')
            ->andReturn(new GenericUser([
                'id' => 'firebase-user-10',
                'email' => 'me@example.com',
            ]));
        $service->shouldReceive('currentUserProfile')
            ->once()
            ->with('firebase-user-10')
            ->andReturn([
                'id' => 'firebase-user-10',
                'email' => 'me@example.com',
                'providers' => ['password'],
                'auth_key_hash_configured' => true,
                'created_at' => 1700000000,
                'updated_at' => 1700000001,
                'last_login_at' => 1700000002,
                'email_verified' => false,
            ]);

        $this->app->instance(FirebaseAuthService::class, $service);

        $this->withHeader('Authorization', 'Bearer firebase-id-token')
            ->getJson('/api/me')
            ->assertOk()
            ->assertJsonPath('id', 'firebase-user-10')
            ->assertJsonPath('email', 'me@example.com');
    }

    public function test_vault_items_are_loaded_from_firestore_repository(): void
    {
        $service = Mockery::mock(FirebaseAuthService::class);
        $service->shouldReceive('authenticate')
            ->once()
            ->with('firebase-id-token')
            ->andReturn(new GenericUser([
                'id' => 'firebase-user-11',
                'email' => 'vault@example.com',
            ]));

        $repository = Mockery::mock(TruvaltFirestoreRepository::class);
        $repository->shouldReceive('listVaultItems')
            ->once()
            ->with('firebase-user-11')
            ->andReturn([
                [
                    'id' => 'item-1',
                    'user_id' => 'firebase-user-11',
                    'type' => 'login',
                    'name' => 'GitHub',
                    'folder_id' => null,
                    'encrypted_data' => base64_encode('secret'),
                    'favorite' => true,
                    'created_at' => 1700000000,
                    'updated_at' => 1700000005,
                    'deleted_at' => null,
                ],
            ]);

        $this->app->instance(FirebaseAuthService::class, $service);
        $this->app->instance(TruvaltFirestoreRepository::class, $repository);

        $this->withHeader('Authorization', 'Bearer firebase-id-token')
            ->getJson('/api/vault/items')
            ->assertOk()
            ->assertJsonCount(1)
            ->assertJsonPath('0.id', 'item-1')
            ->assertJsonPath('0.favorite', true);
    }

    public function test_protected_routes_require_a_firebase_bearer_token(): void
    {
        $this->getJson('/api/vault/items')
            ->assertUnauthorized()
            ->assertJson([
                'message' => 'Unauthenticated.',
            ]);
    }
}
