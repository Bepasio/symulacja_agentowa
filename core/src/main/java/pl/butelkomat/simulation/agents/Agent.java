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
import java.util.List;

public abstract class Agent implements MapElement {
    protected Position position;
    protected int backpackCapacity;
    public ArrayList<Bottle> bottles;
    protected Position currentTarget;
    protected ArrayList<Position> visitedTargets;
    protected int id;
    protected double balance;
    protected List<Position> currentPath = null;
    protected Position pathTarget = null;

    public Agent(Position startPosition, int backpackCapacity, int id) {
        this.position = startPosition;
        this.backpackCapacity = backpackCapacity;
        this.bottles = new ArrayList<>(); // Dajemy agentowi pusty plecak na start
        this.currentTarget = null;
        this.visitedTargets = new ArrayList<>();
        this.id = id;
        this.balance = 0.0;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getTarget(ElementType type, WorldMap map) {
        if (type == ElementType.BOTTLE_MACHINE) {
            return map.nearestBottleMachine(position, visitedTargets);
        }
        if (type == ElementType.TRASH_BIN) {
            return map.nearestTrashBin(position, visitedTargets);
        }
        return null;
    }

    public void moveTowards(Position target, WorldMap map){
        if (target == null || position.equals(target)) return;

        if(pathTarget == null || !pathTarget.equals(target)){
            currentPath = null;
            pathTarget = target;
        };

        if(currentPath == null){
            currentPath = map.pathFinder(position, target);
        }

        if(currentPath == null){
            visitedTargets.add(target);
            currentTarget = null;
            pathTarget = null;
            return;
        }

        if(!currentPath.isEmpty()){
            Position nextStep = currentPath.remove(0);
            position.setX(nextStep.getX());
            position.setY(nextStep.getY());
        }
    }

    public double getBalance() {
        return balance;
    }

    public int getCapacity(){
        return backpackCapacity;
    }

    public void addMoney(double amount) {
        this.balance += amount;
    }

    public int getBottlesAmount() {
        return bottles.size();
    }

    public int getID(){return id;}

    public abstract void step(boolean movePhase, WorldMap map);
}
