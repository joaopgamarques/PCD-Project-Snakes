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

	// Moves the snake to a new cell and handles any interaction with goals.
	protected void move(Cell cell) throws InterruptedException {
		// TODO
		cell.request(this);
		if (cell.isOcupiedByGoal()) {
			captureGoal(cell);
		}
		getCells().addLast(cell);
		if (getLength() > size && growthPending == 0) {
			BoardPosition tail = cells.removeFirst().getPosition();
			board.getCell(tail).release();
		} else if (growthPending > 0) {
			growthPending--;
		}
		board.setChanged();
	}

	// Captures a goal, increments its value, and checks for game termination.
	private void captureGoal(Cell cell) throws InterruptedException {
		Goal goal = cell.removeGoal();
		growthPending += goal.captureGoal();
		goal.incrementValue();
		if (goal.getValue() == Goal.MAX_VALUE) {
			((LocalBoard)board).endGame();
			return;
		}
		relocateGoal(goal);
	}

	// Places the goal in a new random unoccupied position.
	private void relocateGoal(Goal goal) {
		BoardPosition currentPosition = goal.getCurrentPosition();
		BoardPosition nextPosition = getBoard().getUnoccupiedPosition(currentPosition);
		if (!nextPosition.equals(currentPosition)) {
			board.addGameElement(goal);
		}
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