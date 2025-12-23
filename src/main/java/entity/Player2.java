package entity;

import main.gamePanel;
import main.KeyHandler;
import varios.Direccion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player2 extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    // Variables para física de plataformas
    public double velocityY = 0;
    public final double GRAVITY = 0.5;
    public final double JUMP_STRENGTH = -12;
    public final double MAX_FALL_SPEED = 15;
    public boolean isGrounded = false;
    public boolean canJump = true;

    public int panDeAjoCount = 0;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player2(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = 0;
        screenY = 0;

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        speed = 4;
        direction = Direccion.Derecha;
        velocityY = 0;
        isGrounded = false;
        canJump = true;
    }

    public void getPlayerImage() {
        up1 = setup("/player/frenteM");
        up2 = setup("/player/frenteM");
        down1 = setup("/player/frenteM");
        down2 = setup("/player/frenteM");
        left1 = setup("/player/izquierdaM1");
        left2 = setup("/player/izquierdaM2");
        right1 = setup("/player/derechaM1");
        right2 = setup("/player/derechaM2");
    }

    public void update() {
        if (gp.ui.gameFinished || gp.ui.gameOver) {
            return;
        }

        boolean moving = false;

        if (keyH.arrowLeftPressed) {
            direction = Direccion.Izquierda;
            moveHorizontal(-speed);
            moving = true;
        }
        else if (keyH.arrowRightPressed) {
            direction = Direccion.Derecha;
            moveHorizontal(speed);
            moving = true;
        }

        if (keyH.arrowUpPressed && canJump && isGrounded) {
            jump();
        }

        applyGravity();

        if(moving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private void moveHorizontal(int speedX) {
        worldX += speedX;

        // Verificar si estamos colisionando con una pared
        boolean collision = checkWallCollision();

        if(collision) {
            // Empujar al jugador fuera de la pared, 1 píxel a la vez
            if(speedX < 0) {
                // Estábamos moviendo a la izquierda, empujar a la derecha
                while(checkWallCollision() && worldX < gp.screenWidth) {
                    worldX++;
                }
            } else if(speedX > 0) {
                // Estábamos moviendo a la derecha, empujar a la izquierda
                while(checkWallCollision() && worldX > 0) {
                    worldX--;
                }
            }
        }

        // Verificar colisión con otro jugador
        if(checkPlayerCollision(speedX, 0)) {
            worldX -= speedX; // Revertir movimiento
        }
    }

    private boolean checkWallCollision() {
        // Calcular bounds del jugador
        int entityLeftWorldX = worldX + solidArea.x;
        int entityRightWorldX = worldX + solidArea.x + solidArea.width;
        int entityTopWorldY = worldY + solidArea.y;

        // Solo verificar hasta el 75% de la altura para evitar detectar el suelo
        int entityCheckBottomY = worldY + solidArea.y + (int)(solidArea.height * 0.75);

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityCheckBottomRow = entityCheckBottomY / gp.tileSize;

        // Verificar columna izquierda
        if(entityLeftCol >= 0 && entityLeftCol < gp.maxWorldCol) {
            for(int row = entityTopRow; row <= entityCheckBottomRow; row++) {
                if(row >= 0 && row < gp.maxWorldRow) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][row]].collision) {
                        return true;
                    }
                }
            }
        }

        // Verificar columna derecha
        if(entityRightCol >= 0 && entityRightCol < gp.maxWorldCol) {
            for(int row = entityTopRow; row <= entityCheckBottomRow; row++) {
                if(row >= 0 && row < gp.maxWorldRow) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][entityRightCol][row]].collision) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void applyGravity() {
        velocityY += GRAVITY;

        if(velocityY > MAX_FALL_SPEED) {
            velocityY = MAX_FALL_SPEED;
        }

        int oldY = worldY;
        worldY += (int)velocityY;

        collisionOn = false;

        if(velocityY > 0) {
            // Cayendo - verificar colisión con suelo

            // Usar hitbox expandida SOLO para los bordes, no para el centro
            int groundDetectionMargin = 4; // Reducido de 6 a 4

            int entityCenterX = worldX + solidArea.x + solidArea.width / 2;
            int entityBottomY = worldY + solidArea.y + solidArea.height;

            // Verificar 3 puntos: izquierda, centro, derecha
            int leftX = worldX + solidArea.x - groundDetectionMargin;
            int rightX = worldX + solidArea.x + solidArea.width + groundDetectionMargin;

            int leftCol = leftX / gp.tileSize;
            int centerCol = entityCenterX / gp.tileSize;
            int rightCol = rightX / gp.tileSize;
            int bottomRow = entityBottomY / gp.tileSize;

            boolean groundDetected = false;

            // Verificar tiles de suelo (asegurarse de que estén dentro del rango)
            if(bottomRow >= 0 && bottomRow < gp.maxWorldRow) {
                // Izquierda
                if(leftCol >= 0 && leftCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][leftCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }

                // Centro (más importante)
                if(!groundDetected && centerCol >= 0 && centerCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][centerCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }

                // Derecha
                if(!groundDetected && rightCol >= 0 && rightCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][rightCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }
            }

            // También verificar colisión con el otro jugador desde arriba
            if(checkPlayerCollisionFromAbove()) {
                groundDetected = true;
            }

            if(groundDetected) {
                // Ajustar posición para quedar exactamente sobre el tile
                int tileTopY = bottomRow * gp.tileSize;
                worldY = tileTopY - solidArea.y - solidArea.height;

                velocityY = 0;
                isGrounded = true;
                canJump = true;
                // NO setear collisionOn = true aquí, solo afecta movimiento vertical
            } else {
                isGrounded = false;
            }

        } else if(velocityY < 0) {
            // Subiendo - verificar colisión con techo
            gp.cChecker.checkTile(this);

            if(checkPlayerCollision(0, (int)velocityY)) {
                collisionOn = true;
            }

            if(collisionOn) {
                worldY = oldY;
                velocityY = 0;
            }
            isGrounded = false;
        }
    }

    private boolean checkPlayerCollision(int deltaX, int deltaY) {
        if(gp.player == null) {
            return false;
        }

        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x + deltaX,
                worldY + solidArea.y + deltaY,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        return thisRect.intersects(otherRect);
    }

    private boolean checkPlayerCollisionFromAbove() {
        if(gp.player == null) {
            return false;
        }

        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        if(velocityY > 0 && thisRect.intersects(otherRect)) {
            int thisFeet = worldY + solidArea.y + solidArea.height;
            int otherHead = gp.player.worldY + gp.player.solidArea.y;

            if(thisFeet >= otherHead && thisFeet <= otherHead + 16) {
                return true;
            }
        }

        return false;
    }

    private void jump() {
        velocityY = JUMP_STRENGTH;
        isGrounded = false;
        canJump = false;
        gp.playSE(5);
    }

    public void pickUpObject(int i) {
        if (i != 999 && gp.obj[gp.currentMap][i] != null) {
            Entity obj = gp.obj[gp.currentMap][i];
            String objectName = obj.name.toLowerCase().trim();

            if(inventory.size() >= maxInventorySize){
                gp.ui.showMessage("Inventario lleno");
                return;
            }

            inventory.add(obj);

            if (objectName.contains("pan de ajo")) {
                panDeAjoCount++;
                gp.playSE(1);
                speed += 1;
                gp.ui.showMessage("Tienes el pan de ajo!!");
            } else {
                gp.playSE(1);
                gp.ui.showMessage("Has recogido:" + obj.name);
            }
            gp.obj[gp.currentMap][i] = null;
        }
    }

    public void interactNPC(int i) {
        if (i != 999){
            if(keyH.enterPressed == true){
                gp.gameState = gp.dialogueState;
                gp.npc[gp.currentMap][i].speak();
            }
        }
    }

    public void removeItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).name.equalsIgnoreCase(itemName)) {
                inventory.remove(i);
                break;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case Arriba -> image = (spriteNum == 1) ? up1 : up2;
            case Abajo -> image = (spriteNum == 1) ? down1 : down2;
            case Izquierda -> image = (spriteNum == 1) ? left1 : left2;
            case Derecha -> image = (spriteNum == 1) ? right1 : right2;
        }

        g2.drawImage(image, worldX, worldY, gp.tileSize, gp.tileSize, null);

        if(gp.keyH.checkDrawTime) {
            g2.setColor(new Color(255, 50, 50));
            g2.drawRect(worldX + solidArea.x, worldY + solidArea.y,
                    solidArea.width, solidArea.height);

            if(isGrounded) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(worldX + gp.tileSize/2 - 2, worldY + gp.tileSize - 4, 4, 4);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("P2", worldX + 2, worldY + 12);
        }
    }
}