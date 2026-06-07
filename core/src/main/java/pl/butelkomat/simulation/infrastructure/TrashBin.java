package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.ElementType;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;

import java.util.Stack;

public class TrashBin implements MapElement, Interactable {
    private final int capacity = 50;
    private final Stack<Bottle> bottles; //wrzucamy butelki na stos, jak collector wyciąga je z kosza to bierze 'od gory'
    private Position position;

    public TrashBin(Position position) {
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

    public int getBottlesAmount() {
        return bottles.size();
    }

    public int getCapacity(){
        return capacity;
    }

    public void emptying(){
        bottles.clear();
    }

    public boolean isFull(){
        return bottles.size() == capacity;
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

    public ElementType getElementType() {
        return ElementType.TRASH_BIN;
    }
}
