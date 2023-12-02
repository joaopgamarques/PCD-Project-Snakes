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
import game.Snake;

/**
 *  Class to create and configure GUI.
 *  Only the listener to the button should be edited, see TODO below.
 * 
 * @author luismota
 *
 */
public class SnakeGui implements Observer {
	public static final int BOARD_WIDTH = 800;
	public static final int BOARD_HEIGHT = 800;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private final JFrame frame;
	private BoardComponent boardGui;
	private final Board board;
	private boolean isGameCompletedPopupShown = false;

	public SnakeGui(Board board, int x, int y) {
		super();
		this.board=board;
		frame= new JFrame("The Snake Game: "+ (board instanceof LocalBoard? "Local":"Remote"));
		frame.setLocation(x, y);
		buildGui();
	}

	private void buildGui() {
		frame.setLayout(new BorderLayout());
		boardGui = new BoardComponent(board);
		boardGui.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
		frame.add(boardGui,BorderLayout.CENTER);

		JButton resetObstaclesButton=new JButton("Reset snakes' directions");
		resetObstaclesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				board.getSnakes().forEach(snake -> {
					if(!snake.isInterrupted() && snake.isAlive()) {
						snake.interrupt();
					}
				});
			}
		});

		frame.add(resetObstaclesButton,BorderLayout.SOUTH);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		frame.setVisible(true);
		board.addObserver(this);
		board.init();
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
		if (board.isFinished() && !isGameCompletedPopupShown) {
			isGameCompletedPopupShown = true;
			showGameCompletedPopup();
		}
	}

	private void showGameCompletedPopup() {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(frame, "Game completed!", "Snakes", JOptionPane.INFORMATION_MESSAGE);
		});
	}
}