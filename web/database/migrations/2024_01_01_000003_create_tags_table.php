<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('tags', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('name');
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            
            $table->index('user_id');
            $table->unique(['user_id', 'name']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('tags');
    }
};
