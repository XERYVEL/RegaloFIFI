package entity;

import main.gamePanel;
import main.KeyHandler;
import object.OBJ_gemaAzul;
import varios.Direccion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player1 extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    public double velocityY = 0;
    public final double GRAVITY = 0.5;
    public final double JUMP_STRENGTH = -12;
    public final double MAX_FALL_SPEED = 15;
    public boolean isGrounded = false;
    public boolean canJump = true;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player1(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = 0;
        screenY = 0;

        solidArea = new Rectangle();
        solidArea.x = 16;
        solidArea.y = 16;
        solidArea.width = 20;
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
        up1 = setup("/player/frenteH");
        up2 = setup("/player/frenteH");
        down1 = setup("/player/frenteH");
        down2 = setup("/player/frenteH");
        left1 = setup("/player/izquierdaH1");
        left2 = setup("/player/izquierdaH2");
        right1 = setup("/player/derechaH1");
        right2 = setup("/player/derechaH2");
    }

    public void update() {
        if (gp.ui.gameFinished || gp.ui.gameOver) {
            return;
        }

        boolean moving = false;

        if (keyH.leftPressed) {
            direction = Direccion.Izquierda;
            moveHorizontal(-speed);
            moving = true;
        }
        else if (keyH.rightPressed) {
            direction = Direccion.Derecha;
            moveHorizontal(speed);
            moving = true;
        }

        if (keyH.upPressed && canJump && isGrounded) {
            jump();
        }

        applyGravity();

        // ⭐ Verificar colisión con gemas azules
        checkGemCollision();

        if(moving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    // ⭐ Método para verificar colisión con gemas azules
    private void checkGemCollision() {
        Rectangle playerRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        // Verificar colisión con objetos del mapa actual
        for(int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if(gp.obj[gp.currentMap][i] != null && gp.obj[gp.currentMap][i] instanceof OBJ_gemaAzul) {
                Rectangle gemRect = new Rectangle(
                        gp.obj[gp.currentMap][i].worldX,
                        gp.obj[gp.currentMap][i].worldY,
                        gp.tileSize,
                        gp.tileSize
                );

                if(playerRect.intersects(gemRect)) {
                    // ¡Gema azul recolectada!
                    System.out.println("💎 Player 1 recolectó gema AZUL en nivel " + (gp.currentMap + 1));

                    // Marcar como recolectada
                    gp.gemasAzulesPorNivel[gp.currentMap] = true;
                    gp.gemasAzulesRecolectadas++;

                    // Reproducir sonido
                    gp.playSE(5);

                    // Eliminar la gema del mapa
                    gp.obj[gp.currentMap][i] = null;

                    System.out.println("📊 Gemas azules totales: " + gp.gemasAzulesRecolectadas + "/16");
                    System.out.println("📊 Gemas rojas totales: " + gp.gemasRojasRecolectadas + "/16");

                    break;
                }
            }
        }
    }

    private void moveHorizontal(int speedX) {
        // Guardar posición anterior
        int oldX = worldX;

        // Intentar moverse
        worldX += speedX;

        // Verificar colisión con paredes
        if(checkWallCollision()) {
            // Si hay colisión, simplemente volver a la posición anterior
            worldX = oldX;
        }

        // Verificar colisión con el otro jugador
        if(checkPlayerCollision(speedX, 0)) {
            worldX = oldX;
        }
    }

    private boolean checkWallCollision() {
        int entityLeftWorldX = worldX + solidArea.x;
        int entityRightWorldX = worldX + solidArea.x + solidArea.width;
        int entityTopWorldY = worldY + solidArea.y;

        int entityCheckBottomY = worldY + solidArea.y + (int)(solidArea.height * 0.75);

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityCheckBottomRow = entityCheckBottomY / gp.tileSize;

        // Verificar límites del mapa
        if(entityLeftCol < 0 || entityRightCol >= gp.maxWorldCol) {
            return true;
        }

        // Verificar lado izquierdo
        if(entityLeftCol >= 0 && entityLeftCol < gp.maxWorldCol) {
            for(int row = entityTopRow; row <= entityCheckBottomRow; row++) {
                if(row >= 0 && row < gp.maxWorldRow) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][row]].collision) {
                        return true;
                    }
                }
            }
        }

        // Verificar lado derecho
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
            // Cayendo - verificar suelo
            int groundDetectionMargin = 4;

            int entityCenterX = worldX + solidArea.x + solidArea.width / 2;
            int entityBottomY = worldY + solidArea.y + solidArea.height;

            int leftX = worldX + solidArea.x - groundDetectionMargin;
            int rightX = worldX + solidArea.x + solidArea.width + groundDetectionMargin;

            int leftCol = leftX / gp.tileSize;
            int centerCol = entityCenterX / gp.tileSize;
            int rightCol = rightX / gp.tileSize;
            int bottomRow = entityBottomY / gp.tileSize;

            boolean groundDetected = false;

            if(bottomRow >= 0 && bottomRow < gp.maxWorldRow) {
                if(leftCol >= 0 && leftCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][leftCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }

                if(!groundDetected && centerCol >= 0 && centerCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][centerCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }

                if(!groundDetected && rightCol >= 0 && rightCol < gp.maxWorldCol) {
                    if(gp.tileM.tile[gp.tileM.mapTileNum[gp.currentMap][rightCol][bottomRow]].collision) {
                        groundDetected = true;
                    }
                }
            }

            if(checkPlayerCollisionFromAbove()) {
                groundDetected = true;
            }

            if(groundDetected) {
                int tileTopY = bottomRow * gp.tileSize;
                worldY = tileTopY - solidArea.y - solidArea.height;

                velocityY = 0;
                isGrounded = true;
                canJump = true;
            } else {
                isGrounded = false;
            }

        } else if(velocityY < 0) {
            // Subiendo - verificar techo
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
        if(gp.player2 == null) {
            return false;
        }

        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x + deltaX,
                worldY + solidArea.y + deltaY,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                gp.player2.worldX + gp.player2.solidArea.x,
                gp.player2.worldY + gp.player2.solidArea.y,
                gp.player2.solidArea.width,
                gp.player2.solidArea.height
        );

        return thisRect.intersects(otherRect);
    }

    private boolean checkPlayerCollisionFromAbove() {
        if(gp.player2 == null) {
            return false;
        }

        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                gp.player2.worldX + gp.player2.solidArea.x,
                gp.player2.worldY + gp.player2.solidArea.y,
                gp.player2.solidArea.width,
                gp.player2.solidArea.height
        );

        if(velocityY > 0 && thisRect.intersects(otherRect)) {
            int thisFeet = worldY + solidArea.y + solidArea.height;
            int otherHead = gp.player2.worldY + gp.player2.solidArea.y;

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
        gp.playSE(0);
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
            g2.setColor(new Color(0, 120, 255));
            g2.drawRect(worldX + solidArea.x, worldY + solidArea.y,
                    solidArea.width, solidArea.height);

            if(isGrounded) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(worldX + gp.tileSize/2 - 2, worldY + gp.tileSize - 4, 4, 4);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("P1", worldX + 2, worldY + 12);
        }
    }
}