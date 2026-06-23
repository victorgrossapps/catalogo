<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\CatalogoDescarga;
use App\Models\Dispositivo;
use Illuminate\View\View;

class DispositivoController extends Controller
{
    public function index(): View
    {
        return view('admin.dispositivos.index', [
            'dispositivos' => Dispositivo::query()
                ->latest('ultima_sincronizacion')
                ->paginate(20),
        ]);
    }

    public function descargas(): View
    {
        return view('admin.dispositivos.descargas', [
            'descargas' => CatalogoDescarga::query()
                ->with(['catalogo', 'dispositivo'])
                ->latest('descargado_en')
                ->paginate(30),
        ]);
    }
}
