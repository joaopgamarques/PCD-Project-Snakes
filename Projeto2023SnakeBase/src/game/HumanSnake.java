package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

import remote.Direction;

/**
 * Class for a remote snake, controlled by a human.
 *
 * @author luismota
 */

public class HumanSnake extends Snake {
    public HumanSnake(int id, Board board) {
        super(id, board);
    }
    private Direction direction = Direction.RIGHT; // Default initial direction

    // Retrieves the current movement direction of the snake.
    public Direction getDirection() {
        return direction;
    }

    // Sets the movement direction of the snake.
    public void setDirection(Direction direction) {
        this.direction = direction;
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
                // Determine the next position based on the current direction.
                BoardPosition nextPosition = cells.getLast().getPosition().directionalPosition(direction);
                // If the snake is active and the next position is within the board, proceed with the move.
                if (!isIdle && getBoard().isWithinBounds(nextPosition)) {
                    Cell nextCell = getBoard().getCell(nextPosition);
                    move(nextCell);
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + ": Interrupted.");
            }
        }

        System.out.println(Thread.currentThread().getName() + ": Exiting run method.");
    }
}