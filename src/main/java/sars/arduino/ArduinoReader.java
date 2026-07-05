package sars.arduino;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ArduinoReader {

    private SerialPort puerto;
    private Thread hilo;
    private boolean activo = false;
    private Consumer<String> onTagLeido;

    public ArduinoReader(String nombrePuerto, Consumer<String> onTagLeido) {
        this.onTagLeido = onTagLeido;
        this.puerto = SerialPort.getCommPort(nombrePuerto);
        this.puerto.setBaudRate(9600);
    }

    public void iniciar() {
        puerto.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 2000, 0);

        if (!puerto.openPort()) {
            System.err.println("[ARDUINO] ERROR: No se pudo abrir " + puerto.getSystemPortName());
            return;
        }

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        activo = true;
        hilo = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(puerto.getInputStream()));
                String linea;
                while (activo) {
                    if (br.ready() && (linea = br.readLine()) != null) {
                        String codigo = linea.trim().toUpperCase();
                        if (!codigo.isEmpty()
                                && !codigo.contains("LISTO")
                                && !codigo.contains("SARS")
                                && !codigo.contains("ESCANEANDO")) {

                            String codigoLimpio = codigo.replaceAll("[^A-Z0-9]", "");

                            if (!codigoLimpio.isEmpty()) {
                                final String c = codigoLimpio;
                                Platform.runLater(() -> onTagLeido.accept(c));
                            }
                        }
                    }
                    Thread.sleep(100);
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
        if (puerto != null && puerto.isOpen()) puerto.closePort();
    }

    public static String[] listarPuertos() {
        SerialPort[] puertos = SerialPort.getCommPorts();
        String[] nombres = new String[puertos.length];
        for (int i = 0; i < puertos.length; i++) {
            nombres[i] = puertos[i].getSystemPortName();
        }
        return nombres;
    }
}