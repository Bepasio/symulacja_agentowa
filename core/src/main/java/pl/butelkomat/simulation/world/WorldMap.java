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
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class WorldMap {
    public enum TileType { PATH, WATER, GRASS, OBSTACLE }

    private final int width;
    private final int height;
    private final TileType[][] terrainGrid;

    private final ArrayList<MapElement> elements;
    private final ArrayList<Interactable> interactables;
    private final ArrayList<Zone> zones;

    private int litterAmount = 0;

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

    public ArrayList<MapElement> getInteractables() {
        ArrayList<MapElement> interactables = new ArrayList<>();
        for (MapElement element : elements) {
            if (element instanceof Interactable) {
                interactables.add(element);
            }
        }
        return interactables;
    }


    public boolean isWalkable(int x, int y) {
        // Zabezpieczenie przed wyjściem poza mapę
        if (x < 0 || x >= width || y < 0 || y >= height) return false;

        // Blokada wejścia do wody i w ściany
        if (terrainGrid[y][x] == TileType.WATER || terrainGrid[y][x] == TileType.OBSTACLE){
            return false;
        }

        if(getInteractableAt(new Position(x, y)) != null){
            return false;
        }

        return true;
    }

    private class Node implements Comparable<Node> { //wierzcholek grafu
        Position pos; //pozycja gdzie się znajduje
        Node parent; //wierzcholek 'z ktorego idziemy', z ktorego zlicza się koszt podróży gCost
        int gCost; //aktualny koszt podrozy, zwieksza sie z kazdym krokiem od startu
        int hCost; //przewidywany koszt (odleglosc) podrozy, zmniejsza sie z kazdym krokiem ku celu

        Node(Position pos, Node parent, int gCost, int hCost){
            this.pos = pos;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        public int getFCost() {
            return gCost + hCost;
        } //rzeczywisty koszt podróży, mniej = lepiej, to co przeszlismy + to co zostalo do przejscia

        public int compareTo(Node o) {
            int compare = Integer.compare(this.getFCost(), o.getFCost()); //porownujemy je na podstawie kosztu rzeczywistego
            if (compare == 0){
                compare = Integer.compare(this.hCost, o.hCost); //jesli kozt jest taki sam to wybieramy na podstawie odleglosci do celu
            }
            return compare;
        }
    }

    public List<Position> pathFinder(Position start, Position target){
        PriorityQueue<Node> openSet = new PriorityQueue<>(); //wierzcholki ktore widzimy/przeszukujemy (mozliwie 8 wierzcholkow dookola nas) i bedziemy oceniac
        ArrayList<Position> closedSet = new ArrayList<>(); //wierzcholki na ktorych juz bylismy

        openSet.add(new Node(start, null, 0, calculateDistance(start, target))); //punkt startowy, koszt zaczyna sie od 0

        int[] dx = {1, -1, 0, 0, 1, 1, -1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, 1, -1}; //mozliwe kierunki, przod tyl lewo prawo i skosy

        Node targetNode = null;

        while (!openSet.isEmpty()){ //glowna petla
            Node currentNode =  openSet.poll(); //wyciagamy wezel z najmniejszym fCost (bo szukamy najkrotszej sciezki)
            closedSet.add(currentNode.pos); //oznaczamy go jako odwiedzony

            if(currentNode.pos.equals(target)){
                targetNode = currentNode;
                break; //jelsi jestesmy u celu to konczymy szukanie
            }

            for(int i = 0; i < 8; i++){ //sprawdzamy 8 mozliwych sasiadujacych wierzkoclkow
                int nx = currentNode.pos.getX() + dx[i];
                int ny = currentNode.pos.getY() + dy[i];
                Position neighborPos = new Position(nx, ny);

                if(!isWalkable(nx, ny) && !neighborPos.equals(target)) continue; //pomijamy jeśli nie można na niego wejść i nie jest to nasz cel
                if(closedSet.contains(neighborPos)) continue; //jesli juz na nim bylismy to pomijamy

                int moveCostToNeighbor = currentNode.gCost + 1; //zwiekszamy koszt podróży do tego wierzchołka (przejscie na kazdy kolejny wierzcholek ma koszt 1)

                boolean inOpenSet = false;
                for(Node node : openSet){ //przeszukujemy wierzcholki z openSet
                    if(node.pos.equals(neighborPos)){ //jesli w openSet jest wierzcholek ktory sprawdzamy
                        inOpenSet = true;
                        if(moveCostToNeighbor < node.gCost){ //yo jesli koszt dojscia do aktualnego wierzcholka aktualna sciezka jest mniejszy niż do tego co mamy w open set
                            node.gCost = moveCostToNeighbor;
                            node.parent = currentNode; //to zmieniamy rodzica (wierzch9olek z ktorego do niego dochodzimy) i koszt tej podróży na ten mniejszy
                        }
                        break;
                    }
                }
                if(!inOpenSet){ //jesli tego sprawdzanego wierzcholka nie bylo w openSet, to go dodajemy
                    int hCost = calculateDistance(neighborPos, target);
                    openSet.add(new Node(neighborPos, currentNode, moveCostToNeighbor, hCost));
                    //     pozycja gdzie jest (obok)| pozycja z ktorej idziemy
                }
            }
        }
        if(targetNode == null) return null; //jesli openSet jest pusty a targetNode nie osiągnięty, to znaczy ze nie ma do niego drogi

        List<Position> finalPath = new ArrayList<>();
        Node currentNode = targetNode;

        while(currentNode != null){ //w tej petli odtwarzamy sciezke po rodzicach wierzcholka, to jest jakby historia tej drogi, z ktorego wierzcholka na ktory szlismy, odwtwarza sie od celu do startu
            finalPath.add(currentNode.pos);
            currentNode = currentNode.parent;
        }

        Collections.reverse(finalPath); //trzeba odwrocic zeby bylo od startu do celu

        if(!finalPath.isEmpty()){
            finalPath.remove(0); //usuwamy pierwsze pole (start), bo agent na nim już stoi
        };

        if(!finalPath.isEmpty()) return finalPath;
        return null;
    }


    public int getMaxBottleAmount(){
        int sum = 0;
        for (MapElement element : elements) {
            sum += element.getCapacity();
        }
        return sum;
    }

    public int everyBottleMachineCapacity(){
        int sum = 0;
        for (MapElement element : elements) {
            if(element instanceof BottleMachine) {
                sum += element.getCapacity();
            }
        }
        return sum;
    }

    public int everyTrashBinCapacity(){
        int sum = 0;
        for (MapElement element : elements) {
            if(element instanceof TrashBin) {
                sum += element.getCapacity();
            }
        }
        return sum;
    }

    public Position getRandomPosition() {
        int Xrange = width;
        int Yrange = height;

        int randX;
        int randY;
        do {
            randX = (int) (Math.random() * Xrange);
            randY = (int) (Math.random() * Yrange);
        } while (!isWalkable(randX, randY));
        return new Position(randX, randY);
    }

    public void addLitter(int amount){
        litterAmount += amount;
    }

    public int getLitterAmount(){
        return litterAmount;
    }

    public double getLitterLevel(){
        double percentage = (litterAmount / (double)(everyTrashBinCapacity() + everyBottleMachineCapacity())) * 100;

        return Math.round(percentage * 100.0) / 100.0;
    }

    public int brokenMachinesAmount(){
        int sum = 0;

        for (BottleMachine machine : getBottleMachines()) {
            if(machine.isBroken()) sum++;
        }

        return sum;
    }

    public void setTileType(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            terrainGrid[y][x] = type;
        }
    }

    public double richestAgent(){
        ArrayList<Agent> agents = getAgents();
        double highestBalance = 0;
        for(int i = 0; i < agents.size(); i++){
            if(agents.get(i).getBalance() > highestBalance){
                highestBalance = agents.get(i).getBalance();
            }
        }
        return highestBalance;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public TileType getTileType(int x, int y) { return terrainGrid[y][x]; }
    public ArrayList<Zone> getZones() { return zones; }
}
