package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import environment.Board;
import environment.Cell;
import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.ObstacleMover;
import game.Server;
import game.Snake;

import game.AutomaticSnake;

/**
 * Class for a remote snake, controlled by a human
 * 
 * @author luismota
 *
 */
public abstract class HumanSnake extends Snake {

	protected boolean human = true;

	public HumanSnake(int id, Board board) {
		super(id, board);
	}

	public Cell getHeadCellAbove() {

		if (cells.getFirst().getPosition().y > 0) {
			Cell c =getBoard().getCell(cells.getFirst().getPosition().getCellAbove());
			if(!c.isOcupied())
			return c;
		} 
			return cells.getFirst();
	}

	public Cell getHeadCellRight() {
		if (cells.getFirst().getPosition().x < 29 ) {
			Cell c =getBoard().getCell(cells.getFirst().getPosition().getCellRight());
			System.out.println(c.getPosition().toString());
			if(!c.isOcupied())
				return c;
			} 
				return cells.getFirst();
		}

	public Cell getHeadCellLeft() {
		if (cells.getFirst().getPosition().x > 0) {
			Cell c =getBoard().getCell(cells.getFirst().getPosition().getCellLeft());
			if(!c.isOcupied())
				return c;
			} 
				return cells.getFirst();
		}

	public Cell getHeadCellBelow() {
		if (cells.getFirst().getPosition().y < 29) {
			Cell c = getBoard().getCell(cells.getFirst().getPosition().getCellBelow());
			if(!c.isOcupied())
				return c;
			} 
				return cells.getFirst();
		}

}
