package game;

import environment.Board;
import environment.BoardPosition;
import environment.LocalBoard;

import java.util.ArrayList;
import java.util.List;

public class Goal extends GameElement  {
	private int value = 1;
	private final Board board;
	public static final int MAX_VALUE = 10;
	private BoardPosition currentPosition;

	public Goal(Board board) {
		this.board = board;
	}

	@Override
	public BoardPosition getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public void setCurrentPosition(BoardPosition position) {
		this.currentPosition = position;
	}
	
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