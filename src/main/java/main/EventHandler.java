package main;

import entity.*;
import varios.Direccion;

import java.awt.*;

public class EventHandler {

    gamePanel gp;

    EventRect eventRect [][][];

    private boolean llamo = false;
    private boolean piensa = false;
    private boolean vioGPS = false;

    int previousEventX, previousEventY;

    boolean canTouchEvent = true;

    public EventHandler (gamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        int map = 0;
        int col = 0;
        int row = 0;

        while(map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow){

            eventRect [map][col][row] = new EventRect();

            eventRect [map][col][row].x = 0;
            eventRect [map][col][row].y = 0;
            eventRect [map][col][row].width = gp.tileSize/2;
            eventRect [map][col][row].height = gp.tileSize/2;

            eventRect [map][col][row].eventRectDefaultX = eventRect [map][col][row].x;
            eventRect [map][col][row].eventRectDefaultY = eventRect [map][col][row].y;

            col++;
            if(col == gp.maxWorldCol) {
                col = 0;
                row++;

                if(row == gp.maxWorldRow) {
                    row = 0;
                    map++;
                }
            }
        }
    }

    public void checkEvent() {
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);

        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        if (canTouchEvent) {
            // MAPA 0 es 20x12 (columnas 0-19, filas 0-11)

            // Evento de interacción en el centro del mapa
            if (hit(0, 10, 5, Direccion.Arriba)) {
                interactuarEntorno(0, 10, 5, gp.dialogueState);
            }

            // Evento al llegar al borde derecho (cerca de ganar)
            if (hit(0, 19, 10, Direccion.Derecha)) {
                if(!piensa){
                    mensajeLugar(gp.dialogueState);
                    piensa = true;
                }
            }

            // Evento al llegar a la plataforma superior (objetivo - fila 1)
            // Detectar si cualquier jugador llega a la cima
            if (hit(0, 8, 1, Direccion.Any) ||
                    hit(0, 9, 1, Direccion.Any) ||
                    hit(0, 10, 1, Direccion.Any) ||
                    hit(0, 11, 1, Direccion.Any)) {
                if(!llamo) {
                    victoria(gp.dialogueState);
                    llamo = true;
                }
            }
        }
    }


    public boolean hit(int map, int col, int row, Direccion reqDirection){
        boolean hit = false;

        // Validar que las coordenadas estén dentro del rango
        if(col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return false;
        }

        if (map == gp.currentMap) {
            // Verificar ambos jugadores
            Player[] players = {gp.player, gp.player2};

            for(Player player : players) {
                if(player == null) continue;

                player.solidArea.x = player.worldX + player.solidArea.x;
                player.solidArea.y = player.worldY + player.solidArea.y;
                eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
                eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;

                if(player.solidArea.intersects(eventRect[map][col][row]) && eventRect[map][col][row].eventDone == false) {

                    if(player.direction == reqDirection || reqDirection == Direccion.Any){
                        hit = true;

                        previousEventX = player.worldX;
                        previousEventY = player.worldY;
                        break; // Un jugador es suficiente
                    }
                }

                player.solidArea.x = player.solidAreaDefaultX;
                player.solidArea.y = player.solidAreaDefaultY;
            }

            eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
            eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
        }
        return hit;
    }

    public void mensajeLugar(int gameState) {
        gp.gameState = gameState;
        gp.ui.currentDialogue = "Casi llegas a la cima!\nSigue subiendo!";
        canTouchEvent = false;
    }

    public void interactuarEntorno(int map, int col, int row, int gameState) {
        if(gp.keyH.enterPressed == true) {
            gp.gameState = gameState;
            gp.ui.currentDialogue = "Una plataforma misteriosa...";
            eventRect[map][col][row].eventDone = true;
        }
    }

    public void victoria(int gameState) {
        gp.playSE(2); // Sonido de victoria
        gp.ui.gameFinished = true;
        canTouchEvent = false;

        // Detener el reloj
        gp.reloj.actualizarTiempo();
    }

    public void teleport(int map, int col, int row) {
        if(col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return;
        }

        gp.currentMap = map;
        gp.player.worldX = gp.tileSize * col;
        gp.player.worldY = gp.tileSize * row;
        previousEventX = gp.player.worldX;
        previousEventY = gp.player.worldY;
        canTouchEvent = false;
    }
}