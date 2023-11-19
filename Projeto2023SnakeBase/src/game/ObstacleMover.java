package game;

import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

public class ObstacleMover extends Thread {
	private final Obstacle obstacle;
	private final LocalBoard board;

	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		this.obstacle = obstacle;
		this.board = board;
		this.board.addObstacleMover(this);
	}

	// Movement logic for the obstacle, runs until game ends or moves are exhausted.
	@Override
	public void run() {
		// TODO
		Thread.currentThread().setName("Obstacle " + obstacle.getId());
		System.out.println(Thread.currentThread().getName() + ": Started.");

		// Check if the game has finished before starting the loop.
		if (board.isFinished()) {
			System.out.println(Thread.currentThread().getName() + ": Game already finished.");
			return;
		}

		while (!Thread.currentThread().isInterrupted() && !board.isFinished() && obstacle.getRemainingMoves() > 0) {
			try {
				// Wait for the designated interval before attempting the next move.
				Thread.sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);

				// Check if the game has finished before attempting to move.
				if (board.isFinished()) {
					System.out.println(Thread.currentThread().getName() + ": The game is finished during sleep.");
					break;
				}

				// Execute the move operation for the obstacle.
				move();

			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + ": Interrupted.");
				Thread.currentThread().interrupt(); // Preserve the interrupt status.
				break; // Exit the loop if the game is finished or the thread is interrupted.
			}
		}

		System.out.println(Thread.currentThread().getName() + ": Exiting run method.");
	}

	// Handles the movement of the obstacle to a new position.
	private void move() {
		// Retrieve the current position of the obstacle.
		BoardPosition currentPosition = obstacle.getCurrentPosition();
		// Find a new position for the obstacle that is currently unoccupied.
		BoardPosition nextPosition = board.getUnoccupiedPosition(currentPosition);
		// Obtain the cell objects for both the current and next positions.
		Cell currentCell = board.getCell(currentPosition);
		Cell nextCell = board.getCell(nextPosition);
		// Call the method to handle the actual movement of the obstacle from the current cell to the next cell.
		// obstacleMoverHandler(currentCell, nextCell);
		Cell.obstacleMoverHandler(obstacle, currentCell, nextCell);
		// Once the obstacle has been moved, decrement its remaining moves count.
		obstacle.decrementRemainingMoves();
		// Notify the board that a change has occurred. This could be used to update the game state,
		// refresh the UI, or notify other components that are observing the board.
		board.setChanged();
	}

	/*
	// Moves an obstacle from one cell to another in a thread-safe manner.
	private void obstacleMoverHandler(Cell currentCell, Cell nextCell) {
		// Lock objects for both the current and destination cells.
		try {
			// Acquire lock on the current cell.
			currentCell.getLock().lock();
			try {
				// Acquire lock on the destination cell.
				nextCell.getLock().lock();
				try {
					// Remove obstacle from the current cell.
					currentCell.removeObstacle();
					// Place the obstacle in the destination cell.
					nextCell.setGameElement(obstacle);
				} finally {
					// Ensure the lock on the destination cell is released.
					nextCell.getLock().unlock();
				}
			} finally {
				// Ensure the lock on the current cell is released.
				currentCell.getLock().unlock();
			}
		} catch (Exception e) {
			System.out.println("Exception in ObstacleMover move method: " + e.getMessage());
		}
	}
	 */
}