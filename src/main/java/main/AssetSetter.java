package main;

import entity.*;
import object.*;

public class AssetSetter {
    gamePanel gp;

    public AssetSetter(gamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        // Limpiar objetos previos
        for(int map = 0; map < gp.maxMap; map++) {
            for(int i = 0; i < gp.obj[map].length; i++) {
                gp.obj[map][i] = null;
            }
        }

        // ⭐ Colocar gemas en todos los niveles
        colocarGemasEnNiveles();
    }

    private void colocarGemasEnNiveles() {
        // Nivel 1
        if(!gp.gemasAzulesPorNivel[0]) {
            gp.obj[0][0] = new OBJ_gemaAzul(gp);
            gp.obj[0][0].worldX = gp.tileSize * 5;
            gp.obj[0][0].worldY = gp.tileSize * 8;
            gp.obj[0][0].mapIndex = 0;
        }
        if(!gp.gemasRojasPorNivel[0]) {
            gp.obj[0][1] = new OBJ_gemaRoja(gp);
            gp.obj[0][1].worldX = gp.tileSize * 14;
            gp.obj[0][1].worldY = gp.tileSize * 8;
            gp.obj[0][1].mapIndex = 0;
        }

        // Nivel 2
        if(!gp.gemasAzulesPorNivel[1]) {
            gp.obj[1][0] = new OBJ_gemaAzul(gp);
            gp.obj[1][0].worldX = gp.tileSize * 7;
            gp.obj[1][0].worldY = gp.tileSize * 5;
            gp.obj[1][0].mapIndex = 1;
        }
        if(!gp.gemasRojasPorNivel[1]) {
            gp.obj[1][1] = new OBJ_gemaRoja(gp);
            gp.obj[1][1].worldX = gp.tileSize * 12;
            gp.obj[1][1].worldY = gp.tileSize * 5;
            gp.obj[1][1].mapIndex = 1;
        }

        // Nivel 3
        if(!gp.gemasAzulesPorNivel[2]) {
            gp.obj[2][0] = new OBJ_gemaAzul(gp);
            gp.obj[2][0].worldX = gp.tileSize * 5;
            gp.obj[2][0].worldY = gp.tileSize * 6;
            gp.obj[2][0].mapIndex = 2;
        }
        if(!gp.gemasRojasPorNivel[2]) {
            gp.obj[2][1] = new OBJ_gemaRoja(gp);
            gp.obj[2][1].worldX = gp.tileSize * 14;
            gp.obj[2][1].worldY = gp.tileSize * 6;
            gp.obj[2][1].mapIndex = 2;
        }

        // Nivel 4
        if(!gp.gemasAzulesPorNivel[3]) {
            gp.obj[3][0] = new OBJ_gemaAzul(gp);
            gp.obj[3][0].worldX = gp.tileSize * 3;
            gp.obj[3][0].worldY = gp.tileSize * 2;
            gp.obj[3][0].mapIndex = 3;
        }
        if(!gp.gemasRojasPorNivel[3]) {
            gp.obj[3][1] = new OBJ_gemaRoja(gp);
            gp.obj[3][1].worldX = gp.tileSize * 16;
            gp.obj[3][1].worldY = gp.tileSize * 2;
            gp.obj[3][1].mapIndex = 3;
        }

        // Nivel 5
        if(!gp.gemasAzulesPorNivel[4]) {
            gp.obj[4][0] = new OBJ_gemaAzul(gp);
            gp.obj[4][0].worldX = gp.tileSize * 10;
            gp.obj[4][0].worldY = gp.tileSize * 7;
            gp.obj[4][0].mapIndex = 4;
        }
        if(!gp.gemasRojasPorNivel[4]) {
            gp.obj[4][1] = new OBJ_gemaRoja(gp);
            gp.obj[4][1].worldX = gp.tileSize * 9;
            gp.obj[4][1].worldY = gp.tileSize * 7;
            gp.obj[4][1].mapIndex = 4;
        }

        // Nivel 6
        if(!gp.gemasAzulesPorNivel[5]) {
            gp.obj[5][0] = new OBJ_gemaAzul(gp);
            gp.obj[5][0].worldX = gp.tileSize * 10;
            gp.obj[5][0].worldY = gp.tileSize * 5;
            gp.obj[5][0].mapIndex = 5;
        }
        if(!gp.gemasRojasPorNivel[5]) {
            gp.obj[5][1] = new OBJ_gemaRoja(gp);
            gp.obj[5][1].worldX = gp.tileSize * 9;
            gp.obj[5][1].worldY = gp.tileSize * 5;
            gp.obj[5][1].mapIndex = 5;
        }

        // Nivel 7
        if(!gp.gemasAzulesPorNivel[6]) {
            gp.obj[6][0] = new OBJ_gemaAzul(gp);
            gp.obj[6][0].worldX = gp.tileSize * 3;
            gp.obj[6][0].worldY = gp.tileSize * 1;
            gp.obj[6][0].mapIndex = 6;
        }
        if(!gp.gemasRojasPorNivel[6]) {
            gp.obj[6][1] = new OBJ_gemaRoja(gp);
            gp.obj[6][1].worldX = gp.tileSize * 16;
            gp.obj[6][1].worldY = gp.tileSize * 1;
            gp.obj[6][1].mapIndex = 6;
        }

        // Nivel 8
        if(!gp.gemasAzulesPorNivel[7]) {
            gp.obj[7][0] = new OBJ_gemaAzul(gp);
            gp.obj[7][0].worldX = gp.tileSize * 18;
            gp.obj[7][0].worldY = gp.tileSize * 9;
            gp.obj[7][0].mapIndex = 7;
        }
        if(!gp.gemasRojasPorNivel[7]) {
            gp.obj[7][1] = new OBJ_gemaRoja(gp);
            gp.obj[7][1].worldX = gp.tileSize * 1;
            gp.obj[7][1].worldY = gp.tileSize * 9;
            gp.obj[7][1].mapIndex = 7;
        }

        // Nivel 9
        if(!gp.gemasAzulesPorNivel[8]) {
            gp.obj[8][0] = new OBJ_gemaAzul(gp);
            gp.obj[8][0].worldX = gp.tileSize * 6;
            gp.obj[8][0].worldY = gp.tileSize * 4;
            gp.obj[8][0].mapIndex = 8;
        }
        if(!gp.gemasRojasPorNivel[8]) {
            gp.obj[8][1] = new OBJ_gemaRoja(gp);
            gp.obj[8][1].worldX = gp.tileSize * 13;
            gp.obj[8][1].worldY = gp.tileSize * 4;
            gp.obj[8][1].mapIndex = 8;
        }

        // Nivel 10
        if(!gp.gemasAzulesPorNivel[9]) {
            gp.obj[9][0] = new OBJ_gemaAzul(gp);
            gp.obj[9][0].worldX = gp.tileSize * 5;
            gp.obj[9][0].worldY = gp.tileSize * 5;
            gp.obj[9][0].mapIndex = 9;
        }
        if(!gp.gemasRojasPorNivel[9]) {
            gp.obj[9][1] = new OBJ_gemaRoja(gp);
            gp.obj[9][1].worldX = gp.tileSize * 14;
            gp.obj[9][1].worldY = gp.tileSize * 5;
            gp.obj[9][1].mapIndex = 9;
        }

        // Nivel 11
        if(!gp.gemasAzulesPorNivel[10]) {
            gp.obj[10][0] = new OBJ_gemaAzul(gp);
            gp.obj[10][0].worldX = gp.tileSize * 3;
            gp.obj[10][0].worldY = gp.tileSize * 7;
            gp.obj[10][0].mapIndex = 10;
        }
        if(!gp.gemasRojasPorNivel[10]) {
            gp.obj[10][1] = new OBJ_gemaRoja(gp);
            gp.obj[10][1].worldX = gp.tileSize * 16;
            gp.obj[10][1].worldY = gp.tileSize * 7;
            gp.obj[10][1].mapIndex = 10;

        }

        // Nivel 12
        if(!gp.gemasAzulesPorNivel[11]) {
            gp.obj[11][0] = new OBJ_gemaAzul(gp);
            gp.obj[11][0].worldX = gp.tileSize * 3;
            gp.obj[11][0].worldY = gp.tileSize * 3;
            gp.obj[11][0].mapIndex = 11;
        }
        if(!gp.gemasRojasPorNivel[11]) {
            gp.obj[11][1] = new OBJ_gemaRoja(gp);
            gp.obj[11][1].worldX = gp.tileSize * 16;
            gp.obj[11][1].worldY = gp.tileSize * 8;
            gp.obj[11][1].mapIndex = 11;

        }

        // Nivel 13
        if(!gp.gemasAzulesPorNivel[12]) {
            gp.obj[12][0] = new OBJ_gemaAzul(gp);
            gp.obj[12][0].worldX = gp.tileSize * 1;
            gp.obj[12][0].worldY = gp.tileSize * 5;
            gp.obj[12][0].mapIndex = 12;
        }
        if(!gp.gemasRojasPorNivel[12]) {
            gp.obj[12][1] = new OBJ_gemaRoja(gp);
            gp.obj[12][1].worldX = gp.tileSize * 15;
            gp.obj[12][1].worldY = gp.tileSize * 2;
            gp.obj[12][1].mapIndex = 12;
        }

        // Nivel 14
        if(!gp.gemasAzulesPorNivel[13]) {
            gp.obj[13][0] = new OBJ_gemaAzul(gp);
            gp.obj[13][0].worldX = gp.tileSize * 8;
            gp.obj[13][0].worldY = gp.tileSize * 10;
            gp.obj[13][0].mapIndex = 13;
        }
        if(!gp.gemasRojasPorNivel[13]) {
            gp.obj[13][1] = new OBJ_gemaRoja(gp);
            gp.obj[13][1].worldX = gp.tileSize * 11;
            gp.obj[13][1].worldY = gp.tileSize * 10;
            gp.obj[13][1].mapIndex = 13;
        }

        // Nivel 15
        if(!gp.gemasAzulesPorNivel[14]) {
            gp.obj[14][0] = new OBJ_gemaAzul(gp);
            gp.obj[14][0].worldX = gp.tileSize * 12;
            gp.obj[14][0].worldY = gp.tileSize * 2;
            gp.obj[14][0].mapIndex = 14;
        }
        if(!gp.gemasRojasPorNivel[14]) {
            gp.obj[14][1] = new OBJ_gemaRoja(gp);
            gp.obj[14][1].worldX = gp.tileSize * 7;
            gp.obj[14][1].worldY = gp.tileSize * 2;
            gp.obj[14][1].mapIndex = 14;
        }

        // Nivel 16
        if(!gp.gemasAzulesPorNivel[15]) {
            gp.obj[15][0] = new OBJ_gemaAzul(gp);
            gp.obj[15][0].worldX = gp.tileSize * 2;
            gp.obj[15][0].worldY = gp.tileSize * 10;
            gp.obj[15][0].mapIndex = 15;
        }
        if(!gp.gemasRojasPorNivel[15]) {
            gp.obj[15][1] = new OBJ_gemaRoja(gp);
            gp.obj[15][1].worldX = gp.tileSize * 4;
            gp.obj[15][1].worldY = gp.tileSize * 5;
            gp.obj[15][1].mapIndex = 15;
        }
    }

