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

        // Cargar el nivel 1 (20x12)
        loadMap("/Mapas/LEVEL1.txt", 0);
    }

    public void getTileImage() {
        // Tile 0: Aire (sin colisión, transparente)
        setup(0, "borde", false);
        // Tile 1: Plataforma sólida (con colisión)
        setup(1, "borde", true);
        setup(2, "borde", true);
        setup(3, "borde", true);
        setup(4, "fondo", false);
        setup(5, "piso", true);
        setup(6, "puertaH", false);
        setup(7, "puertaM", false);
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();
        try {
            tile[index] = new Tile();

            InputStream is = getClass().getResourceAsStream("/Tiles/" + imageName + ".png");

            if (is == null) {
                System.out.println("Imagen no encontrada: " + imageName + ", creando tile de color");
                tile[index].Image = createColoredTile(index, gp.tileSize, gp.tileSize);
            } else {
                tile[index].Image = ImageIO.read(is);
                tile[index].Image = uTool.scaleImage(tile[index].Image, gp.tileSize, gp.tileSize);
            }

            tile[index].collision = collision;

        } catch (IOException e) {
            System.err.println("Error cargando tile " + imageName + ": " + e.getMessage());
            tile[index] = new Tile();
            tile[index].Image = createColoredTile(index, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        }
    }

    private java.awt.image.BufferedImage createColoredTile(int index, int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        switch(index) {
            case 0: // Aire - transparente/negro
                g2.setColor(new Color(0, 0, 0, 0));
                break;
            case 1: // Plataforma - marrón
                g2.setColor(new Color(139, 69, 19));
                break;
            case 2: // Plataforma alternativa - gris
                g2.setColor(new Color(128, 128, 128));
                break;
            default:
                g2.setColor(new Color(100 + (index * 15) % 155,
                        100 + (index * 25) % 155,
                        100 + (index * 35) % 155));
        }

        g2.fillRect(0, 0, width, height);

        if(index > 0) {
            g2.setColor(Color.BLACK);
            g2.drawRect(0, 0, width - 1, height - 1);
        }

        g2.dispose();
        return image;
    }

    public void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);

            if (is == null) {
                System.out.println("ERROR: Mapa no encontrado: " + filePath);
                System.out.println("Verifica que el archivo existe en res/Mapas/");
                createDefaultMap(map);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Leer fila por fila
            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();

                if (line == null) {
                    System.out.println("Advertencia: El mapa tiene menos filas de lo esperado");
                    break;
                }

                String[] numbers = line.trim().split(" ");

                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    try {
                        int num = Integer.parseInt(numbers[col].trim());
                        mapTileNum[map][col][row] = num;
                    } catch (NumberFormatException e) {
                        System.out.println("Error parseando número en fila " + row + ", col " + col);
                        mapTileNum[map][col][row] = 0;
                    }
                }
            }

            br.close();
            System.out.println("Mapa cargado exitosamente: " + filePath + " (20x12)");

        } catch (Exception e) {
            System.err.println("Error cargando mapa " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            createDefaultMap(map);
        }
    }

    private void createDefaultMap(int map) {
        System.out.println("Creando mapa de respaldo 20x12...");

        // Limpiar todo como aire
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapTileNum[map][col][row] = 0;
            }
        }

        // Piso en la fila 11 (última fila, índice 11)
        for (int col = 0; col < gp.maxWorldCol; col++) {
            mapTileNum[map][col][11] = 1;
        }

        // Plataformas bajas (fila 9)
        for (int col = 3; col <= 5; col++) {
            mapTileNum[map][col][9] = 1;
        }

        for (int col = 14; col <= 16; col++) {
            mapTileNum[map][col][9] = 1;
        }

        // Plataformas medias (fila 7)
        for (int col = 1; col <= 3; col++) {
            mapTileNum[map][col][7] = 1;
        }

        for (int col = 8; col <= 11; col++) {
            mapTileNum[map][col][7] = 1;
        }

        for (int col = 16; col <= 18; col++) {
            mapTileNum[map][col][7] = 1;
        }

        // Plataformas altas (fila 5)
        for (int col = 5; col <= 7; col++) {
            mapTileNum[map][col][5] = 1;
        }

        for (int col = 12; col <= 14; col++) {
            mapTileNum[map][col][5] = 1;
        }

        // Plataforma superior (fila 3)
        for (int col = 8; col <= 11; col++) {
            mapTileNum[map][col][3] = 1;
        }

        // Mini plataforma muy alta (fila 1)
        for (int col = 9; col <= 10; col++) {
            mapTileNum[map][col][1] = 1;
        }

        System.out.println("Mapa de respaldo 20x12 creado");
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // Dibujar directamente en pantalla (cámara fija)
        while (worldCol < gp.maxScreenCol && worldRow < gp.maxScreenRow) {

            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

            int screenX = worldCol * gp.tileSize;
            int screenY = worldRow * gp.tileSize;

            g2.drawImage(tile[tileNum].Image, screenX, screenY, null);

            worldCol++;

            if (worldCol == gp.maxScreenCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}