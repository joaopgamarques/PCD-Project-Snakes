package game;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import environment.Board;
import environment.LocalBoard;
import environment.Cell;
import environment.BoardPosition;

public class AutomaticSnake extends Snake {
	private Boolean isInterruptedByUser = false;

	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);
	}

	// The main run method that dictates the automatic movement of the snake.
	@Override
	public void run() {
		doInitialPositioning();
		System.err.println("initial size:" + cells.size());
		// TODO: automatic movement.
		System.out.println(Thread.currentThread().getName() + ": Started.");

		try {
			Thread.sleep(Board.REMOTE_CONNECTION_SETUP_DELAY);
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + ": Interrupted during initial wait.");
		}
		isIdle = false;

		// Check if the game has finished before starting the loop.
		if (getBoard().isFinished()) {
			System.out.println(Thread.currentThread().getName() + ": Game already finished.");
			return;
		}

		// Loop until the thread is interrupted or the game is finished.
		while (!Thread.currentThread().isInterrupted() && !getBoard().isFinished()) {
			try {
				// Wait for a fixed interval before attempting the next move.
				Thread.sleep(LocalBoard.PLAYER_PLAY_INTERVAL);

				// Check if the game has finished during the sleep interval.
				if (getBoard().isFinished()) {
					System.out.println(Thread.currentThread().getName() + ": The game is finished during sleep.");
					break;
				}

				// Check if the snake is trapped and cannot make a move.
				if (isTrapped()) {
					System.out.println(Thread.currentThread().getName() + " is trapped and cannot move.");
					break;
				}

				// Determine the next position for the snake to move to.
				BoardPosition nextPosition;
				if (isInterruptedByUser) {
					nextPosition = getRandomPosition();
					isInterruptedByUser = false;
				} else {
					nextPosition = getNextPositionTowardsGoal();
				}
				Cell nextCell = getBoard().getCell(nextPosition);

				// Execute the move operation for the snake.
				move(nextCell);

			} catch (InterruptedException e) {
				if (!getBoard().isFinished() && !isTrapped()) {
					System.out.println(Thread.currentThread().getName() + ": Interrupted. Choosing a direction.");
					isInterruptedByUser = true;
				} else {
					String message = getBoard().isFinished() ? "Game already finished." :
							Thread.currentThread().getName() + " is trapped and cannot move.";
					System.out.println(Thread.currentThread().getName() + ": Interrupted. " + message);
					Thread.currentThread().interrupt(); // Preserve the interrupt status.
					break; // Exit the loop if the game is finished or the thread is interrupted.
				}
			}
        }

		System.out.println(Thread.currentThread().getName() + ": Exiting run method.");
	}

	// Chooses the neighboring position that is closest to the goal.
	private BoardPosition getNextPositionTowardsGoal() {
		Cell head = cells.getLast();
		List<BoardPosition> neighboringPositions = getBoard().getNeighboringPositions(head);
		BoardPosition nextPosition =  head.getPosition();
		double minimumDistanceToGoal = Double.MAX_VALUE;

		for (BoardPosition neighbor : neighboringPositions) {
			if (!isPositionOccupiedBySnake(neighbor)) {
				double distance = neighbor.distanceTo(getBoard().getGoalPosition());
				if (distance < minimumDistanceToGoal) {
					minimumDistanceToGoal = distance;
					nextPosition = neighbor;
				}
			}
		}

		return nextPosition;
	}

	// Gets a random unoccupied neighboring position.
	private BoardPosition getRandomPosition() {
		Cell head = cells.getLast();
		List<BoardPosition> neighboringPositions = getBoard().getNeighboringPositions(head);
		neighboringPositions.removeIf(position -> getBoard().getCell(position).isOccupied());
		if (neighboringPositions.isEmpty()) {
			return head.getPosition();
		}
		return neighboringPositions.get(new Random().nextInt(neighboringPositions.size()));
	}

	// Check if all neighboring positions are occupied by the snake or an immovable obstacle.
	public boolean isTrapped() {
		Cell head = cells.getLast();
		List<BoardPosition> neighboringPositions = getBoard().getNeighboringPositions(head);
		for (BoardPosition position : neighboringPositions) {
			Cell neighbor = getBoard().getCell(position);
			if (!isPositionOccupiedBySnake(position) && !(neighbor.getGameElement() instanceof Obstacle &&
					((Obstacle)neighbor.getGameElement()).getRemainingMoves() == 0)) {
				return false;
			}
		}
		return true;
	}

	// Checks if the given position is currently occupied by a segment of the snake.
	private boolean isPositionOccupiedBySnake(BoardPosition position) {
		LinkedList<BoardPosition> path = getPath();
		return path.contains(position);
	}
}