<?php

namespace App\Services\Catalogo;

use App\Models\Catalogo;
use App\Models\User;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;

class CatalogPublishService
{
    public function publish(Catalogo $catalogo, ?User $user = null): Catalogo
    {
        if (! $catalogo->esPublicable()) {
            throw ValidationException::withMessages([
                'catalogo' => 'El catálogo debe tener PDF existente, peso y checksum antes de publicarse.',
            ]);
        }

        return DB::transaction(function () use ($catalogo, $user) {
            Catalogo::query()
                ->where('activo', true)
                ->whereKeyNot($catalogo->id)
                ->update([
                    'activo' => false,
                    'estado' => Catalogo::ESTADO_ARCHIVADO,
                    'updated_by' => $user?->id,
                    'updated_at' => now(),
                ]);

            $catalogo->forceFill([
                'activo' => true,
                'estado' => Catalogo::ESTADO_PUBLICADO,
                'publicado_en' => now(),
                'updated_by' => $user?->id,
            ])->save();

            return $catalogo->refresh();
        });
    }

    public function archive(Catalogo $catalogo, ?User $user = null): Catalogo
    {
        $catalogo->forceFill([
            'activo' => false,
            'estado' => Catalogo::ESTADO_ARCHIVADO,
            'updated_by' => $user?->id,
        ])->save();

        return $catalogo->refresh();
    }
}
