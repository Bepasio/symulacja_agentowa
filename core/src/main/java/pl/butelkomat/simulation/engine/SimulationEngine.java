package pl.butelkomat.simulation.engine;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.WorldMap;

public class SimulationEngine {
    private final WorldMap map;
    private final TimeManager timeManager;
    private boolean isPaused = false;
    private boolean hasBeenEmptied = false;

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
            //oproznianie butelkomatu i ladowanie papieru raz w tygodniu
            if(timeManager.getDayOfWeekIndex() == 5 && timeManager.getHour() > 6){
                if(!hasBeenEmptied) {
                    for (BottleMachine machine : map.getBottleMachines()) {
                        machine.emptying();
                        machine.paperRefill();
                        machine.repair();
                    }
                    for (TrashBin bin : map.getTrashBins()) {
                        bin.emptying();
                    }
                    hasBeenEmptied = true;
                    LoggerService.getInstance().log("Służby miejskie opróżniły śmietniki i butelkomaty!");
                }
                }else if(timeManager.getDayOfWeekIndex() != 5){
                hasBeenEmptied = false;
            }
            boolean movePhase = timeManager.shouldAgentsMove();
            for (Agent agent : map.getAgents()) {
                 agent.step(movePhase, map);
            }
        }
    }

    public WorldMap getMap() { return map; }
    public TimeManager getTimeManager() { return timeManager; }
}
