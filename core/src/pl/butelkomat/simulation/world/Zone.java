package pl.butelkomat.simulation.world;
//tutej deklarujemy size strefy i jaki ma mnoznik
public class Zone {
    private String name;
    private int startX;
    private int endX;
    private int startY;
    private int endY;
    private double multiplier; //mnoznik strefy np. do generowania butelek przez consumera

    public Zone(String name, int startX, int endX, int startY, int endY, double multiplier) {
        if (startX >= endX || startY >= endY) {
            throw new IllegalArgumentException("Współrzędne końcowe strefy muszą być większe niż początkowe!");
        }
        this.name = name;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.multiplier = multiplier;
    }

    public boolean isInZone(Position position) {
        return position.getX() >= startX && position.getX() <= endX && position.getY() >= startY && position.getY() <= endY;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getName() {
        return name;
    }

    public int getStartX() {
        return startX;
    }
    public int getEndX() {
        return endX;
    }
    public int getStartY() {
        return startY;
    }
    public int getEndY() {
        return endY;
    }

    public String toString() {
        return name;
    }
}
