<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('catalogo_descargas', function (Blueprint $table) {
            $table->id();
            $table->foreignId('catalogo_id')->constrained('catalogos')->cascadeOnDelete();
            $table->foreignId('dispositivo_id')->nullable()->constrained('dispositivos')->nullOnDelete();
            $table->string('version_app', 30)->nullable();
            $table->string('version_catalogo', 50);
            $table->string('estado', 30)->default('exitoso')->index();
            $table->string('ip', 45)->nullable();
            $table->string('user_agent', 500)->nullable();
            $table->string('mensaje_error', 500)->nullable();
            $table->timestamp('descargado_en')->nullable();
            $table->timestamps();

            $table->index(['catalogo_id', 'descargado_en']);
            $table->index(['dispositivo_id', 'descargado_en']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('catalogo_descargas');
    }
};
