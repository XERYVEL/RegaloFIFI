package main;

import entity.Entity;

public class CollisionChecker {

    gamePanel gp;

    public CollisionChecker(gamePanel gp) {
        this.gp = gp;

    }

    public void checkTile(Entity entity) {
        // Expandir hitbox horizontalmente SOLO para detección de suelo
        int groundDetectionMargin = 6; // píxeles extra a cada lado para detectar suelo

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Para detección lateral (izquierda/derecha), usar hitbox normal
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case Arriba:
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
            case Abajo:
                // MEJORADO: Usar hitbox expandida para detección de suelo
                int groundLeftWorldX = entity.worldX + entity.solidArea.x - groundDetectionMargin;
                int groundRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width + groundDetectionMargin;
                int groundLeftCol = groundLeftWorldX / gp.tileSize;
                int groundRightCol = groundRightWorldX / gp.tileSize;

                // Verificar también el centro para mejor detección
                int groundCenterWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width / 2;
                int groundCenterCol = groundCenterWorldX / gp.tileSize;

                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;

                // Verificar izquierda, centro y derecha
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][groundLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][groundRightCol][entityBottomRow];
                int tileNum3 = gp.tileM.mapTileNum[gp.currentMap][groundCenterCol][entityBottomRow];

                if (gp.tileM.tile[tileNum1].collision == true ||
                        gp.tileM.tile[tileNum2].collision == true ||
                        gp.tileM.tile[tileNum3].collision == true) {
                    entity.collisionOn = true;
                }
                break;
            case Izquierda:
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
            case Derecha:
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Entity entity, boolean player) {

        int index = 999;

        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x;
                gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y;

                switch (entity.direction) {
                    case Arriba:
                        entity.solidArea.y -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
                            if (gp.obj[gp.currentMap][i].collision == true) {
                                entity.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case Abajo:
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
                            if (gp.obj[gp.currentMap][i].collision == true) {
                                entity.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case Derecha:
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
                            if (gp.obj[gp.currentMap][i].collision == true) {
                                entity.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                    case Izquierda:
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
                            if (gp.obj[gp.currentMap][i].collision == true) {
                                entity.collisionOn = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].solidAreaDefaultX;
                gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].solidAreaDefaultY;
            }
        }
        return index;
    }

    public int checkEntity(Entity entity, Entity[][] target) {
        int index = 999;
        for (int i = 0; i < target[1].length; i++) {
            if (target[gp.currentMap][i] != null && target[gp.currentMap][i] != entity) {

                entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
                entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;
                target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].worldX + target[gp.currentMap][i].solidAreaDefaultX;
                target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].worldY + target[gp.currentMap][i].solidAreaDefaultY;

                switch (entity.direction) {
                    case Arriba:
                        entity.solidArea.y -= entity.speed;
                        if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case Abajo:
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case Derecha:
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case Izquierda:
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                            entity.collisionOn = true;
                            index = i;
                        }
                        break;
                }
                if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                    entity.collisionOn = true;
                    index = i;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].solidAreaDefaultX;
                target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].solidAreaDefaultY;
            }
        }
        return index;
    }

    public void checkPlayer(Entity entity) {
        entity.solidArea.x = entity.worldX + entity.solidAreaDefaultX;
        entity.solidArea.y = entity.worldY + entity.solidAreaDefaultY;

        gp.player.solidArea.x = gp.player.worldX + gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidAreaDefaultY;

        switch (entity.direction) {
            case Arriba:
                entity.solidArea.y -= entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case Abajo:
                entity.solidArea.y += entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case Derecha:
                entity.solidArea.x += entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
            case Izquierda:
                entity.solidArea.x -= entity.speed;
                if (entity.solidArea.intersects(gp.player.solidArea)) {
                    entity.collisionOn = true;
                }
                break;
        }
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;
    }
}