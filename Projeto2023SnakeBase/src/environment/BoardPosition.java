package environment;

/** Classe representing a position on the board, with some utilities
 * 
 * @author luismota
 *
 */

import remote.Direction;

import java.io.Serializable;

public class BoardPosition implements Serializable, Comparable<BoardPosition> {
	public final int x; // X-coordinate of the position
	public final int y; // Y-coordinate of the position

	public BoardPosition(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public boolean equals(Object obj) {
		BoardPosition other = (BoardPosition) obj;
		return other.x == x && other.y == y;
	}

	// Allows BoardPosition to be compared, first by x-coordinate, then by y-coordinate.
	@Override
	public int compareTo(BoardPosition other) {
		if (this.x == other.x) {
			return Integer.compare(this.y, other.y);
		}
		return Integer.compare(this.x, other.x);
	}

	// Calculates the Euclidean distance to another BoardPosition.
	public double distanceTo(BoardPosition other) {
		double delta_x = x - other.x;
		double delta_y = y - other.y;
		return Math.sqrt(delta_x * delta_x + delta_y * delta_y);
	}

	// Returns the position directly above the current position.
	public BoardPosition getCellAbove() {
		return new BoardPosition(x, y-1);
	}

	// Returns the position directly below the current position.
	public BoardPosition getCellBelow() {
		return new BoardPosition(x, y+1);
	}

	// Returns the position directly to the left of the current position.
	public BoardPosition getCellLeft() {
		return new BoardPosition(x-1, y);
	}

	// Returns the position directly to the right of the current position.
	public BoardPosition getCellRight() {
		return new BoardPosition(x+1, y);
	}

	// Returns a new BoardPosition based on a given direction.
	public BoardPosition directionalPosition(Direction direction) {
        return switch (direction) {
            case UP -> getCellAbove();
            case DOWN -> getCellBelow();
            case LEFT -> getCellLeft();
            case RIGHT -> getCellRight();
        };
	}
}