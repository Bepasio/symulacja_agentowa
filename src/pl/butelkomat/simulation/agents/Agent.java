package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.Position;

import java.util.ArrayList;

abstract class Agent {
//    currentposition
//    metoda step zeby wymuszala posiadanie przez podklasy step()

    protected Position position; //Position przechowuje informacje o pozycji, wylicza gdzie jest najblizszy kosz/automat
    protected int backpackCapacity;
    protected ArrayList<Bottle> bottles;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
