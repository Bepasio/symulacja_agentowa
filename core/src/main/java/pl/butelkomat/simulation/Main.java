package pl.butelkomat.simulation;

import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

public class Main {
//to jest useless
    public static void main(String[] args) {
        WorldMap worldMap = new WorldMap(89, 25);
        BottleMachine bottleMachine = new BottleMachine(100, 10, new Position(100, 100));
        Zone zone = new Zone("Wyspa Słodowa",7, 18, 3, 7, 2.0);
        Zone zone2 = new Zone("Ogród Botaniczny", 32, 63, 0, 6, 2.0);
        Zone zone3 = new Zone("Plac Grunwaldzki", 54, 89, 7, 14, 1.5);
        Zone zone4 = new Zone("Politechnika Wrocławska", 50, 89, 15, 25, 1.2);
        worldMap.addButelkomat(bottleMachine);
        worldMap.addZone(zone);
    }
}
