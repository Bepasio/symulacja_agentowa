package pl.butelkomat.simulation.agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

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
                if (Math.random() < 0.05) {
                    if (bottles.isEmpty()) {
                        visitedTargets.clear();
                        currentTarget = null;
                    }

                    boolean isRefundable = Math.random() < 0.5;
                    bottles.add(new Bottle(isRefundable));
                    System.out.println("Consumer" + id + " wygenerowal butelke isRefundable=" + isRefundable);
                }
            }

            if (currentTarget == null) {
                if(!bottles.isEmpty()) {
                    Position nearestTrashBin = map.nearestTrashBin(position, visitedTargets);
                    Position nearestBottleMachine = null;
                    if (hasRefundableBottle()) {
                        nearestBottleMachine = map.nearestBottleMachine(position, visitedTargets);
                    }

                    if (nearestTrashBin == null && nearestBottleMachine == null) {
                        System.out.println("Consumer" + id + ": wszytko pelne/odrzucone. Nie udalo sie wyznaczyc celu");
                        visitedTargets.clear();
//                        bottles.clear();
                    } else if (nearestTrashBin == null) currentTarget = nearestBottleMachine;
                    else if (nearestBottleMachine == null) currentTarget = nearestTrashBin;
                    else if (map.calculateDistance(position, nearestBottleMachine) < map.calculateDistance(position, nearestTrashBin)) {
                        currentTarget = nearestBottleMachine;
                    } else {
                        currentTarget = nearestTrashBin;
                    }
                }
                else{
                    int randomX = (int) (Math.random() * 90) + 1;
                    int randomY = (int) (Math.random() * 26) + 1;
                    currentTarget = new Position(randomX, randomY);
                }
            }

            if (currentTarget != null) {
                moveTowards(currentTarget);
                boolean interacted = false;
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
                                System.out.println("Consumer" + id + " wyrzucil " + thrownAway + " butelek do kosza. W plecaku zostalo: " + bottles.size());
                                interacted = true;
                                break;
                            } else if (element instanceof BottleMachine machine) {
                                int accepted = machine.processDeposit(this.bottles);
                                System.out.println("Consumer" + id + " oddal " + accepted + " butelek do butelkomatu. W plecaku zostalo: " + bottles.size());
                                interacted = true;
                                break;
                            }
                        }
                    }
                        if (interacted && !bottles.isEmpty()) {
                            System.out.println("Consumer"+id+" Obiekt pelny lub odrzucil butelki! Dodaje go do czarnej listy (visitedTargets).");
                            visitedTargets.add(currentTarget);
                        }
                        currentTarget = null;
                }
            }
        }
    }
}
