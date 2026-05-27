package pl.butelkomat.simulation.agents;

import pl.butelkomat.simulation.world.Position;

//    jakas metoda collectFromBin
//    okreslic ile butelek moze nosic
//    zalozmy ze chodzi od kosza do kosza, jak zapelni eq to idzie do najblizszego butelkomatu
abstract class Collector extends Agent {
    public Collector(Position startPosition) {
        super(startPosition, 20);
    }
}
