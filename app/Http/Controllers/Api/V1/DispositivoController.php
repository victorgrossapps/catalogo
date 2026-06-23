<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Dispositivo;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class DispositivoController extends Controller
{
    public function sync(Request $request): JsonResponse
    {
        $data = $request->validate([
            'device_uuid' => ['required', 'string', 'max:100'],
            'nombre_dispositivo' => ['nullable', 'string', 'max:150'],
            'version_app' => ['nullable', 'string', 'max:30'],
            'ultimo_catalogo_version' => ['nullable', 'string', 'max:50'],
        ]);

        $dispositivo = Dispositivo::query()->updateOrCreate(
            ['device_uuid' => $data['device_uuid']],
            [
                'nombre_dispositivo' => $data['nombre_dispositivo'] ?? null,
                'version_app' => $data['version_app'] ?? null,
                'ultimo_catalogo_version' => $data['ultimo_catalogo_version'] ?? null,
                'ultima_sincronizacion' => now(),
                'activo' => true,
            ]
        );

        return response()->json([
            'ok' => true,
            'dispositivo_id' => $dispositivo->id,
        ]);
    }
}
