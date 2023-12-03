package gui;

import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard board = new LocalBoard();
		SnakeGui game = new SnakeGui(board,100,0);
		game.init();
		// TODO
		// Launch the server.
		Server server = new Server(board);
		server.run();
	}
}