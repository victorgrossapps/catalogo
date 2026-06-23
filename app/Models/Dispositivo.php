<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Model;

#[Fillable([
    'device_uuid',
    'nombre_dispositivo',
    'alias',
    'version_app',
    'ultimo_catalogo_version',
    'ultima_sincronizacion',
    'activo',
])]
class Dispositivo extends Model
{
    protected function casts(): array
    {
        return [
            'activo' => 'boolean',
            'ultima_sincronizacion' => 'datetime',
        ];
    }

    public function descargas()
    {
        return $this->hasMany(CatalogoDescarga::class);
    }
}
