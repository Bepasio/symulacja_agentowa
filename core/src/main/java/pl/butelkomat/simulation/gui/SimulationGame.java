package pl.butelkomat.simulation.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pl.butelkomat.simulation.engine.SimulationEngine;
import pl.butelkomat.simulation.infrastructure.Butelkomat;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.DataLoader;
import pl.butelkomat.simulation.world.WorldMap;

public class SimulationGame extends ApplicationAdapter {
    private SimulationEngine engine;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private final int TILE_SIZE = 12;

    @Override
    public void create() {
        WorldMap worldMap = new WorldMap(90, 26);
        
        // 1. Ładowanie tła z ASCII
        worldMap.loadBackgroundFromAscii("cfg/wroclaw_map.txt");

        // 2. Ładowanie Twoich plików konfiguracyjnych!
        DataLoader loader = new DataLoader();
        loader.loadZones(worldMap, "cfg/zones.txt");
        loader.loadButelkomats(worldMap, "cfg/butelkomats.txt");
        loader.loadTrashBins(worldMap, "cfg/trashBins.txt");

        engine = new SimulationEngine(worldMap);
        engine.getTimeManager().setSpeedMultiplier(5.0f); // Przyspieszenie z GUI

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        WorldMap map = engine.getMap();
        int h = map.getHeight();

        // Rysowanie mapy (terenu)
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                float rx = x * TILE_SIZE;
                float ry = (h - 1 - y) * TILE_SIZE;
                WorldMap.TileType tile = map.getTileType(x, y);
                
                if (tile == null) shapeRenderer.setColor(Color.DARK_GRAY);
                else if (tile == WorldMap.TileType.WATER) shapeRenderer.setColor(Color.BLUE);
                else if (tile == WorldMap.TileType.GRASS) shapeRenderer.setColor(Color.FOREST);
                else if (tile == WorldMap.TileType.PATH) shapeRenderer.setColor(Color.CORAL);
                else shapeRenderer.setColor(Color.DARK_GRAY);

                shapeRenderer.rect(rx, ry, TILE_SIZE, TILE_SIZE);
            }
        }

        // Rysowanie infrastruktury (z Twoich plików txt)
        shapeRenderer.setColor(Color.GREEN);
        for (Butelkomat b : map.getButelkomats()) {
            shapeRenderer.rect(b.getPosition().getX() * TILE_SIZE, (h - 1 - b.getPosition().getY()) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        shapeRenderer.setColor(Color.LIGHT_GRAY);
        for (TrashBin t : map.getTrashBins()) {
            shapeRenderer.rect(t.getPosition().getX() * TILE_SIZE, (h - 1 - t.getPosition().getY()) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        shapeRenderer.end();
    }
}
