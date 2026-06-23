<?php

namespace Tests\Feature;

use App\Models\Catalogo;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Config;
use Illuminate\Support\Facades\Storage;
use Tests\TestCase;

class CatalogoApiTest extends TestCase
{
    use RefreshDatabase;

    public function test_catalogo_actual_requires_api_key(): void
    {
        Config::set('services.catalogo.api_key', 'test-key');

        $this->getJson('/api/v1/catalogo/actual')
            ->assertUnauthorized()
            ->assertJson(['ok' => false]);
    }

    public function test_catalogo_actual_returns_active_catalog_with_generated_urls(): void
    {
        Storage::fake('public');
        Storage::disk('public')->put('catalogos/catalogo.pdf', 'PDF test');
        Config::set('services.catalogo.api_key', 'test-key');

        Catalogo::query()->create([
            'titulo' => 'Catálogo General',
            'version_codigo' => '2026.06.01',
            'version_numero' => 20260601,
            'archivo_path' => 'catalogos/catalogo.pdf',
            'tipo' => 'pdf',
            'peso_bytes' => 8,
            'checksum' => hash('sha256', 'PDF test'),
            'estado' => Catalogo::ESTADO_PUBLICADO,
            'activo' => true,
            'publicado_en' => now(),
        ]);

        $this->withHeader('X-API-Key', 'test-key')
            ->getJson('/api/v1/catalogo/actual')
            ->assertOk()
            ->assertJsonPath('ok', true)
            ->assertJsonPath('catalogo.version_numero', 20260601)
            ->assertJsonStructure(['catalogo' => ['archivo_url', 'checksum', 'peso_bytes']]);
    }

    public function test_download_registration_creates_device_and_event(): void
    {
        Config::set('services.catalogo.api_key', 'test-key');

        $catalogo = Catalogo::query()->create([
            'titulo' => 'Catálogo General',
            'version_codigo' => '2026.06.01',
            'version_numero' => 20260601,
            'archivo_path' => 'catalogos/catalogo.pdf',
            'tipo' => 'pdf',
            'peso_bytes' => 8,
            'checksum' => str_repeat('a', 64),
            'estado' => Catalogo::ESTADO_PUBLICADO,
            'activo' => true,
            'publicado_en' => now(),
        ]);

        $this->withHeader('X-API-Key', 'test-key')
            ->postJson('/api/v1/catalogo/descarga', [
                'catalogo_id' => $catalogo->id,
                'device_uuid' => 'tablet-001',
                'nombre_dispositivo' => 'Tablet asesor',
                'version_app' => '1.0.0',
                'version_catalogo' => '2026.06.01',
                'estado' => 'exitoso',
            ])
            ->assertCreated()
            ->assertJson(['ok' => true]);

        $this->assertDatabaseHas('dispositivos', [
            'device_uuid' => 'tablet-001',
            'ultimo_catalogo_version' => '2026.06.01',
        ]);
        $this->assertDatabaseHas('catalogo_descargas', [
            'catalogo_id' => $catalogo->id,
            'estado' => 'exitoso',
        ]);
    }
}
