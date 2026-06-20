# Pruebas del Sistema SARS

## Tipos de Pruebas

### Unitarias
Validan componentes individuales del sistema de forma aislada.
- Validación de DNI vacío
- Validación de tiempo máximo
- Validación de estado de tag RFID
- Cálculo de tiempo excedido y advertencia

### Funcionales
Validan flujos completos del sistema de inicio a fin.
- Registro completo de ingreso de visitante
- Bloqueo de visitante con estancia activa
- Cierre de estancia y liberación de tag
- Generación automática de alertas y advertencias

### Rendimiento
Validan que el sistema responda dentro de tiempos aceptables.
- Verificación de 100 alertas en menos de 1 segundo
- Validación de formulario x1000 en menos de 1 segundo
- Cálculo de estados de 500 estancias en menos de 1 segundo
- Procesamiento de lecturas RFID x1000 en menos de 1 segundo

## Cómo ejecutar las pruebas

En IntelliJ IDEA:
1. Clic derecho sobre el archivo de prueba
2. Seleccionar "Run"
3. Ver resultados en la consola

## Resultados esperados

Todas las pruebas deben pasar con ✅ verde.
Las pruebas de rendimiento imprimen el tiempo real en consola.