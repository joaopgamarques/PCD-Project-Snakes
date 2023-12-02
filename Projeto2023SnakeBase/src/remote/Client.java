package remote;


import environment.LocalBoard;
import gui.SnakeGui;

/** Remore client, only for part II
 *
 * @author luismota
 *
 */

public class Client {

	public static void main(String[] args) {
		// TODO
		RemoteBoard board = new RemoteBoard();
		SnakeGui game = new SnakeGui(board,1000,0);
		game.init();
	}

}