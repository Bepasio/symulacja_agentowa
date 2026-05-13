package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;

import java.util.Stack;

public class TrashBin implements Interactable{
    private final int capacity;
    private final Stack<Bottle> bottles; //wrzucamy butelki na stos, jak collector wyciąga je z kosza to bierze 'od gory'
//    private boolean canCollectBottle;

    public TrashBin(int capacity) {
        this.capacity = capacity;
        this.bottles = new Stack<>();
//        this.canCollectBottle = true; //domyslnie kazdy moze przyjac
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
}
