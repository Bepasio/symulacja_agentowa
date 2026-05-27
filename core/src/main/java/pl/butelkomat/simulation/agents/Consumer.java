package pl.butelkomat.simulation.agents;

import java.util.concurrent.*;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

//    generuje butle (generateBottle), musi zdecydowac gdzie isc (decideTarget), musi isc do targetu (moveToTarget)
//    ma liste ktora zawiera butelki do pozbycia sie
//    metoda step, czyli krok wykonywany w każdym cyklu symulacji
//    w zaleznosci od miejsca i czasu jest jakis mnoznik generujemy butelke, losowo trzeba wybrac czy będzie kaucyjna
//    jesli mamy pustą butelke to idziemy ją wyrzucic do najblizszego celu
class Consumer extends Agent {
    public Consumer(Position startPosition) {
        super(startPosition, 2);
    }

    public void step(boolean movePhase, WorldMap map) {
        if(bottles.size() < backpackCapacity){
            //tutaj bedzie np. generowal sobie ta butelke i dodawal do backpack
        }

        if(movePhase) {
            if(!bottles.isEmpty()){
                if(currentTarget == null){
                    Position nearestTrashBin = map.nearestTrashBin(position);
                    Position nearestBottleMachine = map.nearestBottleMachine(position);

                    if(nearestTrashBin == null) currentTarget = nearestBottleMachine;
                    else if(nearestBottleMachine == null) currentTarget = nearestTrashBin;
                    else if(map.calculateDistance(position, nearestBottleMachine) < map.calculateDistance(position, nearestTrashBin)) {
                        currentTarget = nearestBottleMachine;
                    }else{
                        currentTarget = nearestTrashBin;
                    }
                }
            }
            if(currentTarget != null){
                moveTowards(currentTarget);
            }
        }
    }
}
