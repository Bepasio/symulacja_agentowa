package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.Position;

public interface Interactable {
    boolean isFull();
    void emptying();
    int getBottlesAmount();
    boolean canCollectBottle();
    Position getPosition();
    boolean addBottle(Bottle bottle);
}