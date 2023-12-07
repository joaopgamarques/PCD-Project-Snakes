package game;

import environment.Board;
import environment.BoardPosition;

public class Obstacle extends GameElement {
	private static final int NUMBER_MOVES = 3; // Total number of moves an obstacle can make.
	public static final int OBSTACLE_MOVE_INTERVAL = 2000; // Time interval between obstacle moves.
	private int remainingMoves = NUMBER_MOVES; // Tracks the remaining number of moves for the obstacle.
	private transient final Board board; // Reference to the game board.
	private final int id; // Unique identifier for the obstacle.
	private BoardPosition currentPosition; // Current position of the obstacle on the board.

	public Obstacle(int id, Board board) {
		super();
		this.board = board;
		this.id = id;
	}

	// Retrieves the unique identifier of the obstacle.
	public int getId() {
		return id;
	}

	// Retrieves the game board associated with the obstacle.
	public Board getBoard() {
		return board;
	}

	// Gets the current position of the obstacle on the board.
	@Override
	public BoardPosition getCurrentPosition() {
		return currentPosition;
	}

	// Sets the current position of the obstacle.
	@Override
	public void setCurrentPosition(BoardPosition position) {
		this.currentPosition = position;
	}

	// Retrieves the number of remaining moves for the obstacle.
	public int getRemainingMoves() {
		return remainingMoves;
	}

	// Decrements the count of remaining moves by one. This is typically called after an obstacle has moved.
	public void decrementRemainingMoves() {
		remainingMoves--;
	}
}
