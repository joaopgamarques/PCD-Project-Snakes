package environment;

import game.Snake;

import java.io.Serializable;

import java.util.LinkedList;

public class GameState implements Serializable {
    private Cell[][] cells; // Two-dimensional array representing the cells of the game board.
    private LinkedList<Snake> snakes; // List of all snakes currently in the game.

    public GameState(Cell[][] cells, LinkedList<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }

    // Returns the current state of the cells on the board.
    public Cell[][] getCells() {
        return cells;
    }

    // Returns the list of snakes currently in the game.
    public LinkedList<Snake> getSnakes() {
        return snakes;
    }

    // Updates the game state with new cell configurations and snake positions.
    public void update(Cell[][] cells, LinkedList<Snake> snakes) {
        this.cells = cells;
        this.snakes = snakes;
    }
}
