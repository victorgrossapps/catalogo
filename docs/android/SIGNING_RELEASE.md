# Firma y distribución Android

## Reglas de keystore

- Crear un keystore release desde la primera versión productiva.
- Mantener el keystore fuera del repositorio Git.
- Guardar copia de respaldo en ubicación segura.
- Documentar alias, responsable de custodia y procedimiento de firma.
- Diferenciar APK debug, staging y release.

## Variables recomendadas

Estas variables deben configurarse fuera del repositorio:

- `CATALOGO_STORE_FILE`
- `CATALOGO_STORE_PASSWORD`
- `CATALOGO_KEY_ALIAS`
- `CATALOGO_KEY_PASSWORD`

## Riesgo principal

La pérdida del keystore puede impedir actualizaciones directas sobre la misma instalación de la app y obligar a reinstalaciones manuales en tablets.
