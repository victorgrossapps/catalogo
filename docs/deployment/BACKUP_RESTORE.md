# Backup y restauración

## Respaldos mínimos

- Base de datos MariaDB.
- Directorio `storage/app/public/catalogos`.
- Directorio `storage/app/public/empresa`.
- Archivo `.env` protegido.
- Registro del keystore Android custodiado fuera del repositorio.

## Frecuencia recomendada

- Base de datos: semanal y antes de publicar un catálogo importante.
- Archivos PDF y portadas: después de cada publicación.
- `.env`: cada vez que cambien credenciales o dominio.

## Restauración mínima

1. Restaurar archivos del proyecto Laravel.
2. Restaurar `.env` productivo.
3. Restaurar base de datos MariaDB.
4. Restaurar `storage/app/public`.
5. Ejecutar `php artisan storage:link` si el enlace no existe.
6. Validar `GET /api/v1/health`.
7. Validar `GET /api/v1/catalogo/actual` con API key.
8. Confirmar que el checksum del catálogo activo coincide con el archivo restaurado.

## Nota operativa

Los catálogos históricos dependen de archivos físicos. Un backup solo de base de datos no es suficiente para restaurar correctamente el sistema.
