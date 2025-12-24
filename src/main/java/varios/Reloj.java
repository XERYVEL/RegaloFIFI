package varios;

import main.UI;
import main.gamePanel;

public class Reloj {

    private final gamePanel gp;
    private double playTime;
    private long startTime = System.nanoTime();
    private UI condicion;
    private boolean derrotaSonoReproducido = false; // Para evitar reproducir múltiples veces

    public int min;
    public int seg;
    public int ms;

    public Reloj(gamePanel gp, UI condicion){
        this.gp = gp;
        this.condicion = condicion;
        this.startTime = System.nanoTime();
    }


    public void actualizarTiempo(){
        if (!condicion.gameFinished && !condicion.gameOver){
            long now = System.nanoTime();
            long elapsedNanos = now - startTime;
            playTime += elapsedNanos / 1_000_000.0;
            startTime = now;

            min = (int) (playTime / 1000) / 60;
            seg = (int) (playTime / 1000) % 60;
            ms = (int) (playTime % 1000);
        }
    }

    public void agregarTiempo(int segundosExtra) {
        playTime += segundosExtra * 1000.0;
    }

    public void derrota() {
        if(min >= 1 && !derrotaSonoReproducido){
            System.out.println("⏰ ¡Tiempo agotado! Game Over");

            // ⭐ DETENER MÚSICA DEL NIVEL
            gp.stopMusic();

            // ⭐ REPRODUCIR SONIDO DE PIERDE (índice 1)
            gp.playSE(1);

            // Cambiar estado a game over
            gp.gameState = gp.gameOverState;
            condicion.gameOver = true;

            derrotaSonoReproducido = true;

            System.out.println("🔇 Música detenida");
            System.out.println("🔊 Sonido 'Pierde' reproducido");
        }
    }

    public void reiniciarTiempo() {
        playTime = 0;
        min = 0;
        seg = 0;
        ms = 0;
        startTime = System.nanoTime();
        derrotaSonoReproducido = false; // ⭐ Resetear flag al reiniciar
    }

    public int getMinutos() {
        return min;
    }

    public int getSegundos() {
        return seg;
    }

    public int getMilisegundos() {
        return ms;
    }
}