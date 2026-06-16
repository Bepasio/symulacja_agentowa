package pl.butelkomat.simulation.engine;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.CsvStatsSaver;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.WorldMap;

public class SimulationEngine {
    private final WorldMap map;
    private final TimeManager timeManager;
    private final CsvStatsSaver csvStatsSaver;
    private boolean isPaused = false;
    private boolean hasBeenEmptied = false;

    public SimulationEngine(WorldMap map) {
        this.map = map;
        this.timeManager = new TimeManager();
        this.csvStatsSaver = new CsvStatsSaver();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public void update(float deltaTime) {
        if(isPaused) return; //jesli jest pauza, to nie zliczamy ticków, pomijamy krok pętli

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
            boolean movePhase = timeManager.shouldAgentsMove(); //jesli prawda (co 2 tick) to wtedy sie ruszaja
            for (Agent agent : map.getAgents()) {
                 agent.step(movePhase, map);
            }
            csvStatsSaver.recordSnapshot(this);
        }
    }

    public WorldMap getMap() { return map; }
    public TimeManager getTimeManager() { return timeManager; }
    public CsvStatsSaver getCsvStatsSaver() { return csvStatsSaver; }
}
