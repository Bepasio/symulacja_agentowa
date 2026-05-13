package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;

import java.util.ArrayList;
import java.util.Iterator;

public class Butelkomat implements Interactable {
    private final int capacity;
    private final ArrayList<Bottle> bottles;
    private int paperStock;

    public boolean canCollectBottle() {
        return bottles.size() < capacity && paperStock > 0;
    }

    public Butelkomat(int capacity, int paperStock) {
        this.capacity = capacity;
        this.bottles = new ArrayList<>();
        this.paperStock = paperStock;
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
}