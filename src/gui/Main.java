package gui;

import java.io.Console;
import java.io.IOException;
import java.util.LinkedList;

import javax.net.ssl.StandardConstants;

import environment.Cell;
import environment.LocalBoard;
import game.Server;
import remote.Client;
import remote.RemoteBoard;

public class Main {

	public static void main(String[] args) {

		LocalBoard board = new LocalBoard();
		SnakeGui game = new SnakeGui(board, 600, 0);
		// game.init();

		// Launch server
		// TODO

		try {
			new Server(game).startServing();
		} catch (IOException e) {
		}

		/////////////
		// AQUI RODA O SERVER
		///////////

	}

}
