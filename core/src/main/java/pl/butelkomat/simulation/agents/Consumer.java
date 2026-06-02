package pl.butelkomat.simulation.agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

public class Consumer extends Agent {
    private static int consumerIdCounter = 1;

    public Consumer(Position startPosition) {
        super(startPosition, 2, consumerIdCounter++);
    }

    private boolean hasRefundableBottle() {
        for (Bottle b : bottles) {
            if (b.isRefundable()) return true;
        }
        return false;
    }

    public void step(boolean movePhase, WorldMap map) {

        if (movePhase) {
            if (bottles.size() < backpackCapacity) {
                double multiplier = 1.0;

                for(Zone zone : map.getZones()) {
                    if(zone.isInZone(position)) {
                        multiplier = zone.getMultiplier();
                        break;
                    }
                }

                double baseChance = 0.05; //domyslna szansa na wygenerowanie butelki
                double finalChance = multiplier * baseChance; // szansa na wygenerowanie butelki po uwzglednieniu mnoznika strefy

                if (Math.random() < finalChance) {
                    if (bottles.isEmpty()) {
                        visitedTargets.clear();
                        currentTarget = null;
                    }

                    boolean isRefundable = Math.random() < 0.5;
                    bottles.add(new Bottle(isRefundable));
                    LoggerService.getInstance().log("Consumer" + id + " wygenerowal butelke isRefundable=" + isRefundable);
                }
            }

            if(currentTarget == null){
                if(!bottles.isEmpty()){
                    Position target = map.nearestInteractable(position, visitedTargets);
                    if(target == null){
                        LoggerService.getInstance().log("Consumer" + id + ": wszytko pelne/odrzucone. Nie udalo sie wyznaczyc celu");
                        visitedTargets.clear();
                    }else{
                        currentTarget = target;
                    }
                }else{
                    int randomX;
                    int randomY;
                    do {
                        randomX = (int) (Math.random() * 90);
                        randomY = (int) (Math.random() * 26);
                    }while(!map.isWalkable(randomX, randomY));
                    currentTarget = new Position(randomX, randomY);
                }
            }

            if (currentTarget != null) {
                moveTowards(currentTarget, map);

                boolean interacted = false;
                if (position.equals(currentTarget)) {
                    Interactable targetElement = map.getInteractableAt(position);

                    if(targetElement instanceof TrashBin bin){
                        int thrownAway = 0;

                        Iterator<Bottle> iterator = bottles.iterator();
                        while(iterator.hasNext()){
                            Bottle b = iterator.next();
                            if (bin.addBottle(b)) {
                                iterator.remove();
                                thrownAway++;
                            } else {
                                break;
                            }
                        }
                        LoggerService.getInstance().log("Consumer" + id + " wyrzucil " + thrownAway + " butelek do kosza. W plecaku zostalo: " + bottles.size());
                        interacted = true;
                    }else if(targetElement instanceof BottleMachine machine){
                        int accepted = machine.processDeposit(this);
                        LoggerService.getInstance().log("Consumer" + id + " oddal " + accepted + " butelek do butelkomatu. W plecaku zostalo: " + bottles.size());
                        interacted = true;
                    }

                        if (interacted && !bottles.isEmpty()) {
                            LoggerService.getInstance().log("Consumer" + id + " Obiekt pelny lub odrzucil butelki! Dodaje go do czarnej listy.");
                            visitedTargets.add(currentTarget);
                        }
                        currentTarget = null;
                }
            }
        }
    }
}
