package pl.butelkomat.simulation.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import pl.butelkomat.simulation.agents.Agent;
import pl.butelkomat.simulation.agents.Collector;
import pl.butelkomat.simulation.agents.Consumer;
import pl.butelkomat.simulation.engine.SimulationEngine;
import pl.butelkomat.simulation.infrastructure.BottleMachine;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.DataLoader;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.ElementType;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.ArrayList;

public class SimulationGame extends ApplicationAdapter {
    private SimulationEngine engine;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private final int TILE_SIZE = 18;

    private Stage stage;
    private Skin skin;
    private Slider speedSlider;
    private Label speedLabel;

    // Etykiety statystyk
    private Label consumersLabel;
    private Label collectorsLabel;
    private Label wholeBottlesLabel;
    private Label consumerBottlesLabel;
    private Label collectorBottlesLabel;
    private Label bottleMachinesBottleLabel;
    private Label trashBinsBottleLabel;
    private Label bottleMachinesLabel;
    private Label trashBinsLabel;

    // Logi
    private TextArea logsTextArea;
    private ScrollPane logsScrollPane;

    // tekstury
    private Texture textureWater;
    private Texture textureGrass;
    private Texture texturePath;
    private Texture textureWall;      // ściana/budynek
    private Texture textureBottle;
    private Texture textureTrash;

    private Texture textureConsumer;
    private Texture textureCollector;

    //czas
    private Label timeLabel;

