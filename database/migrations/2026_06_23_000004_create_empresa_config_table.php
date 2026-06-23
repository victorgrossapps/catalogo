<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('empresa_config', function (Blueprint $table) {
            $table->id();
            $table->string('nombre_comercial', 200);
            $table->string('logo_path', 500)->nullable();
            $table->text('texto_quienes_somos')->nullable();
            $table->string('telefono', 60)->nullable();
            $table->string('whatsapp', 60)->nullable();
            $table->string('correo', 150)->nullable();
            $table->string('direccion', 300)->nullable();
            $table->json('redes_sociales_json')->nullable();
            $table->string('imagen_portada_path', 500)->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('empresa_config');
    }
};
