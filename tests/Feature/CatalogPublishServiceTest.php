<?php

namespace Tests\Feature;

use App\Models\Catalogo;
use App\Services\Catalogo\CatalogPublishService;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Storage;
use Illuminate\Validation\ValidationException;
use Tests\TestCase;

class CatalogPublishServiceTest extends TestCase
{
    use RefreshDatabase;

    public function test_publishing_catalog_archives_previous_active_catalog(): void
    {
        Storage::fake('public');
        Storage::disk('public')->put('catalogos/v1.pdf', 'v1');
        Storage::disk('public')->put('catalogos/v2.pdf', 'v2');

        $previous = Catalogo::query()->create([
            'titulo' => 'Anterior',
            'version_codigo' => '2026.05.01',
            'version_numero' => 20260501,
            'archivo_path' => 'catalogos/v1.pdf',
            'peso_bytes' => 2,
            'checksum' => hash('sha256', 'v1'),
            'estado' => Catalogo::ESTADO_PUBLICADO,
            'activo' => true,
            'publicado_en' => now()->subDay(),
        ]);

        $next = Catalogo::query()->create([
            'titulo' => 'Nuevo',
            'version_codigo' => '2026.06.01',
            'version_numero' => 20260601,
            'archivo_path' => 'catalogos/v2.pdf',
            'peso_bytes' => 2,
            'checksum' => hash('sha256', 'v2'),
            'estado' => Catalogo::ESTADO_ARCHIVADO,
            'activo' => false,
        ]);

        app(CatalogPublishService::class)->publish($next);

        $this->assertFalse($previous->refresh()->activo);
        $this->assertSame(Catalogo::ESTADO_ARCHIVADO, $previous->estado);
        $this->assertTrue($next->refresh()->activo);
        $this->assertSame(Catalogo::ESTADO_PUBLICADO, $next->estado);
    }

    public function test_catalog_without_valid_file_cannot_be_published(): void
    {
        $catalogo = Catalogo::query()->create([
            'titulo' => 'Incompleto',
            'version_codigo' => '2026.06.01',
            'version_numero' => 20260601,
            'archivo_path' => 'catalogos/missing.pdf',
            'peso_bytes' => 0,
            'checksum' => null,
            'estado' => Catalogo::ESTADO_BORRADOR,
            'activo' => false,
        ]);

        $this->expectException(ValidationException::class);

        app(CatalogPublishService::class)->publish($catalogo);
    }
}
