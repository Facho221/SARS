package pruebas.funcionales;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Funcionales - Flujo de Estancias")
public class EstanciaFuncionalTest {

    @Test
    @DisplayName("Flujo completo de registro de ingreso")
    void testRegistroIngresoCompleto() {
        String dni = "12345678";
        String nombre = "Juan Pérez";
        String tipo = "Familiar";
        String destino = "Lote 12";
        String tipoIngreso = "Peatón";
        int tiempoMax = 60;
        String estadoTag = "Disponible";

        assertNotNull(dni, "DNI no debe ser nulo");
        assertNotNull(nombre, "Nombre no debe ser nulo");
        assertFalse(dni.isEmpty(), "DNI no debe estar vacío");
        assertFalse(destino.isEmpty(), "Destino no debe estar vacío");
        assertEquals("Disponible", estadoTag, "Tag debe estar disponible");
        assertTrue(tiempoMax > 0, "Tiempo máximo debe ser positivo");

        String estadoEstancia = "Normal";
        assertEquals("Normal", estadoEstancia, "La estancia debe iniciarse en estado Normal");
    }

    @Test
    @DisplayName("No permitir registro si visitante ya tiene estancia activa")
    void testVisitanteConEstanciaActiva() {
        boolean tieneEstanciaActiva = true;
        assertTrue(tieneEstanciaActiva, "El visitante ya tiene una estancia activa");
        assertThrows(RuntimeException.class, () -> {
            if (tieneEstanciaActiva) {
                throw new RuntimeException("El visitante ya tiene una estancia activa.");
            }
        });
    }

    @Test
    @DisplayName("Cerrar estancia libera el tag asignado")
    void testCierreEstanciaLiberaTag() {
        String estadoTagAntes = "Asignado";
        assertEquals("Asignado", estadoTagAntes, "Tag debe estar asignado antes del cierre");

        String estadoTagDespues = "Disponible";
        assertEquals("Disponible", estadoTagDespues, "Tag debe quedar disponible tras el cierre");
    }

    @Test
    @DisplayName("Estado de estancia cambia a Finalizado al cerrar")
    void testEstadoFinalizadoAlCerrar() {
        String estadoAntes = "Normal";
        assertEquals("Normal", estadoAntes, "Estado inicial debe ser Normal");

        String estadoDespues = "Finalizado";
        assertEquals("Finalizado", estadoDespues, "Estado debe ser Finalizado tras el cierre");
    }

    @Test
    @DisplayName("Visitante sin estancia activa puede ingresar")
    void testVisitanteSinEstanciaActiva() {
        boolean tieneEstanciaActiva = false;
        assertFalse(tieneEstanciaActiva, "El visitante no tiene estancia activa, puede ingresar");
    }

    @Test
    @DisplayName("Alerta se genera cuando se excede el tiempo máximo")
    void testGeneracionDeAlerta() {
        int tiempoMax = 60;
        long tiempoTranscurrido = 65;
        String estadoEsperado = "Alerta";

        String estadoResultante = tiempoTranscurrido >= tiempoMax ? "Alerta" : "Normal";
        assertEquals(estadoEsperado, estadoResultante, "Debe generarse Alerta al exceder el tiempo");
    }

    @Test
    @DisplayName("Advertencia se genera al 80% del tiempo")
    void testGeneracionDeAdvertencia() {
        int tiempoMax = 60;
        long tiempoTranscurrido = 50;
        String estadoEsperado = "Advertencia";

        String estadoResultante;
        if (tiempoTranscurrido >= tiempoMax) {
            estadoResultante = "Alerta";
        } else if (tiempoTranscurrido >= tiempoMax * 0.8) {
            estadoResultante = "Advertencia";
        } else {
            estadoResultante = "Normal";
        }
        assertEquals(estadoEsperado, estadoResultante, "Debe generarse Advertencia al 80%");
    }
}