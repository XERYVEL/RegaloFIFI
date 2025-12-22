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

    // Variables para las zonas de meta
    public Rectangle player1GoalZone;
    public Rectangle player2GoalZone;
    public boolean player1InGoal = false;
    public boolean player2InGoal = false;

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

        // Inicializar las zonas de meta (en la fila 1, parte superior del mapa)
        // Player 1: columnas 8-9
        // Player 2: columnas 10-11
        setupGoalZones();
    }

    public void setupGoalZones() {
        // Zona de meta para Player 1 (color azul)
        // Posición: columna 8-9, fila 0 (arriba del mapa)
        int p1Col = 17;
        int p1Row = 10;
        player1GoalZone = new Rectangle(
                p1Col * gp.tileSize,
                p1Row * gp.tileSize,
                gp.tileSize * 1,  // 2 tiles de ancho
                gp.tileSize        // 1 tile de alto
        );

        // Zona de meta para Player 2 (color rojo)
        // Posición: columna 10-11, fila 0
        int p2Col = 2;
        int p2Row = 10;
        player2GoalZone = new Rectangle(
                p2Col * gp.tileSize,
                p2Row * gp.tileSize,
                gp.tileSize * 1,  // 2 tiles de ancho
                gp.tileSize        // 1 tile de alto
        );
    }

    public void checkEvent() {
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);

        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        // Verificar si los jugadores están en sus zonas de meta
        checkGoalZones();

        if (canTouchEvent) {
            // Evento de interacción en el centro del mapa
            if (hit(0, 10, 5, Direccion.Arriba)) {
                interactuarEntorno(0, 10, 5, gp.dialogueState);
            }

            // Evento al llegar al borde derecho
            if (hit(0, 19, 10, Direccion.Derecha)) {
                if(!piensa){
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

        // Verificar que las zonas estén inicializadas
        if(player1GoalZone == null || player2GoalZone == null) {
            setupGoalZones();
            return;
        }

        // Crear rectángulos para las áreas sólidas de los jugadores
        Rectangle p1Rect = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        Rectangle p2Rect = new Rectangle(
                gp.player2.worldX + gp.player2.solidArea.x,
                gp.player2.worldY + gp.player2.solidArea.y,
                gp.player2.solidArea.width,
                gp.player2.solidArea.height
        );

        // Verificar si Player 1 está en su zona
        boolean wasP1InGoal = player1InGoal;
        player1InGoal = player1GoalZone.intersects(p1Rect);

        // Debug cuando cambia el estado
        if(player1InGoal != wasP1InGoal) {
            System.out.println("Player 1 " + (player1InGoal ? "ENTRÓ" : "SALIÓ") + " de su zona");
        }

        // Verificar si Player 2 está en su zona
        boolean wasP2InGoal = player2InGoal;
        player2InGoal = player2GoalZone.intersects(p2Rect);

        // Debug cuando cambia el estado
        if(player2InGoal != wasP2InGoal) {
            System.out.println("Player 2 " + (player2InGoal ? "ENTRÓ" : "SALIÓ") + " de su zona");
        }

        // Si AMBOS jugadores están en sus zonas, ¡VICTORIA!
        if(player1InGoal && player2InGoal && !gp.ui.gameFinished) {
            System.out.println("¡¡¡VICTORIA ACTIVADA!!!");
            victoria(gp.dialogueState);
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
        gp.playSE(2); // Sonido de victoria
        gp.ui.gameFinished = true;
        canTouchEvent = false;

        // Detener el reloj
        gp.reloj.actualizarTiempo();

        System.out.println("¡VICTORIA! Ambos jugadores llegaron a sus zonas de meta");
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