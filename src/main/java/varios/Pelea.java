package varios;

import entity.NPC_Panadera;
import main.gamePanel;

public class Pelea {

    gamePanel gp;
    int numDados = 1;
    byte numCaras = 6;

    public Pelea(gamePanel gp){
        this.gp = gp;
    }

    public void iniciarCombate(NPC_Panadera npcPan){

        System.out.println("Debug -> tienePan? " + npcPan.tienePan);
        if (!npcPan.tienePan) {
            gp.gameState = gp.gameOverState;
            System.out.println("No tenías pan de ajo, perdiste.");
            return;
        }

        int tirada = new Dados(numCaras, numDados).tirarDados()[0];

        if (tirada <= 4){
            gp.reloj.agregarTiempo(180);
            System.out.println("salio 1/2/3/4 Se agregaron 3 minutos");
            gp.gameState = gp.dialogueState;
            gp.ui.currentDialogue = "\"Ah! Esta bien! \n Te dire todo lo que se. \n Hay una nave secuestrando gente,\n te sugiero ir al planetario\"";
        } else {
            gp.gameState = gp.dialogueState;
            System.out.println("salio 5/6 no se agrega nada");
            gp.ui.currentDialogue = "\"Bueno, solo por que me das pena\n escuche que hay una nave secuestrando\ngente. Te sugiero ir al planetario\"";
        }
    }
}
