package remote;

import environment.Board;
import environment.GameState;

/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Server.
 * Only for part II of the project.
 * @author luismota
 *
 */

public class RemoteBoard extends Board {

	public void setChanged(GameState GameState) {
		this.cells = GameState.getCells();
		this.snakes = GameState.getSnakes();
		super.setChanged();
	}
	
	@Override
	public void handleKeyPress(int keyCode) {
		//TODO
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