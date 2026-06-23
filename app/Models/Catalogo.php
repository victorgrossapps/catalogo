<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Storage;

#[Fillable([
    'titulo',
    'descripcion',
    'version_codigo',
    'version_numero',
    'archivo_path',
    'portada_path',
    'tipo',
    'peso_bytes',
    'checksum',
    'estado',
    'activo',
    'obligatorio',
    'mensaje_actualizacion',
    'publicado_en',
    'created_by',
    'updated_by',
])]
class Catalogo extends Model
{
    public const ESTADO_BORRADOR = 'BORRADOR';
    public const ESTADO_PUBLICADO = 'PUBLICADO';
    public const ESTADO_ARCHIVADO = 'ARCHIVADO';

    protected function casts(): array
    {
        return [
            'activo' => 'boolean',
            'obligatorio' => 'boolean',
            'publicado_en' => 'datetime',
            'version_numero' => 'integer',
            'peso_bytes' => 'integer',
        ];
    }

    public function creator()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function updater()
    {
        return $this->belongsTo(User::class, 'updated_by');
    }

    public function descargas()
    {
        return $this->hasMany(CatalogoDescarga::class);
    }

    public function archivoUrl(): ?string
    {
        return $this->archivo_path ? Storage::disk('public')->url($this->archivo_path) : null;
    }

    public function portadaUrl(): ?string
    {
        return $this->portada_path ? Storage::disk('public')->url($this->portada_path) : null;
    }

    public function esPublicable(): bool
    {
        return filled($this->archivo_path)
            && $this->peso_bytes > 0
            && filled($this->checksum)
            && Storage::disk('public')->exists($this->archivo_path);
    }
}
