# Pruebas de Seguridad - Base de Datos SARS

## Entorno
- Motor: PostgreSQL 15
- Servidor: Railway (nube)
- Host: thomas.proxy.rlwy.net:17892
- Base de datos: railway

---

## Prueba 1 — SQL Injection

**Objetivo:** Verificar que la base de datos no es vulnerable a inyección SQL.

**Script ejecutado:**
```sql
SELECT * FROM vigilante 
WHERE usuario = '' OR '1'='1' 
AND contrasena = '' OR '1'='1';
```

**Resultado esperado:** Sin registros o error.

**Sustentación:** La aplicación Java usa `PreparedStatement` en todas las consultas, lo que parametriza las entradas del usuario antes de enviarlas a PostgreSQL, eliminando el riesgo de SQL Injection a nivel de código.

---

## Prueba 2 — Control de Acceso por Roles

**Objetivo:** Verificar que el rol vigilante no puede eliminar registros.

**Script ejecutado:**
```sql
SET ROLE vigilante_role;
DELETE FROM estancia WHERE id_estancia = 1;
```

**Resultado esperado:**

**Sustentación:** El rol `vigilante_role` tiene permisos únicamente de SELECT e INSERT sobre las tablas operativas, sin capacidad de modificar ni eliminar registros históricos, cumpliendo RNF-05.

---

## Prueba 3 — Integridad Referencial

**Objetivo:** Verificar que no se puede eliminar un visitante con estancias registradas.

**Script ejecutado:**
```sql
DELETE FROM visitante WHERE dni = '12345678';
```

**Resultado esperado:**

**Sustentación:** Las claves foráneas definidas en la tabla ESTANCIA con `ON DELETE RESTRICT` garantizan la integridad referencial de los datos, cumpliendo RNF-01.

---

## Prueba 4 — Verificación de Roles en PostgreSQL

**Objetivo:** Verificar que los roles de seguridad están correctamente creados.

**Script ejecutado:**
```sql
SELECT rolname, rolcanlogin 
FROM pg_roles 
WHERE rolname IN ('vigilante_role', 'admin_role');
```

**Resultado esperado:**
---

## Prueba 5 — Vulnerabilidad Identificada: Contraseñas en Texto Plano

**Objetivo:** Identificar vulnerabilidad de seguridad en almacenamiento de contraseñas.

**Script ejecutado:**
```sql
SELECT usuario, contrasena FROM vigilante;
```

**Resultado:** Las contraseñas se almacenan en texto plano, lo cual representa una vulnerabilidad de seguridad.

**Mejora propuesta:** Implementar encriptación con BCrypt antes del despliegue en producción:
```java
// Encriptar al registrar
String hashContrasena = BCrypt.hashpw(contrasena, BCrypt.gensalt());

// Verificar al hacer login
boolean valida = BCrypt.checkpw(contrasenaIngresada, hashAlmacenado);
```

---

## Resumen de Resultados

| Prueba | Descripción | Resultado |
|---|---|---|
| 1 | SQL Injection | ✅ Bloqueado por PreparedStatement |
| 2 | Control de acceso rol vigilante | ✅ Permission denied |
| 3 | Integridad referencial | ✅ Foreign key violation |
| 4 | Roles de seguridad | ✅ Roles creados correctamente |
| 5 | Contraseñas en texto plano | ⚠️ Vulnerabilidad identificada - mejora pendiente |

---

## Conclusión

El sistema SARS implementa medidas de seguridad a nivel de base de datos mediante el uso de roles diferenciados, restricciones de integridad referencial y consultas parametrizadas. La única vulnerabilidad identificada es el almacenamiento de contraseñas en texto plano, la cual está documentada como mejora prioritaria para el despliegue en producción.