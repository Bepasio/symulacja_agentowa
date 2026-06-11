package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.agents.Consumer;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.ElementType;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;

import java.util.ArrayList;
import java.util.Iterator;

public class BottleMachine implements MapElement, Interactable {
    private final int capacity = 200;
    private final ArrayList<Bottle> bottles;
    private final int maxPaperStock = 60;
    private int paperStock ;
    private Position position;
    private final double bottlePrice = 0.50;
    private int stressLevel = 0;
    private boolean isBroken = false;

    public boolean canCollectBottle() {
        return bottles.size() < capacity && paperStock > 0;
    }

    public BottleMachine(Position position) {
        this.bottles = new ArrayList<>();
        this.paperStock = maxPaperStock;
        this.position = position;
    }

    public boolean addBottle(Bottle bottle) {
        if (!bottle.isRefundable() || !canCollectBottle()) {
            return false;
        }

        bottles.add(bottle);
        return true;
    }

    public int processDeposit(Agent agent) {
        if (!canCollectBottle()) {
            return 0;
        }

        double breakChance = stressLevel * 0.0005; //szansa na awarie zalezy od stress level, ktory rosnie przy kazdym skorzystaniu z niego
        if(Math.random() < breakChance) {
            isBroken = true;
            LoggerService.getInstance().logError("AWARIA! Butelkomat na " + position.getX() + "," + position.getY() + " zaciął się z przeciążenia!");
            return 0;
        }

        int acceptedBottles = 0;
        Iterator<Bottle> iterator = agent.getBottles().iterator();

        while (iterator.hasNext()) {
            if (bottles.size() >= capacity) { //jesli butelkomat jest pelny to nie przyjmuje butelek
                break;
            }

            Bottle bottle = iterator.next();
            if (bottle.isRefundable()) {
                bottles.add(bottle);
                iterator.remove();
                acceptedBottles++; //jesli jest zwrotna to przyjmuje butelke i ITERATOR usuwa ją z eq agenta
                stressLevel++; //stress rosnie z kazda oddana butelka
            }
        }

        if (acceptedBottles > 0) {
            paperStock--;
            double payout = acceptedBottles * bottlePrice;
            agent.addMoney(payout); //agent dostaje kase za kaucje
            if(agent instanceof Consumer) {
                LoggerService.getInstance().log("Consumer-" + agent.getID() + " zarobił " + payout + " zł");
            }else{
                LoggerService.getInstance().log("Collector-" + agent.getID() + " zarobił " + payout + " zł");
            }
        }

        return acceptedBottles;
    }

    public void repair(){
        isBroken = false;
        stressLevel = 0;
    }

    public String getStatistics() {
        return "Butelkomat\n" +
                "Butelki: " + bottles.size() + "/" + capacity + "\n" +
                "Czy pelny: " + isFull() + "\n" +
                "Czy zepsuty: " + isBroken;
    }

    public ElementType getElementType() {
        return ElementType.BOTTLE_MACHINE;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void paperRefill() {
        paperStock = maxPaperStock ;
    }

    public void emptying() {
        bottles.clear();
    }

    public int getBottlesAmount() {
        return bottles.size();
    }

    public boolean isFull() {
        return bottles.size() == capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public Position getPosition() {
        return position;
    }
}