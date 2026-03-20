<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('vault_items', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('type'); // login, passkey, secure_note, etc.
            $table->string('name');
            $table->uuid('folder_id')->nullable();
            $table->binary('encrypted_data'); // AES-256-GCM encrypted blob
            $table->boolean('favorite')->default(false);
            $table->bigInteger('created_at');
            $table->bigInteger('updated_at');
            $table->bigInteger('deleted_at')->nullable();
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('folder_id')->references('id')->on('folders')->onDelete('set null');
            
            $table->index(['user_id', 'deleted_at']);
            $table->index(['user_id', 'type']);
            $table->index(['user_id', 'folder_id']);
            $table->index('updated_at');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('vault_items');
    }
};
