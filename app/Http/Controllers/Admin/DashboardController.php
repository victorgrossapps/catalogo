<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Catalogo;
use App\Models\CatalogoDescarga;
use App\Models\Dispositivo;
use Illuminate\View\View;

class DashboardController extends Controller
{
    public function __invoke(): View
    {
        return view('admin.dashboard', [
            'catalogoActivo' => Catalogo::query()->where('activo', true)->first(),
            'catalogosCount' => Catalogo::query()->count(),
            'dispositivosCount' => Dispositivo::query()->count(),
            'descargasCount' => CatalogoDescarga::query()->count(),
        ]);
    }
}
