package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.Position;

interface Interactable {
    boolean addBottle(Bottle bottle);
    boolean canCollectBottle(); //sprawdza czy jest pelny/uszkodzony
    int getCapacity();
    int getCurrentFillLevel();
    Position getPosition();

//    musialaby jeszcze byc funkcja zwracajaca pozycje smietnika/butelkomatu
}
