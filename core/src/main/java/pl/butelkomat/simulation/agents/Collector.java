package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

import java.util.ArrayList;

//    jakas metoda collectFromBin
//    okreslic ile butelek moze nosic
//    zalozmy ze chodzi od kosza do kosza, jak zapelni eq to idzie do najblizszego butelkomatu
public class Collector extends Agent {
    private static int collectorIdCounter = 1;
    public Collector(Position startPosition) {
        super(startPosition, 20, collectorIdCounter++);
        visitedTargets = new ArrayList<>();
    }

    public void step(boolean movePhase, WorldMap map) {
        if(movePhase) {
            if (currentTarget == null) {
                if (bottles.size() < backpackCapacity) {
                    currentTarget = map.nearestTrashBin(position, visitedTargets);
                } else {
                    currentTarget = map.nearestBottleMachine(position, visitedTargets);
                }
            }
        }
        if(currentTarget != null){
            moveTowards(currentTarget);
        }
    }
}
