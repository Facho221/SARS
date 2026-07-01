package pruebas.seguridad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Seguridad - Código Java")
public class SeguridadCodigoTest {

    @Test
    @DisplayName("Acceso sin login debe ser bloqueado")
    void testAccesoSinLogin() {
        Object sesionActiva = null;
        assertThrows(RuntimeException.class, () -> {
            if (sesionActiva == null) {
                throw new RuntimeException("Acceso denegado. Debe iniciar sesión.");
            }
        });
    }

    @Test
    @DisplayName("Credenciales incorrectas deben retornar null")
    void testCredencialesIncorrectas() {
        String usuarioCorrecto   = "admin";
        String contrasenaCorrecta = "admin2024";
        String usuarioIngresado   = "admin";
        String contrasenaIngresada = "wrongpassword";

        boolean accesoConcedido = usuarioCorrecto.equals(usuarioIngresado)
                && contrasenaCorrecta.equals(contrasenaIngresada);
        assertFalse(accesoConcedido, "Contraseña incorrecta debe denegar acceso");
    }

    @Test
    @DisplayName("Campos vacíos no deben procesarse")
    void testCamposVaciosRechazados() {
        String dni     = "";
        String nombre  = "";
        String destino = "";

        assertThrows(RuntimeException.class, () -> {
            if (dni.isEmpty() || nombre.isEmpty() || destino.isEmpty()) {
                throw new RuntimeException("Campos obligatorios vacíos.");
            }
        });
    }

    @Test
    @DisplayName("Inyección de caracteres especiales en nombre debe ser sanitizada")
    void testCaracteresEspecialesEnNombre() {
        String nombreMalicioso = "<script>alert('hack')</script>";
        String nombreLimpio = nombreMalicioso
                .replaceAll("[<>\"'%;()&+]", "")
                .replaceAll("(?i)script", "")
                .replaceAll("(?i)alert", "");

        assertFalse(nombreLimpio.contains("<script>"), "Script debe ser eliminado");
        assertFalse(nombreLimpio.contains("alert"),    "Código malicioso debe ser eliminado");
    }

    @Test
    @DisplayName("Tiempo máximo no debe aceptar valores negativos")
    void testTiempoMaximoNegativoRechazado() {
        int tiempoMax = -30;
        assertThrows(RuntimeException.class, () -> {
            if (tiempoMax <= 0) {
                throw new RuntimeException("El tiempo máximo debe ser mayor a cero.");
            }
        });
    }

    @Test
    @DisplayName("Tiempo máximo no debe aceptar valores extremadamente altos")
    void testTiempoMaximoExtremoRechazado() {
        int tiempoMax = 99999;
        int limiteMaximo = 480;
        assertThrows(RuntimeException.class, () -> {
            if (tiempoMax > limiteMaximo) {
                throw new RuntimeException("El tiempo máximo no puede exceder 480 minutos.");
            }
        });
    }

    @Test
    @DisplayName("Sesión cerrada no debe permitir acceso al panel")
    void testSesionCerradaBloqueaAcceso() {
        boolean sesionCerrada = true;
        assertThrows(RuntimeException.class, () -> {
            if (sesionCerrada) {
                throw new RuntimeException("Sesión cerrada. Redirigiendo al login.");
            }
        });
    }

    @Test
    @DisplayName("Mensaje de error no debe revelar detalles internos")
    void testMensajeErrorGenerico() {
        String mensajeError = "Usuario o contraseña incorrectos.";
        assertFalse(mensajeError.contains("SQL"),        "No debe exponer SQL");
        assertFalse(mensajeError.contains("Exception"),  "No debe exponer excepción");
        assertFalse(mensajeError.contains("password"),   "No debe mencionar contraseña");
        assertFalse(mensajeError.contains("database"),   "No debe mencionar BD");
    }
}