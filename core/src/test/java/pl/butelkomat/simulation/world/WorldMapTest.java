package pl.butelkomat.simulation.world;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    @Test
    void shouldReturnFalseIfOutsideMap() {
        WorldMap map = new WorldMap(10, 10);

        assertFalse(map.isWalkable(-1, 5), "Powinno zablokowac ujemny x");
        assertFalse(map.isWalkable(5, -1), "Powinno zablokowac ujemny y");
        assertFalse(map.isWalkable(10, 5), "Powinno zablokowac x rowny rozmiarowi mapy");
        assertFalse(map.isWalkable(5, 10), "Powinno zablokowac y rowny rozmiarowi mapy");
    }

    @Test
    void shouldReturnFalseWhenWaterOrObstacle() {
        WorldMap map = new WorldMap(10, 10);
        map.setTileType(3, 3, WorldMap.TileType.WATER);
        map.setTileType(5, 5, WorldMap.TileType.OBSTACLE);

        assertFalse(map.isWalkable(3, 3), "Powinno zablokowac wejscie do wody");
        assertFalse(map.isWalkable(5, 5), "Powinno zablokowac wejscie w sciane");
    }

    @Test
    void shouldReturnTrueWhenTileIsGrassOrPath() {
        WorldMap map = new WorldMap(10, 10);
        map.setTileType(2, 2, WorldMap.TileType.GRASS);
        map.setTileType(7, 7, WorldMap.TileType.PATH);

        assertTrue(map.isWalkable(2, 2), "Nie powinno blokowac wejscia na trawe");
        assertTrue(map.isWalkable(7, 7), "Nie powinno blokowac wejscia na sciezke");
    }
}