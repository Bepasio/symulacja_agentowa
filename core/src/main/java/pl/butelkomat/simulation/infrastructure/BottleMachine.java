package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;

import java.util.ArrayList;
import java.util.Iterator;

public class BottleMachine implements MapElement {
    private final int capacity;
    private final ArrayList<Bottle> bottles;
    private int paperStock;
    private Position position;

    public boolean canCollectBottle() {
        return bottles.size() < capacity && paperStock > 0;
    }

    public BottleMachine(int capacity, int paperStock, Position position) {
        this.capacity = capacity;
        this.bottles = new ArrayList<>();
        this.paperStock = paperStock;
        this.position = position;
    }

    public boolean addBottle(Bottle bottle) {
        if (!bottle.isRefundable() || !canCollectBottle()) {
            return false;
        }

        bottles.add(bottle);
        return true;
    }

    public int processDeposit(ArrayList<Bottle> agentBottles) {
        if (!canCollectBottle()) {
            return 0;
        }

        int acceptedBottles = 0;
        Iterator<Bottle> iterator = agentBottles.iterator();

        while (iterator.hasNext()) {
            if (bottles.size() >= capacity) {
                break;
            }

            Bottle bottle = iterator.next();
            if (bottle.isRefundable()) {
                bottles.add(bottle);
                iterator.remove();
                acceptedBottles++;
            }
        }

        if (acceptedBottles > 0) {
            paperStock--;
        }

        return acceptedBottles;
    }

    public void paperRefill(int refillAmount) {
        paperStock = refillAmount;
    }

    public void emptying() {
        bottles.clear();
    }

    public int getCurrentFillLevel() {
        return bottles.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public Position getPosition() {
        return position;
    }
}