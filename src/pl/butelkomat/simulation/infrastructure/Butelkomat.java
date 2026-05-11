package pl.butelkomat.simulation.infrastructure;

import pl.butelkomat.simulation.item.Bottle;

import java.util.ArrayList;

public class Butelkomat implements Interactable{
//    jak butla jest refundable to przyjmuje
//    trzeba okreslic pojemnosc, czy dziala, czy ma papier do drukowania
    private final int capacity;
    private final ArrayList<Bottle> bottles;
    private boolean canCollectBottle;

    public Butelkomat(int capacity){
        this.capacity = capacity;
        this.bottles = new ArrayList<>();
        this.canCollectBottle = true;
    }

    public boolean addBottle(Bottle bottle) {
        if(!bottle.isRefundable()){return false;}
        if(bottles.size() < capacity){
            bottles.add(bottle);
            if(bottles.size() == capacity){
                canCollectBottle = false;
            }
            return true;
        }
        return false;
    }

    public void emptying(){
        bottles.clear();
        canCollectBottle = true;
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
}
