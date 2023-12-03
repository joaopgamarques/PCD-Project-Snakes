package remote;

import environment.Board;
import environment.GameState;

import java.awt.event.KeyEvent;

/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Server.
 * Only for part II of the project.
 * @author luismota
 *
 */

public class RemoteBoard extends Board {
	private Client client;

	public void setClient(Client client) {
		this.client = client;
	}

	public void setChanged(GameState GameState) {
		this.cells = GameState.getCells();
		this.snakes = GameState.getSnakes();
		super.setChanged();
	}

	@Override
	public void handleKeyPress(int keyCode) {
		//TODO
		Direction direction = null;
		switch (keyCode) {
			case KeyEvent.VK_UP -> direction = Direction.UP;
			case KeyEvent.VK_DOWN -> direction = Direction.DOWN;
			case KeyEvent.VK_LEFT -> direction = Direction.LEFT;
			case KeyEvent.VK_RIGHT -> direction = Direction.RIGHT;
		}
		if (direction != null && client != null) {
			client.sendDirection(direction);
		}
	}

	@Override
	public void handleKeyRelease() {
		// TODO

	}

	@Override
	public void init() {
		// TODO 		
	}

}