    /**
     * Tworzy teksturę PNG o rozmiarze TILE_SIZE x TILE_SIZE z danym kolorem
     */
    private Texture createTextureFromColor(Color color) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * Tworzy teksturę PNG z wzorem/teksturą (np. trawa, woda)
     */
    private Texture createPatternTexture(Color baseColor, Color accentColor) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        // Wypełnij bazowym kolorem
        pixmap.setColor(baseColor);
        pixmap.fill();
        // Dodaj wzór
        pixmap.setColor(accentColor);
        for (int i = 0; i < TILE_SIZE; i += 4) {
            pixmap.drawPixel(i, i);
            pixmap.drawPixel(i + 2, i + 2);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * zostawcie tę sekcję bo drawable jest do koloru suwaka!!!
     */
    private Drawable createGrayDrawable(Color color) {
        Pixmap pixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void create() {
        LoggerService.getInstance().log("=== Starowanie symulacji ===");

        WorldMap worldMap = new WorldMap(135, 39);

        // 1. Ładowanie tła z ASCII
        worldMap.loadBackgroundFromAscii("cfg/wroclaw_map.txt");

        // 2. Ładowanie Twoich plików konfiguracyjnych!
        //tutaj musi byc input i bedziemy wskazywac ile jakich agentow dodajemy
        DataLoader loader = new DataLoader();
        loader.loadZones(worldMap, "cfg/zones.txt");
        loader.loadElements(worldMap, "cfg/butelkomats.txt", ElementType.BOTTLE_MACHINE);
        loader.loadElements(worldMap, "cfg/trashBins.txt", ElementType.TRASH_BIN);
        addCollectors(50, worldMap);
        addConsumers(50, worldMap);
        //te 50tki zamienic zmienna ktora bedzie inputowana

        engine = new SimulationEngine(worldMap);
        engine.getTimeManager().setSpeedMultiplier(1.0f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // zmiana renderu na sprite
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();  // zostawcie to do UI jakby się z...epsuło

        /* TUTAJ BYŁO SZPONCENIE TEKSTUR W RAMIE (SPRITE'Y BEZUŻYTECZNE)
        // ładowanie tekstur PNG dla terenu
        textureWater = createPatternTexture(new Color(0.2f, 0.5f, 0.8f, 1.0f), new Color(0.1f, 0.3f, 0.6f, 1.0f));
        textureGrass = createPatternTexture(new Color(0.2f, 0.7f, 0.2f, 1.0f), new Color(0.1f, 0.5f, 0.1f, 1.0f));
        texturePath = createTextureFromColor(new Color(0.9f, 0.7f, 0.5f, 1.0f));  // Beż/piasek
        textureWall = createTextureFromColor(new Color(0.5f, 0.5f, 0.5f, 1.0f));   // Szarość dla ścian
//        textureBottle = createTextureFromColor(new Color(0.2f, 0.8f, 0.2f, 1.0f)); // Zielony
//        textureTrash = createTextureFromColor(new Color(0.7f, 0.7f, 0.7f, 1.0f));  // Szary
        // Jasno szary (Śmietnik)
        textureTrash = createTextureFromColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));

        // Wyraźny żółty (Butelkomat)
        textureBottle = createTextureFromColor(new Color(1.0f, 0.9f, 0.1f, 1.0f));
        textureConsumer = createTextureFromColor(new Color(0.8f, 0.2f, 0.2f, 1.0f)); // Czerwony consumer
        textureCollector = createTextureFromColor(new Color(0.2f, 0.2f, 0.8f, 1.0f)); // Niebieski collector
        */

        // Wczytywanie prawdziwych plików PNG z folderu assets/textures/
        textureWater = new Texture(Gdx.files.internal("textures/water.png"));
        textureGrass = new Texture(Gdx.files.internal("textures/grass.png"));
        texturePath = new Texture(Gdx.files.internal("textures/path.png"));
        textureWall = new Texture(Gdx.files.internal("textures/wall.png"));
        textureTrash = new Texture(Gdx.files.internal("textures/trash.png"));
        textureBottle = new Texture(Gdx.files.internal("textures/bottle.png"));
        textureConsumer = new Texture(Gdx.files.internal("textures/consumer.png"));
        textureCollector = new Texture(Gdx.files.internal("textures/collector.png"));

        // init UI z Scene2D
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // tworzenie tabeli UI
        Table table = new Table();
        table.setFillParent(true);
        table.top().left().pad(10);

        // label wyświetlający aktualną prędkość
        speedLabel = new Label("Predkosc: 1.0x", skin);
        table.add(speedLabel).padBottom(5).row();

        // slider do kontroli prędkości (na razie zakres 0.1x do 5.0x)
        // zakres 30x na razie bo powyzej juz laguje animacja
        speedSlider = new Slider(0.1f, 300.0f, 0.1f, false, skin);
        speedSlider.setValue(1.0f);

        // a tu macie setowanie koloru suwaka do prędkości (na razie)
        Slider.SliderStyle sliderStyle = speedSlider.getStyle();
        sliderStyle.background = createGrayDrawable(new Color(0.3f, 0.3f, 0.3f, 1.0f));      // Ciemnoszary background (ścieżka)
        sliderStyle.knob = createGrayDrawable(new Color(0.7f, 0.7f, 0.7f, 1.0f));             // Siedmiodziesiątoszary knob
        sliderStyle.knobBefore = createGrayDrawable(new Color(0.5f, 0.5f, 0.5f, 1.0f));       // Średnio szary dla części "przesunięty"

        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float newSpeed = speedSlider.getValue();
                engine.getTimeManager().setSpeedMultiplier(newSpeed);
                speedLabel.setText(String.format("Predkosc: %.1fx", newSpeed));
            }
        });
        table.add(speedSlider).width(150).padBottom(15).row();

        TextButton pauseButton = new TextButton("Pauza", skin);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                //zmienia stan w simulationEngine
                engine.togglePause();

                if (engine.isPaused()) {
                    pauseButton.setText("Wznow");
                    LoggerService.getInstance().log("--- SYMULACJA WSTRZYMANA ---");
                } else {
                    pauseButton.setText("Pauza");
                    LoggerService.getInstance().log("--- SYMULACJA WZNOWIONA ---");
                }
            }
        });
        table.add(pauseButton).width(150).padBottom(20).row();

        //czas
        timeLabel = new Label("Zegar: Poniedzialek 00:00", skin);
        timeLabel.setColor(Color.YELLOW);
        table.add(timeLabel).padBottom(15).row();

        // full staty
        // Inicjalizacja etykiet statystyk
        consumersLabel = new Label("Konsumenci: 0", skin);
        table.add(consumersLabel).padBottom(3).row();

        collectorsLabel = new Label("Kolektorzy: 0", skin);
        table.add(collectorsLabel).padBottom(3).row();

        bottleMachinesLabel = new Label("Butelkomaty: 0", skin);
        table.add(bottleMachinesLabel).padBottom(3).row();

        trashBinsLabel = new Label("Smietniky: 0", skin);
        table.add(trashBinsLabel).padBottom(3).row();

        wholeBottlesLabel = new Label("Butelki: 0", skin);
        table.add(wholeBottlesLabel).padBottom(3).row();

        consumerBottlesLabel = new Label("Butelki: 0", skin);
        table.add(consumerBottlesLabel).padBottom(3).row();

        collectorBottlesLabel = new Label("Butelki: 0", skin);
        table.add(collectorBottlesLabel).padBottom(3).row();

        bottleMachinesBottleLabel = new Label("Butelki: 0", skin);
        table.add(bottleMachinesBottleLabel).padBottom(3).row();

        trashBinsBottleLabel = new Label("Butelki: 0", skin);
        table.add(trashBinsBottleLabel).padBottom(3).row();


        stage.addActor(table);

        // ===== PANEL LOGÓW =====
        Table logsTable = new Table();
        logsTable.setFillParent(true);
        logsTable.top().right().pad(10);

        // Etykieta "Logi"
        Label logsTitle = new Label("Logi symulacji:", skin);
        logsTable.add(logsTitle).padBottom(5).row();

        // Pole tekstowe dla logów
        logsTextArea = new TextArea("", skin);
        logsTextArea.setDisabled(true);
        logsTextArea.setSize(350, 150);

        // ScrollPane dla tekstu
        logsScrollPane = new ScrollPane(logsTextArea, skin);
        logsScrollPane.setSize(350, 150);
        logsTable.add(logsScrollPane).width(350).height(150).row();

        stage.addActor(logsTable);
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        WorldMap map = engine.getMap();
        int h = map.getHeight();

        // Rysowanie mapy (terenu)... teraz z teksturami PNG
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                float rx = x * TILE_SIZE;
                float ry = (h - 1 - y) * TILE_SIZE;
                WorldMap.TileType tile = map.getTileType(x, y);

                Texture tileTexture = textureWall;  // domyślnie szara ściana
                if (tile != null) {
                    if (tile == WorldMap.TileType.WATER) tileTexture = textureWater;
                    else if (tile == WorldMap.TileType.GRASS) tileTexture = textureGrass;
                    else if (tile == WorldMap.TileType.PATH) tileTexture = texturePath;
                }

                spriteBatch.draw(tileTexture, rx, ry, TILE_SIZE, TILE_SIZE);
            }
        }

        // rysowanie infrastruktury z teksturami
        for (BottleMachine b : map.getBottleMachines()) {
            spriteBatch.draw(textureBottle, b.getPosition().getX() * TILE_SIZE, (h - 1 - b.getPosition().getY()) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        for (TrashBin t : map.getTrashBins()) {
            spriteBatch.draw(textureTrash, t.getPosition().getX() * TILE_SIZE, (h - 1 - t.getPosition().getY()) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        for (Agent a : map.getAgents()) {
            // Sprawdzamy, kim jest agent, żeby dobrać kolor
            Texture tex;

            if (a instanceof Consumer) {
                tex = textureConsumer;
            } else {
                tex = textureCollector;
            }

            spriteBatch.draw(tex, a.getPosition().getX() * TILE_SIZE, (h - 1 - a.getPosition().getY()) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        spriteBatch.end();

        // aktualizacja statystyk
        updateStatistics();

        // aktualizacja logów
        updateLogs();

        // aktualizacja i renderowanie UI
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * aktualizuje etykiety ze statystykami mapy
     */
    private void updateStatistics() {
        WorldMap map = engine.getMap();
        ArrayList<MapElement> elements = map.getElements();

        //aktualizacja zegara
        timeLabel.setText(engine.getTimeManager().getFormattedTime());

        // liczenie konsumentów i kolektorów
        int consumers = 0;
        int collectors = 0;
        int totalBottles = 0;
        int collectorBottles = 0;
        int consumerBottles = 0;
        int machineBottles = 0;
        int trashBinBottles = 0;
        // zlicza wszystkie butelki jakie sa w obiegu (w smietnikach/butelkomatach/plecakach)
        for (MapElement element : elements) {
            if (element instanceof Consumer) {
                consumers++;
                consumerBottles += element.getBottlesAmount();
            } else if (element instanceof Collector) {
                collectors++;
                collectorBottles += element.getBottlesAmount();
            } else if (element instanceof TrashBin) {
                trashBinBottles += element.getBottlesAmount();
            } else if (element instanceof BottleMachine) {
                machineBottles += element.getBottlesAmount();
            }
            // dodawanie butli z plecaka
            totalBottles += element.getBottlesAmount();
        }

        int bottleMachines = map.getBottleMachines().size();
        int trashBins = map.getTrashBins().size();

        // aktualizacja etykiet
        consumersLabel.setText("Konsumenci: " + consumers);
        collectorsLabel.setText("Kolektorzy: " + collectors);
        bottleMachinesLabel.setText("Butelkomaty: " + bottleMachines);
        trashBinsLabel.setText("Smietniki: " + trashBins);

        collectorBottlesLabel.setText("Wszystkie butelki collectorow: " + collectorBottles);
        consumerBottlesLabel.setText("Wszystkie butelki consumerow: " + consumerBottles);

        wholeBottlesLabel.setText("Wszystkie butelki w obiegu: " + totalBottles + "/" + map.getMaxBottleAmount());
        bottleMachinesBottleLabel.setText("Wszystkie butelki w butelkomatach: " + machineBottles + "/" + map.everyBottleMachineCapacity());
        trashBinsBottleLabel.setText("Wszystkie butelki w smietnikach: " + trashBinBottles + "/" + map.everyTrashBinCapacity());
    }

    /**
     * Aktualizuje pole logów
     */
    private void updateLogs() {
        LoggerService logger = LoggerService.getInstance();
        ArrayList<String> allLogs = logger.getLogs();

        // Weź ostatnie 20 logów
        int startIdx = Math.max(0, allLogs.size() - 20);
        StringBuilder logsText = new StringBuilder();

        for (int i = startIdx; i < allLogs.size(); i++) {
            logsText.append(allLogs.get(i)).append("\n");
        }

        logsTextArea.setText(logsText.toString());
        // Scroll do dołu
        logsScrollPane.setScrollPercentY(1.0f);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        stage.dispose();
        skin.dispose();

        // czyszczenie tekstur terenu
        textureWater.dispose();
        textureGrass.dispose();
        texturePath.dispose();
        textureWall.dispose();
        textureBottle.dispose();
        textureTrash.dispose();

        // czyszczenie tekstur z szarych drawable'ów
        Slider.SliderStyle sliderStyle = speedSlider.getStyle();
        if (sliderStyle.background instanceof TextureRegionDrawable) {
            ((TextureRegionDrawable) sliderStyle.background).getRegion().getTexture().dispose();
        }
        if (sliderStyle.knob instanceof TextureRegionDrawable) {
            ((TextureRegionDrawable) sliderStyle.knob).getRegion().getTexture().dispose();
        }
        if (sliderStyle.knobBefore instanceof TextureRegionDrawable) {
            ((TextureRegionDrawable) sliderStyle.knobBefore).getRegion().getTexture().dispose();
        }
    }

    public void addConsumers(int value, WorldMap worldMap){
        int Xrange = worldMap.getWidth();
        int Yrange = worldMap.getHeight();

        for(int i = 0; i < value; i++) {
            int randX;
            int randY;
            do {
                randX = (int) (Math.random() * Xrange);
                randY = (int) (Math.random() * Yrange);
            }while(!worldMap.isWalkable(randX, randY));
            Position consumerPos = new Position(randX, randY);
            worldMap.addElement(new Consumer(consumerPos));
        }
    }

    public void addCollectors(int value, WorldMap worldMap){
        int Xrange = worldMap.getWidth();
        int Yrange = worldMap.getHeight();

        for(int i = 0; i < value; i++) {
            int randX;
            int randY;
            do {
                randX = (int) (Math.random() * Xrange);
                randY = (int) (Math.random() * Yrange);
            }while(!worldMap.isWalkable(randX, randY));
            Position collectorPos = new Position(randX, randY);
            worldMap.addElement(new Collector(collectorPos));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}