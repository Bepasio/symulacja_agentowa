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
import com.badlogic.gdx.graphics.g2d.BitmapFont; // import dla czcionki
import com.badlogic.gdx.math.Vector3;           // import dla pozycji myszy
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
import pl.butelkomat.simulation.infrastructure.Interactable;
import pl.butelkomat.simulation.infrastructure.TrashBin;
import pl.butelkomat.simulation.utils.DataLoader;
import pl.butelkomat.simulation.utils.LoggerService;
import pl.butelkomat.simulation.world.MapElement;
import pl.butelkomat.simulation.world.Position;
import pl.butelkomat.simulation.world.WorldMap;
import pl.butelkomat.simulation.world.ElementType;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.ArrayList;

public class SimulationGame extends ApplicationAdapter {
    private SimulationEngine engine;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font; // pole przechowujące czcionkę hovera
    private OrthographicCamera camera;
    private final int TILE_SIZE = 16;
    private float mapScale = 1.0f;  // skala mapy
    private final int GUI_HEIGHT = 380;  // wysokość panelu GUI (px)

    private Stage stage;
    private Skin skin;
    private Slider speedSlider;
    private Label speedLabel;

    // flagi sterujące stanem symulacji
    private boolean simulationStarted = false;
    private boolean simulationEnded = false;     // flaga zakończenia symulacji

    // zmienne do obsługi liczenia czasu (żeby po tygodniu pozytywny warunek wyznaczyć)
    private int daysElapsed = 0;                  // licznik minionych dni
    private String lastDay = "";                  // dzień z poprzedniej klatki

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
    private Label litterLevel;

    private Label brokenMachinesLabel;

    // Logi
    private TextArea logsTextArea;
    private ScrollPane logsScrollPane;

    // tekstury
    private Texture textureWater;
    private Texture textureGrass;
    private Texture texturePath;
    private Texture textureWall;
    private Texture textureBottle;
    private Texture textureTrash;

    private Texture textureConsumer;
    private Texture textureCollector;

    //czas
    private Label timeLabel;

    private int countCollectors = 50;
    private int countConsumers = 50;
    private int countMachines = 50;
    private int countBins = 50;

