package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;

import java.util.Stack;

public class Collector extends Agent {
    private static int collectorIdCounter = 1;
    private boolean isFull = false;

    public Collector(Position startPosition) {
        super(startPosition, 20, collectorIdCounter++);
    }

    public void step(boolean movePhase, WorldMap map) {
        if (movePhase) {
            if (bottles.size() >= backpackCapacity) {
                isFull = true;  // szuka tylko butelkomatow
            } else if (bottles.isEmpty()) {
                isFull = false; // szuka tylko smietnikow
            }

            if (currentTarget == null) {
                if (!isFull) {
                    currentTarget = map.nearestTrashBin(position, visitedTargets);

                    if (currentTarget == null) {
                        visitedTargets.clear();
                        currentTarget = map.getRandomPosition();
                    }
                } else {
                    currentTarget = map.nearestBottleMachine(position, visitedTargets);
                    if (currentTarget == null) {
                        System.out.println("Collector-" + id + ": Wszystkie automaty na czarnej liscie");
                    }
                }
            }

            if (currentTarget != null) {
                moveTowards(currentTarget, map);

                if (position.equals(currentTarget)) {
                    if (!isFull && map.nearestTrashBin(position, visitedTargets) == null) {
                        currentTarget = null;
                        return;
                    }

                    Interactable interactable = map.getInteractableAt(position);
                    if (interactable != null) {
                        if (interactable instanceof TrashBin bin) {
                            int collectedCount = 0;
                            Stack<Bottle> rejectedBottles = new Stack<>();

                            while (bottles.size() < backpackCapacity) {
                                Bottle bottle = bin.takeBottle();
                                if (bottle != null) {
                                    if (bottle.isRefundable()) {
                                        bottles.add(bottle);
                                        collectedCount++;
                                    } else {
                                        rejectedBottles.push(bottle);
                                    }
                                } else {
                                    break;
                                }
                            }

                            while (!rejectedBottles.isEmpty()) {
                                bin.addBottle(rejectedBottles.pop());
                            }

                            LoggerService.getInstance().log("Collector-" + id + " wyciagnal " + collectedCount + " butelek KAUCYJNYCH z kosza. W plecaku:" + bottles.size() + "/20");
                            visitedTargets.add(currentTarget);
                        } else if (interactable instanceof BottleMachine machine && isFull) {
                            int accepted = machine.processDeposit(this);
                            LoggerService.getInstance().log("Collector-" + id + " oddal " + accepted + " butelek do butelkomatu.");

                            if (!bottles.isEmpty()) {
                                LoggerService.getInstance().log("Collector-" + id + ": nie oddano wszystkiego, pozostalo " + bottles.size() + "/20 butelek");
                                visitedTargets.add(currentTarget);
                            } else {
                                visitedTargets.clear();
                            }
                        }
                    }

                    currentTarget = null;
                }
            }
        }
    }
}