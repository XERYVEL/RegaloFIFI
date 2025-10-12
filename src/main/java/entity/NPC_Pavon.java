package entity;

import main.gamePanel;
import object.OBJ_valePorComida;
import varios.Direccion;

import java.util.Random;

public class NPC_Pavon extends Entity {

    public int dialogueIndexNPC = 0;
    public NPC_Pavon(gamePanel gp) {
        super(gp);
        direction = Direccion.Abajo;
        speed = 1;
        getImage();
        setDialogue();
        setColisionArea();
    }

    public void getImage() {
        up1 = setup("/Pavon/pavondetras1");
        up2 = setup("/Pavon/pavondetras2");
        down1 = setup("/Pavon/pavonfrente1");
        down2 = setup("/Pavon/pavonfrente2");
        left1 = setup("/Pavon/pavonizquierda1");
        left2 = setup("/Pavon/pavonizquierda2");
        right1 = setup("/Pavon/pavonderecha1");
        right2 = setup("/Pavon/pavonderecha2");
    }

    public void setDialogue() {
        dialogues[0] = "Volpin... ";
        dialogues[1] = "Hola cuanto tiempo...";
        dialogues[2] = "Me tengo que ir pero ten, \nde seguro te viene bien\n algo de comer";
        dialogues[3] = "Me voy a la galeria";
        dialogues[4] = "Buena suerte";
    }

    public void setColisionArea() {
        solidAreaDefaultX = 0;
        solidAreaDefaultY = 0;
        solidArea.width = gp.tileSize;
        solidArea.height = gp.tileSize;
    }

    public void setAction() {

        actionLockCounter++;
        if (actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            if (i <= 25) {
                direction = Direccion.Arriba;
            }
            if (i > 25 && i <= 50) {
                direction = Direccion.Abajo;
            }
            if (i > 50 && i <= 75) {
                direction = Direccion.Derecha;
            }
            if (i > 75 && i <= 100) {
                direction = Direccion.Izquierda;
            }
            actionLockCounter = 0;
        }

    }

    @Override
    public void speak() {
        if (!gp.tieneCupon) {
            gp.player.inventory.add(new OBJ_valePorComida(gp));
            gp.tieneCupon = true;
        }

        if (dialogues[dialogueIndexNPC] != null) {
            gp.ui.currentDialogue = dialogues[dialogueIndexNPC];
            dialogueIndexNPC++;
        } else {
            gp.ui.currentDialogue = "Ya te di el vale";
        }
    }
}