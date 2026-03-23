<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\Firebase\TruvaltFirestoreRepository;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;

class FolderController extends Controller
{
    public function __construct(
        private readonly TruvaltFirestoreRepository $repository,
    ) {
    }

    public function index(Request $request)
    {
        $folders = $this->repository->listFolders((string) $request->user()->id);
        usort($folders, fn (array $left, array $right) => strcmp($left['name'], $right['name']));

        return response()->json($folders);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'id' => 'nullable|string',
            'name' => 'required|string',
            'icon' => 'nullable|string',
            'parent_id' => 'nullable|string',
        ]);

        $folder = $this->repository->saveFolder((string) $request->user()->id, [
            'id' => $validated['id'] ?? null,
            'name' => $validated['name'],
            'icon' => $validated['icon'] ?? null,
            'parent_id' => $this->resolveOwnedParentId((string) $request->user()->id, $validated['parent_id'] ?? null),
        ]);

        return response()->json($folder, 201);
    }

    public function update(Request $request, string $id)
    {
        $folder = $this->repository->getFolder((string) $request->user()->id, $id);

        if ($folder === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $validated = $request->validate([
            'name' => 'nullable|string',
            'icon' => 'nullable|string',
            'parent_id' => 'nullable|string',
        ]);

        $updated = $this->repository->saveFolder((string) $request->user()->id, [
            'id' => $id,
            'name' => $validated['name'] ?? $folder['name'],
            'icon' => array_key_exists('icon', $validated) ? $validated['icon'] : $folder['icon'],
            'parent_id' => array_key_exists('parent_id', $validated)
                ? $this->resolveOwnedParentId((string) $request->user()->id, $validated['parent_id'], $id)
                : $folder['parent_id'],
        ]);

        return response()->json($updated);
    }

    public function destroy(Request $request, string $id)
    {
        $folder = $this->repository->getFolder((string) $request->user()->id, $id);

        if ($folder === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $this->repository->deleteFolder((string) $request->user()->id, $id);

        return response()->json(['message' => 'Folder deleted']);
    }

    private function resolveOwnedParentId(string $uid, ?string $parentId, ?string $folderId = null): ?string
    {
        if ($parentId === null) {
            return null;
        }

        if ($folderId !== null && $parentId === $folderId) {
            throw ValidationException::withMessages([
                'parent_id' => ['A folder cannot be its own parent.'],
            ]);
        }

        if ($this->repository->getFolder($uid, $parentId) === null) {
            throw ValidationException::withMessages([
                'parent_id' => ['The selected parent folder is invalid.'],
            ]);
        }

        return $parentId;
    }
}
