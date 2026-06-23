<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Fillable;
use Illuminate\Database\Eloquent\Model;

#[Fillable([
    'catalogo_id',
    'dispositivo_id',
    'version_app',
    'version_catalogo',
    'estado',
    'ip',
    'user_agent',
    'mensaje_error',
    'descargado_en',
])]
class CatalogoDescarga extends Model
{
    protected $table = 'catalogo_descargas';

    protected function casts(): array
    {
        return [
            'descargado_en' => 'datetime',
        ];
    }

    public function catalogo()
    {
        return $this->belongsTo(Catalogo::class);
    }

    public function dispositivo()
    {
        return $this->belongsTo(Dispositivo::class);
    }
}
