package gui;

import environment.LocalBoard;

import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard board = new LocalBoard(); // Create a local game board.
		SnakeGui game = new SnakeGui(board,100,0);  // Initialize the graphical user interface (GUI) for the game.
		game.init(); // Initialize game components and start the GUI.
		// TODO
		// Create and launch the server to handle remote connections. The server uses the same local board as the GUI.
		Server server = new Server(board);
		server.runServer(); // Start the server to listen for incoming client connections.
	}
}