<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class VerifyApiKey
{
    public function handle(Request $request, Closure $next): Response
    {
        $expectedKey = config('services.catalogo.api_key');
        $providedKey = $request->header('X-API-Key');

        if (! $expectedKey || ! $providedKey || ! hash_equals($expectedKey, $providedKey)) {
            return response()->json([
                'ok' => false,
                'message' => 'API key inválida.',
            ], 401);
        }

        return $next($request);
    }
}
