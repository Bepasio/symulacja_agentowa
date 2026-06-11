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

            if(currentTarget == null) { //jesli nie ma celu
                if(!bottles.isEmpty()) { //to jesli ma jakies butelki
                    if(!hasRefundableBottle()) { //i kazda jest bezzwrotna
                        currentTarget = map.nearestTrashBin(position, visitedTargets);//to idzie do smietnika
                    }else{
                        currentTarget = map.nearestInteractable(position, visitedTargets); //ale jesli ktoras z nich jest kaucyjna, to wybiera najblizsze miejsce gdzie moze sie pozbyc butelek
                    }
                    if(currentTarget == null) { //jesli nie udalo sie wyznaczyc zadnego celu to czyscimy jego czarna liste i wybieramy losowa pozycje
                        visitedTargets.clear();
                        currentTarget = map.getRandomPosition();
                    }
                }else{
                    currentTarget = map.getRandomPosition(); //jesli nie ma butelek to idzie byle gdzie
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
                            if(thrownAway > 0){
                                LoggerService.getInstance().log("Consumer-" + id + " wyrzucil " + thrownAway + " butelek do smietnika.");
                                frustrationLevel = 0;
                            }else {
                                LoggerService.getInstance().log("Consumer-" + id + " nie udalo sie oddac butelek do smietnika.");
                                frustrationLevel++; //jesli nie udalo sie oddac butelek, to sie denerwuje
                                littering(map);
                                }
//                            LoggerService.getInstance().log("Consumer" + id + " wyrzucil " + thrownAway + " butelek do kosza. W plecaku zostalo: " + bottles.size());
                        } else if (targetElement instanceof BottleMachine machine) {
                            int accepted = machine.processDeposit(this);
                            if (accepted > 0) {
                                LoggerService.getInstance().log("Consumer-" + id + " oddal " + accepted + " butelek do butelkomatu.");
                                frustrationLevel = 0;
                            } else {
                                LoggerService.getInstance().log("Consumer-" + id + " nie udalo sie oddac butelek do butelkomatu.");
                                frustrationLevel++; //jesli nie udalo sie oddac butelek, to sie denerwuje
                                littering(map);
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

    public double getFrustrationLevel() {
        double percentage = (frustrationLevel / 3.0) * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }

    public void littering(WorldMap map){
        if(frustrationLevel == 3) {
            map.addLitter(bottles.size());
            bottles.clear();
            LoggerService.getInstance().log("Consumer-" + id + " wyrzucil butelki w krzaki z frustracji.");
            frustrationLevel = 0;
        }
    }
    public ElementType getElementType() {
        return ElementType.CONSUMER;
    }
}
