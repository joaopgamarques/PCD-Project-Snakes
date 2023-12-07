package game;

import environment.Board;
import environment.BoardPosition;

public class Goal extends GameElement  {
	private int value = 1; // Initial value of the goal.
	private transient final Board board; // Reference to the board where the goal is placed.
	public static final int MAX_VALUE = 10; // Maximum value that a goal can attain.
	private BoardPosition currentPosition; // Current position of the goal on the board.

	public Goal(Board board) {
		this.board = board;
	}

	// Gets the current position of the goal on the board.
	@Override
	public BoardPosition getCurrentPosition() {
		return currentPosition;
	}

	// Sets the current position of the goal on the board.
	@Override
	public void setCurrentPosition(BoardPosition position) {
		this.currentPosition = position;
	}

	// Retrieves the current value of the goal.
	public int getValue() {
		return value;
	}

	// Increments the value of the goal by one, up to a maximum value.
	public void incrementValue() throws InterruptedException {
		// TODO
		if (value < MAX_VALUE) {
			value++;
		}
	}

	// Captures the goal and returns its value.
	public int captureGoal() {
		// TODO
		return value;
	}
}