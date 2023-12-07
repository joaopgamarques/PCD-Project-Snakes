package gui;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;

import game.Goal;
import game.HumanSnake;
import game.Obstacle;
import game.Snake;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Graphical representarion of the game. This class should not be edited.
 *
 * @author luismota
 */
public class BoardComponent extends JComponent implements KeyListener {
    private final Board board;
    private final Image obstacleImage;

    public BoardComponent(Board board) {
        this.board = board;
        obstacleImage = new ImageIcon(getClass().getResource("/obstacle.png")).getImage();
        // Necessary for key listener.
        setFocusable(true);
        addKeyListener(this);
    }

	// Paints the board component.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final double CELL_WIDTH = getHeight() / (double) SnakeGui.NUM_ROWS;

		// Iterate over each cell and render its contents.
        for (int x = 0; x < LocalBoard.NUMBER_COLUMNS; x++) {
            for (int y = 0; y < LocalBoard.NUMBER_ROWS; y++) {
                Cell cell = board.getCell(new BoardPosition(x, y));
                Image image = null;
                if (cell.getGameElement() != null)
                    if (cell.getGameElement() instanceof Obstacle) {
                        Obstacle obstacle = (Obstacle) cell.getGameElement();
                        image = obstacleImage;
                        g.setColor(Color.BLACK);
                        g.drawImage(image, (int) Math.round(cell.getPosition().x * CELL_WIDTH),
                                (int) Math.round(cell.getPosition().y * CELL_WIDTH),
                                (int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH), null);
                        // Write number of remaining moves.
                        g.setColor(Color.WHITE);
                        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
                        g.drawString(obstacle.getRemainingMoves() + "", (int) Math.round((cell.getPosition().x + 0.15) * CELL_WIDTH),
                                (int) Math.round((cell.getPosition().y + 0.9) * CELL_WIDTH));
                    } else if (cell.getGameElement() instanceof Goal) {
                        Goal goal = (Goal) cell.getGameElement();
                        g.setColor(Color.RED);
                        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
                        g.drawString(goal.getValue() + "", (int) Math.round((cell.getPosition().x + 0.15) * CELL_WIDTH),
                                (int) Math.round((cell.getPosition().y + 0.9) * CELL_WIDTH));
                    }
                if (cell.isOccupiedBySnake()) {
                    // Different color for human player.
                    if (cell.getOccupyingSnake() instanceof HumanSnake)
                        g.setColor(Color.ORANGE);
                    else
                        g.setColor(Color.LIGHT_GRAY);
                    g.fillRect((int) Math.round(cell.getPosition().x * CELL_WIDTH), (int) Math.round(cell.getPosition().y * CELL_WIDTH),
                            (int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH));

                }

            }
            g.setColor(Color.BLACK);
            g.drawLine((int) Math.round(x * CELL_WIDTH), 0, (int) Math.round(x * CELL_WIDTH),
                    (int) Math.round(LocalBoard.NUMBER_ROWS * CELL_WIDTH));
        }
        for (int y = 1; y < LocalBoard.NUMBER_ROWS; y++) {
            g.drawLine(0, (int) Math.round(y * CELL_WIDTH), (int) Math.round(LocalBoard.NUMBER_COLUMNS * CELL_WIDTH),
                    (int) Math.round(y * CELL_WIDTH));
        }
        for (Snake s : board.getSnakes()) {
            if (s.getLength() > 0) {
                g.setColor(new Color(s.getIdentification() * 1000));

                ((Graphics2D) g).setStroke(new BasicStroke(5));
                BoardPosition prevPos = s.getPath().getFirst();
                for (BoardPosition coordinate : s.getPath()) {
                    if (prevPos != null) {
                        g.drawLine((int) Math.round((prevPos.x + .5) * CELL_WIDTH),
                                (int) Math.round((prevPos.y + .5) * CELL_WIDTH),
                                (int) Math.round((coordinate.x + .5) * CELL_WIDTH),
                                (int) Math.round((coordinate.y + .5) * CELL_WIDTH));
                    }
                    prevPos = coordinate;
                }
                ((Graphics2D) g).setStroke(new BasicStroke(1));
            }
        }
    }

    // Only for remote clients: 2. part of the project.
    // Methods keyPressed and keyReleased will react to user pressing and releasing keys on the keyboard.

	// Reacts to key presses for remote clients.
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Got key pressed.");
        if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
                e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
            return; // ignore
        board.handleKeyPress(e.getKeyCode());
    }

	// Reacts to key releases for remote clients.
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
                e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
            return; // ignore

        System.out.println("Got key released.");
        board.handleKeyRelease();
    }

	// Unused, but required by KeyListener interface.
    @Override
    public void keyTyped(KeyEvent arg0) {
        // ignore
    }
}