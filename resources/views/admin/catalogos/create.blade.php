@extends('admin.layouts.app')

@section('title', 'Nuevo catálogo')

@section('content')
    <div class="card">
        <h1>Nuevo catálogo</h1>
        <form method="post" action="{{ route('admin.catalogos.store') }}" enctype="multipart/form-data">
            @include('admin.catalogos._form')
        </form>
    </div>
@endsection
