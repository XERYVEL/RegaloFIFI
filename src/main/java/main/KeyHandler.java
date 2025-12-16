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

        if(gp.gameState == gp.playState){
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
                    // Iniciar juego
                    if(gp.reloj != null) {
                        gp.reloj.reiniciarTiempo();
                    }
                    gp.gameState = gp.playState;
                    gp.playMusic(7);
                }
                if(gp.ui.commandNum == 1) {
                    // Salir
                    System.exit(0);
                }
            }
        }
    }

    public void playState(int code) {
        // Player 1 - WASD
        if(code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = true; // No se usa pero lo mantenemos
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
            arrowDownPressed = true; // No se usa pero lo mantenemos
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
            gp.gameState = gp.pauseState;
        }
        if(code == KeyEvent.VK_T) {
            checkDrawTime = !checkDrawTime;
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
                gp.gameState = gp.playState;
                gp.setupGame();
            } else {
                System.exit(0);
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