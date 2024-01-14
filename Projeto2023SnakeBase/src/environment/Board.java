package environment;

import java.util.*;

import game.*;

public abstract class Board extends Observable {
	protected Cell[][] cells; // 2D array representing the cells of the board.
	private BoardPosition goalPosition; // Current position of the goal in the game.
	public static final long PLAYER_PLAY_INTERVAL = 200; // Interval between each move of a player.
	public static final long REMOTE_REFRESH_INTERVAL = 50;  // Interval for refreshing game state in remote play.
	public static final long REMOTE_CONNECTION_SETUP_DELAY = 10000; // Delay for setting up remote connections.
	public static final int NUMBER_COLUMNS = 30; // Number of columns on the board.
	public static final int NUMBER_ROWS = 30; // Number of rows on the board.
	protected LinkedList<Snake> snakes = new LinkedList<>(); // List of snakes present on the board.
	private final LinkedList<Obstacle> obstacles = new LinkedList<>(); // List of obstacles present on the board.
	protected volatile boolean isFinished; // Flag indicating if the game is finished.

	public Board() {
		cells = new Cell[NUMBER_COLUMNS][NUMBER_ROWS];
		for (int x = 0; x < NUMBER_COLUMNS; x++) {
			for (int y = 0; y < NUMBER_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}
	}

	// Returns the cell at given coordinates.
	public Cell getCell(BoardPosition coordinates) {
		return cells[coordinates.x][coordinates.y];
	}

	// Returns the 2D array of cells.
	public Cell[][] getCells() {
		return cells;
	}

	// Generates a random position within the board's bounds.
	protected BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() * NUMBER_COLUMNS),(int) (Math.random() * NUMBER_ROWS));
	}

	// Returns a list of neighboring positions around a given cell.
	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells = new ArrayList<BoardPosition>();
		BoardPosition position = cell.getPosition();
		if(position.x > 0)
			possibleCells.add(position.getCellLeft());
		if(position.x < NUMBER_COLUMNS -1)
			possibleCells.add(position.getCellRight());
		if(position.y > 0)
			possibleCells.add(position.getCellAbove());
		if(position.y < NUMBER_ROWS -1)
			possibleCells.add(position.getCellBelow());
		return possibleCells;
	}

	// Returns the current position of the goal.
	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	// Sets the goal's position.
	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}

	// Adds a game element (like a goal or obstacle) to the board at a random unoccupied position.
	public void addGameElement(GameElement gameElement) {
		boolean placed = false;
		while(!placed) {
			BoardPosition position = getRandomPosition();
			if(!getCell(position).isOccupied() && !getCell(position).isOcupiedByGoal()) {
				getCell(position).setGameElement(gameElement);
				gameElement.setCurrentPosition(position);
				if(gameElement instanceof Goal) {
					setGoalPosition(position);
					System.out.println("Goal " + ((Goal)gameElement).getValue() + " placed at position " + position + " .");
				}
				placed = true;
			}
		}
	}

	// Adds a goal to the board.
	protected Goal addGoal() {
		Goal goal = new Goal(this);
		addGameElement(goal);
		return goal;
	}

	// Adds a specified number of obstacles to the board.
	protected void addObstacles(int numberObstacles) {
		// Clear obstacle list. Necessary when resetting obstacles.
		int id = 0;
		getObstacles().clear();
		while(numberObstacles > 0) {
			Obstacle obstacle = new Obstacle(id++,this);
			addGameElement(obstacle);
			getObstacles().add(obstacle);
			numberObstacles--;
		}
	}

	// Returns the list of obstacles on the board.
	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}

	// Returns the list of snakes on the board.
	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

	// Adds a snake to the board.
	public void addSnake(Snake snake) {
		snakes.add(snake);
	}

	// Marks the observable object as changed and notifies observers.
	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	// Initializes the board. To be implemented in subclasses.
	public abstract void init();

	// Returns whether the game is finished.
	public boolean isFinished() {
		return isFinished;
	}

	// Attempts to find an unoccupied position on the board, starting with random locations.
	public BoardPosition getUnoccupiedPosition() {
		// Continuously searches for an unoccupied position on the board.
		while(true) {
			BoardPosition position = getRandomPosition();
			if(!getCell(position).isOccupied() && !getCell(position).isOcupiedByGoal()) {
				return position;
			}
		}
	}

	// Checks if a given position is within the bounds of the board.
	public boolean isWithinBounds(BoardPosition position) {
		int x = position.x;
		int y = position.y;
		return (x >= 0 && x < NUMBER_COLUMNS && y >= 0 && y < NUMBER_ROWS);
	}

	// Handles key press events. To be implemented in subclasses.
	public abstract void handleKeyPress(int keyCode);

	// Handles key release events. To be implemented in subclasses.
	public abstract void handleKeyRelease();
}