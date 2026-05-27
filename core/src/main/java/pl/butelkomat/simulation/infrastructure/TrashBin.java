package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;

import java.util.Stack;

public class TrashBin implements MapElement {
    private final int capacity;
    private final Stack<Bottle> bottles; //wrzucamy butelki na stos, jak collector wyciąga je z kosza to bierze 'od gory'
    private Position position;

    public TrashBin(int capacity, Position position) {
        this.capacity = capacity;
        this.bottles = new Stack<>();
        this.position = position;
    }

    public boolean canCollectBottle() {
        return bottles.size() < capacity;
    }

    public boolean addBottle(Bottle bottle) {
        if(!canCollectBottle()){
            return false;
        }
        bottles.push(bottle);
        return true;
    }

    public int getCurrentFillLevel(){
        return bottles.size();
    }

    public int getCapacity(){
        return capacity;
    }

    public Bottle takeBottle(){
        if(!bottles.isEmpty()){
            return bottles.pop();
        }
        return null;
    }

    public Position getPosition(){
        return position;
    }
}
