<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Folder;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class FolderController extends Controller
{
    public function index(Request $request)
    {
        $folders = Folder::where('user_id', $request->user()->id)
            ->orderBy('name')
            ->get();

        return response()->json($folders);
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string',
            'icon' => 'nullable|string',
            'parent_id' => 'nullable|uuid',
        ]);

        $folder = Folder::create([
            'id' => Str::uuid(),
            'user_id' => $request->user()->id,
            'name' => $request->name,
            'icon' => $request->icon,
            'parent_id' => $this->resolveOwnedParentId($request, $request->parent_id),
            'updated_at' => now()->timestamp,
        ]);

        return response()->json($folder, 201);
    }

    public function update(Request $request, $id)
    {
        $folder = Folder::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->firstOrFail();

        $folder->update([
            'name' => $request->name ?? $folder->name,
            'icon' => $request->icon ?? $folder->icon,
            'parent_id' => $request->has('parent_id')
                ? $this->resolveOwnedParentId($request, $request->parent_id, $folder->id)
                : $folder->parent_id,
            'updated_at' => now()->timestamp,
        ]);

        return response()->json($folder);
    }

    public function destroy(Request $request, $id)
    {
        $folder = Folder::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->firstOrFail();

        $folder->delete();

        return response()->json(['message' => 'Folder deleted']);
    }

    private function resolveOwnedParentId(Request $request, ?string $parentId, ?string $folderId = null): ?string
    {
        if ($parentId === null) {
            return null;
        }

        if ($folderId !== null && $parentId === $folderId) {
            throw ValidationException::withMessages([
                'parent_id' => ['A folder cannot be its own parent.'],
            ]);
        }

        $ownsParent = Folder::where('id', $parentId)
            ->where('user_id', $request->user()->id)
            ->exists();

        if (!$ownsParent) {
            throw ValidationException::withMessages([
                'parent_id' => ['The selected parent folder is invalid.'],
            ]);
        }

        return $parentId;
    }
}
