<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\Firebase\TruvaltFirestoreRepository;
use Illuminate\Http\Request;

class TagController extends Controller
{
    public function __construct(
        private readonly TruvaltFirestoreRepository $repository,
    ) {
    }

    public function index(Request $request)
    {
        $tags = $this->repository->listTags((string) $request->user()->id);
        usort($tags, fn (array $left, array $right) => strcmp($left['name'], $right['name']));

        return response()->json($tags);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'id' => 'nullable|string',
            'name' => 'required|string',
        ]);

        $tag = $this->repository->saveTag((string) $request->user()->id, [
            'id' => $validated['id'] ?? null,
            'name' => $validated['name'],
        ]);

        return response()->json($tag, 201);
    }

    public function destroy(Request $request, string $id)
    {
        $tag = $this->repository->getTag((string) $request->user()->id, $id);

        if ($tag === null) {
            return response()->json(['message' => 'Not found.'], 404);
        }

        $this->repository->deleteTag((string) $request->user()->id, $id);

        return response()->json(['message' => 'Tag deleted']);
    }
}
