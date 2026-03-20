<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('vault_item_tags', function (Blueprint $table) {
            $table->uuid('item_id');
            $table->uuid('tag_id');
            
            $table->foreign('item_id')->references('id')->on('vault_items')->onDelete('cascade');
            $table->foreign('tag_id')->references('id')->on('tags')->onDelete('cascade');
            
            $table->primary(['item_id', 'tag_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('vault_item_tags');
    }
};
