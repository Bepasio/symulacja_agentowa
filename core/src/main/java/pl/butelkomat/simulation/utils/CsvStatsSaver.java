package pl.butelkomat.simulation.utils;

import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.agents.Collector;
import pl.butelkomat.simulation.agents.Consumer;
import pl.butelkomat.simulation.engine.SimulationEngine;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.world.WorldMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvStatsSaver {
    private static class StatsRecord {
        long tick;
        String day;
        int hour;
        int minute;
        int consumersCount;
        int collectorsCount;
        int consumerBottles;
        int collectorBottles;
        int trashBinBottles;
        int bottleMachineBottles;
        int litterBottles;
        double litterLevelPercent;
        int brokenMachinesCount;
        double richestAgentBalance;
        double averageAgentBalance;
        double totalMoney;
    }

    private final List<StatsRecord> records = new ArrayList<>();
    private boolean isSaved = false;

    public synchronized void recordSnapshot(SimulationEngine engine) {
        WorldMap map = engine.getMap();
        long totalTicks = engine.getTimeManager().getTotalTicks();
        String day = engine.getTimeManager().getDayOfWeek();
        int hour = engine.getTimeManager().getHour();
        int minute = engine.getTimeManager().getMinute();

        ArrayList<Agent> agents = map.getAgents();
        int consumersCount = 0;
        int collectorsCount = 0;
        int consumerBottles = 0;
        int collectorBottles = 0;
        double totalBalance = 0;
        double richestBalance = 0;

        for (Agent agent : agents) {
            double bal = agent.getBalance();
            totalBalance += bal;
            if (bal > richestBalance) {
                richestBalance = bal;
            }

            if (agent instanceof Consumer) {
                consumersCount++;
                consumerBottles += agent.getBottlesAmount();
            } else if (agent instanceof Collector) {
                collectorsCount++;
                collectorBottles += agent.getBottlesAmount();
            }
        }

        double averageBalance = agents.isEmpty() ? 0.0 : totalBalance / agents.size();

        int trashBinBottles = 0;
        for (TrashBin bin : map.getTrashBins()) {
            trashBinBottles += bin.getBottlesAmount();
        }

        int bottleMachineBottles = 0;
        for (BottleMachine machine : map.getBottleMachines()) {
            bottleMachineBottles += machine.getBottlesAmount();
        }

        int litterBottles = map.getLitterAmount();
        double litterLevelPercent = map.getLitterLevel();
        int brokenMachinesCount = map.brokenMachinesAmount();

        StatsRecord record = new StatsRecord();
        record.tick = totalTicks;
        record.day = day;
        record.hour = hour;
        record.minute = minute;
        record.consumersCount = consumersCount;
        record.collectorsCount = collectorsCount;
        record.consumerBottles = consumerBottles;
        record.collectorBottles = collectorBottles;
        record.trashBinBottles = trashBinBottles;
        record.bottleMachineBottles = bottleMachineBottles;
        record.litterBottles = litterBottles;
        record.litterLevelPercent = litterLevelPercent;
        record.brokenMachinesCount = brokenMachinesCount;
        record.richestAgentBalance = richestBalance;
        record.averageAgentBalance = averageBalance;
        record.totalMoney = totalBalance;

        records.add(record);
    }

    public synchronized void saveToCsv(String filePath) {
        if (isSaved) {
            return;
        }

        File targetFile = new File(filePath);
        if (!targetFile.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            if (userDir != null) {
                File dir = new File(userDir);
                if (dir.getName().equalsIgnoreCase("assets")) {
                    targetFile = new File(dir.getParentFile(), filePath);
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            // CSV Header
            writer.println("Tick,Day,Hour,Minute,ConsumersCount,CollectorsCount,ConsumerBottles,CollectorBottles,TrashBinBottles,BottleMachineBottles,LitterBottles,LitterLevelPercent,BrokenMachinesCount,RichestAgentBalance,AverageAgentBalance,TotalMoney");

            for (StatsRecord record : records) {
                writer.println(String.format(Locale.US,
                    "%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%.2f,%d,%.2f,%.2f,%.2f",
                    record.tick,
                    record.day,
                    record.hour,
                    record.minute,
                    record.consumersCount,
                    record.collectorsCount,
                    record.consumerBottles,
                    record.collectorBottles,
                    record.trashBinBottles,
                    record.bottleMachineBottles,
                    record.litterBottles,
                    record.litterLevelPercent,
                    record.brokenMachinesCount,
                    record.richestAgentBalance,
                    record.averageAgentBalance,
                    record.totalMoney
                ));
            }
            isSaved = true;
            LoggerService.getInstance().log("Dane statystyczne zostaly zapisane do pliku: " + filePath);
        } catch (IOException e) {
            LoggerService.getInstance().logError("Blad podczas zapisu statystyk do CSV: " + e.getMessage());
        }
    }
}
