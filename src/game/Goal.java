package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

import java.util.Observer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Goal extends GameElement {
	private int value = 1;
	private Board board;
	public static final int MAX_VALUE = 10;

	public Goal(Board board2) {
		this.board = board2;
	}

	public int getValue() {
		return value;
	}

	public void incrementValue() throws InterruptedException {
		// TODO
		value++;

	}

	public int captureGoal() {
//		TODO

		
		// cria novo goal que é clone do atual
		Goal goalNovo = this;
		int AtualValue = goalNovo.getValue();
		System.out.println("Atual Goal value:" + AtualValue);

		
		try {
			goalNovo.incrementValue();
			board.getCell(board.getGoalPosition()).removeGoal();
			board.addGameElement(goalNovo);
		} catch (InterruptedException e) {}
		

		System.out.println("New Goal value:" + goalNovo.getValue());
		board.setChanged();

		return -1;
	}

}
