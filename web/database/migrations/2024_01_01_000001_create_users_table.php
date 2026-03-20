<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('users', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->string('email')->unique();
            $table->string('auth_key_hash');
            $table->string('two_factor_secret')->nullable();
            $table->timestamp('two_factor_confirmed_at')->nullable();
            $table->timestamps();
            
            $table->index('email');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('users');
    }
};
