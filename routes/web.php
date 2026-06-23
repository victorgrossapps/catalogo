<?php

use App\Http\Controllers\Admin\AuthController;
use App\Http\Controllers\Admin\CatalogoController;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\DispositivoController;
use App\Http\Controllers\Admin\EmpresaConfigController;
use Illuminate\Support\Facades\Route;

Route::redirect('/', '/admin');
Route::redirect('/login', '/admin/login')->name('login');

Route::prefix('admin')->name('admin.')->group(function () {
    Route::middleware('guest')->group(function () {
        Route::get('login', [AuthController::class, 'showLogin'])->name('login');
        Route::post('login', [AuthController::class, 'login'])->name('login.store');
    });

    Route::middleware('auth')->group(function () {
        Route::post('logout', [AuthController::class, 'logout'])->name('logout');
        Route::get('/', DashboardController::class)->name('dashboard');

        Route::resource('catalogos', CatalogoController::class)->except(['show', 'destroy']);
        Route::post('catalogos/{catalogo}/publish', [CatalogoController::class, 'publish'])->name('catalogos.publish');
        Route::post('catalogos/{catalogo}/archive', [CatalogoController::class, 'archive'])->name('catalogos.archive');

        Route::get('empresa', [EmpresaConfigController::class, 'edit'])->name('empresa.edit');
        Route::put('empresa', [EmpresaConfigController::class, 'update'])->name('empresa.update');

        Route::get('dispositivos', [DispositivoController::class, 'index'])->name('dispositivos.index');
        Route::get('descargas', [DispositivoController::class, 'descargas'])->name('descargas.index');
    });
});
