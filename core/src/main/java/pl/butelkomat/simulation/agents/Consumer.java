package pl.butelkomat.simulation.agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.*;

public class Consumer extends Agent {
    private static int consumerIdCounter = 1;
    private int frustrationLevel = 0;

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
                double multiplier = 1.0; //domyslny mnoznik to 1.0

                for(Zone zone : map.getZones()) {
                    if(zone.isInZone(position)) {
                        multiplier = zone.getMultiplier(); //jesli znajdujemy sie w strefie w ktorej mamy wieksze szanse na generowanie butelek, to przyjmujemy jej mnoznik
                        break;
                    }
                }

                double baseChance = 0.05; //domyslna szansa na wygenerowanie butelki
                double finalChance = multiplier * baseChance; // szansa na wygenerowanie butelki po uwzglednieniu mnoznika strefy

                if (Math.random() < finalChance) { //jesli prawda to generujemy butekle
                    if (bottles.isEmpty()) { //jesli ma puste ręce to czyscimy czarna liste i target to null - bedziemy szukac znowu gfzie isc
                        visitedTargets.clear();
                        currentTarget = null;
                    }

                    boolean hadRefundableBefore = hasRefundableBottle();
                    boolean isRefundable = Math.random() < 0.5; //50% szans na zwrotną butelke
                    bottles.add(new Bottle(isRefundable));
                    if(bottles.size() == 1 || (!hadRefundableBefore && isRefundable)) { //jesli ma tylko jedna (pierwsza) butelke lub dostal nowa zwrotna butelke(i wczesniej nie mial)
                        visitedTargets.clear();                                            //to czyscimy czarna liste zeby na pewno mial mozliwosc isc do butelkomatu
                        currentTarget = null; //przerywamy mu tym trase do ppustego miejsca na mapie, pojdzie sobie od razu do infrastruktury
                    }
                    LoggerService.getInstance().log("Consumer" + id + " wygenerowal butelke isRefundable=" + isRefundable);
                }
            }

            if(currentTarget == null) {
                if(!bottles.isEmpty()) {
                    Position target = null;

                    if(hasRefundableBottle()){ //jesli ma jakas zwrotna butelke
                        target = map.nearestBottleMachine(position, visitedTargets); //szukamy najblizszego butelkomatu

                        if(target == null) { //jestli jest null, bo kazdy byl na czarnej liscie to wyznaczamy smietnik
                            target = map.nearestTrashBin(position, visitedTargets);
                        }
                    }else{
                        target = map.nearestTrashBin(position, visitedTargets); //jesli nie ma zwrotnych butelek to wyznaczamy smietnik
                    }

                    if(target == null){ //jesli nie udalo sie nic wyznaczyc, tocczyscimy czarna liste i idziemy gdziekolwiek
                        LoggerService.getInstance().log("Consumer" + id + ": wszytko pelne/odrzucone. Nie udalo sie wyznaczyc celu");
                        visitedTargets.clear();
                        currentTarget = map.getRandomPosition();
                    }else{
                        currentTarget = target; //jesli wyznaczyl, to sie do niego udajemy
                    }
                }else{
                    currentTarget = map.getRandomPosition();
                }
            }


            if (currentTarget != null) {
                moveTowards(currentTarget, map); //jesli mamy cel to do niego idziemy

                if (position.equals(currentTarget)) {
                    Interactable targetElement = map.getInteractableAt(position);

                    if (targetElement != null) {
                        if (targetElement instanceof TrashBin bin) {
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
                            LoggerService.getInstance().log("Consumer" + id + " wyrzucil " + thrownAway + " butelek do kosza. W plecaku zostalo: " + bottles.size());
                        } else if (targetElement instanceof BottleMachine machine) {
                            int accepted = machine.processDeposit(this);
                            if (accepted > 0) {
                                LoggerService.getInstance().log("Consumer-" + id + " oddal " + accepted + " butelek do butelkomatu.");
                                frustrationLevel = 0;
                            } else {
                                LoggerService.getInstance().log("Consumer-" + id + " nie udalo sie oddac butelek do butelkomatu.");
                                frustrationLevel++; //jesli nie udalo sie oddac butelek, to sie denerwuje
                                if (frustrationLevel == 3) { //jesli odbije sie od 3 butelkomatow to wyrzuca smieci na ziemie
                                    map.addLitter(bottles.size());
                                    bottles.clear();
                                    LoggerService.getInstance().log("Consumer-" + id + " wyrzucil butelki w krzaki z frustracji.");
                                    frustrationLevel = 0; //jak juz wyrzui to zerujemy poziom zdenerwowania
                                }
                            }
                        }

                        if (!bottles.isEmpty()) { //jesli byla infrastruktura i dalej mamy butelki to dodajemy go na czarna list
                            LoggerService.getInstance().log("Consumer" + id + " Obiekt pelny lub odrzucil butelki! Dodaje go do czarnej listy.");
                            visitedTargets.add(currentTarget);
                        }
                    }

                    currentTarget = null; //ustawiamy null i szukamy dalej
                }
            }
        }
    }
    public ElementType getElementType() {
        return ElementType.CONSUMER;
    }
}
