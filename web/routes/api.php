<?php

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\VaultController;
use App\Http\Controllers\Api\FolderController;
use App\Http\Controllers\Api\TagController;
use Illuminate\Support\Facades\Route;

// Health check
Route::get('/health', fn() => response()->json(['status' => 'ok']));
Route::get('/keep-alive', fn() => response()->json([
    'status' => 'ok',
    'purpose' => 'keep-alive',
    'timestamp' => now()->toIso8601String(),
]));

// Public routes
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// Protected routes
Route::middleware('auth:sanctum')->group(function () {
    // Auth
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
    
    // Vault items
    Route::get('/vault/items', [VaultController::class, 'index']);
    Route::post('/vault/items', [VaultController::class, 'store']);
    Route::get('/vault/items/{id}', [VaultController::class, 'show']);
    Route::put('/vault/items/{id}', [VaultController::class, 'update']);
    Route::delete('/vault/items/{id}', [VaultController::class, 'destroy']);
    Route::get('/vault/trash', [VaultController::class, 'trash']);
    Route::post('/vault/items/{id}/restore', [VaultController::class, 'restore']);
    Route::post('/vault/sync', [VaultController::class, 'sync']);
    
    // Folders
    Route::get('/folders', [FolderController::class, 'index']);
    Route::post('/folders', [FolderController::class, 'store']);
    Route::put('/folders/{id}', [FolderController::class, 'update']);
    Route::delete('/folders/{id}', [FolderController::class, 'destroy']);
    
    // Tags
    Route::get('/tags', [TagController::class, 'index']);
    Route::post('/tags', [TagController::class, 'store']);
    Route::delete('/tags/{id}', [TagController::class, 'destroy']);
});
