package main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class UI {

    gamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B, minecraft;

    public int commandNum = 0;
    public int titleScreenState = 0;

    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // Título principal
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 70F));
            String text = "Plataformas 2P";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;

            // Sombra
            g2.setColor(Color.gray);
            g2.drawString(text, x + 5, y + 5);

            // Título
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // Subtítulo
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 30F));
            text = "Cooperativo";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.setColor(new Color(150, 150, 255));
            g2.drawString(text, x, y);

            // Dibujar jugadores
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

            // Menú
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

            text = "Salir";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.setColor(new Color(255, 255, 0));
                g2.drawString(">", x - gp.tileSize, y);
            }

            // Controles
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

    public void drawLevelSelectScreen() {
        // Fondo negro
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Título
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        g2.setColor(Color.white);
        String text = "Selecciona un Nivel";
        int x = getXforCenteredText(text);
        g2.drawString(text, x, gp.tileSize);

        // MENSAJE OCULTO DE FONDO (se revelará con los niveles completados)
        drawHiddenMessage();

        // Dibujar grilla de niveles 4x4
        int levelSize = gp.tileSize + 16; // Tamaño de cada cuadrado de nivel
        int gridStartX = (gp.screenWidth - (levelSize * 4 + 30)) / 2; // 30 = espacios entre cuadrados
        int gridStartY = gp.tileSize * 2;

        for(int row = 0; row < 4; row++) {
            for(int col = 0; col < 4; col++) {
                int levelIndex = row * 4 + col;
                int levelX = gridStartX + col * (levelSize + 10);
                int levelY = gridStartY + row * (levelSize + 10);

                // Determinar estado del nivel
                boolean completed = gp.levelCompleted[levelIndex];
                boolean selected = (levelIndex == gp.selectedLevel);
                boolean unlocked = (levelIndex == 0) || gp.levelCompleted[levelIndex - 1]; // Desbloqueado si es el primero o si el anterior está completado

                // Dibujar cuadrado del nivel
                if(completed) {
                    // Nivel completado - MUY transparente para mostrar el mensaje
                    g2.setColor(new Color(50, 200, 50, 20));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    // Borde verde
                    g2.setColor(new Color(50, 255, 50,30));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    // Marca de completado (check dibujado)
                    g2.setColor(new Color(50, 255, 50,30));
                    g2.setStroke(new BasicStroke(4));

                    int checkCenterX = levelX + levelSize/2;
                    int checkCenterY = levelY + levelSize/2;

                    // Dibujar check con dos líneas
                    // Línea corta (parte izquierda del check)
                    g2.drawLine(checkCenterX - 10, checkCenterY + 5,
                            checkCenterX - 3, checkCenterY + 12);
                    // Línea larga (parte derecha del check)
                    g2.drawLine(checkCenterX - 3, checkCenterY + 12,
                            checkCenterX + 12, checkCenterY - 8);
                } else if(!unlocked) {
                    // Nivel bloqueado - muy oscuro y con candado
                    g2.setColor(new Color(30, 30, 30, 220));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    // Borde rojo/oscuro
                    g2.setColor(new Color(80, 40, 40));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    // Candado dibujado con formas
                    g2.setColor(new Color(150, 50, 50));
                    g2.setStroke(new BasicStroke(3));

                    int lockCenterX = levelX + levelSize/2;
                    int lockCenterY = levelY + levelSize/2;

                    // Cuerpo del candado (rectángulo)
                    g2.fillRoundRect(lockCenterX - 12, lockCenterY, 24, 20, 5, 5);

                    // Arco del candado
                    g2.drawArc(lockCenterX - 10, lockCenterY - 15, 20, 20, 0, 180);
                } else {
                    // Nivel desbloqueado pero no completado
                    g2.setColor(new Color(60, 60, 80, 180));
                    g2.fillRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);

                    // Borde normal
                    g2.setColor(new Color(100, 100, 120));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(levelX, levelY, levelSize, levelSize, 15, 15);
                }

                // Borde de selección
                if(selected) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawRoundRect(levelX - 3, levelY - 3, levelSize + 6, levelSize + 6, 15, 15);
                }

                // Número del nivel
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
                if(completed) {
                    g2.setColor(new Color(150, 150, 150, 100)); // Gris apagado y transparente
                } else if(!unlocked) {
                    g2.setColor(new Color(100, 100, 100)); // Gris para bloqueados
                } else {
                    g2.setColor(new Color(200, 200, 200)); // Blanco grisáceo para desbloqueados
                }
                String levelNum = String.valueOf(levelIndex + 1);
                int numWidth = (int)g2.getFontMetrics().getStringBounds(levelNum, g2).getWidth();
                g2.drawString(levelNum, levelX + levelSize/2 - numWidth/2, levelY + 30);
            }
        }

        // Instrucciones
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        g2.setColor(Color.WHITE);
        text = "WASD para navegar | ENTER para jugar | ESC para volver";
        x = getXforCenteredText(text);
        g2.drawString(text, x, gp.screenHeight - gp.tileSize/2);

        // Mostrar progreso
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

    private void drawHiddenMessage() {
        // El mensaje "Feliz Navidad Fifi" en 3 líneas
        String[] lines = {"Feliz", "Navidad", "Fifi"};

        // Posición centrada detrás de la grilla
        int messageX = gp.screenWidth / 2;
        int messageStartY = gp.tileSize * 3;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 120F));

        // Calcular cuántas letras se pueden ver según niveles completados
        int totalLetters = 0;
        for(String line : lines) {
            totalLetters += line.length();
        }

        int completedLevels = 0;
        for(boolean b : gp.levelCompleted) {
            if(b) completedLevels++;
        }

        // Cada nivel revela aproximadamente 1 letra (16 niveles, ~17 letras total)
        float revealPercentage = (float)completedLevels / 16.0f;
        int lettersToReveal = (int)(totalLetters * revealPercentage);

        int letterCount = 0;
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineY = messageStartY + i * 140;

            // Centrar cada línea
            int lineWidth = (int)g2.getFontMetrics().getStringBounds(line, g2).getWidth();
            int lineX = messageX - lineWidth / 2;

            // Dibujar cada letra
            for(int j = 0; j < line.length(); j++) {
                char letter = line.charAt(j);

                // Determinar opacidad según si está revelada
                if(letterCount < lettersToReveal) {
                    // Revelada - Color brillante con efecto de resplandor
                    // Primero dibujar sombra/resplandor
                    g2.setColor(new Color(255, 100, 255, 100)); // Rosa brillante para resplandor
                    String letterStr = String.valueOf(letter);
                    int letterWidth = (int)g2.getFontMetrics().getStringBounds(letterStr, g2).getWidth();
                    g2.drawString(letterStr, lineX + 3, lineY + 3);

                    // Luego dibujar la letra principal
                    g2.setColor(new Color(255, 50, 255, 255)); // Rosa/magenta brillante
                    g2.drawString(letterStr, lineX, lineY);
                    lineX += letterWidth;
                } else {
                    // Oculta
                    g2.setColor(new Color(30, 30, 30, 60)); // Muy oscuro y transparente
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

        // PANTALLA DE TÍTULO
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
            return;
        }

        // PANTALLA DE SELECCIÓN DE NIVELES
        if (gp.gameState == gp.levelSelectState) {
            drawLevelSelectScreen();
            return;
        }

        // JUGANDO
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

        // DIÁLOGO
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }

        // GAME OVER
        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }

        // INVENTARIO
        if (gp.gameState == gp.characterState) {
            drawInventory();
        }

        // MENSAJES
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

        // VICTORIA
        if (gameFinished) {
            // Marcar el nivel actual como completado
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