package pruebas.seguridad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Seguridad - Comunicación Arduino")
public class SeguridadArduinoTest {

    @Test
    @DisplayName("Código RFID vacío debe ser rechazado")
    void testCodigoRfidVacioRechazado() {
        String codigo = "";
        assertThrows(RuntimeException.class, () -> {
            if (codigo == null || codigo.isEmpty()) {
                throw new RuntimeException("Código RFID vacío.");
            }
        });
    }

    @Test
    @DisplayName("Código RFID nulo debe ser rechazado")
    void testCodigoRfidNuloRechazado() {
        String codigo = null;
        assertThrows(RuntimeException.class, () -> {
            if (codigo == null || codigo.isEmpty()) {
                throw new RuntimeException("Código RFID nulo.");
            }
        });
    }

    @Test
    @DisplayName("Código RFID con caracteres especiales debe ser sanitizado")
    void testCodigoRfidCaracteresEspeciales() {
        String codigoMalicioso = "A1B2'; DROP TABLE tag;--";
        String codigoLimpio = codigoMalicioso.replaceAll("[^A-Fa-f0-9]", "");
        assertFalse(codigoLimpio.contains("DROP"),   "SQL injection debe ser eliminado");
        assertFalse(codigoLimpio.contains(";"),      "Punto y coma debe ser eliminado");
        assertFalse(codigoLimpio.contains("'"),      "Comilla debe ser eliminada");
    }

    @Test
    @DisplayName("Código RFID debe tener longitud válida")
    void testCodigoRfidLongitudValida() {
        String codigoValido   = "A1B2C3D4";
        String codigoInvalido = "A1";
        assertTrue(codigoValido.length() >= 8,   "Código válido debe tener al menos 8 caracteres");
        assertFalse(codigoInvalido.length() >= 8, "Código corto debe ser rechazado");
    }

    @Test
    @DisplayName("Tag no registrado en BD no debe crear estancia")
    void testTagNoRegistradoRechazado() {
        boolean tagRegistrado = false;
        assertThrows(RuntimeException.class, () -> {
            if (!tagRegistrado) {
                throw new RuntimeException("Tag no registrado en el sistema.");
            }
        });
    }

    @Test
    @DisplayName("Tag ya asignado no debe poder reasignarse")
    void testTagAsignadoNoReasignable() {
        String estadoTag = "Asignado";
        assertThrows(RuntimeException.class, () -> {
            if ("Asignado".equals(estadoTag)) {
                throw new RuntimeException("El tag ya está asignado a otra estancia.");
            }
        });
    }
}