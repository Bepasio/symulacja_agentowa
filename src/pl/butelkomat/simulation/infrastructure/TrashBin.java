package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;

import java.util.Stack;

public class TrashBin implements Interactable{
    private final int capacity;
    private final Stack<Bottle> bottles; //wrzucamy butelki na stos, jak collector wyciąga je z kosza to bierze 'od gory'
    private boolean canCollectBottle;

    public TrashBin(int capacity) {
        this.capacity = capacity;
        this.bottles = new Stack<>();
        this.canCollectBottle = true; //domyslnie kazdy moze przyjac
    }

    public boolean addBottle(Bottle bottle) {
        if(bottles.size() < capacity) {
            bottles.push(bottle);
            if(bottles.size() == capacity) {
                canCollectBottle = false;
            }
            return true;
        }
        return false;
    }

    public boolean canCollectBottle(){
        return canCollectBottle;
    }

    public int getCurrentFillLevel(){
        return bottles.size();
    }

    public int getCapacity(){
        return capacity;
    }

    public Bottle takeBottle(){
        if(!bottles.isEmpty()){
            if(bottles.size()-1 < capacity){
                canCollectBottle = true;
            }
            return bottles.pop();
        }
        return null;
    }
}
