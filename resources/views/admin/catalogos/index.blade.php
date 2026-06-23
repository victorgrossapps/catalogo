@extends('admin.layouts.app')

@section('title', 'Catálogos')

@section('content')
    <div class="card">
        <div style="display:flex; justify-content:space-between; align-items:center;">
            <h1>Catálogos</h1>
            <a class="btn" href="{{ route('admin.catalogos.create') }}">Crear catálogo</a>
        </div>

        <table>
            <thead>
            <tr>
                <th>Título</th>
                <th>Versión</th>
                <th>Estado</th>
                <th>Peso</th>
                <th>Publicado</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            @forelse ($catalogos as $catalogo)
                <tr>
                    <td>{{ $catalogo->titulo }}</td>
                    <td>{{ $catalogo->version_codigo }}</td>
                    <td>
                        <span class="badge">{{ $catalogo->estado }}</span>
                        @if ($catalogo->activo)
                            <span class="badge">Activo</span>
                        @endif
                    </td>
                    <td>{{ number_format($catalogo->peso_bytes) }} bytes</td>
                    <td>{{ $catalogo->publicado_en?->format('Y-m-d H:i') ?? '-' }}</td>
                    <td>
                        <a class="btn secondary" href="{{ route('admin.catalogos.edit', $catalogo) }}">Editar</a>
                        @if (! $catalogo->activo)
                            <form method="post" action="{{ route('admin.catalogos.publish', $catalogo) }}" style="display:inline" onsubmit="return confirm('Publicar este catálogo archivará el catálogo activo actual. ¿Continuar?')">
                                @csrf
                                <button class="btn" type="submit">Publicar</button>
                            </form>
                        @endif
                        @if ($catalogo->activo)
                            <form method="post" action="{{ route('admin.catalogos.archive', $catalogo) }}" style="display:inline" onsubmit="return confirm('Archivar el catálogo activo dejará a la app sin catálogo vigente. ¿Continuar?')">
                                @csrf
                                <button class="btn danger" type="submit">Archivar</button>
                            </form>
                        @endif
                    </td>
                </tr>
            @empty
                <tr><td colspan="6">No hay catálogos registrados.</td></tr>
            @endforelse
            </tbody>
        </table>

        {{ $catalogos->links() }}
    </div>
@endsection
