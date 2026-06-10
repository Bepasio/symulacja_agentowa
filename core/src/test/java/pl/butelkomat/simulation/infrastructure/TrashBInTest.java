package pl.butelkomat.simulation.infrastructure;

import org.junit.jupiter.api.Test;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.Position;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrashBInTest {
    @Test
    public void shouldReturnLastAddedBottle() {
        TrashBin bin = new TrashBin(new Position(2,2));
        bin.addBottle(new Bottle(false));
        bin.addBottle(new Bottle(true)); //ostatnia dodana butalka zwrotna

        Bottle bottle = bin.takeBottle();
        assertTrue(bottle.isRefundable(), "Powinien zwrocic butelke zwrotna");
    }
}
