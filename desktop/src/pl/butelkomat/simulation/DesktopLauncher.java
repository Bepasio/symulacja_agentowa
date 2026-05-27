package pl.butelkomat.simulation;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pl.butelkomat.simulation.gui.SimulationGame;

public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Symulacja Agentowa - Wrocław");
        // Rozmiar okna dopasowany do mapy 90x26 kafelków (zakładając TILE_SIZE = 12)
        config.setWindowedMode(1080, 720);
        new Lwjgl3Application(new SimulationGame(), config);
    }
}
