package entity;

import main.gamePanel;
import main.KeyHandler;
import object.OBJ_sube;
import varios.Direccion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player1 extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    // Variables para física de plataformas - AHORA PÚBLICAS
    public double velocityY = 0;
    public final double GRAVITY = 0.5;
    public final double JUMP_STRENGTH = -12;
    public final double MAX_FALL_SPEED = 15;
    public boolean isGrounded = false;
    public boolean canJump = true;

    public int panDeAjoCount = 0;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player1(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        // Posición fija en pantalla
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
        setItems();
    }

    public void setDefaultValues() {
        // Posición inicial Player 1
        worldX = 10 * gp.tileSize;
        worldY = 10 * gp.tileSize;
        speed = 4;
        direction = Direccion.Derecha;

        // Resetear física
        velocityY = 0;
        isGrounded = false;
        canJump = true;
    }

    public void setItems() {
        inventory.add(new OBJ_sube(gp));
    }

    public void getPlayerImage() {
        // Player 1 - Mujer (sprites originales)
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

        // Player 1: WASD
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

        // Aplicar gravedad
        applyGravity();

        // Actualizar animación solo si se está moviendo
        if(moving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private void moveHorizontal(int speedX) {
        collisionOn = false;

        int oldX = worldX;
        worldX += speedX;

        gp.cChecker.checkTile(this);

        if(checkPlayerCollision(speedX, 0)) {
            collisionOn = true;
        }

        if(collisionOn) {
            worldX = oldX;
        }
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
            // Cayendo

            gp.cChecker.checkTile(this);

            if(checkPlayerCollisionFromAbove()) {
                collisionOn = true;
            }

            if(collisionOn) {
                worldY = oldY;
                velocityY = 0;
                isGrounded = true;
                canJump = true;
            } else {
                isGrounded = false;
            }
        } else if(velocityY < 0) {
            // Subiendo

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

        // Debug
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