package pl.butelkomat.simulation.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;

import java.io.BufferedReader;
import java.util.ArrayList;

public class WorldMap {
    public enum TileType { PATH, WATER, GRASS, OBSTACLE }

    private final int width;
    private final int height;
    private final TileType[][] terrainGrid;

    private final ArrayList<BottleMachine> bottleMachines;
    private final ArrayList<TrashBin> trashBins;
    private final ArrayList<Zone> zones;
    private final ArrayList<Agent> agents; // DO TEGO BĘDZIE MIEĆ DOSTĘP SILNIK

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrainGrid = new TileType[height][width];
        this.bottleMachines = new ArrayList<>();
        this.trashBins = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.agents = new ArrayList<>();
    }

    public void loadBackgroundFromAscii(String path) {
        try {
            FileHandle file = Gdx.files.internal(path);
            BufferedReader reader = new BufferedReader(file.reader("UTF-8"));
            String line;
            int y = 0;
            while ((line = reader.readLine()) != null && y < height) {
                for (int x = 0; x < line.length() && x < width; x++) {
                    char c = line.charAt(x);
                    if (c == '~') terrainGrid[y][x] = TileType.WATER;
                    else if (c == '.' || c == '*') terrainGrid[y][x] = TileType.GRASS;
                    else if (c == '=' || c == '|' || c == '/' || c == '\\' || c == '-') terrainGrid[y][x] = TileType.PATH;
                    else terrainGrid[y][x] = TileType.OBSTACLE; // Inne znaki jako bloki
                }
                y++;
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Błąd ładowania mapy ASCII: " + e.getMessage());
        }
    }

    public void addButelkomat(BottleMachine bottleMachine) {
        if(bottleMachine.getPosition().getX() < 0 || bottleMachine.getPosition().getX() > this.width || bottleMachine.getPosition().getY() < 0 || bottleMachine.getPosition().getY() > this.height){
            System.out.println("BLAD: Butelkomat poza mapa");
            return;
        }
        bottleMachines.add(bottleMachine);
    }

    public void addTrashBin(TrashBin trashBin) {
        if(trashBin.getPosition().getX() < 0 || trashBin.getPosition().getX() > this.width || trashBin.getPosition().getY() < 0 || trashBin.getPosition().getY() > this.height){
            System.out.println("BLAD: Smietnik poza mapa");
            return;
        }
        trashBins.add(trashBin);
    }

    public void addZone(Zone zone) {
        if (zone.getStartX() < 0 || zone.getEndX() > this.width || zone.getStartY() < 0 || zone.getEndY() > this.height){
            System.out.println("BLAD: Strefa wychodzi poza mape");
            return;
        }
        zones.add(zone);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public TileType getTileType(int x, int y) { return terrainGrid[y][x]; }
    public ArrayList<BottleMachine> getButelkomats() { return bottleMachines; }
    public ArrayList<TrashBin> getTrashBins() { return trashBins; }
    public ArrayList<Agent> getAgents() { return agents; }
    public ArrayList<Zone> getZones() { return zones; }
}
