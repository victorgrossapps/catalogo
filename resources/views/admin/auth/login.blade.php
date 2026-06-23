@extends('admin.layouts.app')

@section('title', 'Login')

@section('content')
    <div class="card" style="max-width: 420px; margin: 50px auto;">
        <h1>Ingreso administrador</h1>
        <form method="post" action="{{ route('admin.login.store') }}">
            @csrf
            <label for="email">Correo</label>
            <input id="email" name="email" type="email" value="{{ old('email') }}" required autofocus>

            <label for="password">Contraseña</label>
            <input id="password" name="password" type="password" required>

            <label>
                <input type="checkbox" name="remember" value="1" style="width: auto;">
                Recordar sesión
            </label>

            <button class="btn" type="submit">Ingresar</button>
        </form>
    </div>
@endsection
