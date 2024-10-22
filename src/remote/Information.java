package remote;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import environment.Cell;
import game.Snake;

public class Information implements Serializable {

	private final static long serialVersionUID= 1L;
	private  transient Cell[][] cells;
	private  transient LinkedList<Snake> snakes = new LinkedList<Snake>();
	
	public Information(LinkedList<Snake> snakes,Cell[][] cells) {
		this.snakes=snakes;
		this.cells=cells;
	}
	
	public Cell[][] getCells() {
		return cells;
	}
	
	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

}
