<?php

use App\Http\Controllers\Api\V1\CatalogoController;
use App\Http\Controllers\Api\V1\CatalogoDescargaController;
use App\Http\Controllers\Api\V1\DispositivoController;
use App\Http\Controllers\Api\V1\EmpresaController;
use App\Http\Controllers\Api\V1\HealthController;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group(function () {
    Route::get('health', HealthController::class);

    Route::middleware(['api.key', 'throttle:60,1'])->group(function () {
        Route::get('catalogo/actual', [CatalogoController::class, 'actual']);
        Route::get('empresa', [EmpresaController::class, 'show']);
        Route::post('catalogo/descarga', [CatalogoDescargaController::class, 'store']);
        Route::post('dispositivos/sync', [DispositivoController::class, 'sync']);
    });
});
