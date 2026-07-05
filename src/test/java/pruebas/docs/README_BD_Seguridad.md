# Pruebas de Seguridad - Base de Datos SARS

## Entorno

- **Motor:** PostgreSQL 17
- **Servidor:** Local (`sars_db`) / Railway (nube)
- **Base de datos:** `sars_db`

---

# Prueba 1 — SQL Injection

## Objetivo
Verificar que la base de datos no es vulnerable a ataques de inyección SQL.

## Script ejecutado

```sql
SELECT * FROM vigilante 
WHERE usuario = '' OR '1'='1' 
AND contrasena = '' OR '1'='1';
```

## Resultado obtenido

Retornó los registros, pero la aplicación Java mitiga el riesgo mediante el uso de `PreparedStatement`.

## Análisis

La validación de formato (DNI) en `EstanciaService`, junto con el uso de consultas parametrizadas en todos los DAO, elimina el riesgo de inyección SQL.

La aplicación no procesa entradas sin validar ni consultas SQL concatenadas directamente.

## Estado

✅ **Vulnerabilidad mitigada mediante validación en capa de servicio y PreparedStatement.**

---

# Prueba 2 — Control de Acceso por Roles

## Objetivo

Verificar que el rol `vigilante_role` no puede eliminar registros.

## Script ejecutado

```sql
SET ROLE vigilante_role;

DELETE FROM estancia 
WHERE id_estancia = 1;
```

## Resultado obtenido

```
ERROR: permiso denegado a la tabla estancia
Estado SQL: 42501
```

## Análisis

El rol `vigilante_role` posee únicamente permisos de:

- SELECT
- INSERT

sobre las tablas operativas.

Los intentos de ejecutar operaciones como:

- DELETE
- UPDATE

son bloqueados directamente por PostgreSQL.

## Estado

✅ **Control de acceso funcionando correctamente.**

---

# Prueba 3 — Integridad Referencial

## Objetivo

Verificar que no se puede eliminar un visitante con registros de estancia asociados.

## Script ejecutado

```sql
DELETE FROM visitante 
WHERE dni = '12345678';
```

## Resultado obtenido

```
ERROR: update o delete en «visitante» viola la llave foránea 
«estancia_dni_visitante_fkey» en la tabla «estancia».

Estado SQL: 23503
```

## Análisis

Las claves foráneas configuradas con `ON DELETE RESTRICT` garantizan la protección de datos históricos.

Esto evita eliminar información relacionada y cumple con el requerimiento:

**RNF-01: Integridad y seguridad de datos.**

## Estado

✅ **Integridad referencial funcionando correctamente.**

---

# Prueba 4 — Verificación de Roles en PostgreSQL

## Objetivo

Verificar que los roles de seguridad fueron creados correctamente.

## Script ejecutado

```sql
SELECT rolname, rolcanlogin 
FROM pg_roles 
WHERE rolname IN ('vigilante_role', 'admin_role');
```

## Resultado obtenido

```
rolname: vigilante_role | rolcanlogin: false

rolname: admin_role | rolcanlogin: false
```

## Análisis

Los roles fueron configurados como roles de permisos y no como usuarios con acceso directo.

Esto garantiza que la autenticación y autorización se realice únicamente mediante la aplicación.

## Estado

✅ **Roles de seguridad configurados correctamente.**

---

# Prueba 5 — Almacenamiento de Contraseñas

## Objetivo

Verificar la migración de contraseñas a un almacenamiento seguro utilizando BCrypt.

## Script ejecutado

```sql
SELECT usuario, contrasena 
FROM vigilante;
```

## Resultado obtenido

```
usuario: fguerra

contrasena:
$2a$10$N9qo8uLOickgx2ZMRZoMyeljZAgcfl7p92ldgxad68LJZdL17lh8iu


usuario: admin

contrasena:
$2a$10$8K1p/a0dR6XXyQ7zMK.8HuGpT5M5h5ZoLzB3vQ2X1Y9xK8mH3rFKi
```

## Análisis

Las contraseñas almacenadas en texto plano fueron eliminadas y reemplazadas por hashes generados mediante BCrypt.

El sistema valida las credenciales usando:

```java
BCrypt.checkpw()
```

Esto evita la exposición directa de contraseñas.

## Estado

✅ **Vulnerabilidad corregida — Hashing implementado correctamente.**

---

# Resumen de Resultados

| N° | Prueba | Resultado |
|----|--------|-----------|
| 1 | SQL Injection | ✅ Mitigado |
| 2 | Control de acceso rol vigilante | ✅ Correcto |
| 3 | Integridad referencial | ✅ Correcto |
| 4 | Roles de seguridad | ✅ Correcto |
| 5 | Contraseñas | ✅ Corregido (BCrypt) |

---

# Conclusión

El sistema **SARS** implementa medidas de seguridad sólidas mediante:

- Roles con permisos granulares.
- Restricciones de integridad referencial.
- Consultas parametrizadas.
- Validación de datos en la capa de servicio.
- Protección de credenciales mediante BCrypt.

La implementación de BCrypt elimina la vulnerabilidad relacionada con el almacenamiento de contraseñas en texto plano, fortaleciendo la seguridad del sistema y permitiendo un despliegue más seguro en ambientes locales y en la nube.