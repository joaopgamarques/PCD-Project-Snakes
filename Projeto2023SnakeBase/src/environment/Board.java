package environment;

import java.util.*;

import game.*;

public abstract class Board extends Observable {
	protected Cell[][] cells;
	private BoardPosition goalPosition;
	public static final long PLAYER_PLAY_INTERVAL = 100;
	public static final long REMOTE_REFRESH_INTERVAL = 200;
	public static final long REMOTE_CONNECTION_SETUP_DELAY = 10000;
	public static final int NUM_COLUMNS = 30;
	public static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	private final LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
	protected volatile boolean isFinished;

	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}
	}

	public Cell getCell(BoardPosition coordinates) {
		return cells[coordinates.x][coordinates.y];
	}

	protected BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() *NUM_ROWS),(int) (Math.random() * NUM_ROWS));
	}

	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells = new ArrayList<BoardPosition>();
		BoardPosition position = cell.getPosition();
		if(position.x > 0)
			possibleCells.add(position.getCellLeft());
		if(position.x < NUM_COLUMNS-1)
			possibleCells.add(position.getCellRight());
		if(position.y > 0)
			possibleCells.add(position.getCellAbove());
		if(position.y < NUM_ROWS-1)
			possibleCells.add(position.getCellBelow());
		return possibleCells;
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}
	
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

	protected Goal addGoal() {
		Goal goal = new Goal(this);
		addGameElement(goal);
		return goal;
	}

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

	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}

	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

	public void addSnake(Snake snake) {
		snakes.add(snake);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	public abstract void init();

	public boolean isFinished() {
		return isFinished;
	}

	// Attempts to find an unoccupied position on the board, starting with random locations.
	public BoardPosition getUnoccupiedPosition() {
		while(true) {
			BoardPosition position = getRandomPosition();
			if(!getCell(position).isOccupied() && !getCell(position).isOcupiedByGoal()) {
				return position;
			}
		}
	}

	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();
}