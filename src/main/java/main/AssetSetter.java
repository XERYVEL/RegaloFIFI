package main;

import entity.NPC_Aila;
import entity.NPC_LupeMarciana;
import entity.NPC_Panadera;
import entity.NPC_Pavon;

public class AssetSetter {
    gamePanel gp;

    public AssetSetter(gamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
    }

    public void setNPC() {
        int mapNum = 0;
        mapNum = 1;
        mapNum = 2;
    }

    /**
     * Configura las posiciones de spawn para cada nivel
     * @param levelIndex Índice del nivel (0-15)
     */
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
                p1Col = 1; p1Row = 10;
                p2Col = 16; p2Row = 10;
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
                p1Col = 1; p1Row = 10;
                p2Col = 18; p2Row = 10;
                break;

            case 8: // NIVEL 9
                p1Col = 5; p1Row = 10;
                p2Col = 14; p2Row = 10;
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

        // Aplicar las posiciones a los jugadores
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