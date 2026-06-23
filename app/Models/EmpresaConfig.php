<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Storage;

#[Fillable([
    'nombre_comercial',
    'logo_path',
    'texto_quienes_somos',
    'telefono',
    'whatsapp',
    'correo',
    'direccion',
    'redes_sociales_json',
    'imagen_portada_path',
])]
class EmpresaConfig extends Model
{
    protected $table = 'empresa_config';

    protected function casts(): array
    {
        return [
            'redes_sociales_json' => 'array',
        ];
    }

    public function logoUrl(): ?string
    {
        return $this->logo_path ? Storage::disk('public')->url($this->logo_path) : null;
    }

    public function imagenPortadaUrl(): ?string
    {
        return $this->imagen_portada_path ? Storage::disk('public')->url($this->imagen_portada_path) : null;
    }
}
