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
        // Tiles según el archivo LEVEL1.txt
        setup(0, "borde", false);        // 0: Aire/espacio vacío
        setup(1, "borde", true);         // 1: Pared/obstáculo
        setup(2, "piso", true);          // 2: Piso/plataforma
        setup(3, "borde", true);         // 3: Borde del mapa
        setup(4, "fondo", false);        // 4: Fondo decorativo
        setup(5, "piso", true);          // 5: Piso especial
        setup(6, "puertaH", false);      // 6: Puerta del hombre
        setup(7, "puertaM", false);      // 7: Puerta de la mujer
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            tile[index].collision = collision;

            // IMPORTANTE: getResourceAsStream usa rutas relativas desde el classpath
            InputStream is = getClass().getResourceAsStream("/Tiles/" + imageName + ".png");

            if (is == null) {
                System.err.println("⚠️  Tile " + index + " no encontrado: /Tiles/" + imageName + ".png");
                System.err.println("   Usando tile temporal de color");
                // Crear tile temporal de color sólido
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

    // Método temporal para crear tiles de colores cuando no hay imágenes
    private java.awt.image.BufferedImage createTemporaryColorTile(int index, int width, int height) {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Colores según el tipo de tile
        switch(index) {
            case 0: // Aire/borde - gris oscuro
                g2.setColor(new Color(60, 60, 60));
                break;
            case 1: // Borde - negro
                g2.setColor(new Color(30, 30, 30));
                break;
            case 2: // Piso - marrón
                g2.setColor(new Color(139, 69, 19));
                break;
            case 3: // Borde exterior - negro sólido
                g2.setColor(new Color(20, 20, 20));
                break;
            case 4: // Fondo - celeste claro
                g2.setColor(new Color(173, 216, 230));
                break;
            case 5: // Piso especial - marrón claro
                g2.setColor(new Color(160, 82, 45));
                break;
            case 6: // Puerta H (Player 1) - azul
                g2.setColor(new Color(0, 100, 200));
                break;
            case 7: // Puerta M (Player 2) - rojo
                g2.setColor(new Color(200, 50, 50));
                break;
            default:
                g2.setColor(new Color(150, 150, 150));
        }

        // Rellenar el tile completo
        g2.fillRect(0, 0, width, height);

        // Agregar borde solo a tiles sólidos (no al fondo)
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
            // IMPORTANTE: La ruta debe empezar con / y ser relativa al classpath
            InputStream is = getClass().getResourceAsStream(filePath);

            if (is == null) {
                System.err.println("❌ No se encontró el mapa: " + filePath);
                System.err.println("   Verifica que el archivo existe en: res" + filePath);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            System.out.println("📂 Cargando mapa: " + filePath);

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) {
                    System.err.println("⚠️ El mapa tiene menos filas de lo esperado (tiene " + row + ", esperaba " + gp.maxWorldRow + ")");
                    break;
                }

                String[] numbers = line.trim().split(" ");

                if (numbers.length < gp.maxWorldCol) {
                    System.err.println("⚠️ La fila " + row + " tiene menos columnas de lo esperado (tiene " + numbers.length + ", esperaba " + gp.maxWorldCol + ")");
                }

                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    try {
                        mapTileNum[map][col][row] = Integer.parseInt(numbers[col].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Error parseando número en fila " + row + ", col " + col + ": " + numbers[col]);
                        mapTileNum[map][col][row] = 0;
                    }
                }
                row++;
            }

            br.close();
            is.close();

            System.out.println("✅ Mapa cargado exitosamente: " + filePath + " (" + gp.maxWorldCol + "x" + gp.maxWorldRow + ")");

            // Debug: mostrar qué tiles se usan en el mapa
            System.out.println("📊 Tiles usados en el mapa:");
            int[] tileCount = new int[10];
            for (int c = 0; c < gp.maxWorldCol; c++) {
                for (int r = 0; r < gp.maxWorldRow; r++) {
                    int tileNum = mapTileNum[map][c][r];
                    if (tileNum < 10) tileCount[tileNum]++;
                }
            }
            for (int i = 0; i < 10; i++) {
                if (tileCount[i] > 0) {
                    System.out.println("   Tile " + i + ": " + tileCount[i] + " veces");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error cargando mapa " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // Dibujar directamente en pantalla (cámara fija)
        while (worldCol < gp.maxScreenCol && worldRow < gp.maxScreenRow) {

            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

            int screenX = worldCol * gp.tileSize;
            int screenY = worldRow * gp.tileSize;

            // Verificar que el tile existe
            if (tile[tileNum] != null && tile[tileNum].Image != null) {
                g2.drawImage(tile[tileNum].Image, screenX, screenY, null);
            } else {
                // Si no existe el tile, dibujar un cuadrado de color gris
                g2.setColor(new Color(100, 100, 100));
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);

                // Borde negro
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