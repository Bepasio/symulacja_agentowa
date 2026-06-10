package pl.butelkomat.simulation.infrastructure;

import org.junit.jupiter.api.Test;
import pl.butelkomat.simulation.item.Bottle;
import pl.butelkomat.simulation.world.Position;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BottleMachineTest {

    @Test
    void shouldRejectNonRefundable() {
        BottleMachine bottleMachine = new BottleMachine(new Position(2,5));
        assertFalse(bottleMachine.addBottle(new Bottle(false)), "Powinien odrzucic butelke bezzwrotna");
    }

    @Test
    void shouldAcceptRefundable() {
        BottleMachine bottleMachine = new BottleMachine(new Position(2,5));
        assertTrue(bottleMachine.addBottle(new Bottle(true)), "Powinien przyjac butelke zwrotna");
    }
}
