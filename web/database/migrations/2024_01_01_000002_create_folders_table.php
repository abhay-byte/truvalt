<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('folders', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id');
            $table->string('name');
            $table->string('icon')->nullable();
            $table->uuid('parent_id')->nullable();
            $table->bigInteger('updated_at');
            
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('parent_id')->references('id')->on('folders')->onDelete('cascade');
            
            $table->index(['user_id', 'parent_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('folders');
    }
};
