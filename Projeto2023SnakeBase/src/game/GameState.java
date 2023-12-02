package game;

import environment.Cell;
import environment.LocalBoard;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private Cell[][] cells;
    private List<Snake> snakes;

    public GameState(Cell[][] cells, List<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public void update(Cell[][] cells, List<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }
}
