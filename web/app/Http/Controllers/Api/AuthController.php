<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\Firebase\FirebaseAuthService;
use App\Services\Firebase\FirebaseRequestException;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function __construct(
        private readonly FirebaseAuthService $authService,
    ) {
    }

    public function register(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:6',
            'auth_key_hash' => 'nullable|string',
        ]);

        try {
            return response()->json(
                $this->authService->registerWithEmailPassword(
                    $validated['email'],
                    $validated['password'],
                    $validated['auth_key_hash'] ?? null,
                ),
                201,
            );
        } catch (FirebaseRequestException $exception) {
            throw ValidationException::withMessages([
                'email' => [$exception->getMessage()],
            ]);
        }
    }

    public function login(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string|min:6',
            'auth_key_hash' => 'nullable|string',
        ]);

        try {
            return response()->json(
                $this->authService->loginWithEmailPassword(
                    $validated['email'],
                    $validated['password'],
                    $validated['auth_key_hash'] ?? null,
                )
            );
        } catch (FirebaseRequestException $exception) {
            throw ValidationException::withMessages([
                'email' => [$exception->getMessage()],
            ]);
        }
    }

    public function google(Request $request)
    {
        $validated = $request->validate([
            'id_token' => 'required|string',
            'auth_key_hash' => 'nullable|string',
        ]);

        try {
            return response()->json(
                $this->authService->loginWithGoogleIdToken(
                    $validated['id_token'],
                    $validated['auth_key_hash'] ?? null,
                )
            );
        } catch (FirebaseRequestException $exception) {
            throw ValidationException::withMessages([
                'id_token' => [$exception->getMessage()],
            ]);
        }
    }

    public function logout(Request $request)
    {
        $this->authService->logout((string) $request->user()->id);

        return response()->json([
            'message' => 'Logged out successfully. Firebase refresh tokens for this account were revoked.',
        ]);
    }

    public function me(Request $request)
    {
        return response()->json(
            $this->authService->currentUserProfile((string) $request->user()->id)
        );
    }
}
