package pl.butelkomat.simulation.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

import java.util.Scanner;

public class DataLoader {

//    public void loadElements(WorldMap worldMap, String name, ElementType type) {
//        try {
//            FileHandle file = Gdx.files.internal(name);
//            Scanner reader = new Scanner(file.reader("UTF-8"));
//            while (reader.hasNextLine()) {
//                String line = reader.nextLine();
//                if (line.trim().isEmpty()) continue;
//                String[] data = line.split(",");
//                switch (type) {
//                    case TRASH_BIN:
//                        int trashCapacity = Integer.parseInt(data[0]);
//                        Position trashPos = worldMap.getRandomPosition();
//                        worldMap.addElement(new TrashBin(trashCapacity, trashPos));
//                        break;
//
//                    case BOTTLE_MACHINE:
//                        int bottleCapacity = Integer.parseInt(data[0]);
//                        int paperStock = Integer.parseInt(data[1]);
//                        Position bottlePos = worldMap.getRandomPosition();
//                        worldMap.addElement(new BottleMachine(bottleCapacity, paperStock, bottlePos));
//                        break;
//                }
//            }
//            reader.close();
//            LoggerService.getInstance().log("Załadowano śmietniki z pliku: " + name);
//        } catch (Exception e) {
//            LoggerService.getInstance().logError("Błąd ładowania śmietników: " + e.getMessage());
//        }
//    }

    public void loadZones(WorldMap worldMap, String name) {
        try {
            FileHandle file = Gdx.files.internal(name);
            Scanner reader = new Scanner(file.reader("UTF-8"));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                worldMap.addZone(new Zone(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]), Integer.parseInt(data[4]),
                        Double.parseDouble(data[5])));
            }
            reader.close();
            LoggerService.getInstance().log("Załadowano strefy z pliku: " + name);
        } catch (Exception e) {
            LoggerService.getInstance().logError("Błąd ładowania stref: " + e.getMessage());
        }
    }
}
