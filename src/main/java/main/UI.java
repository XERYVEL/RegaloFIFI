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

    public void drawTitleScreen (){
        if(titleScreenState == 0){
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0,0, gp.screenWidth, gp.screenHeight);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60F));
            String text = "La busqueda de Peivon";
            int x = getXforCenteredText(text);
            int y = gp.tileSize*3;

            g2.setColor(Color.gray);
            g2.drawString(text, x+5, y+5);

            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            x = gp.screenWidth/2 - (gp.tileSize*2)/2;
            y += gp.tileSize*2;
            g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            text = "Nuevo Juego";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3.5;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Salir";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
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
            minecraft = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

        if(gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        if(gp.gameState == gp.playState){
            gp.reloj.actualizarTiempo();

            int min = gp.reloj.getMinutos();
            int seg = gp.reloj.getSegundos();
            int ms = gp.reloj.getMilisegundos();

            String tiempoTexto = String.format("%2d:%2d:%03d", min, seg, ms);
            drawPlayScreen(tiempoTexto);
        }

        if(gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }

        if(gp.gameState == gp.gameOverState){
            drawGameOverScreen();
        }

        if(gp.gameState == gp.characterState) {
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
            g2.setColor(Color.black);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setFont(arial_80B);
            g2.setColor(Color.YELLOW);

            int min = gp.reloj.getMinutos();
            int seg = gp.reloj.getSegundos();
            int ms = gp.reloj.getMilisegundos();
            String tiempoTexto = String.format("%2d:%2d:%03d", min, seg, ms);

            int y = gp.screenHeight / 2;

            String text = "Tu tiempo es: " + tiempoTexto;
            int x = getXforCenteredText(text);
            y = gp.screenHeight / 2 - (gp.tileSize * 3);
            g2.drawString(text, x, y);

            text = "¡Continuará!";
            x = getXforCenteredText(text);
            y = gp.screenHeight / 2;
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
            g2.setColor(Color.WHITE);

            text = "Hecho por: Juli, Lupe, Ailu y Fifi.";
            x = getXforCenteredText(text);
            y = gp.screenHeight / 2 + (gp.tileSize * 4);
            g2.drawString(text, x, y);
        }


    }
    public void drawDialogueScreen(){
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height= gp.tileSize * 5;
        drawSubWindow(x, y, width, height);
        g2.setFont(minecraft);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
        x += gp.tileSize;
        y += gp.tileSize;

        for(String line : currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
        }

    }

    public void drawSubWindow(int x, int y, int width, int heigth){
        Color c = new Color( 0, 0 , 0,210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, heigth, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, heigth-10, 25, 25);
    }

    public void drawPlayScreen(String tiempoTexto){
        g2.drawString("Time: " + tiempoTexto, gp.tileSize * 12, 65);
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

        for(int i =0; i < gp.player.inventory.size();i++) {

            g2.drawImage(gp.player.inventory.get(i).down1,slotX,slotY, null);

            slotX += slotSize;

            if(i == 4 || i == 9 || i == 14){
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
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10 ,10);

        int dframeX = frameX;
        int dframeY = frameY + frameHeight;
        int dframeWidth = frameWidth;
        int dframeHeight = gp.tileSize * 3;
        drawSubWindow(dframeX, dframeY, dframeWidth, dframeHeight);

        int textX = dframeX + 20;
        int textY = dframeY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(20F));

        int itemIndex = getItemIndexOnSlot();

        if(itemIndex < gp.player.inventory.size()){

           for(String line: gp.player.inventory.get(itemIndex).descripcion.split("\n")) {
                g2.drawString(line, textX, textY);
               textY += 32;
           }
        }

    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotRow*5);
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
        x =getXforCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text,x,y);

        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y -4);

        g2.setFont(g2.getFont().deriveFont(30f));

        text = "Salir";
        x = getXforCenteredText(text);
        y += 60;
        g2.drawString(text, x, y);

        if(commandNum == 0){
            g2.drawString(">", x-40, y);
        }

    }

    public int getXforCenteredText(String text) {
        int lenght = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - lenght/2;
        return x;
    }
}