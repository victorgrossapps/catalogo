<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('catalogos', function (Blueprint $table) {
            $table->id();
            $table->string('titulo', 200);
            $table->text('descripcion')->nullable();
            $table->string('version_codigo', 50)->unique();
            $table->unsignedInteger('version_numero')->unique();
            $table->string('archivo_path', 500);
            $table->string('portada_path', 500)->nullable();
            $table->string('tipo', 30)->default('pdf');
            $table->unsignedBigInteger('peso_bytes')->default(0);
            $table->char('checksum', 64)->nullable();
            $table->string('estado', 30)->default('BORRADOR')->index();
            $table->boolean('activo')->default(false)->index();
            $table->boolean('obligatorio')->default(false);
            $table->string('mensaje_actualizacion', 500)->nullable();
            $table->timestamp('publicado_en')->nullable();
            $table->foreignId('created_by')->nullable()->constrained('users')->nullOnDelete();
            $table->foreignId('updated_by')->nullable()->constrained('users')->nullOnDelete();
            $table->timestamps();

            $table->index(['activo', 'estado']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('catalogos');
    }
};
