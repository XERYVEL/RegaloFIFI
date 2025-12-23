package main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UI {

    gamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B, minecraft;

    public int commandNum = 0;
    public int titleScreenState = 0;

    // Variables para input de nombre
    public String nombreInput = "";
    public int maxNombreLength = 15;

    // Variables para selección de partida guardada
    public int selectedSaveSlot = 0;
    public List<SaveSystem.SaveData> partidasGuardadas;

    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 70F));
            String text = "Plataformas 2P";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;

            g2.setColor(Color.gray);
            g2.drawString(text, x + 5, y + 5);

            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 30F));
            text = "Cooperativo";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.setColor(new Color(150, 150, 255));
            g2.drawString(text, x, y);

            y += gp.tileSize * 2;
            if (gp.player != null && gp.player.down1 != null) {
                x = gp.screenWidth / 2 - (gp.tileSize * 3);

                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
                g2.setColor(new Color(0, 150, 255));
                g2.drawString("P1", x + 20, y - 10);

                g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
            }

            if (gp.player2 != null && gp.player2.down1 != null) {
                x = gp.screenWidth / 2 + gp.tileSize;

                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
                g2.setColor(new Color(255, 50, 50));
                g2.drawString("P2", x + 20, y - 10);

                g2.drawImage(gp.player2.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
            }

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            text = "Nuevo Juego";
            x = getXforCenteredText(text);
            y += gp.tileSize * 4;
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.setColor(new Color(255, 255, 0));
                g2.drawString(">", x - gp.tileSize, y);
            }

            // NUEVA OPCIÓN: Continuar
            text = "Continuar";
            x = getXforCenteredText(text);
            y += gp.tileSize;

            // Si no hay partidas guardadas, mostrar en gris
            if (!gp.saveSystem.hayPartidasGuardadas()) {
                g2.setColor(new Color(100, 100, 100));
            } else {
                g2.setColor(Color.white);
            }
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.setColor(new Color(255, 255, 0));
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Salir";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.setColor(new Color(255, 255, 0));
                g2.drawString(">", x - gp.tileSize, y);
            }

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
            y += gp.tileSize * 2;

            g2.setColor(new Color(0, 150, 255));
            text = "P1: W (Saltar) A/D (Mover)";
            x = getXforCenteredText(text);
            g2.drawString(text, x, y);

            y += 30;
            g2.setColor(new Color(255, 50, 50));
            text = "P2: ↑ (Saltar) ←/→ (Mover)";
            x = getXforCenteredText(text);
            g2.drawString(text, x, y);

            y += 50;
            g2.setColor(new Color(150, 255, 150));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
            text = "¡Llega a la cima del mapa!";
            x = getXforCenteredText(text);
            g2.drawString(text, x, y);
        }
    }

    // NUEVA FUNCIÓN: Pantalla de ingreso de nombre
    public void drawNameInputScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 56F));
        g2.setColor(Color.white);
        String text = "Ingresa tu nombre";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        g2.drawString(text, x, y);

        // Cuadro de texto para el nombre
        y += gp.tileSize * 2;
        int boxWidth = 500;
        int boxHeight = 80;
        int boxX = gp.screenWidth / 2 - boxWidth / 2;
        int boxY = y - 60;

        g2.setColor(new Color(50, 50, 50));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        g2.setColor(new Color(255, 255, 100));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        // Mostrar nombre ingresado
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 42F));
        g2.setColor(Color.white);
        String displayText = nombreInput.isEmpty() ? "_" : nombreInput + "_";
        int textWidth = (int)g2.getFontMetrics().getStringBounds(displayText, g2).getWidth();
        g2.drawString(displayText, boxX + boxWidth/2 - textWidth/2, y);

        // Instrucciones
        y += gp.tileSize * 2;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        g2.setColor(new Color(200, 200, 200));
        text = "Usa el teclado para escribir tu nombre";
        x = getXforCenteredText(text);
        g2.drawString(text, x, y);

        y += 40;
        text = "Presiona ENTER para continuar";
        x = getXforCenteredText(text);
        g2.setColor(new Color(100, 255, 100));
        g2.drawString(text, x, y);

        y += 40;
        text = "ESC para volver";
        x = getXforCenteredText(text);
        g2.setColor(new Color(255, 100, 100));
        g2.drawString(text, x, y);

        // Contador de caracteres
        y += 60;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        g2.setColor(new Color(150, 150, 150));
        text = nombreInput.length() + "/" + maxNombreLength + " caracteres";
        x = getXforCenteredText(text);
        g2.drawString(text, x, y);
    }

    // NUEVA FUNCIÓN: Pantalla de carga de partidas
    public void drawLoadGameScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 56F));
        g2.setColor(Color.white);
        String text = "Selecciona una Partida";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 2;
        g2.drawString(text, x, y);

        // Cargar partidas guardadas
        if (partidasGuardadas == null) {
            partidasGuardadas = gp.saveSystem.cargarTodasLasPartidas();
        }

        if (partidasGuardadas.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
            g2.setColor(new Color(255, 100, 100));
            text = "No hay partidas guardadas";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);

            y += 60;
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
            g2.setColor(Color.white);
            text = "Presiona ESC para volver";
            x = getXforCenteredText(text);
            g2.drawString(text, x, y);
            return;
        }

        y += gp.tileSize;
        int slotHeight = 100;
        int slotWidth = 700;
        int startY = y;

        for (int i = 0; i < partidasGuardadas.size(); i++) {
            SaveSystem.SaveData save = partidasGuardadas.get(i);

            int slotX = gp.screenWidth / 2 - slotWidth / 2;
            int slotY = startY + (i * (slotHeight + 20));

            // Fondo del slot
            if (i == selectedSaveSlot) {
                g2.setColor(new Color(100, 100, 150, 200));
            } else {
                g2.setColor(new Color(50, 50, 50, 180));
            }
            g2.fillRoundRect(slotX, slotY, slotWidth, slotHeight, 15, 15);

            // Borde
            if (i == selectedSaveSlot) {
                g2.setColor(new Color(255, 255, 100));
                g2.setStroke(new BasicStroke(4));
            } else {
                g2.setColor(new Color(100, 100, 100));
                g2.setStroke(new BasicStroke(2));
            }
            g2.drawRoundRect(slotX, slotY, slotWidth, slotHeight, 15, 15);

            // Información de la partida
            int textX = slotX + 20;
            int textY = slotY + 35;

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
            g2.setColor(Color.white);
            g2.drawString(save.nombreJugador, textX, textY);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
            textY += 30;
            g2.setColor(new Color(200, 200, 200));

            int completados = 0;
            for (boolean b : save.nivelesCompletados) {
                if (b) completados++;
            }

            g2.drawString("Nivel " + (save.nivelActual + 1) + " | Completados: " + completados + "/16", textX, textY);

            textY += 25;
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18F));
            g2.setColor(new Color(150, 150, 150));
            g2.drawString("Última vez: " + save.fechaGuardado, textX, textY);

            // Selector
            if (i == selectedSaveSlot) {
                g2.setColor(new Color(255, 255, 100));
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
                g2.drawString(">", slotX - 40, slotY + slotHeight/2 + 10);
            }
        }

        // Instrucciones
        y = gp.screenHeight - gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        g2.setColor(Color.white);
        text = "W/S: Seleccionar | ENTER: Cargar | DELETE: Eliminar | ESC: Volver";
        x = getXforCenteredText(text);
        g2.drawString(text, x, y);
    }

    public void drawLevelSelectScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Mostrar nombre del jugador arriba
        if (!gp.nombreJugadorActual.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
            g2.setColor(new Color(100, 255, 100));
            String playerName = "Jugador: " + gp.nombreJugadorActual;
            int nameX = gp.tileSize / 2;
            g2.drawString(playerName, nameX, 35);
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        g2.setColor(Color.white);
        String text = "Selecciona un Nivel";
        int x = getXforCenteredText(text);
        g2.drawString(text, x, gp.tileSize);

        drawHiddenMessage();

        int levelSize = gp.tileSize + 16;
        int gridStartX = (gp.screenWidth - (levelSize * 4 + 30)) / 2;
        int gridStartY = gp.tileSize * 2;

        for(int row = 0; row < 4; row++) {
            for(int col = 0; col < 4; col++) {
                int levelIndex = row * 4 + col;
                int levelX = gridStartX + col * (levelSize + 10);
                int levelY = gridStartY + row * (levelSize + 10);

                boolean completed = gp.levelCompleted[levelIndex];
                boolean selected = (levelIndex == gp.selectedLevel);
                boolean unlocked = (levelIndex == 0) || gp.levelCompleted[levelIndex - 1];

                if(completed) {
                    g2.setColor(new Color(50, 200, 50, 20));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    g2.setColor(new Color(50, 255, 50, 30));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    g2.setColor(new Color(50, 255, 50, 30));
                    g2.setStroke(new BasicStroke(4));

                    int checkCenterX = levelX + levelSize/2;
                    int checkCenterY = levelY + levelSize/2;

                    g2.drawLine(checkCenterX - 10, checkCenterY + 5,
                            checkCenterX - 3, checkCenterY + 12);
                    g2.drawLine(checkCenterX - 3, checkCenterY + 12,
                            checkCenterX + 12, checkCenterY - 8);
                } else if(!unlocked) {
                    g2.setColor(new Color(30, 30, 30, 220));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    g2.setColor(new Color(80, 40, 40));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    g2.setColor(new Color(150, 50, 50));
                    g2.setStroke(new BasicStroke(3));

                    int lockCenterX = levelX + levelSize/2;
                    int lockCenterY = levelY + levelSize/2;

                    g2.fillRoundRect(lockCenterX - 12, lockCenterY, 24, 20, 5, 5);
                    g2.drawArc(lockCenterX - 10, lockCenterY - 15, 20, 20, 0, 180);
                } else {
                    g2.setColor(new Color(60, 60, 80, 180));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    g2.setColor(new Color(100, 100, 120));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);
                }

                if(selected && !gp.finalButtonSelected) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawRoundRect(levelX - 3, levelY - 3, levelSize + 6, levelSize + 6, 15, 15);
                }

                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
                if(completed) {
                    g2.setColor(new Color(150, 150, 150, 100));
                } else if(!unlocked) {
                    g2.setColor(new Color(100, 100, 100));
                } else {
                    g2.setColor(new Color(200, 200, 200));
                }
                String levelNum = String.valueOf(levelIndex + 1);
                int numWidth = (int)g2.getFontMetrics().getStringBounds(levelNum, g2).getWidth();
                g2.drawString(levelNum, levelX + levelSize/2 - numWidth/2, levelY + 30);
            }
        }

        int finalButtonY = gridStartY + 4 * (levelSize + 10) + 20;
        int finalButtonWidth = levelSize * 2 + 10;
        int finalButtonHeight = levelSize;
        int finalButtonX = gp.screenWidth / 2 - finalButtonWidth / 2;

        boolean allCompleted = gp.allLevelsCompleted();

        if(allCompleted) {
            g2.setColor(new Color(255, 215, 0, 200));
            g2.fillRoundRect(finalButtonX, finalButtonY, finalButtonWidth, finalButtonHeight, 15, 15);

            g2.setColor(new Color(255, 255, 100));
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(finalButtonX, finalButtonY, finalButtonWidth, finalButtonHeight, 15, 15);
        } else {
            g2.setColor(new Color(30, 30, 30, 220));
            g2.fillRoundRect(finalButtonX, finalButtonY, finalButtonWidth, finalButtonHeight, 15, 15);

            g2.setColor(new Color(80, 40, 40));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(finalButtonX, finalButtonY, finalButtonWidth, finalButtonHeight, 15, 15);

            g2.setColor(new Color(150, 50, 50));
            int lockX = finalButtonX + finalButtonWidth / 2;
            int lockY = finalButtonY + finalButtonHeight / 2;
            g2.fillRoundRect(lockX - 12, lockY, 24, 20, 5, 5);
            g2.drawArc(lockX - 10, lockY - 15, 20, 20, 0, 180);
        }

        if(gp.finalButtonSelected) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(5));
            g2.drawRoundRect(finalButtonX - 3, finalButtonY - 3,
                    finalButtonWidth + 6, finalButtonHeight + 6, 15, 15);
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
        if(allCompleted) {
            g2.setColor(new Color(255, 255, 255));
            text = "¡MENSAJE ESPECIAL!";
        } else {
            g2.setColor(new Color(100, 100, 100));
            text = "???";
        }
        int textWidth = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        g2.drawString(text, finalButtonX + finalButtonWidth/2 - textWidth/2,
                finalButtonY + finalButtonHeight/2 + 12);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        g2.setColor(Color.WHITE);
        text = "WASD para navegar | ENTER para jugar | ESC para volver";
        x = getXforCenteredText(text);
        g2.drawString(text, x, gp.screenHeight - gp.tileSize/2);

        int completedCount = 0;
        for(boolean b : gp.levelCompleted) {
            if(b) completedCount++;
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.setColor(new Color(255, 215, 0));
        text = "Completados: " + completedCount + "/16";
        x = getXforCenteredText(text);
        g2.drawString(text, x, gp.screenHeight - gp.tileSize - 20);
    }

    public void drawFinalScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        g2.setColor(new Color(255, 215, 0));

        String text = "¡FELICITACIONES!";
        int x = getXforCenteredText(text);
        int y = gp.tileSize;

        g2.setColor(new Color(100, 50, 0));
        g2.drawString(text, x + 3, y + 3);

        g2.setColor(new Color(255, 215, 0));
        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        g2.setColor(new Color(255, 255, 255));
        text = "Completaste todos los niveles";
        x = getXforCenteredText(text);
        y += 80;
        g2.drawString(text, x, y);

        // Mensaje principal de amor
        y += 60;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        g2.setColor(new Color(255, 100, 150));

        String[] mensajeLineas = {
                "Amor,",
                "",
                "Solo quería decirte que te quiero un montón.",
                "Me encanta cómo la pasamos juntos, cómo todo",
                "se vuelve más lindo y más divertido cuando estás cerca.",
                "",
                "Amo tu forma de ser y, sí, no puedo evitar decirlo:",
                "me encanta cómo te sonrojás cuando te ponés nerviosa,",
                "es demasiado tierna.",
                "",
                "Ojalá que este regalito te guste.",
                "",
                "Gracias por ser vos y por hacer mis días",
                "mucho mejores.",
                "",
                "Te quiero mucho",
                "Julian"
        };

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22F));
        int lineHeight = 30;

        for(int i = 0; i < mensajeLineas.length; i++) {
            String linea = mensajeLineas[i];

            if(linea.equals("Amor,")) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));
                g2.setColor(new Color(255, 150, 200));
            } else if(linea.equals("Te quiero ❤️")) {
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26F));
                g2.setColor(new Color(255, 100, 150));
            } else if(linea.isEmpty()) {
                y += lineHeight / 2;
                continue;
            } else {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
                g2.setColor(new Color(255, 255, 255));
            }

            int lineWidth = (int)g2.getFontMetrics().getStringBounds(linea, g2).getWidth();
            x = gp.screenWidth / 2 - lineWidth / 2;
            g2.drawString(linea, x, y);
            y += lineHeight;
        }

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18F));
        g2.setColor(new Color(150, 150, 150));
        text = "Presiona ESC o ENTER para volver";
        x = getXforCenteredText(text);
        g2.drawString(text, x, gp.screenHeight - 30);
    }

    private void drawHiddenMessage() {
        String[] lines = {"Feliz", "Navidad", "Fifi"};

        int messageX = gp.screenWidth / 2;
        int messageStartY = gp.tileSize * 3;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 120F));

        int totalLetters = 0;
        for(String line : lines) {
            totalLetters += line.length();
        }

        int completedLevels = 0;
        for(boolean b : gp.levelCompleted) {
            if(b) completedLevels++;
        }

        float revealPercentage = (float)completedLevels / 16.0f;
        int lettersToReveal = (int)(totalLetters * revealPercentage);

        int letterCount = 0;
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineY = messageStartY + i * 140;

            int lineWidth = (int)g2.getFontMetrics().getStringBounds(line, g2).getWidth();
            int lineX = messageX - lineWidth / 2;

            for(int j = 0; j < line.length(); j++) {
                char letter = line.charAt(j);

                if(letterCount < lettersToReveal) {
                    g2.setColor(new Color(255, 100, 255, 100));
                    String letterStr = String.valueOf(letter);
                    int letterWidth = (int)g2.getFontMetrics().getStringBounds(letterStr, g2).getWidth();
                    g2.drawString(letterStr, lineX + 3, lineY + 3);

                    g2.setColor(new Color(255, 50, 255, 255));
                    g2.drawString(letterStr, lineX, lineY);
                    lineX += letterWidth;
                } else {
                    g2.setColor(new Color(30, 30, 30, 60));
                    String letterStr = String.valueOf(letter);
                    int letterWidth = (int)g2.getFontMetrics().getStringBounds(letterStr, g2).getWidth();
                    g2.drawString(letterStr, lineX, lineY);
                    lineX += letterWidth;
                }
                letterCount++;
            }
        }
    }

    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    public boolean gameFinished = false;

    public String currentDialogue = "";
    public boolean gameOver = false;

    public int slotCol = 0;
    public int slotRow = 0;

    public UI(gamePanel gp) {
        this.gp = gp;

        try {
            InputStream is = getClass().getResourceAsStream("/Fonts/Minecraft.ttf");
            if (is != null) {
                minecraft = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                minecraft = new Font("Arial", Font.PLAIN, 32);
            }
        } catch (FontFormatException | IOException e) {
            minecraft = new Font("Arial", Font.PLAIN, 32);
        }

        arial_40 = new Font("Arial", Font.PLAIN, 20);
        arial_80B = new Font("Arial", Font.BOLD, 40);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);

        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
            return;
        }

        if (gp.gameState == gp.nameInputState) {
            drawNameInputScreen();
            return;
        }

        if (gp.gameState == gp.loadGameState) {
            drawLoadGameScreen();
            return;
        }

        if (gp.gameState == gp.levelSelectState) {
            drawLevelSelectScreen();
            return;
        }

        if (gp.gameState == gp.finalScreenState) {
            drawFinalScreen();
            return;
        }

        if (gp.gameState == gp.playState) {
            if (gp.reloj != null) {
                gp.reloj.actualizarTiempo();

                int min = gp.reloj.getMinutos();
                int seg = gp.reloj.getSegundos();
                int ms = gp.reloj.getMilisegundos();

                String tiempoTexto = String.format("%02d:%02d:%03d", min, seg, ms);
                drawPlayScreen(tiempoTexto);
            }
        }

        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }

        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }

        if (gp.gameState == gp.characterState) {
            drawInventory();
        }

        if (messageOn) {
            g2.setFont(g2.getFont().deriveFont(30F));
            g2.setColor(Color.YELLOW);
            g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);

            messageCounter++;
            if (messageCounter > 120) {
                messageCounter = 0;
                messageOn = false;
            }
        }

        if (gameFinished) {
            gp.completeLevel(gp.selectedLevel);

            g2.setColor(Color.black);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(arial_80B);
            g2.setColor(Color.YELLOW);

            int min = gp.reloj.getMinutos();
            int seg = gp.reloj.getSegundos();
            int ms = gp.reloj.getMilisegundos();
            String tiempoTexto = String.format("%02d:%02d:%03d", min, seg, ms);

            int y = gp.screenHeight / 2;

            String text = "¡Nivel " + (gp.selectedLevel + 1) + " Completado!";
            int x = getXforCenteredText(text);
            y = gp.screenHeight / 2 - (gp.tileSize * 2);
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 36F));
            text = "Tu tiempo: " + tiempoTexto;
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
            g2.setColor(Color.WHITE);
            text = "Presiona ESC para volver al menú";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
        }
    }

    public void drawDialogueScreen() {
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 5;
        drawSubWindow(x, y, width, height);
        g2.setFont(minecraft);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x += gp.tileSize;
        y += gp.tileSize;

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public void drawPlayScreen(String tiempoTexto) {
        g2.setFont(arial_40);
        g2.setColor(Color.WHITE);
        g2.drawString("Nivel " + (gp.selectedLevel + 1), gp.tileSize / 2, 30);
        g2.drawString("Time: " + tiempoTexto, gp.screenWidth - 250, 30);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
        g2.drawString("ESC: Menú", gp.tileSize / 2, 55);
    }

    public void drawInventory() {
        int frameX = gp.tileSize * 9;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 6;
        int frameHeight = gp.tileSize * 5;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        for (int i = 0; i < gp.player.inventory.size(); i++) {
            if (gp.player.inventory.get(i).down1 != null) {
                g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);
            }

            slotX += slotSize;

            if (i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        int cursorX = slotXstart + (slotSize * slotCol);
        int cursorY = slotYstart + (slotSize * slotRow);
        int cursorWidth = gp.tileSize;
        int cursorHeight = gp.tileSize;

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

        int dframeX = frameX;
        int dframeY = frameY + frameHeight;
        int dframeWidth = frameWidth;
        int dframeHeight = gp.tileSize * 3;
        drawSubWindow(dframeX, dframeY, dframeWidth, dframeHeight);

        int textX = dframeX + 20;
        int textY = dframeY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(20F));

        int itemIndex = getItemIndexOnSlot();

        if (itemIndex < gp.player.inventory.size()) {
            for (String line : gp.player.inventory.get(itemIndex).descripcion.split("\n")) {
                g2.drawString(line, textX, textY);
                textY += 32;
            }
        }
    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }

    public void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 90f));

        text = "Game Over";
        g2.setColor(Color.black);
        x = getXforCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        g2.setFont(g2.getFont().deriveFont(30f));

        text = "Reintentar";
        x = getXforCenteredText(text);
        y += 60;
        g2.drawString(text, x, y);

        if (commandNum == 0) {
            g2.drawString(">", x - 40, y);
        }

        text = "Menú de Niveles";
        x = getXforCenteredText(text);
        y += 50;
        g2.drawString(text, x, y);

        if (commandNum == 1) {
            g2.drawString(">", x - 40, y);
        }
    }

    public int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
}