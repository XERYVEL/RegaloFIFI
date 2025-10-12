package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    gamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

    boolean checkDrawTime = false;

    public KeyHandler(gamePanel gp) {
        this.gp =gp;
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
            if(code == KeyEvent.VK_W) {
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 1;
                }
            }
            if(code == KeyEvent.VK_S) {
                gp.ui.commandNum++;
                if(gp.ui.commandNum > 1) {
                    gp.ui.commandNum = 0;
                }
            }
            if(code == KeyEvent.VK_ENTER) {
                if(gp.ui.commandNum == 0) {
                    gp.reloj.reiniciarTiempo();
                    gp.gameState = gp.playState;

                }
                if(gp.ui.commandNum == 1) {
                    System.exit(0);
                }
            }
            else if(gp.gameState == gp.playState){
                if(code == KeyEvent.VK_W) {
                    gp.ui.commandNum--;
                    if(gp.ui.commandNum < 0) {
                        gp.ui.commandNum = 3;
                    }
                }
                if(code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    if(gp.ui.commandNum > 3) {
                        gp.ui.commandNum = 0;
                    }
                }
                if(code == KeyEvent.VK_ENTER) {
                    if(gp.ui.commandNum == 0) {
                        System.out.println("Haz algunas cosas especificas de...");
                        gp.gameState = gp.playState;
                        gp.reloj.reiniciarTiempo();
                        gp.ui.titleScreenState = 1;
                        gp.playMusic(0);
                    }
                    if(gp.ui.commandNum == 1) {
                        System.out.println("Haz algunas cosas especificas de...");
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
                    }
                    if(gp.ui.commandNum == 2) {
                        System.out.println("Haz algunas cosas especificas de...");
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
                    }
                    if(gp.ui.commandNum == 3) {
                        gp.gameState = gp.titleState;

                    }
                }
            }
        }


    }
    public void playState(int code) {

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
        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.characterState;
        }
        if(code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if(code == KeyEvent.VK_T) {
            if (checkDrawTime == false) {
                checkDrawTime = true;
            }
            else if(checkDrawTime == true){
                checkDrawTime = false;
            }
        }
    }
    public void characterState(int code) {
        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.playState;
        }
        if(gp.ui.slotRow != 0){
            if(code == KeyEvent.VK_W) {
                gp.ui.slotRow--;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotCol != 0){
            if(code == KeyEvent.VK_A) {
                gp.ui.slotCol--;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotRow != 3){
            if(code == KeyEvent.VK_S) {
                gp.ui.slotRow++;
                gp.playSE(5);
            }
        }
        if(gp.ui.slotCol != 4){
            if(code == KeyEvent.VK_D) {
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

        if(code == KeyEvent.VK_W){
            gp.ui.commandNum--;
            if(gp.ui.commandNum < 0) {
                gp.ui.commandNum = 1;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_S){
            gp.ui.commandNum++;
            if(gp.ui.commandNum > 1) {
                gp.ui.commandNum = 0;
            }
            gp.playSE(5);
        }
        if(code == KeyEvent.VK_ENTER){
            if(gp.ui.commandNum == 0) {
                gp.reloj.reiniciarTiempo();
                gp.gameState = gp.playState;
                System.exit(0);
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

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
    }
}
