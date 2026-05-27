package pl.butelkomat.simulation.engine;

public class  TimeManager {
    // bazowy pojedynczy takt to 0,5s
    private static final float BASE_TICK_DURATION = 0.5f;

    private float speedMultiplier = 1.0f; // mnożnik kontrolowany z gui
    private float timeAccumulator = 0.0f;
    private long totalTicks = 0;

    /**
     * Metoda wywoływana w pętli renderującej gry (libgdx).
     * @param deltaTime Czas jaki upłynął od ostatniej klatki (w sekundach)
     * @return Zwraca liczbę taktów, które wybiły w tym wywołaniu
     */
    public int update(float deltaTime) {
        // dodanie czasu, który upłynął w kompie
        timeAccumulator += deltaTime;

        // obliczanie prędkości obecnego taktu (dla multiplier=2.0 takt=0,25s
        float currentTickDuration = BASE_TICK_DURATION / speedMultiplier;

        int ticksFired = 0;

        // WHILE (zamiast if) MUSI ZOSTAĆ, BO WTEDY NIE ZGUBIMY KLATEK
        while (timeAccumulator >= currentTickDuration) {
            totalTicks++;
            ticksFired++;
            timeAccumulator -= currentTickDuration;
        }

        return ticksFired;
    }

    // dynamiczna zmiana prędkości z GUI
    public void setSpeedMultiplier(float multiplier) {
        if (multiplier > 0) {
            this.speedMultiplier = multiplier;
        }
    }

    public long getTotalTicks() {
        return totalTicks;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Sprawdza, czy w bieżącym takcie agenci mają prawo wykonać ruch.
     * Ruch odbywa się co 2 takty (czyli co 1.0s czasu symulacji).
     */
    public boolean shouldAgentsMove() {
        return totalTicks % 2 == 0;
    }
}
