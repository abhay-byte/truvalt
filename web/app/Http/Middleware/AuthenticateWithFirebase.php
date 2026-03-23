<?php

namespace App\Http\Middleware;

use App\Services\Firebase\FirebaseAuthService;
use Closure;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class AuthenticateWithFirebase
{
    public function __construct(
        private readonly FirebaseAuthService $authService,
    ) {
    }

    public function handle(Request $request, Closure $next): Response
    {
        $idToken = $request->bearerToken();

        if (!is_string($idToken) || $idToken === '') {
            throw new AuthenticationException('Unauthenticated.');
        }

        $user = $this->authService->authenticate($idToken);
        $request->setUserResolver(fn () => $user);

        return $next($request);
    }
}