    // Método simplificado - ya no hace nada con NPCs
    public void setNPC() {
        // Método mantenido por compatibilidad pero ya no carga NPCs
    }

    public void setPlayerSpawns(int levelIndex) {
        int p1Col = 0, p1Row = 0;
        int p2Col = 0, p2Row = 0;

        switch(levelIndex) {
            case 0: // NIVEL 1
                p1Col = 4; p1Row = 10;
                p2Col = 15; p2Row = 10;
                break;

            case 1: // NIVEL 2
                p1Col = 5; p1Row = 10;
                p2Col = 13; p2Row = 10;
                break;

            case 2: // NIVEL 3
                p1Col = 5; p1Row = 10;
                p2Col = 15; p2Row = 10;
                break;

            case 3: // NIVEL 4
                p1Col = 18; p1Row = 10;
                p2Col = 1; p2Row = 10;
                break;

            case 4: // NIVEL 5
                p1Col = 12; p1Row = 10;
                p2Col = 7; p2Row = 10;
                break;

            case 5: // NIVEL 6
                p1Col = 12; p1Row = 10;
                p2Col = 7; p2Row = 10;
                break;

            case 6: // NIVEL 7
                p1Col = 1; p1Row = 10;
                p2Col = 18; p2Row = 10;
                break;

            case 7: // NIVEL 8
                p1Col = 9; p1Row = 10;
                p2Col = 10; p2Row = 10;
                break;

            case 8: // NIVEL 9
                p1Col = 9; p1Row = 10;
                p2Col = 10; p2Row = 10;
                break;

            case 9: // NIVEL 10
                p1Col = 4; p1Row = 3;
                p2Col = 15; p2Row = 3;
                break;

            case 10: // NIVEL 11
                p1Col = 1; p1Row = 4;
                p2Col = 18; p2Row = 4;
                break;

            case 11: // NIVEL 12
                p1Col = 11; p1Row = 10;
                p2Col = 13; p2Row = 10;
                break;

            case 12: // NIVEL 13
                p1Col = 1; p1Row = 10;
                p2Col = 18; p2Row = 10;
                break;

            case 13: // NIVEL 14
                p1Col = 18; p1Row = 10;
                p2Col = 1; p2Row = 10;
                break;

            case 14: // NIVEL 15
                p1Col = 1; p1Row = 10;
                p2Col = 18; p2Row = 10;
                break;

            case 15: // NIVEL 16
                p1Col = 2; p1Row = 10;
                p2Col = 5; p2Row = 10;
                break;

            default:
                System.err.println("⚠️ Nivel inválido: " + levelIndex);
                p1Col = 10; p1Row = 10;
                p2Col = 11; p2Row = 10;
                break;
        }

        if(gp.player != null) {
            gp.player.worldX = p1Col * gp.tileSize;
            gp.player.worldY = p1Row * gp.tileSize;
            gp.player.velocityY = 0;
            gp.player.isGrounded = false;
            gp.player.canJump = true;
            gp.player.speed = 4;
            System.out.println("✅ P1 spawn en nivel " + (levelIndex + 1) + ": col=" + p1Col + ", row=" + p1Row);
        }

        if(gp.player2 != null) {
            gp.player2.worldX = p2Col * gp.tileSize;
            gp.player2.worldY = p2Row * gp.tileSize;
            gp.player2.velocityY = 0;
            gp.player2.isGrounded = false;
            gp.player2.canJump = true;
            gp.player2.speed = 4;
            System.out.println("✅ P2 spawn en nivel " + (levelIndex + 1) + ": col=" + p2Col + ", row=" + p2Row);
        }
    }
}