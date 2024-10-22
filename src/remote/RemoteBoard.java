package remote;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import environment.Board;
import environment.BoardPosition;
import game.HumanSnake;
import gui.BoardComponent;

/**
 * Remote representation of the game, no local threads involved. Game state will
 * be changed when updated info is received from Server. Only for part II of the
 * project.
 * 
 * @author luismota
 *
 */
public class RemoteBoard extends Board implements Serializable, Observer {

	// --------------------------------ENUNCIADO-----------------------------
	//
	// A classe RemoteBoard, a desenvolver, deve representar o estado
	// do jogo que será ciclicamente enviada pelo servidor
	//
	// ------------------------------------------------------------------------
	
	private final static long serialVersionUID = 1L;
	public static final int BOARD_WIDTH = 800;
	public static final int BOARD_HEIGHT = 800;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private JFrame frame = new JFrame("The Snake Game: Remote");
	private BoardComponent boardGui;
	


	public RemoteBoard() {
		super();
		buidGui();
	}
	
	public void buidGui(){
		
		boardGui = new BoardComponent(this);
		boardGui.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
		frame= new JFrame("The Snake Game: Remote");
		frame.setLocation(600, 0);
    	frame.setLayout(new BorderLayout());		
		frame.add(boardGui,BorderLayout.CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void updateBoard(Information info) {
		snakes=info.getSnakes();
		cells=info.getCells();
	    setChanged();
	}

	
	@Override
	public void handleKeyPress(int keyCode) {
		
		 //TODO
		// 38 UP
		// 40 DOWN
		// 37 LEFT
		// 39 RIGHT
		
		String key = "---";
		if (keyCode == 38)
			key = "38";
		if (keyCode == 40)
			key = "40";
		if (keyCode == 37)
			key = "37";
		if (keyCode == 39)
			key = "39";

		Client.key = key;

		// System.out.println(key);
	}

	@Override
	public void handleKeyRelease() {
		// TODO
	}
	
	@Override
	public void init(){	   
		addObserver(this);
		frame.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		boardGui.repaint();
	}      


}
