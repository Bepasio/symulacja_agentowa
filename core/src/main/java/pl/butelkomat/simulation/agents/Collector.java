package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

//    jakas metoda collectFromBin
//    okreslic ile butelek moze nosic
//    zalozmy ze chodzi od kosza do kosza, jak zapelni eq to idzie do najblizszego butelkomatu
public class Collector extends Agent {
    public Collector(Position startPosition) {
        super(startPosition, 20);
    }

    public void step(boolean movePhase, WorldMap map) {
        if(movePhase) {
            if (currentTarget == null) {
                if (bottles.size() < backpackCapacity) {
                    currentTarget = map.nearestTrashBin(position);
                } else {
                    currentTarget = map.nearestBottleMachine(position);
                }
            }
        }
        if(currentTarget != null){
            moveTowards(currentTarget);
        }
    }
}
