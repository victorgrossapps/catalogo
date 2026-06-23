<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Resources\EmpresaConfigResource;
use App\Models\EmpresaConfig;
use Illuminate\Http\JsonResponse;

class EmpresaController extends Controller
{
    public function show(): JsonResponse
    {
        $config = EmpresaConfig::query()->first();

        if (! $config) {
            return response()->json([
                'ok' => false,
                'message' => 'La configuración de empresa no está disponible.',
            ], 404);
        }

        return response()->json([
            'ok' => true,
            'empresa' => EmpresaConfigResource::make($config),
        ]);
    }
}
