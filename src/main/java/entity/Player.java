package entity;

import main.gamePanel;
import main.KeyHandler;
import object.OBJ_sube;
import varios.Direccion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    // Variables para física de plataformas
    private double velocityY = 0;
    private final double GRAVITY = 0.5;
    private final double JUMP_STRENGTH = -12;
    private final double MAX_FALL_SPEED = 15;
    private boolean isGrounded = false;
    private boolean canJump = true;

    public int playerNumber; // 1 o 2
    public int panDeAjoCount = 0;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player(gamePanel gp, KeyHandler keyH, int playerNumber) {
        super(gp);
        this.keyH = keyH;
        this.playerNumber = playerNumber;

        // Posición fija en pantalla (ya no se mueve la cámara)
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
        getPlayersImage();
        if(playerNumber == 1) {
            setItems();
        }
    }

    public void setDefaultValues() {
        if(playerNumber == 1) {
            worldX = 2 * gp.tileSize;
            worldY = 18 * gp.tileSize;
        } else {
            worldX = 4 * gp.tileSize;
            worldY = 18 * gp.tileSize;
        }
        speed = 4; // Velocidad horizontal
        direction = Direccion.Derecha;
    }

    public void setItems() {
        inventory.add(new OBJ_sube(gp));
    }

    public void getPlayersImage() {
        // Usar diferentes sprites para cada jugador
        if(playerNumber == 1) {
            up1 = setup("/player/arriba");
            up2 = setup("/player/arriba2");
            down1 = setup("/player/abajo");
            down2 = setup("/player/abajo2");
            left1 = setup("/player/izquierda");
            left2 = setup("/player/izquierda2");
            right1 = setup("/player/derecha");
            right2 = setup("/player/derecha2");
        } else {
            // Player 2 - puedes usar otros sprites o los mismos
            up1 = setup("/player/arriba");
            up2 = setup("/player/arriba2");
            down1 = setup("/player/abajo");
            down2 = setup("/player/abajo2");
            left1 = setup("/player/izquierda");
            left2 = setup("/player/izquierda2");
            right1 = setup("/player/derecha");
            right2 = setup("/player/derecha2");
        }
    }

    public void update() {
        if (gp.ui.gameFinished || gp.ui.gameOver) {
            return;
        }

        boolean moving = false;

        // Controles según el jugador
        if(playerNumber == 1) {
            // Player 1: WASD
            if (keyH.leftPressed) {
                direction = Direccion.Izquierda;
                moveHorizontal(-speed);
                moving = true;
            }
            if (keyH.rightPressed) {
                direction = Direccion.Derecha;
                moveHorizontal(speed);
                moving = true;
            }
            if (keyH.upPressed && canJump && isGrounded) {
                jump();
            }
        } else {
            // Player 2: Flechas
            if (keyH.arrowLeftPressed) {
                direction = Direccion.Izquierda;
                moveHorizontal(-speed);
                moving = true;
            }
            if (keyH.arrowRightPressed) {
                direction = Direccion.Derecha;
                moveHorizontal(speed);
                moving = true;
            }
            if (keyH.arrowUpPressed && canJump && isGrounded) {
                jump();
            }
        }

        // Aplicar gravedad
        applyGravity();

        // Actualizar animación
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

        // Guardar posición anterior
        int oldX = worldX;

        // Intentar mover
        worldX += speedX;

        // Verificar colisiones horizontales con tiles
        gp.cChecker.checkTile(this);

        // Verificar colisión con el otro jugador
        if(checkPlayerCollision(speedX, 0)) {
            collisionOn = true;
        }

        if(collisionOn) {
            worldX = oldX; // Restaurar posición si hay colisión
        }
    }

    private void applyGravity() {
        // Aplicar gravedad
        velocityY += GRAVITY;

        // Limitar velocidad de caída
        if(velocityY > MAX_FALL_SPEED) {
            velocityY = MAX_FALL_SPEED;
        }

        // Guardar posición anterior
        int oldY = worldY;

        // Aplicar velocidad vertical
        worldY += (int)velocityY;

        // Verificar colisiones verticales con tiles
        collisionOn = false;

        if(velocityY > 0) {
            // Cayendo
            direction = Direccion.Abajo;
            gp.cChecker.checkTile(this);

            // Verificar colisión con el otro jugador (cayendo sobre él)
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
            direction = Direccion.Arriba;
            gp.cChecker.checkTile(this);

            // Verificar colisión con el otro jugador (golpeando desde abajo)
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
        // Obtener el otro jugador
        Player otherPlayer = (playerNumber == 1) ? gp.player2 : gp.player;

        if(otherPlayer == null) {
            return false;
        }

        // Crear rectángulos para detectar colisión
        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x + deltaX,
                worldY + solidArea.y + deltaY,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                otherPlayer.worldX + otherPlayer.solidArea.x,
                otherPlayer.worldY + otherPlayer.solidArea.y,
                otherPlayer.solidArea.width,
                otherPlayer.solidArea.height
        );

        // Verificar si se intersectan
        return thisRect.intersects(otherRect);
    }

    private boolean checkPlayerCollisionFromAbove() {
        // Obtener el otro jugador
        Player otherPlayer = (playerNumber == 1) ? gp.player2 : gp.player;

        if(otherPlayer == null) {
            return false;
        }

        // Verificar si estamos cayendo sobre el otro jugador
        // Solo cuenta como colisión si venimos desde arriba
        Rectangle thisRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        Rectangle otherRect = new Rectangle(
                otherPlayer.worldX + otherPlayer.solidArea.x,
                otherPlayer.worldY + otherPlayer.solidArea.y,
                otherPlayer.solidArea.width,
                otherPlayer.solidArea.height
        );

        // Solo colisión si estamos cayendo y nuestros pies tocan su cabeza
        if(velocityY > 0 && thisRect.intersects(otherRect)) {
            // Verificar que efectivamente venimos desde arriba
            int thisFeet = worldY + solidArea.y + solidArea.height;
            int otherHead = otherPlayer.worldY + otherPlayer.solidArea.y;

            // Permitir estar parado sobre el otro jugador
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
        gp.playSE(5); // Sonido de salto
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

        // Dibujar directamente en posición del mundo (cámara fija)
        g2.drawImage(image, worldX, worldY, gp.tileSize, gp.tileSize, null);

        // Debug - dibujar hitbox y estado
        if(gp.keyH.checkDrawTime) {
            // Hitbox
            g2.setColor(playerNumber == 1 ? Color.RED : Color.GREEN);
            g2.drawRect(worldX + solidArea.x, worldY + solidArea.y,
                    solidArea.width, solidArea.height);

            // Indicador de en el suelo
            if(isGrounded) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(worldX + gp.tileSize/2 - 2, worldY + gp.tileSize - 4, 4, 4);
            }

            // Número del jugador
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("P" + playerNumber, worldX + 2, worldY + 12);
        }
    }
}