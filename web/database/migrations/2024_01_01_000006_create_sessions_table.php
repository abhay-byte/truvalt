<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('sessions', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('device_name');
            $table->string('ip_address', 45)->nullable();
            $table->text('user_agent')->nullable();
            $table->bigInteger('last_active_at');
            $table->timestamps();
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            
            $table->index(['user_id', 'last_active_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('sessions');
    }
};
