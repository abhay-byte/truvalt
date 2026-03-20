<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Folder;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

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
            'parent_id' => 'nullable|uuid|exists:folders,id',
        ]);

        $folder = Folder::create([
            'id' => Str::uuid(),
            'user_id' => $request->user()->id,
            'name' => $request->name,
            'icon' => $request->icon,
            'parent_id' => $request->parent_id,
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
            'parent_id' => $request->parent_id ?? $folder->parent_id,
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
}
