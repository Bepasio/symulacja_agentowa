package pl.butelkomat.simulation.engine;

public class TimeManager {
    private static final float BASE_TICK_DURATION = 0.5f; // bazowy takt 0,5s [rzeczywiste]
    private float speedMultiplier = 1.0f; // mnożnik prędkości GUI
    private float timeAccumulator = 0.0f;
    private long totalTicks = 0;

    public int update(float deltaTime) {
        timeAccumulator += deltaTime;
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
        // ruch co 2 takty (1 sekundę czasu symulacji)
        return totalTicks % 2 == 0;
    }

    public void setSpeedMultiplier(float multiplier) {
        if (multiplier > 0) this.speedMultiplier = multiplier;
    }

    public float getSpeedMultiplier() { return speedMultiplier; }
    public long getTotalTicks() { return totalTicks; }
}