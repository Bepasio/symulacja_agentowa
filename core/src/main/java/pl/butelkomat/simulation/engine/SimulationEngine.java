package pl.butelkomat.simulation.engine;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.world.WorldMap;

public class SimulationEngine {
    private final WorldMap map;
    private final TimeManager timeManager;

    public SimulationEngine(WorldMap map) {
        this.map = map;
        this.timeManager = new TimeManager();
    }

    public void update(float deltaTime) {
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