    private Texture createTextureFromColor(Color color) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createPatternTexture(Color baseColor, Color accentColor) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(baseColor);
        pixmap.fill();
        pixmap.setColor(accentColor);
        for (int i = 0; i < TILE_SIZE; i += 4) {
            pixmap.drawPixel(i, i);
            pixmap.drawPixel(i + 2, i + 2);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

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

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final WorldMap worldMap = new WorldMap(135, 39);
        worldMap.loadBackgroundFromAscii("cfg/wroclaw_map.txt");

        DataLoader loader = new DataLoader();
        loader.loadZones(worldMap, "cfg/zones.txt");

        engine = new SimulationEngine(worldMap);
        engine.getTimeManager().setSpeedMultiplier(1.0f);

        // ustawienie kamery na cały rozmiar okna (żeby OpenGL nie rozciągał O_O)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // przygotowanie czcionki dla tooltipów
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(1.2f);

        textureWater = new Texture(Gdx.files.internal("textures/water.png"));
        textureGrass = new Texture(Gdx.files.internal("textures/grass.png"));
        texturePath = new Texture(Gdx.files.internal("textures/path.png"));
        textureWall = new Texture(Gdx.files.internal("textures/wall.png"));
        textureTrash = new Texture(Gdx.files.internal("textures/trash.png"));
        textureBottle = new Texture(Gdx.files.internal("textures/bottle.png"));
        textureConsumer = new Texture(Gdx.files.internal("textures/consumer.png"));
        textureCollector = new Texture(Gdx.files.internal("textures/collector.png"));

        // tabela układająca UI
        final Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().pad(10);
        rootTable.setVisible(false); // pokaże się po przejściu okienka konfiguracyjnego

        // lewa sekcja (statystyki i sterowanie)
        Table leftTable = new Table();
        leftTable.top().left();

        speedLabel = new Label("Predkosc: 1.0x", skin);
        leftTable.add(speedLabel).padBottom(5).row();

        speedSlider = new Slider(0.1f, 300.0f, 0.1f, false, skin);
        speedSlider.setValue(1.0f);

        Slider.SliderStyle sliderStyle = speedSlider.getStyle();
        sliderStyle.background = createGrayDrawable(new Color(0.3f, 0.3f, 0.3f, 1.0f));
        sliderStyle.knob = createGrayDrawable(new Color(0.7f, 0.7f, 0.7f, 1.0f));
        sliderStyle.knobBefore = createGrayDrawable(new Color(0.5f, 0.5f, 0.5f, 1.0f));

        sliderStyle.knob.setMinWidth(20);
        sliderStyle.knob.setMinHeight(20);
        sliderStyle.background.setMinHeight(8);
        sliderStyle.knobBefore.setMinHeight(8);

        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float newSpeed = speedSlider.getValue();
                engine.getTimeManager().setSpeedMultiplier(newSpeed);
                speedLabel.setText(String.format("Predkosc: %.1fx", newSpeed));
            }
        });
        leftTable.add(speedSlider).width(150).padBottom(15).row();

        TextButton pauseButton = new TextButton("Pauza", skin);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (simulationEnded) return;
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
        leftTable.add(pauseButton).width(150).padBottom(20).row();

        timeLabel = new Label("Zegar: Poniedzialek 00:00", skin);
        timeLabel.setColor(Color.YELLOW);
        leftTable.add(timeLabel).padBottom(15).row();

        consumersLabel = new Label("Konsumenci: 0", skin);
        leftTable.add(consumersLabel).padBottom(3).row();

        collectorsLabel = new Label("Kolektorzy: 0", skin);
        leftTable.add(collectorsLabel).padBottom(3).row();

        bottleMachinesLabel = new Label("Butelkomaty: 0", skin);
        leftTable.add(bottleMachinesLabel).padBottom(3).row();

        trashBinsLabel = new Label("Smietniky: 0", skin);
        leftTable.add(trashBinsLabel).padBottom(3).row();

        wholeBottlesLabel = new Label("Butelki: 0", skin);
        leftTable.add(wholeBottlesLabel).padBottom(3).row();

        consumerBottlesLabel = new Label("Butelki: 0", skin);
        leftTable.add(consumerBottlesLabel).padBottom(3).row();

        collectorBottlesLabel = new Label("Butelki: 0", skin);
        leftTable.add(collectorBottlesLabel).padBottom(3).row();

        bottleMachinesBottleLabel = new Label("Butelki: 0", skin);
        leftTable.add(bottleMachinesBottleLabel).padBottom(3).row();

        trashBinsBottleLabel = new Label("Butelki: 0", skin);
        leftTable.add(trashBinsBottleLabel).padBottom(3).row();

        litterLevel = new Label("Butelki: 0", skin);
        leftTable.add(litterLevel).padBottom(3).row();

        brokenMachinesLabel = new Label("Butelki: 0", skin);
        leftTable.add(brokenMachinesLabel).padBottom(3).row();

        // prawa sekcja (logi)
        Table rightTable = new Table();
        rightTable.top().right();

        Label logsTitle = new Label("Logi symulacji:", skin);
        rightTable.add(logsTitle).padBottom(5).row();

        logsTextArea = new TextArea("", skin);
        logsTextArea.setDisabled(true);

        logsScrollPane = new ScrollPane(logsTextArea, skin);
        rightTable.add(logsScrollPane).width(350).height(130).row();

        // dodanie lewej i prawej sekcji wyrównanie (left/right)
        rootTable.add(leftTable).expandX().left().top();
        rootTable.add(rightTable).right().top();

        stage.addActor(rootTable);

        final TextField collectorsField = new TextField("50", skin);
        final TextField consumersField = new TextField("50", skin);
        final TextField machinesField = new TextField("50", skin);
        final TextField binsField = new TextField("50", skin);

        Dialog setupDialog = new Dialog("Konfiguracja poczatkowa", skin) {
            @Override
            protected void result(Object object) {
                try {
                    countCollectors = Integer.parseInt(collectorsField.getText());
                    countConsumers = Integer.parseInt(consumersField.getText());
                    countMachines = Integer.parseInt(machinesField.getText());
                    countBins = Integer.parseInt(binsField.getText());
                } catch (NumberFormatException e) {
                    LoggerService.getInstance().log("Blad parsowania liczb! Uzyto domyslnych wartosci (50).");
                }

                addCollectors(countCollectors, worldMap);
                addConsumers(countConsumers, worldMap);
                addBottleMachines(countMachines, worldMap);
                addTrashBins(countBins, worldMap);

                simulationStarted = true;
                rootTable.setVisible(true); // pokaże interfejs po zamknięciu dialogu
            }
        };

        Table dialogContent = setupDialog.getContentTable();
        dialogContent.pad(20);
        dialogContent.add(new Label("Liczba kolektorow:", skin)).left().pad(5);
        dialogContent.add(collectorsField).width(70).pad(5).row();

        dialogContent.add(new Label("Liczba konsumentow:", skin)).left().pad(5);
        dialogContent.add(consumersField).width(70).pad(5).row();

        dialogContent.add(new Label("Liczba butelkomatow:", skin)).left().pad(5);
        dialogContent.add(machinesField).width(70).pad(5).row();

        dialogContent.add(new Label("Liczba smietnikow:", skin)).left().pad(5);
        dialogContent.add(binsField).width(70).pad(5).row();

        setupDialog.button("Uruchom symulacje", true);
        setupDialog.show(stage);
    }

    @Override
    public void render() {
        if (simulationStarted && !simulationEnded) {
            engine.update(Gdx.graphics.getDeltaTime());
            checkSimulationEndConditions();
        }

        // aktualizacja kamery, żeby zawsze dopasowywała się do ekranu bez wyciągania mapy
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // przechwycenie i konwersja układu współrzędnych myszki
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 1);

        WorldMap map = engine.getMap();
        int h = map.getHeight();
        int w = map.getWidth();

        // obliczanie skali i pozycji tak, żeby mapa mieściła się w DOLNEJ DOLNEJ DOLNEJ części okna :>
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int availableHeight = screenH - GUI_HEIGHT; // odliczamy miejsce na GUI

        float scaleX = (float) screenW / (w * TILE_SIZE);
        float scaleY = (float) availableHeight / (h * TILE_SIZE);
        mapScale = Math.min(scaleX, scaleY);

        float totalMapWidth = w * TILE_SIZE * mapScale;
        float totalMapHeight = h * TILE_SIZE * mapScale;

        // centrowanie mapy na środku poziomo i w dostępnej przestrzeni pionowo
        float offsetX = (screenW - totalMapWidth) / 2;
        float offsetY = (availableHeight - totalMapHeight) / 2;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float rx = offsetX + x * TILE_SIZE * mapScale;
                float ry = offsetY + (h - 1 - y) * TILE_SIZE * mapScale;
                WorldMap.TileType tile = map.getTileType(x, y);

                Texture tileTexture = textureWall;
                if (tile != null) {
                    if (tile == WorldMap.TileType.WATER) tileTexture = textureWater;
                    else if (tile == WorldMap.TileType.GRASS) tileTexture = textureGrass;
                    else if (tile == WorldMap.TileType.PATH) tileTexture = texturePath;
                }
                spriteBatch.draw(tileTexture, rx, ry, TILE_SIZE * mapScale, TILE_SIZE * mapScale);
            }
        }

        for(MapElement e : map.getElements()) {
            Texture tex = getTextureForElement(e.getElementType());

            float size = TILE_SIZE * mapScale;
            float rx = offsetX + e.getPosition().getX() * size;
            float ry = offsetY + (h - 1 - e.getPosition().getY()) * size;

            spriteBatch.draw(tex, rx, ry, size, size);

            // sprawdzanie czy element jest agentem i czy myszka nad nim hoveruje
            if (e instanceof Agent) {
                if (mousePos.x >= rx && mousePos.x <= rx + size &&
                        mousePos.y >= ry && mousePos.y <= ry + size) {

                    String hoverText = "Butelki: " + e.getBottlesAmount();
                    // tekst nad głową agenta
                    font.draw(spriteBatch, hoverText, rx - 10, ry + size + 20);
                }
            }
        }

        spriteBatch.end();

        if (simulationStarted) {
            updateStatistics();
            updateLogs();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void checkSimulationEndConditions() {
        String reason;
        WorldMap map = engine.getMap();

        int machineBottles = 0;
        int trashBinBottles = 0;
        int brokenMachines = 0;
        for (MapElement element : map.getElements()) {
            if (element instanceof TrashBin) {
                trashBinBottles += element.getBottlesAmount();
            } else if (element instanceof BottleMachine) {
                if(((BottleMachine) element).isBroken()){
                    brokenMachines++;
                    if(brokenMachines > countMachines*0.8){
                        reason = "Więcej niż 80% butelkomatów jest zepsutych";
                        endSimulation(false, reason);
                        LoggerService.getInstance().log("Więcej niż 80% butelkomatów jest zepsutych");
                        return;
                    }
                }
                machineBottles += element.getBottlesAmount();
            }
        }

        int maxMachineCapacity = map.everyBottleMachineCapacity();
        int maxTrashCapacity = map.everyTrashBinCapacity();

        if (maxMachineCapacity > 0 && maxTrashCapacity > 0 && machineBottles >= maxMachineCapacity && trashBinBottles >= maxTrashCapacity) {
            reason = "Wszystkie butelkomaty i śmietniki są zapełnione!";
            endSimulation(false, reason);
            return;
        }

        String timeStr = engine.getTimeManager().getFormattedTime();
        if (timeStr != null && !timeStr.isEmpty()) {
            String currentDay = timeStr.split(" ")[0];

            if (!currentDay.equals(lastDay)) {
                if (!lastDay.isEmpty()) {
                    daysElapsed++;
                }
                lastDay = currentDay;
            }
        }

        if (map.getLitterLevel() > 10.0) {
            reason = "Butelki wyrzucone na ziemie zajmują więcej niż 10% pojemności infrastruktury!";
            endSimulation(false, reason);
            return;
        }

        if (daysElapsed >= 7) {
            reason = "SUKCES! Tydzień bezproblemowej pracy infrastruktury";
            endSimulation(true, reason);
        }
    }

    private void endSimulation(boolean success, String reason) {
        simulationEnded = true;

        WorldMap map = engine.getMap();
        int totalBottles = 0;
        for (MapElement element : map.getElements()) {
            totalBottles += element.getBottlesAmount();
        }

        Dialog summaryDialog = new Dialog("Koniec Symulacji", skin);
        Table content = summaryDialog.getContentTable();
        content.pad(25);

        if (success) {
            Label winLabel = new Label(reason, skin);
            winLabel.setColor(Color.GREEN);
            content.add(winLabel).padBottom(15).row();
            LoggerService.getInstance().log("=== SYMULACJA ZAKOŃCZONA SUKCESEM ===");
        } else {
            Label loseLabel = new Label(reason, skin);
            loseLabel.setColor(Color.RED);
            content.add(loseLabel).padBottom(15).row();
            LoggerService.getInstance().log("=== SYMULACJA PRZERWANA - AWARIA SYSTEMU ===");
        }

        content.add(new Label("Czas zakonczenia: " + engine.getTimeManager().getFormattedTime(), skin)).left().padBottom(5).row();
        content.add(new Label("Liczba aktywnych agentow: " + map.getAgents().size(), skin)).left().padBottom(5).row();
        content.add(new Label("Butelkomaty: " + map.getBottleMachines().size(), skin)).left().padBottom(5).row();
        content.add(new Label("Smietniki: " + map.getTrashBins().size(), skin)).left().padBottom(5).row();
        content.add(new Label("Butelki w obiegu: " + totalBottles + "/" + map.getMaxBottleAmount(), skin)).left().padBottom(5).row();

        summaryDialog.button("Zamknij program", true);

        summaryDialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
            }
        });

        summaryDialog.show(stage);
    }

    private void updateStatistics() {
        WorldMap map = engine.getMap();
        ArrayList<MapElement> elements = map.getElements();

        timeLabel.setText(engine.getTimeManager().getFormattedTime());

        int consumers = 0;
        int collectors = 0;
        int totalBottles = 0;
        int collectorBottles = 0;
        int consumerBottles = 0;
        int machineBottles = 0;
        int trashBinBottles = 0;

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
            totalBottles += element.getBottlesAmount();
        }

        int bottleMachines = map.getBottleMachines().size();
        int trashBins = map.getTrashBins().size();

        consumersLabel.setText("Konsumenci: " + consumers);
        collectorsLabel.setText("Kolektorzy: " + collectors);
        bottleMachinesLabel.setText("Butelkomaty: " + bottleMachines);
        trashBinsLabel.setText("Smietniki: " + trashBins);

        collectorBottlesLabel.setText("Wszystkie butelki collectorow: " + collectorBottles);
        consumerBottlesLabel.setText("Wszystkie butelki consumerow: " + consumerBottles);

        wholeBottlesLabel.setText("Wszystkie butelki w obiegu: " + totalBottles + "/" + map.getMaxBottleAmount());
        bottleMachinesBottleLabel.setText("Wszystkie butelki w butelkomatach: " + machineBottles + "/" + map.everyBottleMachineCapacity());
        trashBinsBottleLabel.setText("Wszystkie butelki w smietnikach: " + trashBinBottles + "/" + map.everyTrashBinCapacity());
        litterLevel.setText("Butelki smieci: " + map.getLitterLevel() + "% pojemności infrastruktury.");

        brokenMachinesLabel.setText("Zepsute butelkomaty: " + map.brokenMachinesAmount());
    }

    private void updateLogs() {
        LoggerService logger = LoggerService.getInstance();
        ArrayList<String> allLogs = logger.getLogs();

        int startIdx = Math.max(0, allLogs.size() - 20);
        StringBuilder logsText = new StringBuilder();

        for (int i = startIdx; i < allLogs.size(); i++) {
            logsText.append(allLogs.get(i)).append("\n");
        }

        logsTextArea.setText(logsText.toString());
        logsScrollPane.setScrollPercentY(1.0f);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        stage.dispose();
        skin.dispose();

        // bezpieczne czyszczenie zasobów czcionki
        if (font != null) {
            font.dispose();
        }

        textureWater.dispose();
        textureGrass.dispose();
        texturePath.dispose();
        textureWall.dispose();
        textureBottle.dispose();
        textureTrash.dispose();

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
        for(int i = 0; i < value; i++){
            Position consumerPos = worldMap.getRandomPosition();
            worldMap.addElement(new Consumer(consumerPos));
        }
    }

    public void addCollectors(int value, WorldMap worldMap){
        for(int i = 0; i < value; i++){
            Position consumerPos = worldMap.getRandomPosition();
            worldMap.addElement(new Collector(consumerPos));
        }
    }

    public void addTrashBins(int value, WorldMap worldMap){
        for(int i = 0; i < value; i++){
            Position trashBinPos = worldMap.getRandomPosition();
            worldMap.addElement(new TrashBin(trashBinPos));
        }
    }

    public void addBottleMachines(int value, WorldMap worldMap){
        for(int i = 0; i < value; i++){
            Position bottleMachinePos = worldMap.getRandomPosition();
            worldMap.addElement(new BottleMachine(bottleMachinePos));
        }
    }

    private Texture getTextureForElement(ElementType type){
        switch (type){
            case BOTTLE_MACHINE: return textureBottle;
            case TRASH_BIN:      return textureTrash;
            case CONSUMER:       return textureConsumer;
            case COLLECTOR:      return textureCollector;
            default: return textureWater;
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}