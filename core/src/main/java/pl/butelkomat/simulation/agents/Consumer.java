package pl.butelkomat.simulation.agents;

import java.util.Iterator;
import java.util.concurrent.*;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

//    generuje butle (generateBottle), musi zdecydowac gdzie isc (decideTarget), musi isc do targetu (moveToTarget)
//    ma liste ktora zawiera butelki do pozbycia sie
//    metoda step, czyli krok wykonywany w każdym cyklu symulacji
//    w zaleznosci od miejsca i czasu jest jakis mnoznik generujemy butelke, losowo trzeba wybrac czy będzie kaucyjna
//    jesli mamy pustą butelke to idziemy ją wyrzucic do najblizszego celu
public class Consumer extends Agent {
    public Consumer(Position startPosition) {
        super(startPosition, 2);
    }

    public void step(boolean movePhase, WorldMap map) {
        if (bottles.size() < backpackCapacity) {
            if (Math.random() < 0.05) {
                if(Math.random() < 0.5) {
                    bottles.add(new Bottle(true));
                }else{
                    bottles.add(new Bottle(false));
                }

            }
        }

        if (movePhase) {
            if (!bottles.isEmpty()) {
                if (currentTarget == null) {
                    Position nearestTrashBin = map.nearestTrashBin(position);
                    Position nearestBottleMachine = map.nearestBottleMachine(position);

                    if (nearestTrashBin == null) currentTarget = nearestBottleMachine;
                    else if (nearestBottleMachine == null) currentTarget = nearestTrashBin;
                    else if (map.calculateDistance(position, nearestBottleMachine) < map.calculateDistance(position, nearestTrashBin)) {
                        currentTarget = nearestBottleMachine;
                    } else {
                        currentTarget = nearestTrashBin;
                    }
                }
            }
            if (currentTarget != null) {
                moveTowards(currentTarget);

                if (position.equals(currentTarget)) {
                    for (MapElement element : map.getElements()) {
                        if (element.getPosition().equals(this.position)) {
                            if (element instanceof TrashBin bin) {
                                int thrownAway = 0;

                                Iterator<Bottle> iterator = bottles.iterator();
                                while (iterator.hasNext()) {
                                    Bottle b = iterator.next();

                                    if (bin.addBottle(b)) {
                                        iterator.remove();
                                        thrownAway++;
                                    } else {
                                        break;
                                    }
                                }
                                System.out.println("Consumer wyrzucil " + thrownAway + " butelek do kosza. W plecaku zostalo: " + bottles.size());
                            } else if (element instanceof BottleMachine machine) {
                                int accepted = machine.processDeposit(this.bottles);
                                System.out.println("Consumer oddal " + accepted + " butelek do butelkomatu. W plecaku zostalo: " + bottles.size());

                                if (!bottles.isEmpty()) {
                                    System.out.println("Maszyna zepsuta/pelna! Consumer idzie do smietnika.");
                                    currentTarget = map.nearestTrashBin(position);
                                    return;
                                }
                                break;
                            }
                        }
                    }
                    currentTarget = null;
                }
            }
        }
    }
}
