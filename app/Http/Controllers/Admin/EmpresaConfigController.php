<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\EmpresaConfig;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Illuminate\View\View;

class EmpresaConfigController extends Controller
{
    public function edit(): View
    {
        $config = EmpresaConfig::query()->firstOrNew([
            'id' => 1,
        ], [
            'nombre_comercial' => config('app.name'),
        ]);

        return view('admin.empresa.edit', compact('config'));
    }

    public function update(Request $request): RedirectResponse
    {
        $data = $request->validate([
            'nombre_comercial' => ['required', 'string', 'max:200'],
            'texto_quienes_somos' => ['nullable', 'string'],
            'telefono' => ['nullable', 'string', 'max:60'],
            'whatsapp' => ['nullable', 'string', 'max:60'],
            'correo' => ['nullable', 'email', 'max:150'],
            'direccion' => ['nullable', 'string', 'max:300'],
            'redes_sociales_json' => ['nullable', 'json'],
            'logo' => ['nullable', 'image', 'mimes:jpg,jpeg,png,webp', 'max:4096'],
            'imagen_portada' => ['nullable', 'image', 'mimes:jpg,jpeg,png,webp', 'max:8192'],
        ]);

        $config = EmpresaConfig::query()->firstOrNew(['id' => 1]);

        if ($request->hasFile('logo')) {
            $data['logo_path'] = $request
                ->file('logo')
                ->storeAs('empresa', 'logo-'.Str::random(8).'.'.$request->file('logo')->extension(), 'public');
        }

        if ($request->hasFile('imagen_portada')) {
            $data['imagen_portada_path'] = $request
                ->file('imagen_portada')
                ->storeAs('empresa', 'portada-'.Str::random(8).'.'.$request->file('imagen_portada')->extension(), 'public');
        }

        if (isset($data['redes_sociales_json'])) {
            $data['redes_sociales_json'] = json_decode($data['redes_sociales_json'], true);
        }

        $config->fill($data)->save();

        return redirect()
            ->route('admin.empresa.edit')
            ->with('status', 'Configuración institucional actualizada.');
    }
}
