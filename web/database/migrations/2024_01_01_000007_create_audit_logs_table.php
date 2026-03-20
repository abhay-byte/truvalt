<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('audit_logs', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('action'); // created, updated, deleted, viewed, exported, etc.
            $table->uuid('item_id')->nullable();
            $table->string('item_type')->nullable(); // vault_item, folder, tag
            $table->string('ip_address', 45)->nullable();
            $table->text('user_agent')->nullable();
            $table->bigInteger('performed_at');
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            
            $table->index(['user_id', 'performed_at']);
            $table->index('item_id');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('audit_logs');
    }
};
