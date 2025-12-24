package object;

import entity.Entity;
import main.gamePanel;

public class OBJ_gemaRoja extends Entity {


    public OBJ_gemaRoja(gamePanel gp) {
        super(gp);
        name = "Gema Roja";
        descripcion = "[" + name + "] \n gema de color rojo que ayuda al portador abrir puertas";
        down1 = setup("/Objects/gemaRoja");
    }
}