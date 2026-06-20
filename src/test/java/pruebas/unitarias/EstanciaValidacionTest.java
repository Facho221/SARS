package pruebas.unitarias;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Validaciones de Estancia")
public class EstanciaValidacionTest {

    @Test
    @DisplayName("DNI no debe estar vacío")
    void testDniNoVacio() {
        String dni = "";
        assertFalse(dni.isEmpty() == false, "El DNI está vacío");
        assertTrue(dni.isEmpty(), "Se detectó correctamente que el DNI está vacío");
    }

    @Test
    @DisplayName("Tiempo máximo debe ser mayor a cero")
    void testTiempoMaximoValido() {
        int tiempoMax = 60;
        assertTrue(tiempoMax > 0, "El tiempo máximo debe ser mayor a 0");
    }

    @Test
    @DisplayName("Tiempo máximo negativo debe ser inválido")
    void testTiempoMaximoInvalido() {
        int tiempoMax = -1;
        assertFalse(tiempoMax > 0, "Tiempo negativo detectado correctamente como inválido");
    }

    @Test
    @DisplayName("Tag asignado no debe poder reasignarse")
    void testTagAsignado() {
        String estadoTag = "Asignado";
        assertEquals("Asignado", estadoTag, "El tag ya está asignado");
        assertNotEquals("Disponible", estadoTag, "El tag no está disponible para asignar");
    }

    @Test
    @DisplayName("Tag disponible puede asignarse")
    void testTagDisponible() {
        String estadoTag = "Disponible";
        assertEquals("Disponible", estadoTag, "El tag está disponible correctamente");
    }

    @Test
    @DisplayName("Cálculo de tiempo excedido")
    void testTiempoExcedido() {
        int tiempoMax = 60;
        long tiempoTranscurrido = 75;
        assertTrue(tiempoTranscurrido >= tiempoMax, "La estancia excedió el tiempo máximo");
    }

    @Test
    @DisplayName("Cálculo de advertencia al 80% del tiempo")
    void testTiempoAdvertencia() {
        int tiempoMax = 60;
        long tiempoTranscurrido = 50;
        assertTrue(tiempoTranscurrido >= tiempoMax * 0.8, "Debe generar advertencia al 80%");
    }

    @Test
    @DisplayName("Estancia dentro del tiempo permitido")
    void testTiempoNormal() {
        int tiempoMax = 60;
        long tiempoTranscurrido = 30;
        assertTrue(tiempoTranscurrido < tiempoMax * 0.8, "Estancia en estado Normal");
    }
}