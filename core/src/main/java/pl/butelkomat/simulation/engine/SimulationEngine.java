package pl.butelkomat.simulation.engine;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.world.WorldMap;

public class SimulationEngine {
    private final WorldMap map;
    private final TimeManager timeManager;
    private boolean isPaused = false;

    public SimulationEngine(WorldMap map) {
        this.map = map;
        this.timeManager = new TimeManager();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public void update(float deltaTime) {
        if(isPaused) return;

        int ticks = timeManager.update(deltaTime);
        for (int i = 0; i < ticks; i++) {
            boolean movePhase = timeManager.shouldAgentsMove();
            for (Agent agent : map.getAgents()) {
                 agent.step(movePhase, map);
            }
        }
    }

    public WorldMap getMap() { return map; }
    public TimeManager getTimeManager() { return timeManager; }
}
