package environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import game.*;

/** Class representing the state of a game running locally.
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board {
	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 25;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;
	private final ExecutorService obstacleMoverThreadPool; // ExecutorService to manage ObstacleMover threads.
	private GameState gameState;

	public LocalBoard() {
		// Initialize the thread pool with the fixed number of threads for moving obstacles.
		obstacleMoverThreadPool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);
		// Initialize snakes and obstacles on the board.
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}
		addObstacles(NUM_OBSTACLES);
		Goal goal = addGoal();
		System.err.println("All game elements placed.");
	}

	// Initializes the game by starting all snake threads and scheduling obstacle movers.
	public void init() {
		gameState = new GameState(cells, snakes);
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
		gameState.update(cells, snakes);
	}

	// Marks the game as finished, triggering a graceful shutdown process. This method should be called when the game is to be concluded.
	// All active game entities should periodically check the 'isFinished' flag and terminate their operations if it is set to true.
	public void endGame() {
		isFinished = true; // Signal all game entities that the game has ended.
		snakes.forEach(snake -> snake.interrupt()); // Stop all snakes.
		shutdownNow(); // Stop all obstacle movers.
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

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}
}