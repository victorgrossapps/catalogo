@extends('admin.layouts.app')

@section('title', 'Editar catálogo')

@section('content')
    <div class="card">
        <h1>Editar catálogo</h1>
        <p>
            <span class="badge">{{ $catalogo->estado }}</span>
            @if ($catalogo->activo)
                <span class="badge">Activo</span>
            @endif
        </p>
        <p>Peso: {{ number_format($catalogo->peso_bytes) }} bytes</p>
        <p>Checksum: <code>{{ $catalogo->checksum ?? 'Pendiente' }}</code></p>

        <form method="post" action="{{ route('admin.catalogos.update', $catalogo) }}" enctype="multipart/form-data">
            @method('put')
            @include('admin.catalogos._form')
        </form>
    </div>
@endsection
