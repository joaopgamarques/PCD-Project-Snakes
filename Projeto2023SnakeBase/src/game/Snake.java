package game;

import java.io.Serializable;
import java.util.LinkedList;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

/** Base class for representing Snakes.
 * Will be extended by HumanSnake and AutomaticSnake.
 * Common methods will be defined here.
 * @author luismota
 *
 */
public abstract class Snake extends Thread implements Serializable{
	protected LinkedList<Cell> cells = new LinkedList<Cell>();
	protected int size = 5;
	private final int id;
	private final Board board;
	private int growthPending = 0;
	
	public Snake(int id, Board board) {
		this.id = id;
		this.board = board;
		this.setName("Snake " + id);
	}

	public Board getBoard() {
		return board;
	}

	public int getSize() {
		return size;
	}

	public int getIdentification() {
		return id;
	}

	public int getLength() {
		return cells.size();
	}
	
	public LinkedList<Cell> getCells() {
		return cells;
	}

	// Increases the growth pending for the snake.
	public void increaseGrowthPending(int additionalGrowth) {
		growthPending += additionalGrowth;
	}

	// Moves the snake to a new cell and handles any interaction with goals.
	protected void move(Cell cell) throws InterruptedException {
		// TODO
		// Request access to the cell for the snake.
		cell.request(this);
		// Add the cell to the snake's path.
		getCells().addLast(cell);

		// Check if the cell contains a goal.
		if (cell.isOcupiedByGoal()) {
			// Increment the growth pending for the snake as it captures the goal.
			Goal goal = (Goal)cell.getGameElement();
			increaseGrowthPending(goal.captureGoal());
			Cell.goalCaptureAndMoveHandler(board);
		}

		// Release the tail cell if the snake has not grown.
		if (getLength() > size && growthPending == 0) {
			BoardPosition tail = cells.removeFirst().getPosition();
			board.getCell(tail).release();
		} else if (growthPending > 0) {
			// If growth is pending, decrement it.
			growthPending--;
		}

		// Notify the board that a change has occurred. This could be used to update the game state,
		// refresh the UI, or notify other components that are observing the board.
		board.setChanged();
	}

	// Returns a list of positions representing the snake's body.
	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
			coordinates.add(cell.getPosition());
		}
		return coordinates;
	}

	// Determines the initial position of the snake on the board.
	protected void doInitialPositioning() {
		// Random position on the first column. At startup, the snake occupies a single cell.
		int x = 0;
		int y = (int) (Math.random() * Board.NUM_ROWS);
		BoardPosition at = new BoardPosition(x, y);
		while (board.getCell(at).isOccupied()) {
			y = (int) (Math.random() * Board.NUM_ROWS);
			at = new BoardPosition(x, y);
		}
		try {
			board.getCell(at).request(this);
		} catch (InterruptedException e1) {
			System.out.println(currentThread().getName() + ": Interrupted.");
		}
		cells.add(board.getCell(at));
		System.err.println("Snake "+ getIdentification() + " starting at:" + getCells().getLast());
	}
}