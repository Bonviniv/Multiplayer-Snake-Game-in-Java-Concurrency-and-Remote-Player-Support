package environment;

import java.io.Serializable;
import javax.sound.midi.SysexMessage;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;
import game.AutomaticSnake;

/**
 * Main class for game representation.
 * 
 * @author luismota
 *
 */
public class Cell {
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement = null;

	//////////////////////////////
	private Lock lock = new ReentrantLock();
	private Condition Occupied = lock.newCondition();
	//////////////////////////////

	public GameElement getGameElement() {
		return gameElement;
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public void request(Snake snake) throws InterruptedException {

		// TODO coordination and mutual exclusion
		lock.lock();

		if (snake == null) {
			ocuppyingSnake = snake;
		} else {
			if (snake.getCells().size() > 0) {
				while (isOcupied()) {
					Occupied.await();
				}
				ocuppyingSnake = snake;
			}
		}
		lock.unlock();
	}

	public void release() {
		// TODO
		lock.lock();
		try {
			try {
				Occupied.signal();
			} finally {
			}
			try {
				request(null);
			} catch (InterruptedException ignore) {
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake != null;
	}

	public void setGameElement(GameElement element) {
		// TODO coordination and mutual exclusion
		lock.lock();
		try {
			gameElement = element;
		} finally {
			lock.unlock();
		}
	}

	public boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement != null && gameElement instanceof Obstacle);
	}

	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}

	public Goal removeGoal() {
		// TODO
		lock.lock();
		try {
			if (gameElement instanceof Goal) {
				setGameElement(null);
			}
		} finally {
			lock.unlock();
		}
		return null;
	}

	public void removeObstacle() {
		// TODO
		lock.lock();
		try {
			if (gameElement instanceof Obstacle) {
				setGameElement(null);
			}
		} finally {
			lock.unlock();
		}
	}

	public Goal getGoal() {
		return (Goal) gameElement;
	}

	public boolean isOcupiedByGoal() {
		return (gameElement != null && gameElement instanceof Goal);
	}

}
