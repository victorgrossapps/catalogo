<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class CatalogoResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'titulo' => $this->titulo,
            'descripcion' => $this->descripcion,
            'version_codigo' => $this->version_codigo,
            'version_numero' => $this->version_numero,
            'tipo' => $this->tipo,
            'archivo_url' => $this->publicStorageUrl($request, $this->archivo_path),
            'portada_url' => $this->publicStorageUrl($request, $this->portada_path),
            'peso_bytes' => $this->peso_bytes,
            'checksum' => $this->checksum,
            'obligatorio' => $this->obligatorio,
            'mensaje_actualizacion' => $this->mensaje_actualizacion,
            'publicado_en' => $this->publicado_en?->toISOString(),
        ];
    }

    private function publicStorageUrl(Request $request, ?string $path): ?string
    {
        return $path ? $request->root().'/storage/'.ltrim($path, '/') : null;
    }
}
