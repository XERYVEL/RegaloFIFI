package Tiles;

import main.UtilityTool;
import main.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    gamePanel gp;

    public Tile[] tile;
    public int[][][] mapTileNum;

    public TileManager(gamePanel gp) {
        this.gp = gp;
        tile = new Tile[100];

        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();

        // Cargar todos los 16 niveles
        loadAllLevels();
    }

    public void getTileImage() {
        // Tiles según el archivo LEVEL.txt
        setup(0, "borde", false);        // 0: Aire/espacio vacío
        setup(1, "borde", true);         // 1: Pared/obstáculo
        setup(2, "piso", true);          // 2: Piso/plataforma
        setup(3, "borde", true);         // 3: Borde del mapa
        setup(4, "fondo", false);        // 4: Fondo decorativo
        setup(5, "piso", true);          // 5: Piso especial
        setup(6, "puertaH", false);      // 6: Puerta del hombre (Player 1 - Azul)
        setup(7, "puertaM", false);      // 7: Puerta de la mujer (Player 2 - Rojo)
    }

    public void loadAllLevels() {
        // Cargar los 16 niveles
        for (int i = 1; i <= 16; i++) {
            String levelFile = "/Mapas/LEVEL" + i + ".txt";
            int mapIndex = i - 1; // El índice del array empieza en 0
            loadMap(levelFile, mapIndex);
        }
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            tile[index].collision = collision;

            InputStream is = getClass().getResourceAsStream("/Tiles/" + imageName + ".png");

            if (is == null) {
                System.err.println("⚠️  Tile " + index + " no encontrado: /Tiles/" + imageName + ".png");
                System.err.println("   Usando tile temporal de color");
                tile[index].Image = createTemporaryColorTile(index, gp.tileSize, gp.tileSize);
            } else {
                tile[index].Image = ImageIO.read(is);
                tile[index].Image = uTool.scaleImage(
                        tile[index].Image,
                        gp.tileSize,
                        gp.tileSize
                );
                System.out.println("✅ Tile " + index + " cargado: " + imageName);
                is.close();
            }

        } catch (IOException e) {
            System.err.println("❌ Error cargando tile " + index + " (" + imageName + "): " + e.getMessage());
            tile[index] = new Tile();
            tile[index].collision = collision;
            tile[index].Image = createTemporaryColorTile(index, gp.tileSize, gp.tileSize);
        }
    }

    private java.awt.image.BufferedImage createTemporaryColorTile(int index, int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        switch(index) {
            case 0:
                g2.setColor(new Color(60, 60, 60));
                break;
            case 1:
                g2.setColor(new Color(30, 30, 30));
                break;
            case 2:
                g2.setColor(new Color(139, 69, 19));
                break;
            case 3:
                g2.setColor(new Color(20, 20, 20));
                break;
            case 4:
                g2.setColor(new Color(173, 216, 230));
                break;
            case 5:
                g2.setColor(new Color(160, 82, 45));
                break;
            case 6:
                g2.setColor(new Color(0, 100, 200));
                break;
            case 7:
                g2.setColor(new Color(200, 50, 50));
                break;
            default:
                g2.setColor(new Color(150, 150, 150));
        }

        g2.fillRect(0, 0, width, height);

        if(index != 4) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(0, 0, width - 1, height - 1);
        }

        g2.dispose();
        return image;
    }

    public void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);

            if (is == null) {
                System.err.println("❌ No se encontró el mapa: " + filePath);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            System.out.println("📂 Cargando mapa: " + filePath);

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) {
                    System.err.println("⚠️ El mapa tiene menos filas de lo esperado");
                    break;
                }

                String[] numbers = line.trim().split(" ");

                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    try {
                        mapTileNum[map][col][row] = Integer.parseInt(numbers[col].trim());
                    } catch (NumberFormatException e) {
                        mapTileNum[map][col][row] = 0;
                    }
                }
                row++;
            }

            br.close();
            is.close();

            System.out.println("✅ Mapa " + (map + 1) + " cargado exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error cargando mapa " + filePath + ": " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxScreenCol && worldRow < gp.maxScreenRow) {

            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

            int screenX = worldCol * gp.tileSize;
            int screenY = worldRow * gp.tileSize;

            if (tile[tileNum] != null && tile[tileNum].Image != null) {
                g2.drawImage(tile[tileNum].Image, screenX, screenY, null);
            } else {
                g2.setColor(new Color(100, 100, 100));
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);

                g2.setColor(Color.BLACK);
                g2.drawRect(screenX, screenY, gp.tileSize - 1, gp.tileSize - 1);
            }

            worldCol++;

            if (worldCol == gp.maxScreenCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}