<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Tag;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class TagController extends Controller
{
    public function index(Request $request)
    {
        $tags = Tag::where('user_id', $request->user()->id)
            ->orderBy('name')
            ->get();

        return response()->json($tags);
    }

    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string',
        ]);

        $tag = Tag::create([
            'id' => Str::uuid(),
            'user_id' => $request->user()->id,
            'name' => $request->name,
        ]);

        return response()->json($tag, 201);
    }

    public function destroy(Request $request, $id)
    {
        $tag = Tag::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->firstOrFail();

        $tag->delete();

        return response()->json(['message' => 'Tag deleted']);
    }
}
