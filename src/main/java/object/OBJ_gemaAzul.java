package object;

import entity.Entity;
import main.gamePanel;

public class OBJ_gemaAzul extends Entity {


    public OBJ_gemaAzul(gamePanel gp) {
        super(gp);
        name = "Gema Azul";
        descripcion = "[" + name + "] \n gema de color azul que ayuda al portador abrir puertas";
        down1 = setup("/Objects/gemaAzul");
    }
}