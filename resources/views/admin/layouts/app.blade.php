<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>@yield('title', 'Panel Administrativo') - {{ config('app.name') }}</title>
    <style>
        body { font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; margin: 0; background: #f5f7fb; color: #1f2937; }
        header { background: #111827; color: #fff; padding: 16px 24px; display: flex; justify-content: space-between; align-items: center; }
        nav a, nav button { color: #fff; margin-right: 14px; text-decoration: none; background: transparent; border: 0; cursor: pointer; font: inherit; }
        main { max-width: 1180px; margin: 28px auto; padding: 0 20px; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 20px; box-shadow: 0 1px 2px rgba(0,0,0,.04); margin-bottom: 18px; }
        .grid { display: grid; gap: 16px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
        label { display: block; font-weight: 600; margin: 14px 0 6px; }
        input, textarea, select { width: 100%; padding: 10px; border: 1px solid #d1d5db; border-radius: 8px; box-sizing: border-box; }
        textarea { min-height: 110px; }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { border-bottom: 1px solid #e5e7eb; padding: 10px; text-align: left; vertical-align: top; }
        .btn { display: inline-block; padding: 9px 12px; border-radius: 8px; border: 1px solid #1f2937; background: #1f2937; color: #fff; text-decoration: none; cursor: pointer; }
        .btn.secondary { background: #fff; color: #1f2937; }
        .btn.danger { background: #991b1b; border-color: #991b1b; }
        .status { padding: 10px 12px; border-radius: 8px; background: #ecfdf5; border: 1px solid #bbf7d0; margin-bottom: 16px; }
        .errors { padding: 10px 12px; border-radius: 8px; background: #fef2f2; border: 1px solid #fecaca; margin-bottom: 16px; }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 999px; background: #e5e7eb; font-size: 12px; }
    </style>
</head>
<body>
<header>
    <strong>{{ config('app.name') }}</strong>
    @auth
        <nav>
            <a href="{{ route('admin.dashboard') }}">Dashboard</a>
            <a href="{{ route('admin.catalogos.index') }}">Catálogos</a>
            <a href="{{ route('admin.empresa.edit') }}">Empresa</a>
            <a href="{{ route('admin.dispositivos.index') }}">Dispositivos</a>
            <a href="{{ route('admin.descargas.index') }}">Descargas</a>
            <form action="{{ route('admin.logout') }}" method="post" style="display:inline">
                @csrf
                <button type="submit">Salir</button>
            </form>
        </nav>
    @endauth
</header>
<main>
    @if (session('status'))
        <div class="status">{{ session('status') }}</div>
    @endif

    @if ($errors->any())
        <div class="errors">
            <strong>Revisa los campos:</strong>
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    @yield('content')
</main>
</body>
</html>
