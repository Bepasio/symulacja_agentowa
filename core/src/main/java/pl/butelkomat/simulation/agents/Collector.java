package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.ElementType;
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
                    currentTarget = map.nearestTrashBin(position, visitedTargets); //wyszukujemy najblizszy smietnik

                    if (currentTarget == null) {
                        visitedTargets.clear(); //czyscimy liste odwiedzonych, jesli kazdy najblizszy byl odwiedzony
                        currentTarget = map.getRandomPosition(); //nadajemy mu randomowy target zeby nie stal w miejscu
                    }
                } else {
                    currentTarget = map.nearestBottleMachine(position, visitedTargets);
                    if (currentTarget == null) {
                        visitedTargets.clear();
                        currentTarget = map.getRandomPosition();
                    }
                }
            }

            if (currentTarget != null) {
                moveTowards(currentTarget, map); // jesli ma target to idziemy do niego

                if (position.equals(currentTarget)) {
                    if (!isFull && map.nearestTrashBin(position, visitedTargets) == null) {
                        currentTarget = null; //jesli mamy miejsce w plecaku i nie mamy wyznaczonego celu do smietnika, to usuwamy target
                        return;
                    }

                    Interactable interactable = map.getInteractableAt(position);
                    if (interactable != null) {
                        if (interactable instanceof TrashBin bin) {
                            int collectedCount = 0;
                            Stack<Bottle> rejectedBottles = new Stack<>(); //jesli wyciagamy butelki bezzwrotne to odkladmay na bok

                            while (bottles.size() < backpackCapacity) {
                                Bottle bottle = bin.takeBottle();
                                if (bottle != null) {
                                    if (bottle.isRefundable()) {
                                        bottles.add(bottle); //bierzemy do plecaka butelkki zwrotne
                                        collectedCount++; //zliczamy je
                                    } else {
                                        rejectedBottles.push(bottle);
                                    }
                                } else {
                                    break;
                                }
                            }

                            while (!rejectedBottles.isEmpty()) {
                                bin.addBottle(rejectedBottles.pop()); //po przeszukaniu smietnika wkladamy do niego spowrotem bezzwrotne butelki
                            }

                            LoggerService.getInstance().log("Collector-" + id + " wyciagnal " + collectedCount + " butelek KAUCYJNYCH z kosza. W plecaku:" + bottles.size() + "/20");
                            visitedTargets.add(currentTarget); //po przeszukaniu smietnika dodajemy go do listy juz odwiedzonych
                        } else if (interactable instanceof BottleMachine machine && isFull) {
                            int accepted = machine.processDeposit(this); //processDeposit zwraca liczbe odanych butelek do butelkomatu
                            LoggerService.getInstance().log("Collector-" + id + " oddal " + accepted + " butelek do butelkomatu.");

                            if (!bottles.isEmpty()) { //jesli zostaly butelki to dodajemy butelkomat na czarna liste
                                LoggerService.getInstance().log("Collector-" + id + ": nie oddano wszystkiego, pozostalo " + bottles.size() + "/20 butelek");
                                visitedTargets.add(currentTarget);
                            } else {
                                visitedTargets.clear(); //jesli wszystko oddalismy to czyscimy czarna liste i szukamy od nowa po mapie
                            }
                        }
                    }
                    currentTarget = null; //po wykonanych czynnosciach target jest null i w nastepnym kroku sprawdzmay znowu co robic
                }
            }
        }
    }

    public ElementType getElementType() {
        return ElementType.COLLECTOR;
    }
}