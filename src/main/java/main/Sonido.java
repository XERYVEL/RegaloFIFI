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
        sonidoURL[0] = getClass().getResource("/sonido/Pan de ajo.wav");
        sonidoURL[1] = getClass().getResource("/sonido/Pierde.wav");
        sonidoURL[2] = getClass().getResource("/sonido/Victoria.wav");
        sonidoURL[3] = getClass().getResource("/sonido/Derrota.wav");
        sonidoURL[5] = getClass().getResource("/sonido/cursor.wav");
        sonidoURL[4] = getClass().getResource("/sonido/aiaiai.wav");
        sonidoURL[6] = getClass().getResource("/sonido/MG.wav");

        sonidoURL[7] = getClass().getResource("/sonido/fondo1.wav");
        sonidoURL[8] = getClass().getResource("/sonido/fondo2.wav");
        sonidoURL[9] = getClass().getResource("/sonido/fondo3.wav");

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

        } catch (Exception e) {
            System.err.println("ERROR: El archivo " + i + " (" + sonidoURL[i] + ") falló la carga. Revisa la ruta o el formato (debe ser WAV/PCM).");
        }
    }

    public void playMusic(int i) {
        if (musicIndex == i && clip[i] != null && clip[i].isRunning()) {
            return;
        }

        if (musicIndex != -1 && clip[musicIndex] != null && clip[musicIndex].isRunning()) {
            clip[musicIndex].stop();
        }

        if (clip[i] != null) {
            musicIndex = i;
            clip[i].setFramePosition(0);
            clip[i].loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        if (musicIndex != -1 && clip[musicIndex] != null && clip[musicIndex].isRunning()) {
            clip[musicIndex].stop();
        }
        musicIndex = -1;
    }

    public void playSE(int i) {

        for (int j = 7; j <= 9; j++) {
            if (clip[j] != null && clip[j].isRunning()) {
                clip[j].stop();
            }
        }

        musicIndex = -1;

        if (clip[i] != null) {
            clip[i].stop();
            clip[i].setFramePosition(0);
            clip[i].start();
        }
    }
}