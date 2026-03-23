<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        User::updateOrCreate(
            ['email' => 'seeded-api-user@truvalt.local'],
            [
                'auth_key_hash' => password_hash('seeded_auth_key_hash_for_local_testing', PASSWORD_ARGON2ID),
                'two_factor_secret' => null,
                'two_factor_confirmed_at' => null,
            ]
        );
    }
}
