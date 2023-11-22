package environment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.*;

/** Main class for game representation.
 * 
 * @author luismota
 *
 */
public class Cell {
	private final BoardPosition position;
	private Snake occupyingSnake = null;
	private GameElement gameElement = null;
	private final Lock lock = new ReentrantLock();
	private final Condition notOccupied = lock.newCondition();
	// private static Lock obstacleMoverLock = new ReentrantLock();
	// private static Lock captureGoalLock = new ReentrantLock();

	public GameElement getGameElement() {
		return gameElement;
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public Lock getLock() {
		return lock;
	}

	// Requests access to this cell for a snake, blocking until it is not occupied.
	public void request(Snake snake) throws InterruptedException {
		// TODO coordination and mutual exclusion.
		lock.lock();
		try {
			while (isOccupied()) {
				notOccupied.await();
			}
			occupyingSnake = snake;
		} finally {
			lock.unlock();
		}
	}

	// Releases this cell from being occupied by a snake, signaling any waiting threads.
	public void release() {
		// TODO
		lock.lock();
		try {
			occupyingSnake = null;
			notOccupied.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public boolean isOccupiedBySnake() {
		return occupyingSnake != null;
	}

	// Sets a game element to this cell if it's not already occupied. Waits if the cell is occupied.
	public void setGameElement(GameElement element) {
		// TODO coordination and mutual exclusion.
		lock.lock();
		try {
			while (isOccupied()) {
				notOccupied.await();
			}
			gameElement = element;
			gameElement.setCurrentPosition(position);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Thread was interrupted while setting a game element.");
		} finally {
			lock.unlock();
		}
	}

	public boolean isOccupied() {
		return isOccupiedBySnake() || (gameElement != null && gameElement instanceof Obstacle);
	}

	public Snake getOccupyingSnake() {
		return occupyingSnake;
	}

	// Removes the goal from this cell and signals any waiting threads.
	public Goal removeGoal() {
		// TODO
		lock.lock();
		try {
			if (gameElement instanceof Goal) {
				Goal goal = (Goal)gameElement;
				gameElement = null;
				notOccupied.signalAll();
				return goal;
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	// Removes an obstacle from this cell and signals any waiting threads.
	public void removeObstacle() {
		// TODO
		lock.lock();
		try {
			if (gameElement instanceof Obstacle) {
				gameElement = null;
				notOccupied.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	public Goal getGoal() {
		return (Goal)gameElement;
	}

	public boolean isOcupiedByGoal() {
		return (gameElement != null && gameElement instanceof Goal);
	}

	// Moves an obstacle from one cell to another in a thread-safe manner.
	public static void obstacleMoverHandler(Obstacle obstacle, Cell currentCell, Cell nextCell) {
		Lock firstLock, secondLock;

		// Determine the order of locks based on the positions of the cells to avoid deadlocks.
		// Locks are always acquired in a consistent global order.
		if (currentCell.getPosition().compareTo(nextCell.getPosition()) < 0) {
			firstLock = currentCell.getLock();
			secondLock = nextCell.getLock();
		} else {
			firstLock = nextCell.getLock();
			secondLock = currentCell.getLock();
		}

		// Acquire the first lock.
		firstLock.lock();
		try {
			// Acquire the second lock.
			secondLock.lock();
			try {
				// Check if the next cell is suitable for the move.
				if (!nextCell.isOccupied()) {
					// Remove obstacle from the current cell.
					currentCell.removeObstacle();
					// Place the obstacle in the destination cell.
					nextCell.setGameElement(obstacle);
					// Once the obstacle has been moved, decrement its remaining moves count.
					obstacle.decrementRemainingMoves();
				}
			} finally {
				// Ensure the second lock is released.
				secondLock.unlock();
			}
		} finally {
			// Ensure the first lock is released.
			firstLock.unlock();
		}
	}

	// Moves the goal from one cell to another in a thread-safe manner.
	public static void captureGoalHandler(Snake snake, Cell currentCell, Cell nextCell) {
		Lock firstLock, secondLock;

		// Determine the order of locks based on the positions of the cells to avoid deadlocks.
		// Locks are always acquired in a consistent global order.
		if (currentCell.getPosition().compareTo(nextCell.getPosition()) < 0) {
			firstLock = currentCell.getLock();
			secondLock = nextCell.getLock();
		} else {
			firstLock = nextCell.getLock();
			secondLock = currentCell.getLock();
		}

		// Acquire the first lock.
		firstLock.lock();
		try {
			// Acquire the second lock.
			secondLock.lock();
			try {
				// Get the board context for the obstacle.
				Board board = snake.getBoard();
				// Remove the goal from the current cell.
				Goal goal = currentCell.removeGoal();
				// Increment the growth pending for the snake as it captures the goal.
				snake.increaseGrowthPending(goal.captureGoal());
				// Increment the goal's value and check for game termination.
				goal.incrementValue();
				if (goal.getValue() == Goal.MAX_VALUE) {
					((LocalBoard)board).endGame();
					return;
				}
				// Set the goal in its new position and update the board's goal position.
				nextCell.setGameElement(goal);
				board.setGoalPosition(nextCell.getPosition());
			} catch (InterruptedException e) {
				System.out.println("Exception in captureGoalHandler: " + e.getMessage());
            } finally {
				// Ensure the second lock is released.
				secondLock.unlock();
			}
		} finally {
			// Ensure the first lock is released.
			firstLock.unlock();
		}
	}

	/*
	// Moves an obstacle from one cell to another in a thread-safe manner.
	public void obstacleMoverHandler(Obstacle obstacle) {
		// Lock to ensure exclusive access for moving the obstacle.
		obstacleMoverLock.lock();
		try {
			// Get the board context for the obstacle.
			Board board = obstacle.getBoard();
			// Retrieve the current position of the obstacle.
			BoardPosition currentPosition = obstacle.getCurrentPosition();
			// Find a new position for the obstacle that is currently unoccupied.
			BoardPosition nextPosition = board.getUnoccupiedPosition(currentPosition);
			// Obtain the cell objects for both the current and next positions.
			Cell currentCell = board.getCell(currentPosition);
			Cell nextCell = board.getCell(nextPosition);
			// Remove obstacle from the current cell.
			currentCell.removeObstacle();
			// Place the obstacle in the destination cell.
			nextCell.setGameElement(obstacle);
			// Once the obstacle has been moved, decrement its remaining moves count.
			obstacle.decrementRemainingMoves();
		} catch (Exception e) {
			System.out.println("Exception in ObstacleMover move method: " + e.getMessage());
		} finally {
			// Ensure that the lock is always released after the operation.
			obstacleMoverLock.unlock();
		}
	}

	// Moves the goal from one cell to another in a thread-safe manner.
	public void captureGoalHandler(Snake snake) {
		// Lock to ensure exclusive access for moving the goal.
		captureGoalLock.lock();
		try{
			// Get the board context for the obstacle.
			Board board = snake.getBoard();
			// Retrieve the current position of the goal.
			BoardPosition currentPosition = getPosition();
			// Determine a new unoccupied position for the goal.
			BoardPosition nextPosition = board.getUnoccupiedPosition(currentPosition);
			// Obtain the cell objects for both the current and next positions.
			Cell currentCell = board.getCell(currentPosition);
			Cell nextCell = board.getCell(nextPosition);
			// Remove the goal from the current cell.
			Goal goal = currentCell.removeGoal();
			// Increment the growth pending for the snake as it captures the goal.
			snake.increaseGrowthPending(goal.captureGoal());
			// Increment the goal's value and check for game termination.
			goal.incrementValue();
			if (goal.getValue() == Goal.MAX_VALUE) {
				((LocalBoard)board).endGame();
				return;
			}
			// Set the goal in its new position and update the board's goal position.
			nextCell.setGameElement(goal);
			board.setGoalPosition(nextCell.getPosition());
		} catch (InterruptedException e) {
			System.out.println("Exception in captureGoalHandler: " + e.getMessage());
		} finally {
			// Ensure that the lock is always released after the operation.
			captureGoalLock.unlock();
		}
	}
	*/
}