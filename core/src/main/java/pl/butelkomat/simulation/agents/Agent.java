package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.ElementType;

import java.lang.classfile.TypeAnnotation;
import java.util.ArrayList;

public abstract class Agent implements MapElement {
    protected Position position;
    protected int backpackCapacity;
    protected ArrayList<Bottle> bottles;
    protected Position currentTarget;

    public Agent(Position startPosition, int backpackCapacity) {
        this.position = startPosition;
        this.backpackCapacity = backpackCapacity;
        this.bottles = new ArrayList<>(); // Dajemy agentowi pusty plecak na start
        this.currentTarget = null;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

//    public enum TargetType {
//        BOTTLE,
//        TRASH
//    }

    public Position getTarget(ElementType type, WorldMap map) {
        if (type == ElementType.BOTTLE_MACHINE) {
            return map.nearestBottleMachine(position);
        }
        if (type == ElementType.TRASH_BIN) {
            return map.nearestTrashBin(position);
        }
        return null;
    }

    public void moveTowards(Position target){
        if (target == null || position.equals(target)) return;

        if (position.getX() < target.getX()) {
            position.setX(position.getX() + 1);
        }
        else if (position.getX() > target.getX()) {
            position.setX(position.getX() - 1);
        }
        else if (position.getY() < target.getY()) {
            position.setY(position.getY() + 1);
        }
        else if (position.getY() > target.getY()) {
            position.setY(position.getY() - 1);
        }
    }

    public abstract void step(boolean movePhase, WorldMap map);
//    w zaleznosci od typu zwroci nam najblizszy obiekt
//    w klasie consumer/collector bedzie metoda ktora wybierze co chce znalezc

}
