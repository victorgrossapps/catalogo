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
            'archivo_url' => $this->archivoUrl(),
            'portada_url' => $this->portadaUrl(),
            'peso_bytes' => $this->peso_bytes,
            'checksum' => $this->checksum,
            'obligatorio' => $this->obligatorio,
            'mensaje_actualizacion' => $this->mensaje_actualizacion,
            'publicado_en' => $this->publicado_en?->toISOString(),
        ];
    }
}
