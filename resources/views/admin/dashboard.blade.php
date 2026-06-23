@extends('admin.layouts.app')

@section('title', 'Dashboard')

@section('content')
    <h1>Dashboard</h1>
    <div class="grid">
        <div class="card">
            <strong>Catálogo activo</strong>
            <p>{{ $catalogoActivo?->titulo ?? 'No hay catálogo activo' }}</p>
            @if ($catalogoActivo)
                <span class="badge">{{ $catalogoActivo->version_codigo }}</span>
            @endif
        </div>
        <div class="card"><strong>Catálogos</strong><p>{{ $catalogosCount }}</p></div>
        <div class="card"><strong>Dispositivos</strong><p>{{ $dispositivosCount }}</p></div>
        <div class="card"><strong>Descargas registradas</strong><p>{{ $descargasCount }}</p></div>
    </div>
@endsection
