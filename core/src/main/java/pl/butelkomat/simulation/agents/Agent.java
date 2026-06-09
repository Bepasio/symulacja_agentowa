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
    protected ArrayList<Bottle> bottles;
    protected Position currentTarget; //to jest okreslony cel podrozy, np butelkomat; przekazujemy z kazdym tickiem wywolujac moveTowards
    protected ArrayList<Position> visitedTargets;
    protected int id;
    protected double balance;
    protected List<Position> currentPath = null;
    protected Position pathTarget = null; //to jest cel ktory zapisujemy, zeby nie wyszukiwac za kazdym razem sciezki do celu; jesli to sie rozni od targetu przekazanego w moveTowards, to wtedy zmieniamy ten cel

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

    public void moveTowards(Position target, WorldMap map){
        if (target == null || position.equals(target)) return; //jesli nie przekazano celu albo agent juz tam jest, to przerywamy

        if(pathTarget == null || !pathTarget.equals(target)){ //jesli target zmienil sie od poprzedniego ticka, to czyscimy stary path i zapamietujemy nowy cel
            currentPath = null;
            pathTarget = target;
        };

        if(currentPath == null){
            currentPath = map.pathFinder(position, target); //jesli nie ma sciezki, to obliczamy nową (1 cel - 1 sciezka)
        }

        if(currentPath == null){ //jesli null to znaczy ze nie da sie dojsc
            visitedTargets.add(target); //czarna lista bo punkt nieosioagalny
            currentTarget = null;
            pathTarget = null;
            return;
        }

        if(!currentPath.isEmpty()){ //jesli w sciezce mamy punkty
            Position nextStep = currentPath.remove(0); //pobieramy pierwszy z brzegu i ustawiamy na niego agenta (1 tick - 1 ruch)
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

    public ArrayList<Bottle> getBottles() {
        return bottles;
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
