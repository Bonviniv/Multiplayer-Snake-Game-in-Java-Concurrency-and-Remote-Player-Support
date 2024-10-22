package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;


public class Obstacle extends GameElement {
		
	private static final int NUM_MOVES=3;
	private static final int OBSTACLE_MOVE_INTERVAL = 400;
	private int remainingMoves=NUM_MOVES;
	private Board board;
	/////////////////
	public BoardPosition boardPosition;
    ////////////////
	
	public Obstacle(Board board) {
		super();
		this.board = board;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}
	
	/////////////////////
	public int getMoveTime() {
		return OBSTACLE_MOVE_INTERVAL;
	}
	
	public void setBoardPosition(BoardPosition boardPosition) {
		this.boardPosition=boardPosition;
	}
	
	public BoardPosition getBoardPosition() {
		return this.boardPosition;
	}
	
	public void decRemainMoves() {
		int aux=this.remainingMoves;
		if(this.remainingMoves>0)
		this.remainingMoves=aux-1;
		
	}
	////////////////////////
	
}
