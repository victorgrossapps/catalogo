<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('dispositivos', function (Blueprint $table) {
            $table->id();
            $table->string('device_uuid', 100)->unique();
            $table->string('nombre_dispositivo', 150)->nullable();
            $table->string('alias', 150)->nullable();
            $table->string('version_app', 30)->nullable();
            $table->string('ultimo_catalogo_version', 50)->nullable();
            $table->timestamp('ultima_sincronizacion')->nullable();
            $table->boolean('activo')->default(true)->index();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('dispositivos');
    }
};
