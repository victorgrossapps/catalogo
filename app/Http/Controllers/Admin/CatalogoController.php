<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Catalogo;
use App\Services\Catalogo\CatalogPublishService;
use App\Services\Catalogo\ChecksumService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Illuminate\View\View;

class CatalogoController extends Controller
{
    public function index(): View
    {
        return view('admin.catalogos.index', [
            'catalogos' => Catalogo::query()->latest()->paginate(15),
        ]);
    }

    public function create(): View
    {
        return view('admin.catalogos.create', [
            'catalogo' => new Catalogo([
                'estado' => Catalogo::ESTADO_BORRADOR,
                'tipo' => 'pdf',
            ]),
        ]);
    }

    public function store(Request $request, ChecksumService $checksumService): RedirectResponse
    {
        $data = $this->validatedData($request);
        $data = $this->handleFiles($request, $data, $checksumService);
        $data['created_by'] = $request->user()->id;
        $data['updated_by'] = $request->user()->id;
        $data['estado'] = Catalogo::ESTADO_BORRADOR;
        $data['activo'] = false;

        $catalogo = Catalogo::query()->create($data);

        return redirect()
            ->route('admin.catalogos.edit', $catalogo)
            ->with('status', 'Catálogo creado en borrador.');
    }

    public function edit(Catalogo $catalogo): View
    {
        return view('admin.catalogos.edit', compact('catalogo'));
    }

    public function update(Request $request, Catalogo $catalogo, ChecksumService $checksumService): RedirectResponse
    {
        $data = $this->validatedData($request, $catalogo->id);
        $data = $this->handleFiles($request, $data, $checksumService, $catalogo);
        $data['updated_by'] = $request->user()->id;

        if ($catalogo->activo) {
            $data['estado'] = Catalogo::ESTADO_PUBLICADO;
            $data['activo'] = true;
        }

        $catalogo->update($data);

        return redirect()
            ->route('admin.catalogos.edit', $catalogo)
            ->with('status', 'Catálogo actualizado.');
    }

    public function publish(Catalogo $catalogo, CatalogPublishService $publishService): RedirectResponse
    {
        $publishService->publish($catalogo, auth()->user());

        return redirect()
            ->route('admin.catalogos.index')
            ->with('status', 'Catálogo publicado como activo.');
    }

    public function archive(Catalogo $catalogo, CatalogPublishService $publishService): RedirectResponse
    {
        $publishService->archive($catalogo, auth()->user());

        return redirect()
            ->route('admin.catalogos.index')
            ->with('status', 'Catálogo archivado.');
    }

    private function validatedData(Request $request, ?int $catalogoId = null): array
    {
        return $request->validate([
            'titulo' => ['required', 'string', 'max:200'],
            'descripcion' => ['nullable', 'string'],
            'version_codigo' => ['required', 'string', 'max:50', 'unique:catalogos,version_codigo,'.($catalogoId ?? 'NULL').',id'],
            'version_numero' => ['required', 'integer', 'min:1', 'unique:catalogos,version_numero,'.($catalogoId ?? 'NULL').',id'],
            'mensaje_actualizacion' => ['nullable', 'string', 'max:500'],
            'obligatorio' => ['sometimes', 'boolean'],
            'archivo_pdf' => [$catalogoId ? 'nullable' : 'required', 'file', 'mimetypes:application/pdf', 'max:204800'],
            'portada' => ['nullable', 'image', 'mimes:jpg,jpeg,png,webp', 'max:10240'],
        ]);
    }

    private function handleFiles(
        Request $request,
        array $data,
        ChecksumService $checksumService,
        ?Catalogo $catalogo = null
    ): array {
        $data['obligatorio'] = $request->boolean('obligatorio');
        $data['tipo'] = 'pdf';

        if ($request->hasFile('archivo_pdf')) {
            $file = $request->file('archivo_pdf');
            $slug = Str::slug($data['titulo']).'-'.$data['version_codigo'];
            $path = $file->storeAs('catalogos', $slug.'.pdf', 'public');

            $data['archivo_path'] = $path;
            $data['peso_bytes'] = Storage::disk('public')->size($path);
            $data['checksum'] = $checksumService->forPublicStoragePath($path);
        } elseif ($catalogo) {
            $data['archivo_path'] = $catalogo->archivo_path;
            $data['peso_bytes'] = $catalogo->peso_bytes;
            $data['checksum'] = $catalogo->checksum;
        }

        if ($request->hasFile('portada')) {
            $file = $request->file('portada');
            $slug = Str::slug($data['titulo']).'-'.$data['version_codigo'];
            $data['portada_path'] = $file->storeAs('catalogos', $slug.'-portada.'.$file->extension(), 'public');
        } elseif ($catalogo) {
            $data['portada_path'] = $catalogo->portada_path;
        }

        return $data;
    }
}
