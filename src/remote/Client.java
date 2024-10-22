package remote;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;

import environment.Board;
import environment.Cell;
import game.Snake;
import gui.BoardComponent;
import gui.SnakeGui;

public class Client {


	private Socket socket;
	private ObjectInputStream in;
	private ObjectInputStream inDir;
	private PrintWriter out;
	public static final int PORTO = 8081;
	public static String key = "38";
	private static RemoteBoard remoteBoard;
	private Information info;

/////////////
// AQUI RODA O CLIENTE
///////////

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		// TODO
		// connect() -> serve()
		remoteBoard = new RemoteBoard();
		remoteBoard.init();
		new Client().clientInit();

	}

	public void clientInit() throws ClassNotFoundException, IOException, InterruptedException {
		try {
			connect();
			new RecieveInfo().start();
			new SendInfo().start();
			serve();
		} finally {
			closeConnections();
		}
	}

	public void connect() throws IOException {
		InetAddress addr = InetAddress.getByName(null);
		socket = new Socket(addr, PORTO);
		System.out.println("Addr : " + addr + " Socket " + socket);

		in = new ObjectInputStream(socket.getInputStream());
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

		System.out.println("Client connected");
	}

	public void closeConnections() {
		try {
			if (in != null) {
				in.close();
				out.close();
				socket.close();
			}
		} catch (IOException e) {
		}
	}

	public void serve() throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println("Client serving");
		while (true) {

		}
	}

	public class RecieveInfo extends Thread {
		@Override
	    public void run() {
			
	        System.out.println("RecieveInfo");
	        while (true) {
	            serve();
	            try {
					sleep(Board.PLAYER_PLAY_INTERVAL);
				} catch (InterruptedException e) {e.printStackTrace();}
	        }
	    }

	    private void serve() {
	        try {
	            info = (Information) in.readObject();
	            if (info.getCells() == null)
	            	System.out.println("getcells null");
	            if (info.getSnakes() == null)
	            	System.out.println("getSnakes null");
	            if (!(info == null) && !(info.getCells() == null) && !(info.getSnakes() == null)) {
	                updateBoard(info);
	                remoteBoard.setChanged();
	            }
	        } catch (ClassNotFoundException | IOException e) {
	            e.printStackTrace();
	            // Lidar com exceções (por exemplo, reconectar ou encerrar a aplicação)
	        }
	    }
	        
	        // Adicione este método para atualizar a RemoteBoard com as informações recebidas.
	        private void updateBoard(Information info) {
	            remoteBoard.updateBoard(info);
	        }
	    }



	public class SendInfo extends Thread {
		public void serve() {
			System.out.println("Client key " + key);
			if (key != null) {
				out.println(key);
			} else {
				out.println(".");
			}
		}

		@Override
		public void run() {
			System.out.println("SendInfo");
			while (true) {
				serve();
				try {
					sleep(Board.PLAYER_PLAY_INTERVAL);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}


}
