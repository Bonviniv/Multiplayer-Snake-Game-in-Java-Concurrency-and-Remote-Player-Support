package game;

import environment.LocalBoard;
import java.util.Observable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import javax.swing.text.Position;
import environment.LocalBoard;
import gui.SnakeGui;
import remote.RemoteBoard;
import environment.Cell;
import game.Goal;
import environment.Board;
import environment.BoardPosition;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObstacleMover extends Thread {
	private Obstacle obstacle;
	private LocalBoard board;
	private RemoteBoard remoteBoard;

	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}

	// contrutor para a parte 2
	public ObstacleMover(Obstacle obstacle, RemoteBoard remoteBoard) {
		super();
		this.obstacle = obstacle;
		this.remoteBoard = remoteBoard;
	}

	// corre a local Board
	public void runLocalBoard() {

		while (obstacle.getRemainingMoves() > 0 && !board.finished()) {
			try {
				Thread.sleep(obstacle.getMoveTime());
				if (board.finished()) {
					interrupt();
				}
				obstacle.decRemainMoves();
				board.getCell(obstacle.boardPosition).removeObstacle();
				board.addGameElement(obstacle);
				board.setChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// corre a remoteBoard
	public void runRemoteBoard() {

		while (obstacle.getRemainingMoves() > 0 && !remoteBoard.finished()) {
			try {
				Thread.sleep(obstacle.getMoveTime());
				if (remoteBoard.finished()) {
					interrupt();
				}
				obstacle.decRemainMoves();
				remoteBoard.getCell(obstacle.boardPosition).removeObstacle();
				remoteBoard.addGameElement(obstacle);
				remoteBoard.setChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		// TODO

		// corre a remoteBoard
		if (board == null && remoteBoard != null) {
			runRemoteBoard();
		}else{
			// corre a local Board
			runLocalBoard();
		}

	}
}
