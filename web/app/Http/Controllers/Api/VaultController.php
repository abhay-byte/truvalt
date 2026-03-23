<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Folder;
use App\Models\VaultItem;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class VaultController extends Controller
{
    public function index(Request $request)
    {
        $query = VaultItem::where('user_id', $request->user()->id)
            ->whereNull('deleted_at');

        if ($request->has('updated_after')) {
            $query->where('updated_at', '>', $request->updated_after);
        }

        if ($request->has('type')) {
            $query->where('type', $request->type);
        }

        if ($request->has('folder_id')) {
            $query->where('folder_id', $request->folder_id);
        }

        $items = $query->orderBy('updated_at', 'desc')->get();

        return response()->json($items);
    }

    public function store(Request $request)
    {
        $request->validate([
            'type' => 'required|string',
            'name' => 'required|string',
            'encrypted_data' => 'required|string',
            'folder_id' => 'nullable|uuid',
            'favorite' => 'boolean',
        ]);

        $item = VaultItem::create([
            'id' => Str::uuid(),
            'user_id' => $request->user()->id,
            'type' => $request->type,
            'name' => $request->name,
            'folder_id' => $this->resolveOwnedFolderId($request, $request->folder_id),
            'encrypted_data' => $this->decodeEncryptedData($request->encrypted_data),
            'favorite' => $request->favorite ?? false,
            'created_at' => now()->timestamp,
            'updated_at' => now()->timestamp,
        ]);

        return response()->json($item, 201);
    }

    public function show(Request $request, $id)
    {
        $item = VaultItem::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->whereNull('deleted_at')
            ->firstOrFail();

        return response()->json($item);
    }

    public function update(Request $request, $id)
    {
        $item = VaultItem::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->whereNull('deleted_at')
            ->firstOrFail();

        $item->update([
            'name' => $request->name ?? $item->name,
            'encrypted_data' => $request->filled('encrypted_data')
                ? $this->decodeEncryptedData($request->encrypted_data)
                : $item->getRawOriginal('encrypted_data'),
            'folder_id' => $request->has('folder_id')
                ? $this->resolveOwnedFolderId($request, $request->folder_id)
                : $item->folder_id,
            'favorite' => $request->favorite ?? $item->favorite,
            'updated_at' => now()->timestamp,
        ]);

        return response()->json($item);
    }

    public function destroy(Request $request, $id)
    {
        $item = VaultItem::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->whereNull('deleted_at')
            ->firstOrFail();

        $item->update(['deleted_at' => now()->timestamp]);

        return response()->json(['message' => 'Item moved to trash']);
    }

    public function trash(Request $request)
    {
        $items = VaultItem::where('user_id', $request->user()->id)
            ->whereNotNull('deleted_at')
            ->orderBy('deleted_at', 'desc')
            ->get();

        return response()->json($items);
    }

    public function restore(Request $request, $id)
    {
        $item = VaultItem::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->whereNotNull('deleted_at')
            ->firstOrFail();

        $item->update(['deleted_at' => null, 'updated_at' => now()->timestamp]);

        return response()->json($item);
    }

    public function sync(Request $request)
    {
        $request->validate([
            'items' => 'required|array',
            'items.*.id' => 'required|uuid',
            'items.*.type' => 'required|string',
            'items.*.name' => 'required|string',
            'items.*.encrypted_data' => 'required',
            'items.*.updated_at' => 'required|integer',
        ]);

        $synced = [];
        $conflicts = [];

        foreach ($request->items as $itemData) {
            $existing = VaultItem::where('user_id', $request->user()->id)
                ->where('id', $itemData['id'])
                ->first();

            if ($existing && $existing->updated_at > $itemData['updated_at']) {
                $conflicts[] = $existing;
            } else {
                $item = VaultItem::updateOrCreate(
                    ['id' => $itemData['id'], 'user_id' => $request->user()->id],
                    [
                        'type' => $itemData['type'],
                        'name' => $itemData['name'],
                        'encrypted_data' => $this->decodeEncryptedData($itemData['encrypted_data']),
                        'folder_id' => $this->resolveOwnedFolderId($request, $itemData['folder_id'] ?? null),
                        'favorite' => $itemData['favorite'] ?? false,
                        'created_at' => $itemData['created_at'] ?? now()->timestamp,
                        'updated_at' => $itemData['updated_at'],
                        'deleted_at' => $itemData['deleted_at'] ?? null,
                    ]
                );
                $synced[] = $item;
            }
        }

        return response()->json([
            'synced' => $synced,
            'conflicts' => $conflicts,
        ]);
    }

    private function decodeEncryptedData(string $encoded): string
    {
        $decoded = base64_decode($encoded, true);

        if ($decoded === false) {
            throw ValidationException::withMessages([
                'encrypted_data' => ['The encrypted_data field must be valid base64.'],
            ]);
        }

        return $decoded;
    }

    private function resolveOwnedFolderId(Request $request, ?string $folderId): ?string
    {
        if ($folderId === null) {
            return null;
        }

        $ownsFolder = Folder::where('id', $folderId)
            ->where('user_id', $request->user()->id)
            ->exists();

        if (!$ownsFolder) {
            throw ValidationException::withMessages([
                'folder_id' => ['The selected folder is invalid.'],
            ]);
        }

        return $folderId;
    }
}
