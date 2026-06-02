package pl.butelkomat.simulation.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.LoggerService;

import java.io.BufferedReader;
import java.util.ArrayList;

public class WorldMap {
    public enum TileType { PATH, WATER, GRASS, OBSTACLE }

    private final int width;
    private final int height;
    private final TileType[][] terrainGrid;

    private final ArrayList<MapElement> elements;
    private final ArrayList<Interactable> interactables;
    private final ArrayList<Zone> zones;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrainGrid = new TileType[height][width];
        this.zones = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.interactables = new ArrayList<>();
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
            LoggerService.getInstance().logError("Błąd ładowania mapy ASCII: " + e.getMessage());
        }
    }

    public void addElement(MapElement element) {
        Position pos = element.getPosition();
        int posX = pos.getX();
        int posY = pos.getY();
        if(!isWalkable(posX, posY)) return;

        elements.add(element);
        if(element instanceof Interactable) {
            interactables.add((Interactable) element);
        }
    }

    public Position nearestTrashBin(Position agentPosition, ArrayList<Position> ignoredPositions) {
        MapElement nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (MapElement element : elements) {

            if (element instanceof TrashBin) {
                if (ignoredPositions != null && ignoredPositions.contains(element.getPosition())) continue;

                int distance = calculateDistance(agentPosition, element.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = element;
                }
            }
        }

        if (nearest == null) {
            return null;
        }
        return nearest.getPosition();
    }

    public Position nearestBottleMachine(Position agentPosition, ArrayList<Position> ignoredPositions) {
        MapElement nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (MapElement element : elements) {

            if (element instanceof BottleMachine) {
                if (ignoredPositions != null && ignoredPositions.contains(element.getPosition())) continue;

                int distance = calculateDistance(agentPosition, element.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = element;
                }
            }
        }

        if (nearest == null) {
            return null;
        }
        return nearest.getPosition();
    }

    public Position nearestInteractable(Position agentPosition, ArrayList<Position> ignoredPositions) {
        Interactable nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for(Interactable interactable : interactables) {
            if(ignoredPositions != null && ignoredPositions.contains(interactable.getPosition())) continue;
            int distance = calculateDistance(agentPosition, interactable.getPosition());
            if(distance < minDistance) {
                minDistance = distance;
                nearest = interactable;
            }
        }

        if(nearest == null) {
            return null;
        }
        return nearest.getPosition();
    }

    public Interactable getInteractableAt(Position position){
        for(Interactable interactable : interactables) {
            if(interactable.getPosition().equals(position)) return interactable;
        }
        return null;
    }

    public void addZone(Zone zone) {
        if (zone.getStartX() < 0 || zone.getEndX() > this.width || zone.getStartY() < 0 || zone.getEndY() > this.height){
            LoggerService.getInstance().logError("Strefa wychodzi poza mape");
            return;
        }
        zones.add(zone);
    }

    public int calculateDistance(Position p1, Position p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    public ArrayList<BottleMachine> getBottleMachines() {
        ArrayList<BottleMachine> machines = new ArrayList<>();
        for (MapElement element : elements) {
            if (element instanceof BottleMachine) {
                machines.add((BottleMachine) element);
            }
        }
        return machines;
    }

    public ArrayList<TrashBin> getTrashBins() {
        ArrayList<TrashBin> bins = new ArrayList<>();
        for (MapElement element : elements) {
            if (element instanceof TrashBin) {
                bins.add((TrashBin) element);
            }
        }
        return bins;
    }

    public ArrayList<Agent> getAgents() {
        ArrayList<Agent> agentsList = new ArrayList<>();
        for (MapElement element : elements) {
            if (element instanceof Agent) {
                agentsList.add((Agent) element);
            }
        }
        return agentsList;
    }

    public ArrayList<MapElement> getElements() {
        return elements;
    }

    public boolean isWalkable(int x, int y) {
        // Zabezpieczenie przed wyjściem poza mapę
        if (x < 0 || x >= width || y < 0 || y >= height) return false;

        // Blokada wejścia do wody i w ściany
        if (terrainGrid[y][x] == TileType.WATER) {
            return false;
        }

        return true;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public TileType getTileType(int x, int y) { return terrainGrid[y][x]; }
    public ArrayList<Zone> getZones() { return zones; }
}
