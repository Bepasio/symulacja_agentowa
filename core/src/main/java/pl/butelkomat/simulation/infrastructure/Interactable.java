package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.world.Position;

public interface Interactable {
    boolean isFull();
    void emptying();
    int getBottlesAmount();
    boolean canCollectBottles();
    Position getPosition();
    boolean addBottle();
}
