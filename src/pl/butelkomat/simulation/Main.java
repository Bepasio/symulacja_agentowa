package pl.butelkomat.simulation;

import pl.butelkomat.simulation.infrastructure.Butelkomat;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

public class Main {


    public static void main(String[] args) {
        WorldMap worldMap = new WorldMap(100, 100);
        Butelkomat butelkomat = new Butelkomat(100, 10, new Position(100, 100));
        Zone zone = new Zone("CIpka",-1, 4, 122, 2312, 2.5);
        worldMap.addButelkomat(butelkomat);
        worldMap.addZone(zone);
    }
}
