package main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sonido {

    Clip clip[] = new Clip[30];
    URL sonidoURL[] = new URL[30];
    int musicIndex = -1;

    public Sonido() {
        sonidoURL[0] = getClass().getResource("/sonido/pixelJump.wav");
        sonidoURL[1] = getClass().getResource("/sonido/Pierde.wav");
        sonidoURL[2] = getClass().getResource("/sonido/Victoria.wav");
        sonidoURL[3] = getClass().getResource("/sonido/Derrota.wav");
        sonidoURL[4] = getClass().getResource("/sonido/aiaiai.wav");
        sonidoURL[5] = getClass().getResource("/sonido/cursor.wav");
        sonidoURL[6] = getClass().getResource("/sonido/MG.wav");
        sonidoURL[7] = getClass().getResource("/sonido/fondo.wav");
        sonidoURL[8] = getClass().getResource("/sonido/Final.wav");

        // Cargar todos los sonidos
        for (int i = 0; i < sonidoURL.length; i++) {
            if (sonidoURL[i] != null) {
                setFile(i);
            }
        }
    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sonidoURL[i]);
            clip[i] = AudioSystem.getClip();
            clip[i].open(ais);
            System.out.println("✅ Sonido " + i + " cargado correctamente");
        } catch (Exception e) {
            System.err.println("❌ ERROR: El archivo " + i + " (" + sonidoURL[i] + ") falló la carga.");
            System.err.println("   Revisa la ruta o el formato (debe ser WAV/PCM).");
            e.printStackTrace();
        }
    }

    public void playMusic(int i) {
        // Si ya está sonando esta música, no hacer nada
        if (musicIndex == i && clip[i] != null && clip[i].isRunning()) {
            return;
        }

        // Detener música anterior
        if (musicIndex != -1 && clip[musicIndex] != null && clip[musicIndex].isRunning()) {
            clip[musicIndex].stop();
        }

        // Reproducir nueva música
        if (clip[i] != null) {
            musicIndex = i;
            clip[i].setFramePosition(0);
            clip[i].loop(Clip.LOOP_CONTINUOUSLY);
            clip[i].start();
            System.out.println("🎵 Música " + i + " iniciada");
        } else {
            System.err.println("❌ No se pudo reproducir música " + i + " (clip es null)");
        }
    }

    public void stopMusic() {
        if (musicIndex != -1 && clip[musicIndex] != null) {
            if (clip[musicIndex].isRunning()) {
                clip[musicIndex].stop();
                System.out.println("🔇 Música " + musicIndex + " detenida");
            }
        }
        musicIndex = -1;
    }

    public void playSE(int i) {
        if (clip[i] != null) {
            // Detener el sonido si ya está reproduciéndose
            if (clip[i].isRunning()) {
                clip[i].stop();
            }
            clip[i].setFramePosition(0);
            clip[i].start();
            System.out.println("🔊 Efecto de sonido " + i + " reproducido");
        } else {
            System.err.println("❌ No se pudo reproducir efecto " + i + " (clip es null)");
            System.err.println("   Verifica que el archivo existe en: /sonido/");
        }
    }
}