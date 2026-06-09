package pl.butelkomat.simulation.world;

//przechowuje info o tym gdzie sie znajduje agent, trzeba zrobic funkcje ktora oblicza najblizszy infrastructure do ktorego bedzie zmierzac

import java.util.Objects;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y; //uzywajac .equals(position position) sprawdzamy czy sa te same klasy i porownujemy x do x, y do y
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
