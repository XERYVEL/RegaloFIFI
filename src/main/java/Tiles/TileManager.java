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

        // Cargar solo el nivel 1 por ahora
        loadMap("/Mapas/LEVEL1.txt", 0);

        // Si quieres cargar los mapas antiguos, descomenta estas líneas:
        // loadMap("/Mapas/MAPAMPYTO.txt", 1);
        // loadMap("/Mapas/MAPAAILA2.txt", 2);
        // loadMap("/Mapas/PLANETARIO.txt", 3);
    }

    public void getTileImage() {
        // Tile 0: Aire (sin colisión, transparente)
        setup(0, "00", false);
        // Tile 1: Plataforma sólida (con colisión)
        setup(1, "01", true);
        setup(2, "02", true);

        // Si necesitas más tiles, agrégalos aquí
        // Por ahora dejamos solo estos básicos
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();
        try {
            tile[index] = new Tile();

            InputStream is = getClass().getResourceAsStream("/Tiles/" + imageName + ".png");

            if (is == null) {
                // Si no encuentra la imagen, crear un tile de color
                System.out.println("Imagen no encontrada: " + imageName + ", creando tile de color");
                tile[index].Image = createColoredTile(index, gp.tileSize, gp.tileSize);
            } else {
                tile[index].Image = ImageIO.read(is);
                tile[index].Image = uTool.scaleImage(tile[index].Image, gp.tileSize, gp.tileSize);
            }

            tile[index].collision = collision;

        } catch (IOException e) {
            System.err.println("Error cargando tile " + imageName + ": " + e.getMessage());
            // Crear tile de emergencia
            tile[index] = new Tile();
            tile[index].Image = createColoredTile(index, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        }
    }

    private java.awt.image.BufferedImage createColoredTile(int index, int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Colores según el índice del tile
        switch(index) {
            case 0: // Aire - transparente/negro
                g2.setColor(new Color(0, 0, 0, 0));
                break;
            case 1: // Plataforma - marrón
                g2.setColor(new Color(139, 69, 13));
                break;
            case 2: // Plataforma alternativa - gris
                g2.setColor(new Color(128, 128, 128));
                break;
            default: // Otros - color aleatorio
                g2.setColor(new Color(100 + (index * 15) % 155,
                        100 + (index * 25) % 155,
                        100 + (index * 35) % 155));
        }

        g2.fillRect(0, 0, width, height);

        // Agregar borde si es sólido
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
                System.out.println("Mapa no encontrado: " + filePath + ", creando mapa por defecto");
                createDefaultMap(map);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                while (col < gp.maxWorldCol) {
                    String[] numbers = line.split(" ");

                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[map][col][row] = num;
                    }
                    col++;
                }
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error cargando mapa " + filePath + ": " + e.getMessage());
            createDefaultMap(map);
        }
    }

    private void createDefaultMap(int map) {
        System.out.println("Creando mapa de plataformas 20x20 por defecto...");

        // Limpiar el mapa (todo aire)
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapTileNum[map][col][row] = 0; // Aire
            }
        }

        // ===== PISO PRINCIPAL =====
        for (int col = 0; col < 20; col++) {
            mapTileNum[map][col][13] = 1;
        }

        // ===== PLATAFORMAS BAJAS =====
        // Izquierda baja
        for (int col = 2; col <= 4; col++) {
            mapTileNum[map][col][16] = 1;
        }

        // Centro baja
        for (int col = 8; col <= 11; col++) {
            mapTileNum[map][col][17] = 1;
        }

        // Derecha baja
        for (int col = 15; col <= 17; col++) {
            mapTileNum[map][col][16] = 1;
        }

        // ===== PLATAFORMAS MEDIAS =====
        // Izquierda media
        for (int col = 1; col <= 3; col++) {
            mapTileNum[map][col][13] = 1;
        }

        // Centro-izquierda media
        for (int col = 6; col <= 8; col++) {
            mapTileNum[map][col][13] = 1;
        }

        // Centro-derecha media
        for (int col = 11; col <= 13; col++) {
            mapTileNum[map][col][13] = 1;
        }

        // Derecha media
        for (int col = 16; col <= 18; col++) {
            mapTileNum[map][col][12] = 1;
        }

        // ===== PLATAFORMAS ALTAS =====
        // Izquierda alta
        for (int col = 3; col <= 5; col++) {
            mapTileNum[map][col][10] = 1;
        }

        // Centro alta
        for (int col = 8; col <= 11; col++) {
            mapTileNum[map][col][9] = 1;
        }

        // Derecha alta
        for (int col = 13; col <= 16; col++) {
            mapTileNum[map][col][10] = 1;
        }

        // ===== PLATAFORMAS MUY ALTAS =====
        // Plataforma superior izquierda
        for (int col = 1; col <= 3; col++) {
            mapTileNum[map][col][6] = 1;
        }

        // Plataforma superior centro
        for (int col = 9; col <= 10; col++) {
            mapTileNum[map][col][5] = 1;
        }

        // Plataforma superior derecha
        for (int col = 16; col <= 18; col++) {
            mapTileNum[map][col][7] = 1;
        }

        // ===== PLATAFORMAS PEQUEÑAS (SALTOS DIFÍCILES) =====
        // Mini plataforma izquierda
        mapTileNum[map][5][8] = 1;
        mapTileNum[map][6][8] = 1;

        // Mini plataforma centro
        mapTileNum[map][12][11] = 1;

        // Mini plataforma derecha
        mapTileNum[map][13][8] = 1;

        // ===== PLATAFORMA EN LA CIMA =====
        for (int col = 8; col <= 11; col++) {
            mapTileNum[map][col][2] = 1;
        }

        // Decoración: torre izquierda
        mapTileNum[map][0][17] = 1;
        mapTileNum[map][0][18] = 1;

        // Decoración: torre derecha
        mapTileNum[map][13][17] = 1;
        mapTileNum[map][13][18] = 1;

        System.out.println("Mapa de plataformas 20x20 creado exitosamente");
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // CÁMARA FIJA: Solo dibujamos lo que cabe en pantalla
        // Sin offset del jugador
        while (worldCol < gp.maxScreenCol && worldRow < gp.maxScreenRow) {

            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

            // Posición directa en pantalla (sin cámara)
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