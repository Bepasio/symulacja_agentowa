package pl.butelkomat.simulation.utils;

import pl.butelkomat.simulation.infrastructure.Butelkomat;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataLoader {

    public void loadTrashBins(WorldMap worldMap, String name) {
        try{
            File trashBins = new File(name);
            Scanner reader = new Scanner(trashBins);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                String[] data =  line.split(",");//jedna linia = jeden smietnik: capacity,x,y
                int capacity = Integer.parseInt(data[0]);
                Position position = new Position(Integer.parseInt(data[1]), Integer.parseInt(data[2]));

                worldMap.addTrashBin(new TrashBin(capacity, position));
            }
            System.out.println("Załadowano śmietniki z pliku: " + name);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku: " + name);
            throw new RuntimeException(e);
        }
    }

    public void loadButelkomats(WorldMap worldMap, String name) {
        try{
            File butelkomats = new File(name);
            Scanner reader = new Scanner(butelkomats);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                String[] data =  line.split(",");//jedna linia = jeden butelkomat: capacity,paperstock,x,y
                int capacity = Integer.parseInt(data[0]);
                int paperStock = Integer.parseInt(data[1]);
                Position position = new Position(Integer.parseInt(data[2]), Integer.parseInt(data[3]));

                worldMap.addButelkomat(new Butelkomat(capacity, paperStock, position));
            }
            System.out.println("Załadowano butelkomaty z pliku: " + name);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku: " + name);
            throw new RuntimeException(e);
        }
    }

    public void loadZones(WorldMap worldMap, String name) {
        try{
            File zones = new File(name);
            Scanner reader = new Scanner(zones);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                String[] data =  line.split(",");//jedna linia = jedna strefa: name,startX,endX,startY,endY,multiplier

                String zoneName = data[0];
                int startX = Integer.parseInt(data[1]);
                int endX = Integer.parseInt(data[2]);
                int startY = Integer.parseInt(data[3]);
                int endY = Integer.parseInt(data[4]);
                double multiplier = Double.parseDouble(data[5]);

                worldMap.addZone(new Zone(zoneName, startX, endX, startY, endY, multiplier));
            }
            System.out.println("Załadowano butelkomaty z pliku: " + name);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku: " + name);
            throw new RuntimeException(e);
        }
    }
}
