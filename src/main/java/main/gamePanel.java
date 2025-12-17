package main;

import Tiles.TileManager;
import entity.Entity;
import entity.Player;
import varios.Reloj;
import javafx.application.Platform;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class gamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 4;

    public VideosSwing videos;
    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;  // Ancho: 20
    public final int maxScreenRow = 14;  // Alto: 14
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public boolean videoMostrado = false;
    public boolean videoMostrado2 = false;
    public boolean tieneCupon = false;

    public final int maxWorldCol = 20;  // Ancho: 20
    public final int maxWorldRow = 14;  // Alto: 14
    public final int maxMap = 10;
    public int currentMap = 0;

    int FPS = 60;

    public Reloj reloj;
    TileManager tileM = new TileManager(this);

    public KeyHandler keyH = new KeyHandler(this);
    Sonido sonido = new Sonido();
    Thread gameThread;

    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);

    // DOS JUGADORES
    public Player player;  // Player 1
    public Player player2; // Player 2

    public EventHandler eHandler = new EventHandler(this);
    public Entity obj[][] = new Entity[maxMap][10];
    public Entity npc[][] = new Entity[maxMap][10];

    ArrayList<Entity> entityList = new ArrayList<>();

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int gameOverState = 4;
    public final int characterState = 5;
    public final int videoState = 6;

    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.reloj = new Reloj(this, ui);

        videos = new VideosSwing(screenWidth, screenHeight);

        this.setLayout(null);
        videos.getFXPanel().setBounds(0, 0, screenWidth, screenHeight);
        videos.getFXPanel().setVisible(false);
        this.add(videos.getFXPanel());

        videos.loadVideo("pavon", "res/Videos/MercadoPatio.mp4");
        videos.loadVideo("monu", "res/Videos/Monumento.mp4");
    }

    public void showVideo(String key) {
        System.out.println("=== Mostrando video: " + key + " ===");

        gameState = videoState;

        videos.getFXPanel().setVisible(true);
        videos.getFXPanel().revalidate();
        videos.getFXPanel().repaint();

        Platform.runLater(() -> {
            videos.play(key);

            if (videos.currentPlayer != null) {
                videos.currentPlayer.setOnEndOfMedia(() -> {
                    Platform.runLater(() -> {
                        videos.stop();
                        videos.getFXPanel().setVisible(false);

                        if (key.equals("monu")) {
                            ui.gameFinished = true;
                            gameState = playState;
                        } else {
                            gameState = playState;
                        }
                    });
                });
            }
        });
    }

    public void setupGame() {
        // Inicializar los dos jugadores
        player = new Player(this, keyH, 1);   // Player 1 - WASD
        player2 = new Player(this, keyH, 2);  // Player 2 - Flechas

        aSetter.setObject();
        aSetter.setNPC();
        gameState = titleState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {
        if(gameState == playState){
            // Actualizar ambos jugadores solo si existen
            if(player != null) {
                player.update();
            }
            if(player2 != null) {
                player2.update();
            }

            // NPCs (si los necesitas en el plataformero)
            for (int i = 0; i < npc[1].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            eHandler.checkEvent();

            if(reloj != null) {
                reloj.actualizarTiempo();
                reloj.derrota();
            }
        }

        checkMusic();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        long drawStart = 0;
        if (keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        if(gameState == titleState) {
            ui.draw(g2);
        } else if (gameState == videoState) {
            return;
        } else {
            // Dibujar tiles (cámara fija - sin offset)
            tileM.draw(g2);

            // Agregar entidades a la lista para ordenar por profundidad
            entityList.clear();
            entityList.add(player);
            entityList.add(player2);

            for (Entity[] row : npc) {
                for (Entity n : row) {
                    if (n != null) entityList.add(n);
                }
            }

            for (Entity[] row : obj) {
                for (Entity o : row) {
                    if (o != null) entityList.add(o);
                }
            }

            // Ordenar por posición Y (profundidad)
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return Integer.compare(o1.worldY, o2.worldY);
                }
            });

            // Dibujar todas las entidades
            for (Entity e : entityList) {
                e.draw(g2);
            }

            // UI
            ui.draw(g2);

            // Debug info
            if (keyH.checkDrawTime) {
                long drawEnd = System.nanoTime();
                long passed = drawEnd - drawStart;
                g2.setColor(Color.white);
                g2.setFont(new Font("Arial", Font.PLAIN, 20));

                int x = 10;
                int y = 400;

                g2.drawString("P1 X: " + player.worldX, x, y); y += 22;
                g2.drawString("P1 Y: " + player.worldY, x, y); y += 22;
                g2.drawString("P2 X: " + player2.worldX, x, y); y += 22;
                g2.drawString("P2 Y: " + player2.worldY, x, y); y += 22;
                g2.drawString("Draw time: " + passed, x, y);
            }
        }

        g2.dispose();
    }

    public void checkMusic() {
        if (ui.gameFinished) {
            stopMusic();
            return;
        }

        if (gameState == playState || gameState == dialogueState || gameState == characterState) {
            if (currentMap == 0) {
                playMusic(7);
            } else if (currentMap == 1) {
                playMusic(8);
            } else if (currentMap == 2) {
                playMusic(9);
            } else {
                stopMusic();
            }
        } else {
            stopMusic();
        }
    }

    public void playMusic(int i) {
        sonido.playMusic(i);
    }

    public void stopMusic() {
        sonido.stopMusic();
    }

    public void playSE(int i) {
        sonido.playSE(i);
    }
}