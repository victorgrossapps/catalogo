<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Resources\CatalogoResource;
use App\Models\Catalogo;
use Illuminate\Http\JsonResponse;

class CatalogoController extends Controller
{
    public function actual(): JsonResponse
    {
        $catalogo = Catalogo::query()
            ->where('activo', true)
            ->where('estado', Catalogo::ESTADO_PUBLICADO)
            ->latest('publicado_en')
            ->first();

        if (! $catalogo) {
            return response()->json([
                'ok' => false,
                'message' => 'No hay catálogo activo.',
            ], 404);
        }

        return response()->json([
            'ok' => true,
            'catalogo' => CatalogoResource::make($catalogo),
        ]);
    }
}
