package game;

import environment.Board;
import environment.BoardPosition;
import environment.LocalBoard;

public class Obstacle extends GameElement {
	private static final int NUM_MOVES = 3;
	public static final int OBSTACLE_MOVE_INTERVAL = 2000; // 400
	private int remainingMoves = NUM_MOVES;
	private final Board board;
	private final int id;
	private BoardPosition currentPosition;

	public Obstacle(int id, Board board) {
		super();
		this.board = board;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Board getBoard() {
		return board;
	}

	@Override
	public BoardPosition getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public void setCurrentPosition(BoardPosition position) {
		this.currentPosition = position;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}

	public void decrementRemainingMoves() {
		remainingMoves--;
	}
}
