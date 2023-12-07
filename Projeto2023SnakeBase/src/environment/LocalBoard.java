package environment;

import game.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Class representing the state of a game running locally.
 * 
 * @author luismota
 *
 */

public class LocalBoard extends Board {
	private static final int NUM_SNAKES = 2; // Number of snakes in the game.
	private static final int NUM_OBSTACLES = 25; // Number of obstacles on the board.
	public static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3; // Number of obstacles that can move simultaneously.
	private transient final ExecutorService obstacleMoverThreadPool; // ExecutorService to manage ObstacleMover threads.
	private GameState gameState; // Current state of the game, including all game elements.

	public LocalBoard() {
		// Initialize the thread pool with the fixed number of threads for moving obstacles.
		obstacleMoverThreadPool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);
		// Initialize snakes and obstacles on the board.
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}
		addObstacles(NUM_OBSTACLES); // Add obstacles to the board.
		Goal goal = addGoal(); // Add a goal to the board.
		System.out.println("All game elements placed.");
	}

	// Initializes the game by starting all snake threads and scheduling obstacle movers.
	public void init() {
		gameState = new GameState(cells, snakes, isFinished); // Initialize the game state.
		for(Snake snake: snakes) {
			snake.start();
		}
		// TODO: launch other threads.
		for (Obstacle obstacle : getObstacles()) { // Schedule ObstacleMover tasks in the thread pool.
			ObstacleMover obstacleMover = new ObstacleMover(obstacle, this);
			obstacleMoverThreadPool.submit(obstacleMover); // Executes using the thread pool.
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		gameState.update(cells, snakes, isFinished);
	}

	// Marks the game as finished, triggering a graceful shutdown process. This method should be called when the game is to be concluded.
	// All active game entities should periodically check the 'isFinished' flag and terminate their operations if it is set to true.
	public void endGame() {
		isFinished = true; // Signal all game entities that the game has ended.
		for (Snake snake : snakes) {
			if (snake instanceof AutomaticSnake) {
				snake.interrupt(); // Stop all snakes.
			}
		}
		shutdownNow(); // Stop all obstacle movers.
		gameState.update(cells, snakes, isFinished);
	}

	// Shuts down the thread pool immediately and interrupts all running tasks.
	// This method should be called to ensure all obstacle movers stop when the game ends.
	public void shutdownNow() {
		obstacleMoverThreadPool.shutdownNow();
		try {
			if (!obstacleMoverThreadPool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
				obstacleMoverThreadPool.shutdownNow(); // Forcefully shutdown if tasks did not terminate.
			}
		} catch (InterruptedException e) {
			obstacleMoverThreadPool.shutdownNow();
		}
	}

	// Initiate an orderly shutdown by preventing submission of new tasks, but allowing existing tasks to complete.
	public void shutdown() {
		obstacleMoverThreadPool.shutdown();
	}

	// Returns the current game state.
	public GameState getGameState() {
		return gameState;
	}

	// Checks if all automatic snakes on the board are idle.
	public boolean areAllSnakesIdle() {
		for (Snake snake : getSnakes()) {
			if (snake instanceof AutomaticSnake && !snake.isIdle()) {
				return false;
			}
		}
		return true;
	}

	// Implementation for handling key presses. Not relevant for local game.
	@Override
	public void handleKeyPress(int keyCode) {
		// Do nothing... No keys relevant in local game.
	}

	// Implementation for handling key releases. Not relevant for local game.
	@Override
	public void handleKeyRelease() {
		// Do nothing... No keys relevant in local game.
	}
}