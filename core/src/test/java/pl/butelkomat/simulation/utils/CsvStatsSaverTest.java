package pl.butelkomat.simulation.utils;

import org.junit.jupiter.api.Test;
import pl.butelkomat.simulation.engine.SimulationEngine;
import pl.butelkomat.simulation.world.WorldMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvStatsSaverTest {
    @Test
    public void shouldRecordStatsAndWriteCsv() throws IOException {
        WorldMap map = new WorldMap(10, 10);
        SimulationEngine engine = new SimulationEngine(map);
        
        // Simulating some ticks
        engine.update(1.0f); // this triggers timeManager and updates ticks/snapshots
        
        CsvStatsSaver saver = engine.getCsvStatsSaver();
        File tempFile = File.createTempFile("test_simulation_stats", ".csv");
        tempFile.deleteOnExit();

        saver.saveToCsv(tempFile.getAbsolutePath());

        assertTrue(tempFile.exists(), "CSV stats file should be created");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String header = reader.readLine();
            assertTrue(header != null && header.contains("Tick"), "Header should contain Tick column");
            assertTrue(header.contains("ConsumersCount"), "Header should contain ConsumersCount column");
            assertTrue(header.contains("TotalMoney"), "Header should contain TotalMoney column");
        }
    }
}
