package environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;

public abstract class Board extends Observable {
	protected Cell[][] cells;
	private BoardPosition goalPosition;
	public static final long PLAYER_PLAY_INTERVAL = 100;
	public static final long REMOTE_REFRESH_INTERVAL = 200;
	public static final int NUM_COLUMNS = 30;
	public static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	protected LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
	protected boolean isFinished;
	
	//////////////
	private Lock lock = new ReentrantLock();
	private boolean started=false;
    //////////////
	
	public boolean finished() {
		return this.isFinished;
	}
	public boolean isStarted() {
		return this.started;
	}
	public void setStarted() {
		started=true;
	}

	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}

	}
	
    public Cell[][] getCells(){
    	return cells;
    }
	
	public Cell getCell(BoardPosition cellCoord) {
		return cells[cellCoord.x][cellCoord.y];
	}

	protected BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() * NUM_ROWS), (int) (Math.random() * NUM_ROWS));
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}

	public void addGameElement(GameElement gameElement) {
		try {lock.lock();
		boolean placed = false;
		if(gameElement instanceof Goal) {
			if (((Goal) gameElement).getValue() >= ((Goal) gameElement).MAX_VALUE) {
				isFinished = true;
				placed = true;
			}
		}
		while (!placed) {
			BoardPosition pos = getRandomPosition();
			if (!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
				getCell(pos).setGameElement(gameElement);
				if (gameElement instanceof Goal) {
					
						setGoalPosition(pos);
					
				}
				if (gameElement instanceof Obstacle) {
					((Obstacle) gameElement).setBoardPosition(pos);
				}

				placed = true;
			}
		}
		}finally {lock.unlock();}
		
	}

	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells = new ArrayList<BoardPosition>();
		BoardPosition pos = cell.getPosition();
		if (pos.x > 0)
			possibleCells.add(pos.getCellLeft());
		if (pos.x < NUM_COLUMNS - 1)
			possibleCells.add(pos.getCellRight());
		if (pos.y > 0)
			possibleCells.add(pos.getCellAbove());
		if (pos.y < NUM_ROWS - 1)
			possibleCells.add(pos.getCellBelow());
		return possibleCells;

	}

	protected Goal addGoal() {
		Goal goal = new Goal(this);
		addGameElement(goal);
		return goal;
	}

	protected void addObstacles(int numberObstacles) {
		// clear obstacle list , necessary when resetting obstacles.
		
			getObstacles().clear();
		while (numberObstacles > 0) {
			Obstacle obs = new Obstacle(this);
			addGameElement(obs);
			getObstacles().add(obs);
			numberObstacles--;
		}
		
		
		
	}

	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}

	public abstract void init();

	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();

	public void addSnake(Snake snake) {
		try {lock.lock();
	snakes.add(snake);
	}finally {lock.unlock();}
		
	}
	
	

}