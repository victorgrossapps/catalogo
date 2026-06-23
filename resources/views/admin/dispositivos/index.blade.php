@extends('admin.layouts.app')

@section('title', 'Dispositivos')

@section('content')
    <div class="card">
        <h1>Dispositivos registrados</h1>
        <table>
            <thead>
            <tr>
                <th>UUID</th>
                <th>Nombre</th>
                <th>Alias</th>
                <th>App</th>
                <th>Catálogo</th>
                <th>Última sincronización</th>
                <th>Activo</th>
            </tr>
            </thead>
            <tbody>
            @forelse ($dispositivos as $dispositivo)
                <tr>
                    <td><code>{{ $dispositivo->device_uuid }}</code></td>
                    <td>{{ $dispositivo->nombre_dispositivo ?? '-' }}</td>
                    <td>{{ $dispositivo->alias ?? '-' }}</td>
                    <td>{{ $dispositivo->version_app ?? '-' }}</td>
                    <td>{{ $dispositivo->ultimo_catalogo_version ?? '-' }}</td>
                    <td>{{ $dispositivo->ultima_sincronizacion?->format('Y-m-d H:i') ?? '-' }}</td>
                    <td>{{ $dispositivo->activo ? 'Sí' : 'No' }}</td>
                </tr>
            @empty
                <tr><td colspan="7">No hay dispositivos registrados.</td></tr>
            @endforelse
            </tbody>
        </table>

        {{ $dispositivos->links() }}
    </div>
@endsection
