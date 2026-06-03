package sars.arduino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Lector de puerto serial para Arduino + RFID.
 * Dependencia: jSerialComm (agregar al pom.xml o classpath)
 *
 * Arduino envía: el código del tag cada vez que se escanea.
 * Ejemplo de lo que llega por serial: "A1B2C3D4\n"
 */
public class ArduinoReader {

    private SerialPort    puerto;
    private Thread        hilo;
    private boolean       activo = false;
    private Consumer<String> onTagLeido; // callback con el código RFID

    /**
     * @param nombrePuerto  Ej: "COM3" en Windows, "/dev/ttyUSB0" en Linux
     * @param onTagLeido    Función que se ejecuta cuando llega un tag
     */
    public ArduinoReader(String nombrePuerto, Consumer<String> onTagLeido) {
        this.onTagLeido = onTagLeido;
        this.puerto = SerialPort.getCommPort(nombrePuerto);
        this.puerto.setBaudRate(9600);
    }

    public void iniciar() {
        if (!puerto.openPort()) {
            System.err.println("[ARDUINO] No se pudo abrir el puerto: " + puerto.getSystemPortName());
            return;
        }
        activo = true;
        hilo = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(puerto.getInputStream()))) {
                String linea;
                while (activo && (linea = br.readLine()) != null) {
                    String codigo = linea.trim();
                    if (!codigo.isEmpty()) {
                        System.out.println("[ARDUINO] Tag leído: " + codigo);
                        if (onTagLeido != null) {
                            // Ejecutar en JavaFX thread
                            javafx.application.Platform.runLater(() -> onTagLeido.accept(codigo));
                        }
                    }
                }
            } catch (Exception e) {
                if (activo) System.err.println("[ARDUINO] Error: " + e.getMessage());
            }
        }, "arduino-reader");
        hilo.setDaemon(true);
        hilo.start();
        System.out.println("[ARDUINO] Escuchando en " + puerto.getSystemPortName());
    }

    public void detener() {
        activo = false;
        if (puerto.isOpen()) puerto.closePort();
    }

    /** Lista los puertos disponibles en el sistema */
    public static String[] listarPuertos() {
        SerialPort[] puertos = SerialPort.getCommPorts();
        String[] nombres = new String[puertos.length];
        for (int i = 0; i < puertos.length; i++) nombres[i] = puertos[i].getSystemPortName();
        return nombres;
    }
}
