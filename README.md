# Catálogo comercial offline

Solución compuesta por backend Laravel, MariaDB/cPanel y app Android nativa para presentar un catálogo comercial offline en tablets.

## Backend Laravel

El backend Laravel se encuentra en la raíz del proyecto:

`c:\laragon\www\app-catalogo`

Componentes implementados:

- Panel administrativo Blade en `/admin`.
- Login de administrador.
- CRUD de catálogos con PDF, portada, checksum, peso y estados.
- Publicación controlada con un solo catálogo activo.
- Rollback operativo mediante republicación de catálogos archivados válidos.
- Tablas principales: `catalogos`, `dispositivos`, `catalogo_descargas` y `empresa_config`.
- Seeders para usuario administrador inicial y configuración de empresa.

### Instalación local

```bash
composer install
cp .env.example .env
php artisan key:generate
php artisan migrate:fresh --seed
php artisan storage:link
php artisan serve
```

`php artisan migrate:fresh --seed` debe usarse solo en desarrollo porque borra y recrea tablas.

### Producción

En producción no se debe ejecutar `migrate:fresh`.

```bash
php artisan migrate --force
php artisan db:seed --class=AdminUserSeeder --force
php artisan storage:link
```

Usuario inicial:

- Correo: `admin@example.com`
- Password: `admin12345`

Cambiar estas credenciales antes de usar el sistema en producción.

### Panel administrativo

- `GET /admin`
- Login administrador
- Gestión de catálogos
- Publicación controlada
- Configuración institucional
- Dispositivos y descargas

## API v1

Todas las rutas de consumo de la app requieren header:

```http
X-API-Key: change-me-in-production
```

Endpoints principales:

- `GET /api/v1/health`
- `GET /api/v1/catalogo/actual`
- `GET /api/v1/empresa`
- `POST /api/v1/catalogo/descarga`
- `POST /api/v1/dispositivos/sync`

## Android

El proyecto fuente está en `android-app/`.

Componentes implementados:

- Kotlin + Jetpack Compose.
- Retrofit/OkHttp con API key.
- DataStore para metadata local y UUID de dispositivo.
- Descarga con progreso.
- Validación de tamaño y checksum.
- Archivo temporal, promoción a catálogo activo y conservación del catálogo anterior.
- Registro de descarga.
- Visor PDF básico con `PdfRenderer`.
- Pantallas base: inicio, quiénes somos, catálogo, actualización y configuración.

Requisitos para compilar:

- Android Studio.
- JDK compatible con Android Gradle Plugin.
- SDK Android 35.

La URL de API y API key se configuran en `android-app/app/build.gradle.kts` mediante `BuildConfig`.

### Estado de validación Android

La app Android ya compila localmente con el JDK embebido de Android Studio y Gradle Wrapper. El APK debug se genera en:

`android-app/app/build/outputs/apk/debug/app-debug.apk`

Comando usado:

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT="$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug --no-daemon
```

Resultado local reportado: `BUILD SUCCESSFUL`.

Antes de considerarla validada de extremo a extremo todavía se debe:

1. Abrir `android-app/` en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en emulador o tablet real.
4. Probar descarga con PDF real.
5. Activar modo avión y validar catálogo offline.
6. Validar UI en orientación horizontal.

## Validación backend

```bash
php artisan migrate:fresh --seed
php artisan route:list --except-vendor
php artisan test
```

Resultado local reportado: `7 tests passed`.

## Estado real del avance

El backend quedó implementado y validado con pruebas iniciales. La app Android quedó implementada y compila en modo debug, pero aún requiere prueba funcional en emulador o tablet Android real antes de considerarse validada de extremo a extremo.

## Git

El repositorio local está inicializado y vinculado al remoto:

`https://github.com/victorgrossapps/catalogo.git`

El primer commit base ya fue enviado a `origin/main`. Los cambios posteriores de compilación Android deben confirmarse en un nuevo commit.

`.gitignore` ya contempla:

- `.env`
- `vendor/`
- `node_modules/`
- `public/storage`
- `storage/*.key`
- `android-app/local.properties`
- `android-app/.gradle`
- builds Android
- archivos `*.jks` y `*.keystore`

## Documentos operativos

- `docs/deployment/CPANEL_CHECKLIST.md`
- `docs/deployment/BACKUP_RESTORE.md`
- `docs/android/PDF_TABLET_TEST_PROTOCOL.md`
- `docs/android/SIGNING_RELEASE.md`
