package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import environment.Board;
import environment.LocalBoard;
import game.AutomaticSnake;
import game.Snake;
import remote.RemoteBoard;

/**
 *  Class to create and configure GUI.
 *  Only the listener to the button should be edited, see TODO below.
 * 
 * @author luismota
 *
 */
public class SnakeGui implements Observer {
	// Constants defining the dimensions of the game board.
	public static final int BOARD_WIDTH = 800;
	public static final int BOARD_HEIGHT = 800;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private final JFrame frame; // Main window frame of the game.
	private BoardComponent boardGui; // Component that visualizes the game board.
	private final Board board; // The game board logic.
	private boolean isGameCompletedPopupShown = false; // Flag to control game completion popup display.

	public SnakeGui(Board board, int x, int y) {
		super();
		this.board = board;
		frame = new JFrame("The Snake Game: "+ (board instanceof LocalBoard? "Local":"Remote"));
		frame.setLocation(x, y);
		buildGui();
	}

	// Method to construct the GUI components and layout.
	private void buildGui() {
		frame.setLayout(new BorderLayout());
		boardGui = new BoardComponent(board);
		boardGui.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
		frame.add(boardGui,BorderLayout.CENTER);

		// Button to reset directions of automatic snakes.
		JButton resetObstaclesButton = new JButton("Reset snakes' directions");
		resetObstaclesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				board.getSnakes().forEach(snake -> {
					if(!snake.isInterrupted() && snake.isAlive() && snake instanceof AutomaticSnake && board instanceof LocalBoard) {
						snake.interrupt();
					}
				});
			}
		});

		// Disable the button if the board is a RemoteBoard instance.
		if (board instanceof RemoteBoard) {
			resetObstaclesButton.setEnabled(false);
		}

		frame.add(resetObstaclesButton, BorderLayout.SOUTH);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Initializes the game GUI and starts the game logic.
	public void init() {
		frame.setVisible(true);
		board.addObserver(this);
		board.init();
	}

	// Method called when the observed object (game board) is updated.
	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
		if (board.isFinished() && !isGameCompletedPopupShown) {
			isGameCompletedPopupShown = true;
			showGameCompletedPopup();
		}
	}

	// Displays a popup dialog to indicate game completion.
	private void showGameCompletedPopup() {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(frame, "Game completed!", "Snakes", JOptionPane.INFORMATION_MESSAGE);
		});
	}
}