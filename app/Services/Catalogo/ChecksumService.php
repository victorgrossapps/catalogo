<?php

namespace App\Services\Catalogo;

use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\Storage;
use RuntimeException;

class ChecksumService
{
    public function forUploadedFile(UploadedFile $file): string
    {
        $checksum = hash_file('sha256', $file->getRealPath());

        if ($checksum === false) {
            throw new RuntimeException('No fue posible calcular el checksum del archivo subido.');
        }

        return $checksum;
    }

    public function forPublicStoragePath(string $path): string
    {
        $absolutePath = Storage::disk('public')->path($path);
        $checksum = is_file($absolutePath) ? hash_file('sha256', $absolutePath) : false;

        if ($checksum === false) {
            throw new RuntimeException('No fue posible calcular el checksum del archivo almacenado.');
        }

        return $checksum;
    }
}
