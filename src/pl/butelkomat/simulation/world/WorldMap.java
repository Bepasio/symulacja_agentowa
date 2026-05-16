package pl.butelkomat.simulation.world;

import pl.butelkomat.simulation.infrastructure.Butelkomat;
import pl.butelkomat.simulation.infrastructure.TrashBin;

import java.util.ArrayList;

public class WorldMap {
//    tu trzeba dac rozmiar width x height
//    strefy dac do jakiejs tablicy zones, definiuje mnoznik
//    dwie listy (butelkomat i smietnik) ktore beda przechowywac kordy gdzie sie znajduja
    private int width; //x
    private int height; //y
    //mapa to przedzial (0, x), (0, y), czyli np. jej szerokosc przy iteracji to width-1
    private ArrayList<Butelkomat> butelkomats;
    private ArrayList<TrashBin> trashBins;
    private ArrayList<Zone> zones;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.butelkomats = new ArrayList<>();
        this.trashBins = new ArrayList<>();
        this.zones = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public void addButelkomat(Butelkomat butelkomat) {
        if(butelkomat.getPosition().getX() < 0 || butelkomat.getPosition().getX() > this.width || butelkomat.getPosition().getY() < 0 || butelkomat.getPosition().getY() > this.height){
            System.out.println("BLAD: Butelkomat poza mapa");
            return;
        }
        butelkomats.add(butelkomat);
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

    public double getMultiplierAt(Position position) {
        for (Zone zone : zones) {
            if(zone.isInZone(position)){
                return zone.getMultiplier();
            }
        }
        return 1.0; //domyslnie mnoznik to 1
    }

}
