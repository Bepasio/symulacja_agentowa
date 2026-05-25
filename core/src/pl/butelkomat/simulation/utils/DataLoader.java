package pl.butelkomat.simulation.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.Zone;
import java.util.Scanner;

public class DataLoader {
    public void loadZones(WorldMap worldMap, String name) {
        try {
            // ZMIANA TUTAJ: Używamy Gdx.files zamiast java.io.File
            FileHandle fileHandle = Gdx.files.internal(name);
            Scanner reader = new Scanner(fileHandle.reader("UTF-8"));

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split(",");

                String zoneName = data[0];
                int startX = Integer.parseInt(data[1]);
                int endX = Integer.parseInt(data[2]);
                int startY = Integer.parseInt(data[3]);
                int endY = Integer.parseInt(data[4]);
                double multiplier = Double.parseDouble(data[5]);

                worldMap.addZone(new Zone(zoneName, startX, endX, startY, endY, multiplier));
            }
            System.out.println("Załadowano strefy z pliku: " + name);
            reader.close();
        } catch (Exception e) {
            System.out.println("Błąd podczas ładowania pliku: " + name);
            e.printStackTrace();
        }
    }
}
