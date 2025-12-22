package main;

import entity.*;
import varios.Direccion;

import java.awt.*;

public class EventHandler {

    gamePanel gp;

    EventRect eventRect[][][];

    private boolean llamo = false;
    private boolean piensa = false;
    private boolean vioGPS = false;

    int previousEventX, previousEventY;

    boolean canTouchEvent = true;

    // Variables para las zonas de meta
    public Rectangle player1GoalZone;
    public Rectangle player2GoalZone;
    public boolean player1InGoal = false;
    public boolean player2InGoal = false;

    public EventHandler(gamePanel gp) {
        this.gp = gp;

        // CORREGIDO: Aumentar el tamaño del array para 16 niveles
        eventRect = new EventRect[16][gp.maxWorldCol][gp.maxWorldRow];

        int map = 0;
        int col = 0;
        int row = 0;

        while(map < 16 && col < gp.maxWorldCol && row < gp.maxWorldRow) {

            eventRect[map][col][row] = new EventRect();

            eventRect[map][col][row].x = 0;
            eventRect[map][col][row].y = 0;
            eventRect[map][col][row].width = gp.tileSize / 2;
            eventRect[map][col][row].height = gp.tileSize / 2;

            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

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

    /**
     * Configura las zonas de meta dinámicamente basándose en los tiles 6 y 7 del mapa actual
     * Este método DEBE ser llamado cada vez que se carga un nuevo nivel
     *
     * IMPORTANTE:
     * - Tile 6 = Puerta AZUL = Player 1 (Mujer)
     * - Tile 7 = Puerta ROJA = Player 2 (Hombre)
     */
    public void setupGoalZonesForCurrentMap() {
        int currentMap = gp.currentMap;

        // Buscar posiciones de los tiles 6 y 7
        int tile6Col = -1, tile6Row = -1;
        int tile7Col = -1, tile7Row = -1;

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int tileNum = gp.tileM.mapTileNum[currentMap][col][row];

                if (tileNum == 6) {
                    tile6Col = col;
                    tile6Row = row;
                }
                else if (tileNum == 7) {
                    tile7Col = col;
                    tile7Row = row;
                }
            }
        }

        // CORREGIDO: Tile 6 (puertaH/AZUL) es para Player1 (Mujer)
        if (tile6Col != -1 && tile6Row != -1) {
            player1GoalZone = new Rectangle(
                    tile6Col * gp.tileSize,
                    tile6Row * gp.tileSize,
                    gp.tileSize,
                    gp.tileSize
            );
            System.out.println("✅ Zona P1/AZUL (tile 6) configurada en: col=" + tile6Col + ", row=" + tile6Row);
        } else {
            player1GoalZone = null;
            System.err.println("⚠️ No se encontró tile 6 (zona P1/AZUL) en el nivel " + (currentMap + 1));
        }

        // CORREGIDO: Tile 7 (puertaM/ROJA) es para Player2 (Hombre)
        if (tile7Col != -1 && tile7Row != -1) {
            player2GoalZone = new Rectangle(
                    tile7Col * gp.tileSize,
                    tile7Row * gp.tileSize,
                    gp.tileSize,
                    gp.tileSize
            );
            System.out.println("✅ Zona P2/ROJO (tile 7) configurada en: col=" + tile7Col + ", row=" + tile7Row);
        } else {
            player2GoalZone = null;
            System.err.println("⚠️ No se encontró tile 7 (zona P2/ROJO) en el nivel " + (currentMap + 1));
        }

        // Resetear estados
        player1InGoal = false;
        player2InGoal = false;

        System.out.println("🎯 Zonas de meta configuradas para nivel " + (currentMap + 1));
    }

    public void checkEvent() {
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);

        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        // IMPORTANTE: Verificar zonas de meta en cada frame
        checkGoalZones();

        if (canTouchEvent) {
            if (hit(0, 10, 5, Direccion.Arriba)) {
                interactuarEntorno(0, 10, 5, gp.dialogueState);
            }

            if (hit(0, 19, 10, Direccion.Derecha)) {
                if(!piensa) {
                    mensajeLugar(gp.dialogueState);
                    piensa = true;
                }
            }
        }
    }

    private void checkGoalZones() {
        if(gp.player == null || gp.player2 == null) {
            return;
        }

        if(player1GoalZone == null || player2GoalZone == null) {
            return;
        }

        // Crear rectángulos para Player1 (usando worldX/worldY + solidArea)
        Rectangle p1Rect = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        // Crear rectángulos para Player2
        Rectangle p2Rect = new Rectangle(
                gp.player2.worldX + gp.player2.solidArea.x,
                gp.player2.worldY + gp.player2.solidArea.y,
                gp.player2.solidArea.width,
                gp.player2.solidArea.height
        );

        // Verificar si Player 1 está en su zona (tile 6/AZUL)
        boolean wasP1InGoal = player1InGoal;
        player1InGoal = player1GoalZone.intersects(p1Rect);

        if(player1InGoal != wasP1InGoal) {
            System.out.println("🔵 Player 1 " + (player1InGoal ? "ENTRÓ" : "SALIÓ") + " de su zona AZUL");
        }

        // Verificar si Player 2 está en su zona (tile 7/ROJA)
        boolean wasP2InGoal = player2InGoal;
        player2InGoal = player2GoalZone.intersects(p2Rect);

        if(player2InGoal != wasP2InGoal) {
            System.out.println("🔴 Player 2 " + (player2InGoal ? "ENTRÓ" : "SALIÓ") + " de su zona ROJA");
        }

        // Si AMBOS jugadores están en sus zonas, ¡VICTORIA!
        if(player1InGoal && player2InGoal && !gp.ui.gameFinished) {
            System.out.println("🎉🎉🎉 ¡¡¡VICTORIA ACTIVADA!!! 🎉🎉🎉");
            victoria(gp.dialogueState);
        }
    }

    public boolean hit(int map, int col, int row, Direccion reqDirection) {
        boolean hit = false;

        if(col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return false;
        }

        if (map == gp.currentMap) {
            Entity[] players = {gp.player, gp.player2};

            for(Entity player : players) {
                if(player == null) continue;

                player.solidArea.x = player.worldX + player.solidArea.x;
                player.solidArea.y = player.worldY + player.solidArea.y;
                eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
                eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;

                if(player.solidArea.intersects(eventRect[map][col][row]) && eventRect[map][col][row].eventDone == false) {

                    if(player.direction == reqDirection || reqDirection == Direccion.Any) {
                        hit = true;

                        previousEventX = player.worldX;
                        previousEventY = player.worldY;
                        break;
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
        gp.playSE(2);
        gp.ui.gameFinished = true;
        canTouchEvent = false;

        gp.reloj.actualizarTiempo();

        System.out.println("✅ ¡VICTORIA! Ambos jugadores llegaron a sus zonas de meta");
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