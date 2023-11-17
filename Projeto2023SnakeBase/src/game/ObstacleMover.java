package game;

import environment.BoardPosition;
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

				// Determine the next position for the obstacle to move to.
				BoardPosition currentPosition = obstacle.getCurrentPosition();
				BoardPosition nextPosition = board.getUnoccupiedPosition(currentPosition);
				if (!nextPosition.equals(currentPosition)) {
					board.getCell(currentPosition).removeObstacle();
					board.getCell(nextPosition).setGameElement(obstacle);
				}
				obstacle.decrementRemainingMoves();
				board.setChanged();

			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + ": Interrupted.");
				Thread.currentThread().interrupt(); // Preserve the interrupt status.
				break; // Exit the loop if the game is finished or the thread is interrupted.
			}
		}

		System.out.println(Thread.currentThread().getName() + ": Exiting run method.");
	}
}