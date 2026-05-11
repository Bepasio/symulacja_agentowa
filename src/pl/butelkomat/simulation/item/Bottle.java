package pl.butelkomat.simulation.item;

public class Bottle {
    private final boolean isRefundable;

    public Bottle(boolean isRefundable){
        this.isRefundable = isRefundable;
    }

    public boolean isRefundable() {
        return isRefundable;
    }

    public String toString() {
        return "Bottle(refundable=" + isRefundable + ")";
    }
}


