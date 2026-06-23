@extends('admin.layouts.app')

@section('title', 'Descargas')

@section('content')
    <div class="card">
        <h1>Historial de descargas</h1>
        <table>
            <thead>
            <tr>
                <th>Fecha</th>
                <th>Catálogo</th>
                <th>Dispositivo</th>
                <th>App</th>
                <th>Estado</th>
                <th>Error</th>
                <th>IP</th>
            </tr>
            </thead>
            <tbody>
            @forelse ($descargas as $descarga)
                <tr>
                    <td>{{ $descarga->descargado_en?->format('Y-m-d H:i') ?? '-' }}</td>
                    <td>{{ $descarga->catalogo?->titulo ?? '-' }}<br><span class="badge">{{ $descarga->version_catalogo }}</span></td>
                    <td>{{ $descarga->dispositivo?->nombre_dispositivo ?? $descarga->dispositivo?->device_uuid ?? '-' }}</td>
                    <td>{{ $descarga->version_app ?? '-' }}</td>
                    <td>{{ $descarga->estado }}</td>
                    <td>{{ $descarga->mensaje_error ?? '-' }}</td>
                    <td>{{ $descarga->ip ?? '-' }}</td>
                </tr>
            @empty
                <tr><td colspan="7">No hay descargas registradas.</td></tr>
            @endforelse
            </tbody>
        </table>

        {{ $descargas->links() }}
    </div>
@endsection
