package game;

import environment.Board;
import environment.Cell;
import environment.LocalBoard;

public class ObstacleMover extends Thread {
	private final Obstacle obstacle;
	private final LocalBoard board;

	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		this.obstacle = obstacle;
		this.board = board;
	}

	// Movement logic for the obstacle, runs until game ends or moves are exhausted.
	@Override
	public void run() {
		// TODO
		Thread.currentThread().setName("Obstacle " + obstacle.getId());
		System.out.println(Thread.currentThread().getName() + ": Started.");

		// Checks if all automatic snakes on the board are idle.
		if (((LocalBoard)board).areAllSnakesIdle()) {
			try {
				Thread.sleep(Board.REMOTE_CONNECTION_SETUP_DELAY);
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + ": Interrupted during initial wait.");
			}
		}

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
		// Call the method to handle the actual movement of the obstacle from the current cell to the next cell.
		Cell.obstacleMoveHandler(obstacle);
		// Notify the board that a change has occurred. This could be used to update the game state,
		// refresh the UI, or notify other components that are observing the board.
		board.setChanged();
	}
}