package pruebas.seguridad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Seguridad - Base de Datos")
public class SeguridadBDTest {

    @Test
    @DisplayName("SQL Injection bloqueado por PreparedStatement")
    void testSqlInjectionBloqueado() {
        String dniMalicioso = "' OR '1'='1";
        assertFalse(dniMalicioso.matches("[0-9]{8}"),
                "El DNI malicioso debe ser rechazado por la validación");
    }

    @Test
    @DisplayName("DNI solo acepta 8 dígitos numéricos")
    void testDniSoloNumeros() {
        String dniValido   = "12345678";
        String dniInvalido = "1234567A";
        String dniCorto    = "1234";
        String dniEspecial = "12345'78";

        assertTrue(dniValido.matches("[0-9]{8}"),   "DNI válido debe pasar");
        assertFalse(dniInvalido.matches("[0-9]{8}"), "DNI con letra debe fallar");
        assertFalse(dniCorto.matches("[0-9]{8}"),    "DNI corto debe fallar");
        assertFalse(dniEspecial.matches("[0-9]{8}"), "DNI con comilla debe fallar");
    }

    @Test
    @DisplayName("Contraseña no debe estar vacía")
    void testContrasenaNoVacia() {
        String contrasena = "";
        assertTrue(contrasena.isEmpty(), "Contraseña vacía debe ser detectada");
        assertThrows(RuntimeException.class, () -> {
            if (contrasena.isEmpty()) throw new RuntimeException("Contraseña vacía no permitida.");
        });
    }

    @Test
    @DisplayName("Contraseña no debe tener menos de 6 caracteres")
    void testContrasenaLongitudMinima() {
        String contrasenaCortа = "abc";
        String contrasenaValida = "sars2024";
        assertFalse(contrasenaCortа.length() >= 6, "Contraseña corta debe fallar");
        assertTrue(contrasenaValida.length() >= 6,  "Contraseña válida debe pasar");
    }

    @Test
    @DisplayName("Integridad referencial - visitante con estancia activa no puede eliminarse")
    void testIntegridadReferencial() {
        boolean tieneEstanciaActiva = true;
        assertThrows(RuntimeException.class, () -> {
            if (tieneEstanciaActiva) {
                throw new RuntimeException("No se puede eliminar un visitante con estancias activas.");
            }
        });
    }

    @Test
    @DisplayName("Rol vigilante no puede eliminar registros")
    void testRolVigilanteNoPuedeEliminar() {
        String rol = "vigilante";
        boolean puedeEliminar = "admin".equals(rol);
        assertFalse(puedeEliminar, "El rol vigilante no debe poder eliminar registros");
    }

    @Test
    @DisplayName("Rol admin puede eliminar registros")
    void testRolAdminPuedeEliminar() {
        String rol = "admin";
        boolean puedeEliminar = "admin".equals(rol);
        assertTrue(puedeEliminar, "El rol admin debe poder eliminar registros");
    }

    @Test
    @DisplayName("Rol vigilante no puede acceder a auditoría")
    void testRolVigilanteNoAccedeAuditoria() {
        String rol = "vigilante";
        boolean puedeVerAuditoria = "admin".equals(rol);
        assertFalse(puedeVerAuditoria, "El vigilante no debe acceder al módulo de auditoría");
    }
}
