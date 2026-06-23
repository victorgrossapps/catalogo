# Checklist cPanel para despliegue del catálogo

## Capacidades obligatorias

| Punto | Valor mínimo recomendado | Estado |
|---|---:|---|
| PHP | 8.3 o superior | Pendiente de validar en hosting |
| Composer | Disponible por SSH o flujo alterno documentado | Pendiente |
| SSH | Recomendado | Pendiente |
| MariaDB | Disponible | Pendiente |
| AutoSSL / HTTPS | Activo | Pendiente |
| Subdominio | Apuntando a `public/` de Laravel | Pendiente |

## Límites a confirmar

| Configuración | Valor recomendado MVP |
|---|---:|
| `upload_max_filesize` | 128M a 256M |
| `post_max_size` | Mayor o igual a `upload_max_filesize` |
| `memory_limit` | 256M o superior |
| `max_execution_time` | 120 segundos o superior |
| Espacio en disco | Suficiente para PDF actual + históricos + backup |
| Ancho de banda | Acorde al número de tablets y peso del PDF |

## Estructura recomendada

- Proyecto Laravel fuera de `public_html`.
- Document root del subdominio apuntando a `catalogo-backend/public`.
- Archivos públicos en `storage/app/public`.
- Enlace simbólico creado con `php artisan storage:link`.
- `.env` fuera del web root y con `APP_DEBUG=false`.

## Si el hosting no permite Composer o Artisan

1. Preparar dependencias localmente.
2. Subir `vendor/` junto con el proyecto.
3. Ejecutar migraciones desde un entorno con SSH si está disponible.
4. Si no hay SSH, documentar importación SQL manual y configuración de storage.

Este archivo deja documentada la validación técnica requerida. La confirmación final depende del acceso real al hosting cPanel del cliente.
