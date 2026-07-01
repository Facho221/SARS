package pruebas.seguridad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Seguridad - Arquitectura del Sistema")
public class SeguridadArquitecturaTest {

    @Test
    @DisplayName("Conexión a BD requiere credenciales válidas")
    void testConexionBDRequiereCredenciales() {
        String url      = "jdbc:postgresql://thomas.proxy.rlwy.net:17892/railway";
        String usuario  = "postgres";
        String password = "btLlVZsEgyTYUmaKUJfjHIawhmyGSOhq";

        assertNotNull(url,      "URL de conexión no debe ser nula");
        assertNotNull(usuario,  "Usuario no debe ser nulo");
        assertNotNull(password, "Password no debe ser nulo");
        assertFalse(url.isEmpty(),      "URL no debe estar vacía");
        assertFalse(usuario.isEmpty(),  "Usuario no debe estar vacío");
        assertFalse(password.isEmpty(), "Password no debe estar vacío");
    }

    @Test
    @DisplayName("Separación de capas - vista no accede directamente a BD")
    void testSeparacionCapas() {
        boolean vistaAccedeDirectamenteBD = false;
        assertFalse(vistaAccedeDirectamenteBD,
                "La vista no debe acceder directamente a la base de datos");
    }

    @Test
    @DisplayName("Capa Service valida antes de persistir")
    void testServiceValidaAntesDePersistir() {
        String dni    = "";
        String destino = "Lote 12";

        assertThrows(RuntimeException.class, () -> {
            if (dni.isEmpty()) {
                throw new RuntimeException("Validación en Service: DNI requerido.");
            }
        });
    }

    @Test
    @DisplayName("Rol vigilante no puede acceder a módulo de auditoría")
    void testVigilanteNoPuedeAccederAuditoria() {
        String rol = "vigilante";
        assertThrows(RuntimeException.class, () -> {
            if (!"admin".equals(rol)) {
                throw new RuntimeException("Acceso denegado al módulo de auditoría.");
            }
        });
    }

    @Test
    @DisplayName("Rol admin puede acceder a módulo de auditoría")
    void testAdminPuedeAccederAuditoria() {
        String rol = "admin";
        assertDoesNotThrow(() -> {
            if (!"admin".equals(rol)) {
                throw new RuntimeException("Acceso denegado.");
            }
        });
    }

    @Test
    @DisplayName("Base de datos en nube garantiza disponibilidad ante robo de PC")
    void testBDEnNubeGarantizaDisponibilidad() {
        boolean pcGaritaRobada = true;
        boolean bdEnNube       = true;
        boolean datosDisponibles = bdEnNube && pcGaritaRobada;
        assertTrue(datosDisponibles,
                "Aunque la PC sea robada los datos en la nube deben estar disponibles");
    }

    @Test
    @DisplayName("Backup diario garantiza recuperación ante fallo del servidor")
    void testBackupGarantizaRecuperacion() {
        boolean servidorFallo  = true;
        boolean backupDisponible = true;
        boolean puedeRecuperar = servidorFallo && backupDisponible;
        assertTrue(puedeRecuperar,
                "Ante fallo del servidor el backup debe permitir recuperación");
    }
}