<?php

namespace Database\Seeders;

use App\Models\EmpresaConfig;
use Illuminate\Database\Seeder;

class EmpresaConfigSeeder extends Seeder
{
    public function run(): void
    {
        EmpresaConfig::query()->updateOrCreate(
            ['id' => 1],
            [
                'nombre_comercial' => 'Catálogo Comercial',
                'texto_quienes_somos' => 'Información institucional pendiente de configurar desde el panel administrativo.',
            ]
        );
    }
}
