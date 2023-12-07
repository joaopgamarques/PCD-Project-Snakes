package remote;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.GameState;
import game.Goal;

import java.awt.event.KeyEvent;

/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Server.
 * Only for part II of the project.
 * @author luismota
 *
 */

public class RemoteBoard extends Board {
	private Client client;

	// Client instance for communication with the server.
	public void setClient(Client client) {
		this.client = client;
	}

	// Sets the client instance used for communication with the server.
	public void setChanged(GameState gameState) {
		this.cells = gameState.getCells();
		this.snakes = gameState.getSnakes();
		this.isFinished = gameState.isFinished();
		super.setChanged();
	}

	// Handles key press events for remote control. Sends direction commands to the server.
	@Override
	public void handleKeyPress(int keyCode) {
		//TODO
		Direction direction = null; // Variable to store the direction based on the key pressed.
		// Determine the direction based on the key code.
		switch (keyCode) {
			case KeyEvent.VK_UP -> direction = Direction.UP;
			case KeyEvent.VK_DOWN -> direction = Direction.DOWN;
			case KeyEvent.VK_LEFT -> direction = Direction.LEFT;
			case KeyEvent.VK_RIGHT -> direction = Direction.RIGHT;
		}

		// Sends a direction command to the server.
		if (direction != null && client != null) {
			System.out.println(direction.toString());
			client.getPrintWriter().println(direction.toString());
		}
	}

	// Handles key release events. Sends a stop command to the server.
	@Override
	public void handleKeyRelease() {
		// TODO
		client.getPrintWriter().println("Stop.");
		client.getPrintWriter().flush();
	}

	// Initialize the remote board. Additional setup can be added here if needed.
	@Override
	public void init() {
		// TODO
		isFinished = false;
	}
}