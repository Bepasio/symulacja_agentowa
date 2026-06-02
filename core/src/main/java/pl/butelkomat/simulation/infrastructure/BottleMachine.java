package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.agents.Consumer;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;

import java.util.ArrayList;
import java.util.Iterator;

public class BottleMachine implements MapElement {
    private final int capacity;
    private final ArrayList<Bottle> bottles;
    private final int maxPaperStock;
    private int paperStock;
    private Position position;
    private final double bottlePrice = 0.50;

    public boolean canCollectBottle() {
        return bottles.size() < capacity && paperStock > 0;
    }

    public BottleMachine(int capacity, int paperStock, Position position) {
        this.capacity = capacity;
        this.bottles = new ArrayList<>();
        this.maxPaperStock = paperStock;
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

    public int processDeposit(Agent agent) {
        if (!canCollectBottle()) {
            return 0;
        }

        int acceptedBottles = 0;
        Iterator<Bottle> iterator = agent.bottles.iterator();

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
            double payout = acceptedBottles * bottlePrice;
            agent.addMoney(payout);
            if(agent instanceof Consumer) {
                LoggerService.getInstance().log("Consumer-" + agent.getID() + " zarobił " + payout + " zł");
            }else{
                LoggerService.getInstance().log("Collector-" + agent.getID() + " zarobił " + payout + " zł");
            }
        }

        return acceptedBottles;
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

    public int getCapacity() {
        return capacity;
    }

    public Position getPosition() {
        return position;
    }
}