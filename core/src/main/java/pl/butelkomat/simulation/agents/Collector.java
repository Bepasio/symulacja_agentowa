package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
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
                        int randomX = (int) (Math.random() * 90);
                        int randomY = (int) (Math.random() * 26);
                        currentTarget = new Position(randomX, randomY);
                    }
                } else {
                    currentTarget = map.nearestBottleMachine(position, visitedTargets);
                    if (currentTarget == null) {
                        System.out.println("Collector-" + id + ": Wszystkie automaty na czarnej liscie");
                    }
                }
            }

            if (currentTarget != null) {
                moveTowards(currentTarget);

                if (position.equals(currentTarget)) {
                    if (!isFull && map.nearestTrashBin(position, visitedTargets) == null) {
                        currentTarget = null;
                        return;
                    }

                    for (MapElement element : map.getElements()) {
                        if (element.getPosition().equals(position)) {

                            if (element instanceof TrashBin bin) {
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

                                System.out.println("Collector-" + id + " wyciagnal " + collectedCount + " butelek KAUCYJNYCH z kosza. W plecaku:" + bottles.size() + "/20");
                                visitedTargets.add(currentTarget);
                                break;
                            } else if (element instanceof BottleMachine machine && isFull) {
                                int accepted = machine.processDeposit(this.bottles);
                                System.out.println("Collector-" + id + " oddal " + accepted + " butelek do butelkomatu.");

                                if (!bottles.isEmpty()) {
                                    System.out.println("Collector-" + id + ": nie oddano wszystkiego, pozostalo" + bottles.size() + "/20 butelek");
                                    visitedTargets.add(currentTarget);
                                } else {
                                    visitedTargets.clear();
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