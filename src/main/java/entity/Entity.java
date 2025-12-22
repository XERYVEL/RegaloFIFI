package entity;

import main.UtilityTool;
import main.gamePanel;
import varios.Direccion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity {
    gamePanel gp;
    public int worldX, worldY;

    public int speed;
    public int mapIndex;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public Direccion direction = Direccion.Abajo;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    public int actionLockCounter = 0;

    String dialogues[] = new String[20];
    int dialogueIndex = 0;

    public String name;
    public boolean collision = false;
    public String descripcion = "";

    public Entity(gamePanel gp) {
        this.gp = gp;
    }

    public void setAction() {
    }

    public void speak() {
        if (dialogues[dialogueIndex] == null) {
            dialogueIndex = 0;
        }
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch (gp.player.direction) {
            case Arriba:
                direction = Direccion.Arriba;
                break;
            case Abajo:
                direction = Direccion.Abajo;
                break;
            case Derecha:
                direction = Direccion.Derecha;
                break;
            case Izquierda:
                direction = Direccion.Izquierda;
                break;
        }
    }

    public void update() {
        setAction();
        collisionOn = false;

        gp.cChecker.checkTile(this);

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

    public void draw(Graphics2D g2) {
        if (gp.currentMap != mapIndex) {
            return;
        }

        BufferedImage image = null;

        switch (direction) {
            case Arriba -> image = (spriteNum == 1) ? up1 : up2;
            case Abajo -> image = (spriteNum == 1) ? down1 : down2;
            case Izquierda -> image = (spriteNum == 1) ? left1 : left2;
            case Derecha -> image = (spriteNum == 1) ? right1 : right2;
        }

        if (image != null) {
            g2.drawImage(image, worldX, worldY, gp.tileSize, gp.tileSize, null);
        } else {
            // Si no hay imagen, dibujar un cuadrado de color
            g2.setColor(Color.BLUE);
            g2.fillRect(worldX, worldY, gp.tileSize, gp.tileSize);
        }
    }

    protected BufferedImage setup(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            java.io.InputStream is = getClass().getResourceAsStream(imagePath + ".png");

            if (is == null) {
                System.out.println("Imagen no encontrada: " + imagePath + ", creando sprite de color");
                return createColoredSprite(gp.tileSize, gp.tileSize);
            }

            image = ImageIO.read(is);
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            System.err.println("Error cargando imagen " + imagePath + ": " + e.getMessage());
            return createColoredSprite(gp.tileSize, gp.tileSize);
        }
        return image;
    }

    private BufferedImage createColoredSprite(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Color base según el tipo de entidad
        Color mainColor;

        // Detectar tipo de entidad usando instanceof
        if (this instanceof Player1) {
            mainColor = new Color(0, 120, 255); // Azul para Player 1
        } else if (this instanceof Player2) {
            mainColor = new Color(255, 50, 50); // Rojo para Player 2
        } else if (this.getClass().getSimpleName().contains("NPC")) {
            mainColor = new Color(255, 150, 0); // Naranja para NPCs
        } else {
            mainColor = new Color(150, 150, 150); // Gris para objetos
        }

        // Cuerpo
        g2.setColor(mainColor);
        g2.fillRect(width / 4, height / 3, width / 2, height / 2);

        // Cabeza
        g2.setColor(new Color(255, 220, 177));
        g2.fillOval(width / 3, height / 8, width / 3, width / 3);

        // Ojos
        g2.setColor(Color.BLACK);
        g2.fillOval(width / 3 + 5, height / 8 + 8, 4, 4);
        g2.fillOval(width / 3 + width / 3 - 9, height / 8 + 8, 4, 4);

        // Borde
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(1, 1, width - 2, height - 2);

        g2.dispose();
        return image;
    }
}