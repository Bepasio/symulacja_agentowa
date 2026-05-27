package pl.butelkomat.simulation.engine;

public class SimulationEngine {
//jakas petle zorbic i kazda iteracja wywoluje step() u agentow
//musi losowac awarie, zmiany pogody

    /*
    private TimeManager timeManager;
    private WorldMap worldMap;

    public SimulationEngine() {
        this.timeManager = new TimeManager();
        this.worldMap = new WorldMap();
    }

    // Wywoływane w głównej metodzie render() w LibGDX
    public void renderCycle() {
        // Pobieramy czas klatki z LibGDX (np. ~0.016s dla 60 FPS)
        float dt = Gdx.graphics.getDeltaTime();

        // Aktualizujemy manager czasu i sprawdzamy czy wybił jakiś takt
        int ticksToProcess = timeManager.update(dt);

        for (int i = 0; i < ticksToProcess; i++) {
            // Przetwarzamy logikę dokładnie tyle razy, ile taktów minęło
            processSimulationTick();
        }
    }

    private void processSimulationTick() {
        // Każdy agent dostaje informację o aktualnym stanie czasu
        boolean movePhase = timeManager.shouldAgentsMove();

        // Przykładowa pętla aktualizująca agentów
        // Każdemu agentowi przekazujemy flagę, czy w tym takcie może się przemieścić
        for (Agent agent : worldMap.getAgents()) {
            agent.update(movePhase);
        }
    }

    public TimeManager getTimeManager() {
        return timeManager;
     */

}
