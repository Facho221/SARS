# Pruebas del Sistema SARS

## Tipos de Pruebas

### Unitarias (8 pruebas)
Validan componentes individuales del sistema de forma aislada.

### Funcionales (7 pruebas)
Validan flujos completos del sistema de inicio a fin.

### Rendimiento (4 pruebas)
Validan que el sistema responda dentro de tiempos aceptables.

### Seguridad (29 pruebas)
Validan que el sistema sea resistente ante amenazas de seguridad.

#### Seguridad BD (8 pruebas)
- SQL Injection bloqueado por PreparedStatement
- Validación de formato DNI
- Contraseña no vacía y longitud mínima
- Integridad referencial
- Control de acceso por roles

#### Seguridad Código (8 pruebas)
- Acceso sin login bloqueado
- Credenciales incorrectas rechazadas
- Campos vacíos rechazados
- Sanitización de caracteres especiales
- Validación de rangos de tiempo
- Mensajes de error genéricos sin info interna

#### Seguridad Arduino (6 pruebas)
- Código RFID vacío y nulo rechazado
- Sanitización de código RFID
- Longitud de código válida
- Tag no registrado rechazado
- Tag asignado no reasignable

#### Seguridad Arquitectura (7 pruebas)
- Conexión BD requiere credenciales
- Separación de capas verificada
- Service valida antes de persistir
- Control de acceso por rol en auditoría
- BD en nube garantiza disponibilidad
- Backup garantiza recuperación

## Total: 48 pruebas

## Cómo ejecutar
1. Clic derecho sobre carpeta pruebas en IntelliJ
2. Run All Tests
3. Verificar que todas muestran ✅ verde