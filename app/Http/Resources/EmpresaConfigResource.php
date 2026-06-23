<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class EmpresaConfigResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'nombre_comercial' => $this->nombre_comercial,
            'logo_url' => $this->logoUrl(),
            'texto_quienes_somos' => $this->texto_quienes_somos,
            'telefono' => $this->telefono,
            'whatsapp' => $this->whatsapp,
            'correo' => $this->correo,
            'direccion' => $this->direccion,
            'redes_sociales' => $this->redes_sociales_json ?? [],
            'imagen_portada_url' => $this->imagenPortadaUrl(),
            'updated_at' => $this->updated_at?->toISOString(),
        ];
    }
}
