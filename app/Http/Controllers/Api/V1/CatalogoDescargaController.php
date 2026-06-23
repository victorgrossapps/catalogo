<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Catalogo;
use App\Models\CatalogoDescarga;
use App\Models\Dispositivo;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class CatalogoDescargaController extends Controller
{
    public function store(Request $request): JsonResponse
    {
        $data = $request->validate([
            'catalogo_id' => ['required', 'integer', 'exists:catalogos,id'],
            'device_uuid' => ['required', 'string', 'max:100'],
            'nombre_dispositivo' => ['nullable', 'string', 'max:150'],
            'version_app' => ['nullable', 'string', 'max:30'],
            'version_catalogo' => ['required', 'string', 'max:50'],
            'estado' => ['required', 'in:exitoso,fallido'],
            'mensaje_error' => ['nullable', 'string', 'max:500'],
        ]);

        $catalogo = Catalogo::query()->findOrFail($data['catalogo_id']);

        $dispositivo = Dispositivo::query()->updateOrCreate(
            ['device_uuid' => $data['device_uuid']],
            [
                'nombre_dispositivo' => $data['nombre_dispositivo'] ?? null,
                'version_app' => $data['version_app'] ?? null,
                'ultimo_catalogo_version' => $data['version_catalogo'],
                'ultima_sincronizacion' => now(),
                'activo' => true,
            ]
        );

        CatalogoDescarga::query()->create([
            'catalogo_id' => $catalogo->id,
            'dispositivo_id' => $dispositivo->id,
            'version_app' => $data['version_app'] ?? null,
            'version_catalogo' => $data['version_catalogo'],
            'estado' => $data['estado'],
            'ip' => $request->ip(),
            'user_agent' => substr((string) $request->userAgent(), 0, 500),
            'mensaje_error' => $data['mensaje_error'] ?? null,
            'descargado_en' => now(),
        ]);

        return response()->json(['ok' => true], 201);
    }
}
