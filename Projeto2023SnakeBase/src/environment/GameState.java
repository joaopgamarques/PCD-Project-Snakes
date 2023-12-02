package environment;

import environment.Cell;
import environment.LocalBoard;
import game.Snake;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GameState implements Serializable {
    private Cell[][] cells;
    private LinkedList<Snake> snakes;

    public GameState(Cell[][] cells, LinkedList<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public LinkedList<Snake> getSnakes() {
        return snakes;
    }

    public void update(Cell[][] cells, LinkedList<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }
}
