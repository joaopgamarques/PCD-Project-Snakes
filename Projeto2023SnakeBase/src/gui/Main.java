package gui;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.StandardConstants;

import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;
import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard board = new LocalBoard();
		SnakeGui game = new SnakeGui(board,100,0);
		game.init();
		// TODO
		// Launch the server.
		Server server = new Server(board);
		server.runServer();
	}
}