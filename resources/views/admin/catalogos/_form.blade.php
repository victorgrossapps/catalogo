@csrf

<label for="titulo">Título</label>
<input id="titulo" name="titulo" value="{{ old('titulo', $catalogo->titulo) }}" required>

<label for="descripcion">Descripción</label>
<textarea id="descripcion" name="descripcion">{{ old('descripcion', $catalogo->descripcion) }}</textarea>

<div class="grid">
    <div>
        <label for="version_codigo">Versión visible</label>
        <input id="version_codigo" name="version_codigo" value="{{ old('version_codigo', $catalogo->version_codigo) }}" placeholder="2026.06.01" required>
    </div>
    <div>
        <label for="version_numero">Versión numérica</label>
        <input id="version_numero" name="version_numero" type="number" value="{{ old('version_numero', $catalogo->version_numero) }}" placeholder="20260601" required>
    </div>
</div>

<label for="archivo_pdf">Archivo PDF</label>
<input id="archivo_pdf" name="archivo_pdf" type="file" accept="application/pdf" {{ $catalogo->exists ? '' : 'required' }}>
@if ($catalogo->archivo_path)
    <p>Actual: <a href="{{ $catalogo->archivoUrl() }}" target="_blank">{{ $catalogo->archivo_path }}</a></p>
@endif

<label for="portada">Portada</label>
<input id="portada" name="portada" type="file" accept="image/jpeg,image/png,image/webp">
@if ($catalogo->portada_path)
    <p>Actual: <a href="{{ $catalogo->portadaUrl() }}" target="_blank">{{ $catalogo->portada_path }}</a></p>
@endif

<label for="mensaje_actualizacion">Mensaje de actualización</label>
<input id="mensaje_actualizacion" name="mensaje_actualizacion" value="{{ old('mensaje_actualizacion', $catalogo->mensaje_actualizacion) }}">

<label>
    <input type="checkbox" name="obligatorio" value="1" style="width: auto;" @checked(old('obligatorio', $catalogo->obligatorio))>
    Actualización obligatoria
</label>

<button class="btn" type="submit">Guardar</button>
<a class="btn secondary" href="{{ route('admin.catalogos.index') }}">Cancelar</a>
