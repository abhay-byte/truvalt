<?php

namespace Database\Factories;

use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

/**
 * @extends Factory<User>
 */
class UserFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'email' => fake()->unique()->safeEmail(),
            'auth_key_hash' => password_hash('factory_auth_key_'.Str::lower(Str::random(32)), PASSWORD_ARGON2ID),
            'two_factor_secret' => null,
            'two_factor_confirmed_at' => null,
        ];
    }
}
