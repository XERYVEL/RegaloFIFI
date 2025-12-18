package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    gamePanel gp;

    // Player 1 (WASD)
    public boolean upPressed = false;
    public boolean downPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;

    // Player 2 (Flechas)
    public boolean arrowUpPressed = false;
    public boolean arrowDownPressed = false;
    public boolean arrowLeftPressed = false;
    public boolean arrowRightPressed = false;

    public boolean enterPressed = false;
    public boolean checkDrawTime = false;

    public KeyHandler(gamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(gp.gameState == gp.titleState) {
            titleState(code);
        }
        else if(gp.gameState == gp.levelSelectState) {
            levelSelectState(code);
        }
        else if(gp.gameState == gp.playState){
            playState(code);
        }
        else if(gp.gameState == gp.dialogueState) {
            dialogueState(code);
        }
        else if(gp.gameState == gp.gameOverState) {
            gameOverState(code);
        }
        else if(gp.gameState == gp.characterState) {
            characterState(code);
        }
    }

    public void titleState(int code) {
        if(gp.ui.titleScreenState == 0){
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 1;
                }
                gp.playSE(5);
            }
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if(gp.ui.commandNum > 1) {
                    gp.ui.commandNum = 0;
                }
                gp.playSE(5);
            }
            if(code == KeyEvent.VK_ENTER) {
                if(gp.ui.commandNum == 0) {
                    // Ir a selección de niveles
                    gp.gameState = gp.levelSelectState;
                    gp.playSE(5);
                }
                else if(gp.ui.commandNum == 1) {
                    // Salir
                    System.exit(0);
                }
            }
        }
    }

    public void levelSelectState(int code) {
        // Navegación por la grilla 4x4
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.selectedLevel -= 4;
            if(gp.selectedLevel < 0) {
                gp.selectedLevel += 16;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.selectedLevel += 4;
            if(gp.selectedLevel >= 16) {
                gp.selectedLevel -= 16;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            gp.selectedLevel--;
            if(gp.selectedLevel < 0) {
                gp.selectedLevel = 15;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            gp.selectedLevel++;
            if(gp.selectedLevel >= 16) {
                gp.selectedLevel = 0;
            }
            gp.playSE(5);
        }

        if(code == KeyEvent.VK_ENTER) {
            // Verificar si el nivel está desbloqueado
            boolean unlocked = (gp.selectedLevel == 0) || gp.levelCompleted[gp.selectedLevel - 1];

            if(!unlocked) {
                // Nivel bloqueado - reproducir sonido de error
                gp.playSE(1); // Sonido de error/pierde
                System.out.println("¡Nivel " + (gp.selectedLevel + 1) + " bloqueado! Completa el nivel anterior primero.");
                return;
            }

            // Iniciar el nivel seleccionado
            if(gp.reloj != null) {
                gp.reloj.reiniciarTiempo();
            }

            if(gp.player != null) {
                gp.player.setDefaultValues();
            }
            if(gp.player2 != null) {
                gp.player2.setDefaultValues();
            }

            gp.ui.gameFinished = false;
            gp.ui.gameOver = false;
            gp.ui.messageOn = false;

            gp.currentMap = 0; // Por ahora todos usan el mismo mapa
            gp.gameState = gp.playState;
            gp.playMusic(7);
            gp.playSE(5);
        }

        if(code == KeyEvent.VK_ESCAPE) {
            // Volver al menú principal
            gp.gameState = gp.titleState;
            gp.playSE(5);
        }
    }

    public void playState(int code) {
        // Player 1 - WASD
        if(code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_D) {
            rightPressed = true;
        }

        // Player 2 - Flechas
        if(code == KeyEvent.VK_UP) {
            arrowUpPressed = true;
        }
        if(code == KeyEvent.VK_DOWN) {
            arrowDownPressed = true;
        }
        if(code == KeyEvent.VK_LEFT) {
            arrowLeftPressed = true;
        }
        if(code == KeyEvent.VK_RIGHT) {
            arrowRightPressed = true;
        }

        // Controles generales
        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.characterState;
        }
        if(code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if(code == KeyEvent.VK_ESCAPE) {
            // Volver a selección de niveles
            gp.gameState = gp.levelSelectState;
        }
        if(code == KeyEvent.VK_T) {
            checkDrawTime = !checkDrawTime;
        }

        // TECLA G - GANAR NIVEL INSTANTÁNEAMENTE (para pruebas)
        if(code == KeyEvent.VK_G) {
            System.out.println("¡Ganaste el nivel " + (gp.selectedLevel + 1) + " con G!");
            gp.playSE(2); // Sonido de victoria
            gp.ui.gameFinished = true;
        }
    }

    public void characterState(int code) {
        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.playState;
        }
        if(gp.ui.slotRow != 0){
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.slotRow--;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotCol != 0){
            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.ui.slotCol--;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotRow != 3){
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.slotRow++;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotCol != 4){
            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.ui.slotCol++;
                gp.playSE(5);
            }
        }
    }

    public void dialogueState(int code) {
        if(code == KeyEvent.VK_ENTER) {
            gp.gameState = gp.playState;
        }
    }

    public void gameOverState(int code){
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            gp.ui.commandNum--;
            if(gp.ui.commandNum < 0) {
                gp.ui.commandNum = 1;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            gp.ui.commandNum++;
            if(gp.ui.commandNum > 1) {
                gp.ui.commandNum = 0;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_ENTER){
            if(gp.ui.commandNum == 0) {
                // Reintentar
                gp.setupGame();
                gp.gameState = gp.playState;
            } else {
                // Volver a selección de niveles
                gp.gameState = gp.levelSelectState;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // Player 1 - WASD
        if(code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        // Player 2 - Flechas
        if(code == KeyEvent.VK_UP) {
            arrowUpPressed = false;
        }
        if(code == KeyEvent.VK_DOWN) {
            arrowDownPressed = false;
        }
        if(code == KeyEvent.VK_LEFT) {
            arrowLeftPressed = false;
        }
        if(code == KeyEvent.VK_RIGHT) {
            arrowRightPressed = false;
        }
    }
}