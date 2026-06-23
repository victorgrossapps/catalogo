@extends('admin.layouts.app')

@section('title', 'Empresa')

@section('content')
    <div class="card">
        <h1>Configuración de empresa</h1>
        <form method="post" action="{{ route('admin.empresa.update') }}" enctype="multipart/form-data">
            @csrf
            @method('put')

            <label for="nombre_comercial">Nombre comercial</label>
            <input id="nombre_comercial" name="nombre_comercial" value="{{ old('nombre_comercial', $config->nombre_comercial) }}" required>

            <label for="texto_quienes_somos">Quiénes somos</label>
            <textarea id="texto_quienes_somos" name="texto_quienes_somos">{{ old('texto_quienes_somos', $config->texto_quienes_somos) }}</textarea>

            <div class="grid">
                <div>
                    <label for="telefono">Teléfono</label>
                    <input id="telefono" name="telefono" value="{{ old('telefono', $config->telefono) }}">
                </div>
                <div>
                    <label for="whatsapp">WhatsApp</label>
                    <input id="whatsapp" name="whatsapp" value="{{ old('whatsapp', $config->whatsapp) }}">
                </div>
                <div>
                    <label for="correo">Correo</label>
                    <input id="correo" name="correo" type="email" value="{{ old('correo', $config->correo) }}">
                </div>
            </div>

            <label for="direccion">Dirección</label>
            <input id="direccion" name="direccion" value="{{ old('direccion', $config->direccion) }}">

            <label for="redes_sociales_json">Redes sociales JSON</label>
            <textarea id="redes_sociales_json" name="redes_sociales_json">{{ old('redes_sociales_json', $config->redes_sociales_json ? json_encode($config->redes_sociales_json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) : '') }}</textarea>

            <label for="logo">Logo</label>
            <input id="logo" name="logo" type="file" accept="image/jpeg,image/png,image/webp">
            @if ($config->logo_path)
                <p>Actual: <a href="{{ $config->logoUrl() }}" target="_blank">{{ $config->logo_path }}</a></p>
            @endif

            <label for="imagen_portada">Imagen de portada</label>
            <input id="imagen_portada" name="imagen_portada" type="file" accept="image/jpeg,image/png,image/webp">
            @if ($config->imagen_portada_path)
                <p>Actual: <a href="{{ $config->imagenPortadaUrl() }}" target="_blank">{{ $config->imagen_portada_path }}</a></p>
            @endif

            <button class="btn" type="submit">Guardar configuración</button>
        </form>
    </div>
@endsection
