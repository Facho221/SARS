package sars.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class SonidoAlerta {

    public static void reproducir() {
        new Thread(() -> {
            try {
                tono(880, 250);
                Thread.sleep(80);
                tono(880, 250);
            } catch (Exception ignored) {}
        }).start();
    }

    private static void tono(int frecuenciaHz, int duracionMs) throws Exception {
        float sampleRate = 44100;
        AudioFormat formato = new AudioFormat(sampleRate, 8, 1, true, false);
        SourceDataLine linea = AudioSystem.getSourceDataLine(formato);
        linea.open(formato);
        linea.start();

        int numSamples = (int) (sampleRate * duracionMs / 1000);
        byte[] buffer = new byte[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double angulo = 2.0 * Math.PI * i * frecuenciaHz / sampleRate;
            buffer[i] = (byte) (Math.sin(angulo) * 100);
        }

        linea.write(buffer, 0, buffer.length);
        linea.drain();
        linea.close();
    }
}