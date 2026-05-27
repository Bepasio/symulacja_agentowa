package pl.butelkomat.simulation.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.butelkomat.simulation.agents.Collector;
import pl.butelkomat.simulation.agents.Consumer;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.world.ElementType;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

import java.util.Scanner;

public class DataLoader {

    public void loadElements(WorldMap worldMap, String name, ElementType type) {
        try {
            FileHandle file = Gdx.files.internal(name);
            Scanner reader = new Scanner(file.reader("UTF-8"));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                switch (type) {
                    case TRASH_BIN:
                        int trashCapacity = Integer.parseInt(data[0]);
                        Position trashPos = new Position(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                        worldMap.addElement(new TrashBin(trashCapacity, trashPos));
                        break;

                    case BOTTLE_MACHINE:
                        int bottleCapacity = Integer.parseInt(data[0]);
                        int paperStock = Integer.parseInt(data[1]);
                        Position bottlePos = new Position(Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                        worldMap.addElement(new BottleMachine(bottleCapacity, paperStock, bottlePos));
                        break;
                }
            }
            reader.close();
            System.out.println("Załadowano śmietniki z pliku: " + name);
        } catch (Exception e) {
            System.out.println("Błąd ładowania śmietników: " + e.getMessage());
        }

        worldMap.addElement(new Consumer(new Position(10, 10)));
        worldMap.addElement(new Consumer(new Position(15, 5)));
        worldMap.addElement(new Collector(new Position(40, 12)));
    }

//    public void loadTrashBins(WorldMap worldMap, String name) {
//        try {
//            FileHandle file = Gdx.files.internal(name);
//            Scanner reader = new Scanner(file.reader("UTF-8"));
//            while (reader.hasNextLine()) {
//                String line = reader.nextLine();
//                if (line.trim().isEmpty()) continue;
//                String[] data = line.split(",");
//                int capacity = Integer.parseInt(data[0]);
//                Position position = new Position(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
//                worldMap.addTrashBin(new TrashBin(capacity, position));
//            }
//            reader.close();
//            System.out.println("Załadowano śmietniki z pliku: " + name);
//        } catch (Exception e) {
//            System.out.println("Błąd ładowania śmietników: " + e.getMessage());
//        }
//    }
//
//    public void loadButelkomats(WorldMap worldMap, String name) {
//        try {
//            FileHandle file = Gdx.files.internal(name);
//            Scanner reader = new Scanner(file.reader("UTF-8"));
//            while (reader.hasNextLine()) {
//                String line = reader.nextLine();
//                if (line.trim().isEmpty()) continue;
//                String[] data = line.split(",");
//                int capacity = Integer.parseInt(data[0]);
//                int paperStock = Integer.parseInt(data[1]);
//                Position position = new Position(Integer.parseInt(data[2]), Integer.parseInt(data[3]));
//                worldMap.addButelkomat(new BottleMachine(capacity, paperStock, position));
//            }
//            reader.close();
//            System.out.println("Załadowano butelkomaty z pliku: " + name);
//        } catch (Exception e) {
//            System.out.println("Błąd ładowania butelkomatów: " + e.getMessage());
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
            System.out.println("Załadowano strefy z pliku: " + name);
        } catch (Exception e) {
            System.out.println("Błąd ładowania stref: " + e.getMessage());
        }
    }
}
