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

    public int panDeAjoCount = 0;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;


    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayersImage();
        setItems();
    }

    public void setDefaultValues() {

        worldX = 47 * gp.tileSize;
        worldY = 4 * gp.tileSize;
        speed = 2;
        direction = Direccion.Abajo;
    }

    public void setItems() {
        inventory.add(new OBJ_sube(gp));

    }
    public void getPlayersImage() {

        up1 = setup("/player/arriba");
        up2 = setup("/player/arriba2");
        down1 = setup("/player/abajo");
        down2 = setup("/player/abajo2");
        left1 = setup("/player/izquierda");
        left2 = setup("/player/izquierda2");
        right1 = setup("/player/derecha");
        right2 = setup("/player/derecha2");
    }

    public void update() {

        if (gp.ui.gameFinished || gp.ui.gameOver) {
            return;
        }

        collisionOn = false;
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = Direccion.Arriba;
            else if (keyH.downPressed) direction = Direccion.Abajo;
            else if (keyH.leftPressed) direction = Direccion.Izquierda;
            else if (keyH.rightPressed) direction = Direccion.Derecha;

            gp.cChecker.checkTile(this);

            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);


            if(gp.npc[gp.currentMap] != null) {
                int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
                interactNPC(npcIndex);
            }

            gp.eHandler.checkEvent();
            gp.eHandler.checkEvent();


            gp.keyH.enterPressed = false;

            if (!collisionOn) {
                switch (direction) {
                    case Arriba -> worldY -= speed;
                    case Abajo -> worldY += speed;
                    case Izquierda -> worldX -= speed;
                    case Derecha -> worldX += speed;
                }
            }

            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    public void pickUpObject(int i) {
        if (i != 999 && gp.obj[gp.currentMap][i] != null) {
            Entity obj =gp.obj[gp.currentMap][i];
            String objectName = obj.name.toLowerCase().trim();
            System.out.println("Objeto detectado: " + objectName);

            if(inventory.size()>= maxInventorySize){
                gp.ui.showMessage("Inventario lleno");
                return;
            }

            inventory.add(obj);

            if (objectName.contains("pan de ajo")) {
                panDeAjoCount++;
                gp.playSE(1);
                speed += 1;
                gp.ui.showMessage("Tienes el pan de ajo!!");
                System.out.println("Pan de Ajo: " + panDeAjoCount);

            } else {
                gp.playSE(1);
                gp.ui.showMessage("Has recogido:" + obj.name);
            }
            gp.obj[gp.currentMap][i] = null;
        }
    }

    public void interactNPC(int i) {
        if (i != 999){
            if(gp.keyH.enterPressed == true){
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
        g2.drawImage(image, screenX, screenY, null);
    }
}