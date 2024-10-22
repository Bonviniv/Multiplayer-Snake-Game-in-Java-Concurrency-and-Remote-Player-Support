package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import environment.LocalBoard;
import gui.SnakeGui;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;

/**
 * Base class for representing Snakes. Will be extended by HumanSnake and
 * AutomaticSnake. Common methods will be defined here.
 * 
 * @author luismota
 *
 */
public abstract class Snake extends Thread implements Serializable {
	private static final int DELTA_SIZE = 10;
	protected LinkedList<Cell> cells = new LinkedList<Cell>();

	//////////////
	private int adding = 0;
	public boolean stucked = false;
	private boolean alive = true;
	private boolean human;
	//////////

	protected int size = 5;
	private int id;
	private Board board;

	public void changeAlive() {
		alive = !alive;
	}

	public boolean isSnakeAlive() {
		return this.alive;
	}

	public Snake(int id, Board board) {
		this.id = id;
		this.board = board;
	}

	public int getSize() {
		return size;
	}

	public int getIdentification() {
		return id;
	}

	public int getLength() {
		return cells.size();
	}

	public LinkedList<Cell> getCells() {
		return cells;
	}

	protected void move(Cell cell) throws InterruptedException {
		// TODO

		if (board.finished()) {
			interrupt();
		}

		// se for humano e tuver ocupado ou se for a propria cabeça da cobra

		if (cell == cells.getFirst()) {
			return;
		}

		if (cell.isOcupiedByGoal()) {

			adding = adding + board.getCell(board.getGoalPosition()).getGoal().getValue();
			board.getCell(board.getGoalPosition()).getGoal().captureGoal();

			// System.out.println("Goal capturado com o valor de = " + adding);
			// System.out.println("----adding "+adding+" ----");
		}

		if (adding > 0) {
			// System.out.println("adding move:" + adding);
			cells.push(cell);
			cell.request(this);
			adding--;
			// System.out.println("cells size:" + cells.size());
		} else {

			cell.request(this);
			cells.push(cell);

			cells.get(cells.size() - 1).release();

			cells.removeLast();

		}

	}

	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
			coordinates.add(cell.getPosition());
		}

		return coordinates;
	}

	protected void doInitialPositioning() {
		// Random position on the first column.
		// At startup, snake occupies a single cell
		int posX = 0;
		int posY = (int) (Math.random() * Board.NUM_ROWS);
		BoardPosition at = new BoardPosition(posX, posY);

		if (board.getCell(at).isOcupied()) {
			doInitialPositioning();
		} else {
			try {
				board.getCell(at).request(this);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		cells.add(board.getCell(at));

		System.err.println("Snake " + getIdentification() + " starting at:" + getCells().getLast());
	}

	public Board getBoard() {
		return board;
	}

}
