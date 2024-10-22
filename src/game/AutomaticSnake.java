package game;

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

public class AutomaticSnake extends Snake {
	
    ///////////////
	private final int LIMIT_MAX = 3;
	private int limit = LIMIT_MAX;
	private boolean human=false;
    ///////////////	
	
	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);
	}
	
	public AutomaticSnake(int id, RemoteBoard board) {
		super(id, board);
	}

	@Override
	public void run() {

		doInitialPositioning();
		try {
			
			/////////
			System.err.println("initial size:" + cells.size());
			////////
			
			cells.getLast().request(this);
		} catch (InterruptedException ignored) {
		}
		// TODO: automatic movement

		while (!getBoard().finished()) {
			try {
				Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
				Cell nextCell = nextCell(getBoard());
				if (!nextCell.equals(cells.getFirst())) {
					if (!nextCell.isOcupied()) {
						move(nextCell);
						//System.err.println("nextCell:" + nextCell.toString());
					}
					//System.err.println("bati");
					getBoard().setChanged();
				}

			} catch (InterruptedException e) {
				System.out.println("Thread enterrompida" + currentThread().getName());
			}
		}
	}

	public Cell nextCell(Board board) throws InterruptedException {
		
		// seleciona as células em volta da cabeça da cobra como possiveis proximas células
		List<BoardPosition> possibleCells = board.getNeighboringPositions(cells.getFirst());
		List<BoardPosition> possibleCellsPos = new ArrayList<>();
		
		// lista filtrada para a cobra n ir contra ela mesma
		for (BoardPosition b : possibleCells) {
			if (!getCells().contains(board.getCell(b)))
				possibleCellsPos.add(b);
		}
		
		if (possibleCellsPos.size() < 1)
			return cells.getFirst();
		
		BoardPosition nextBP = possibleCellsPos.get(0);
		if (stucked) {
			List<BoardPosition> possibleCellsStucked = new ArrayList<>();
			stucked = false;
			for (BoardPosition b : possibleCellsPos) {
				if (!board.getCell(b).isOcupied()) {
					possibleCellsStucked.add(b);
				}
			}
			if (possibleCellsStucked.size() > 0) {
				limit = LIMIT_MAX;
				int ind = (int) (possibleCellsStucked.size() * Math.random());
				BoardPosition ntPosStu = possibleCellsStucked.get((int) (possibleCellsStucked.size() * Math.random()));
				while (board.getCell(possibleCellsStucked.get(ind)) == null) {
					ntPosStu = possibleCellsStucked.get((int) (possibleCellsStucked.size() * Math.random()));
				}
				return board.getCell(ntPosStu);
			} else {
				if (limit <= 0) {
					System.out.println("morri " + getName() + " " + getId());
					this.changeAlive();
					this.stop();
					interrupt();
				} else {
					limit--;
					return cells.getFirst();
				}
			}
		}
		
		// seleção do melhor
		for (BoardPosition b : possibleCellsPos) {
			double bdist = b.distanceTo(board.getGoalPosition());
			double nextdist = nextBP.distanceTo(board.getGoalPosition());
			if ((bdist <= nextdist))
				nextBP = b;
		}
		
		Cell next = board.getCell(nextBP);
		if (next.isOcupiedBySnake()) {
			//System.err.println("bati em uma cobra");
		}
			
		return next;
	}

	

}
