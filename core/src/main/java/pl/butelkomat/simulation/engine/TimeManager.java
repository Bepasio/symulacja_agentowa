package pl.butelkomat.simulation.engine;

public class TimeManager {
    private static final float BASE_TICK_DURATION = 0.5f; // bazowy takt 0,5s [rzeczywiste]
    private float speedMultiplier = 1.0f; // mnoznik predkosci GUI
    private float timeAccumulator = 0.0f;
    private long totalTicks = 0;

    //1 tick - 1 minuta; mysle ze 2tick/1minute to przesada (minuta po 120 tickach, w ciagu minuty agent moze przejsc 2/3 mapy)
    private static final int ticksPerMinute = 1;
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public int update(float deltaTime) {
        timeAccumulator += deltaTime;

        if (speedMultiplier <= 0) return 0;

        float currentTickDuration = BASE_TICK_DURATION / speedMultiplier;
        int ticksFired = 0;

        while (timeAccumulator >= currentTickDuration) {
            totalTicks++;
            ticksFired++;
            timeAccumulator -= currentTickDuration;
        }
        return ticksFired;
    }

    public boolean shouldAgentsMove() {
        // ruch co 2 takty
        return totalTicks % 2 == 0;
    }

    public void setSpeedMultiplier(float multiplier) {
        if (multiplier > 0) this.speedMultiplier = multiplier;
    }

    public float getSpeedMultiplier() { return speedMultiplier; }
    public long getTotalTicks() { return totalTicks; }

    public int getMinute() {
        return (int) ((totalTicks / ticksPerMinute) % 60);
    }

    public int getHour() {
        return (int) ((totalTicks / (ticksPerMinute * 60)) % 24);
    }

    public String getDayOfWeek() {
        int totalDays = (int) (totalTicks / (ticksPerMinute * 60 * 24));
        return daysOfWeek[totalDays % 7];
    }

    public int getDayOfWeekIndex() {
        return (int) ((totalTicks / (ticksPerMinute * 60 * 24)))%7;
    }

    public String getFormattedTime() {
        return String.format("%s %02d:%02d", getDayOfWeek(), getHour(), getMinute());
    }
}