<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\Firebase\TruvaltFirestoreRepository;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;

class VaultController extends Controller
{
    public function __construct(
        private readonly TruvaltFirestoreRepository $repository,
    ) {
    }

    public function index(Request $request)
    {
        $items = array_filter(
            $this->repository->listVaultItems((string) $request->user()->id),
            fn (array $item) => $item['deleted_at'] === null
        );

        if ($request->filled('updated_after')) {
            $updatedAfter = (int) $request->query('updated_after');
            $items = array_filter($items, fn (array $item) => $item['updated_at'] > $updatedAfter);
        }

        if ($request->filled('type')) {
            $type = (string) $request->query('type');
            $items = array_filter($items, fn (array $item) => $item['type'] === $type);
        }

        if ($request->filled('folder_id')) {
            $folderId = (string) $request->query('folder_id');
            $items = array_filter($items, fn (array $item) => $item['folder_id'] === $folderId);
        }

        usort($items, fn (array $left, array $right) => $right['updated_at'] <=> $left['updated_at']);

        return response()->json(array_values($items));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'type' => 'required|string',
            'name' => 'required|string',
            'encrypted_data' => 'required|string',
            'folder_id' => 'nullable|string',
            'favorite' => 'boolean',
        ]);

        $item = $this->repository->saveVaultItem((string) $request->user()->id, [
            'type' => $validated['type'],
            'name' => $validated['name'],
            'folder_id' => $this->resolveOwnedFolderId((string) $request->user()->id, $validated['folder_id'] ?? null),
            'encrypted_data' => $this->normalizeEncryptedData($validated['encrypted_data']),
            'favorite' => $validated['favorite'] ?? false,
        ]);

        return response()->json($item, 201);
    }

    public function show(Request $request, string $id)
    {
        $item = $this->activeItem((string) $request->user()->id, $id);

        if ($item === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        return response()->json($item);
    }

    public function update(Request $request, string $id)
    {
        $item = $this->activeItem((string) $request->user()->id, $id);

        if ($item === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $validated = $request->validate([
            'name' => 'nullable|string',
            'encrypted_data' => 'nullable|string',
            'folder_id' => 'nullable|string',
            'favorite' => 'nullable|boolean',
        ]);

        $updated = $this->repository->saveVaultItem((string) $request->user()->id, [
            'id' => $id,
            'type' => $item['type'],
            'name' => $validated['name'] ?? $item['name'],
            'folder_id' => array_key_exists('folder_id', $validated)
                ? $this->resolveOwnedFolderId((string) $request->user()->id, $validated['folder_id'])
                : $item['folder_id'],
            'encrypted_data' => array_key_exists('encrypted_data', $validated)
                ? $this->normalizeEncryptedData((string) $validated['encrypted_data'])
                : $item['encrypted_data'],
            'favorite' => $validated['favorite'] ?? $item['favorite'],
            'created_at' => $item['created_at'],
            'deleted_at' => $item['deleted_at'],
        ]);

        return response()->json($updated);
    }

    public function destroy(Request $request, string $id)
    {
        $item = $this->activeItem((string) $request->user()->id, $id);

        if ($item === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $this->repository->saveVaultItem((string) $request->user()->id, [
            'id' => $id,
            'deleted_at' => now()->timestamp,
            'updated_at' => now()->timestamp,
        ]);

        return response()->json(['message' => 'Item moved to trash']);
    }

    public function trash(Request $request)
    {
        $items = array_filter(
            $this->repository->listVaultItems((string) $request->user()->id),
            fn (array $item) => $item['deleted_at'] !== null
        );

        usort($items, fn (array $left, array $right) => $right['deleted_at'] <=> $left['deleted_at']);

        return response()->json(array_values($items));
    }

    public function restore(Request $request, string $id)
    {
        $item = $this->repository->getVaultItem((string) $request->user()->id, $id);

        if ($item === null || $item['deleted_at'] === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $restored = $this->repository->saveVaultItem((string) $request->user()->id, [
            'id' => $id,
            'deleted_at' => null,
            'updated_at' => now()->timestamp,
        ]);

        return response()->json($restored);
    }

    public function sync(Request $request)
    {
        $validated = $request->validate([
            'items' => 'required|array',
            'items.*.id' => 'required|string',
            'items.*.type' => 'required|string',
            'items.*.name' => 'required|string',
            'items.*.encrypted_data' => 'required|string',
            'items.*.updated_at' => 'required|integer',
            'items.*.created_at' => 'nullable|integer',
            'items.*.folder_id' => 'nullable|string',
            'items.*.favorite' => 'nullable|boolean',
            'items.*.deleted_at' => 'nullable|integer',
        ]);

        $uid = (string) $request->user()->id;
        $synced = [];
        $conflicts = [];

        foreach ($validated['items'] as $itemData) {
            $existing = $this->repository->getVaultItem($uid, $itemData['id']);

            if ($existing !== null && $existing['updated_at'] > $itemData['updated_at']) {
                $conflicts[] = $existing;
                continue;
            }

            $synced[] = $this->repository->saveVaultItem($uid, [
                'id' => $itemData['id'],
                'type' => $itemData['type'],
                'name' => $itemData['name'],
                'encrypted_data' => $this->normalizeEncryptedData($itemData['encrypted_data']),
                'folder_id' => $this->resolveOwnedFolderId($uid, $itemData['folder_id'] ?? null),
                'favorite' => $itemData['favorite'] ?? false,
                'created_at' => $itemData['created_at'] ?? now()->timestamp,
                'updated_at' => $itemData['updated_at'],
                'deleted_at' => $itemData['deleted_at'] ?? null,
            ]);
        }

        return response()->json([
            'synced' => $synced,
            'conflicts' => $conflicts,
        ]);
    }

    private function activeItem(string $uid, string $itemId): ?array
    {
        $item = $this->repository->getVaultItem($uid, $itemId);

        if ($item === null || $item['deleted_at'] !== null) {
            return null;
        }

        return $item;
    }

    private function normalizeEncryptedData(string $encoded): string
    {
        $decoded = base64_decode($encoded, true);

        if ($decoded === false) {
            throw ValidationException::withMessages([
                'encrypted_data' => ['The encrypted_data field must be valid base64.'],
            ]);
        }

        return base64_encode($decoded);
    }

    private function resolveOwnedFolderId(string $uid, ?string $folderId): ?string
    {
        if ($folderId === null) {
            return null;
        }

        if ($this->repository->getFolder($uid, $folderId) === null) {
            throw ValidationException::withMessages([
                'folder_id' => ['The selected folder is invalid.'],
            ]);
        }

        return $folderId;
    }
}
