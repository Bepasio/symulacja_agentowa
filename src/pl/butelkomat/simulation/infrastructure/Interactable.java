package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;

interface Interactable {
    boolean addBottle(Bottle bottle);
    boolean canCollectBottle(); //sprawdza czy jest pelny/uszkodzony
    int getCapacity();
    int getCurrentFillLevel();

//    musialaby jeszcze byc funkcja zwracajaca pozycje smietnika/butelkomatu
}
