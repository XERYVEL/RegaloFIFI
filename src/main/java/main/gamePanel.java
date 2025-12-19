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
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public boolean videoMostrado = false;
    public boolean videoMostrado2 = false;
    public boolean tieneCupon = false;

    public final int maxWorldCol = 20;
    public final int maxWorldRow = 12;
    public final int maxMap = 10;
    public int currentMap = 0;

    int FPS = 60;

    public Reloj reloj;
    TileManager tileM;

    public KeyHandler keyH;
    Sonido sonido = new Sonido();
    Thread gameThread;

    public CollisionChecker cChecker;
    public AssetSetter aSetter;
    public UI ui;

    public Player player;
    public Player player2;

    public EventHandler eHandler;
    public Entity obj[][] = new Entity[maxMap][10];
    public Entity npc[][] = new Entity[maxMap][10];

    ArrayList<Entity> entityList = new ArrayList<>();

    // Sistema de niveles
    public boolean[] levelCompleted = new boolean[16]; // 16 niveles
    public int selectedLevel = 0; // Nivel seleccionado en el menú
    public boolean finalButtonSelected = false; // Para saber si el botón final está seleccionado

    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int gameOverState = 4;
    public final int characterState = 5;
    public final int videoState = 6;
    public final int levelSelectState = 7;
    public final int finalScreenState = 8; // NUEVA PANTALLA FINAL

    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        ui = new UI(this);
        reloj = new Reloj(this, ui);
        tileM = new TileManager(this);
        cChecker = new CollisionChecker(this);
        aSetter = new AssetSetter(this);
        eHandler = new EventHandler(this);

        videos = new VideosSwing(screenWidth, screenHeight);

        this.setLayout(null);
        videos.getFXPanel().setBounds(0, 0, screenWidth, screenHeight);
        videos.getFXPanel().setVisible(false);
        this.add(videos.getFXPanel());

        videos.loadVideo("pavon", "res/Videos/MercadoPatio.mp4");
        videos.loadVideo("monu", "res/Videos/Monumento.mp4");

        // Inicializar niveles completados (todos en false)
        for(int i = 0; i < levelCompleted.length; i++) {
            levelCompleted[i] = false;
        }
    }

    public boolean allLevelsCompleted() {
        for(boolean completed : levelCompleted) {
            if(!completed) return false;
        }
        return true;
    }

    public void showVideo(String key) {
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
        currentMap = 0;

        videoMostrado = false;
        videoMostrado2 = false;
        tieneCupon = false;

        player = new Player(this, keyH, 1);
        player2 = new Player(this, keyH, 2);

        aSetter.setObject();
        aSetter.setNPC();

        ui.gameFinished = false;
        ui.gameOver = false;
        ui.messageOn = false;
        ui.commandNum = 0;

        if(reloj != null) {
            reloj.reiniciarTiempo();
        }

        gameState = titleState;
    }

    public void completeLevel(int levelNumber) {
        if(levelNumber >= 0 && levelNumber < levelCompleted.length) {
            levelCompleted[levelNumber] = true;
            System.out.println("¡Nivel " + (levelNumber + 1) + " completado!");
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
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
            if(player != null) {
                player.update();
            }

            if(player2 != null) {
                player2.update();
            }

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
        }
        else if(gameState == levelSelectState) {
            ui.draw(g2);
        }
        else if(gameState == finalScreenState) {
            ui.draw(g2);
        }
        else if (gameState == videoState) {
            return;
        }
        else {
            // Dibujar tiles
            tileM.draw(g2);

            // Agregar entidades
            entityList.clear();

            if(player != null) {
                entityList.add(player);
            }
            if(player2 != null) {
                entityList.add(player2);
            }

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

            // Ordenar por Y
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return Integer.compare(o1.worldY, o2.worldY);
                }
            });

            // Dibujar entidades
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
                g2.setFont(new Font("Arial", Font.PLAIN, 16));

                int x = 10;
                int y = 400;

                if(player != null) {
                    g2.drawString("P1 X: " + player.worldX, x, y); y += 20;
                    g2.drawString("P1 Y: " + player.worldY, x, y); y += 20;
                }
                if(player2 != null) {
                    g2.drawString("P2 X: " + player2.worldX, x, y); y += 20;
                    g2.drawString("P2 Y: " + player2.worldY, x, y); y += 20;
                }
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
        } else if(gameState == titleState || gameState == levelSelectState || gameState == finalScreenState) {
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