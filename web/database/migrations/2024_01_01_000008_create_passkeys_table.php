<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('passkeys', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('credential_id')->unique();
            $table->text('public_key');
            $table->integer('sign_count')->default(0);
            $table->string('device_name')->nullable();
            $table->timestamps();
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            
            $table->index('user_id');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('passkeys');
    }
};
