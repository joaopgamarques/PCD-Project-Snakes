package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

import remote.Direction;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class for a remote snake, controlled by a human.
 *
 * @author luismota
 */

public class HumanSnake extends Snake {
    private Direction direction;
    private final Lock lock = new ReentrantLock();
    private final Condition newDirectionAvailable = lock.newCondition();

    public HumanSnake(int id, Board board) {
        super(id, board);
    }

    // Retrieves the current movement direction of the snake.
    public Direction getDirection() {
        return direction;
    }

    // Sets the movement direction of the snake.
    public void setDirection(Direction direction) {
        lock.lock();
        try {
            this.direction = direction;
            newDirectionAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Retrieves the next cell for the snake to move to based on the current direction.
    public Cell getNextCell() throws InterruptedException {
        lock.lock();
        try {
            // Wait for a new direction to be set by the client.
            while (direction == null) {
                newDirectionAvailable.await();
            }
            // Determine the next position based on the current direction.
            BoardPosition nextPosition = getHead().getPosition().directionalPosition(direction);
            // If the snake is active and the next position is within the board, proceed with the move.
            if (getBoard().isWithinBounds(nextPosition)) {
                Cell nextCell = getBoard().getCell(nextPosition);
                direction = null; // Reset the direction after obtaining the next cell.
                return nextCell;
            } else {
                return null; // Return null if the next position is not within the board bounds or if the snake is idle.
            }
        } finally {
            lock.unlock();
        }
    }

    // Main execution method for the snake's movement. This method is continuously called during the game's execution.
    @Override
    public void run() {
        // TODO
        doInitialPositioning(); // Determine the initial position of the snake on the board.

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
                Cell nextCell = getNextCell();
                if (nextCell != null && !getBoard().isFinished()) {
                    move(nextCell);
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + ": Interrupted.");
            }
        }

        System.out.println(Thread.currentThread().getName() + ": Exiting run method.");
    }
}