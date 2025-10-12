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
        public final int maxScreenCol = 16;
        public final int maxScreenRow = 12;
        public final int screenWidth = tileSize * maxScreenCol;
        public final int screenHeight = tileSize * maxScreenRow;
        public boolean videoMostrado = false;
        public boolean videoMostrado2 = false;
        public boolean tieneCupon = false;

        public final int maxWorldCol = 50;
        public final int maxWorldRow = 50;
        public final int maxMap =10;
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

        public Player player = new Player(this, keyH);
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

        this.add(videos.getFXPanel());
        videos.getFXPanel().setVisible(false);
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
                player.update();

                for (int i = 0; i < npc[1].length; i++) {
                    if (npc[currentMap][i] != null) {
                        npc[currentMap][i].update();
                    }
                }

                eHandler.checkEvent();

                reloj.actualizarTiempo();
                reloj.derrota();
            }

        checkMusic();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 =(Graphics2D)g;

        long drawStart = 0;
        if (keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        tileM.draw(g2);

        if(gameState == titleState) {
            ui.draw(g2);
        }
        if (gameState == videoState) {
            return;
        }

        else {
            tileM.draw(g2);
        }

        player.draw(g2);
        ui.draw(g2);

        entityList.add(player);

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

        Collections.sort(entityList, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return Integer.compare(o1.worldY, o2.worldY);
            }
        });

        for (Entity e : entityList) {
            e.draw(g2);
        }

        entityList.clear();

        ui.draw(g2);

        if (keyH.checkDrawTime == true) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial",Font.PLAIN,20));

            int x = 10;
            int y = 400;

            g2.drawString("World X: " + player.worldX, x, y); y+= 22;
            g2.drawString("World Y: " + player.worldY, x, y); y+= 22;
            g2.drawString("Col: " + (player.worldX + player.solidArea.x) / tileSize, x, y); y+= 22;
            g2.drawString("Row: " + (player.worldY + player.solidArea.y) / tileSize, x, y); y+= 22;
            g2.drawString("Draw time: " + passed, x, y);
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
        }
        else {
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
