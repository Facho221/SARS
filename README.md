# SARS - Smart Access Residential System

Sistema de escritorio para el control de acceso residencial mediante tarjetas RFID, desarrollado en Java/JavaFX con conexión a base de datos PostgreSQL.

## Requisitos previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

- **Java 17 o superior** (recomendado Java 21)
    - Verifica tu versión abriendo una terminal (cmd) y ejecutando: `java -version`
    - Si no lo tienes, descárgalo de: https://www.oracle.com/java/technologies/downloads/ o https://adoptium.net/
- **PostgreSQL** instalado y corriendo localmente
    - Descarga: https://www.postgresql.org/download/

> El proyecto NO requiere JavaFX instalado por separado: ya viene incluido dentro del `.jar` ejecutable.

## 1. Configurar la base de datos

1. Abre pgAdmin o tu cliente de PostgreSQL preferido.
2. Crea una base de datos llamada `sars_db;` (respeta el nombre exacto, incluyendo el punto y coma).
3. Restaura el respaldo incluido en la carpeta `scripts_bd/backup_sars.sql`:

   Desde la terminal (ajusta usuario si es distinto a `postgres`):
   ```bash
   psql -U postgres -d "sars_db;" -f scripts_bd/backup_sars.sql
   ```

   O desde pgAdmin: clic derecho sobre `sars_db;` → Restore... → selecciona `backup_sars.sql`.

Este script incluye la estructura de tablas y datos de prueba (UIDs de tarjetas RFID y registros de ejemplo).

## 2. Configurar credenciales de conexión

Si tu usuario/contraseña de PostgreSQL son distintos a `postgres` / `12345`, edita el archivo `src/main/resources/config.properties` (si compilas desde el código fuente) con tus propios datos:

```
db.url=jdbc:postgresql://localhost:5432/sars_db;
db.user=TU_USUARIO
db.password=TU_PASSWORD
```

## 3. Ejecutar la aplicación

En la carpeta `bin/` encontrarás:
- `smart-access-residential-1.0.jar` — aplicación ejecutable (incluye todas las dependencias)
- `ejecutar.bat` — script de arranque para Windows

Simplemente haz doble clic en **`ejecutar.bat`**.

Si prefieres ejecutarlo manualmente desde la terminal:
```bash
java -jar smart-access-residential-1.0.jar
```

## Compilar desde el código fuente (opcional)

Si quieres compilar el proyecto tú mismo en lugar de usar el `.jar` ya generado:

```bash
mvn clean package
```

El jar ejecutable con todas las dependencias se generará en `target/smart-access-residential-1.0.jar`.

## Notas sobre el hardware RFID

El sistema está diseñado para integrarse con un lector RFID RC522 conectado vía Arduino. Si no cuentas con el hardware físico, la aplicación permite operar y probar las funciones principales (login, gestión de tarjetas, consultas) sin necesidad del dispositivo conectado.

## Integrantes del equipo

1. Luis Fabian Guerra Cornejo
2. Hugo Gustavo Flores Huertas
3. Carlos Eduardo Castillo Maldonado
4. Dulce María Navarro Gonzales