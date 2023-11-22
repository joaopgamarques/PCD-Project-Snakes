package environment;

/** Classe representing a position on the board, with some utilities
 * 
 * @author luismota
 *
 */

public class BoardPosition implements Comparable<BoardPosition> {
	public final int x;
	public final int y;

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

	@Override
	public int compareTo(BoardPosition other) {
		if (this.x == other.x) {
			return Integer.compare(this.y, other.y);
		}
		return Integer.compare(this.x, other.x);
	}
	
	public double distanceTo(BoardPosition other) {
		double delta_x = x - other.x;
		double delta_y = y - other.y;
		return Math.sqrt(delta_x * delta_x + delta_y * delta_y);
	}

	public BoardPosition getCellAbove() {
		return new BoardPosition(x, y-1);
	}
	public BoardPosition getCellBelow() {
		return new BoardPosition(x, y+1);
	}
	public BoardPosition getCellLeft() {
		return new BoardPosition(x-1, y);
	}
	public BoardPosition getCellRight() {
		return new BoardPosition(x+1, y);
	}
}