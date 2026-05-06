package pl.butelkomat.simulation.item;

//tu chyba wystarczy przechowywac info o tym czy jest kaucyjna ale do przemyslenia

public class Bottle {
    public boolean isRefundable;

    public Bottle(boolean isRefundable){
        this.isRefundable = isRefundable;
    }

    public String toString() {
        return Boolean.toString(isRefundable);
    }
}
