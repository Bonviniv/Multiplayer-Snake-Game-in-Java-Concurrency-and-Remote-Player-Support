package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;
import gui.SnakeGui;
import remote.Client;
import remote.Information;
import remote.RemoteBoard;

public class Server {
	// TODO
	public static final int PORTO = 8081;
	private int id = 042;
	private LocalBoard board;
	private SnakeGui game;
	private boolean notFunctional = false;
	private boolean gameRunning = false;
	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 25;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private boolean ended=false;

	// --------------------------------ENUNCIADO-----------------------------
	//
	// Em termos de comunicação, devem ser respeitadas as seguintes indicacões:
	//
	// 1.
	// O estado do jogo deve ser enviado regularmente a todos os jogadores remotos
	// ligados:
	// sugere-se que se use o intervalo em Board.REMOTE REFRESH INTERVAL.
	// Este envio deve ser feito independentemente do funcionamento do jogo,
	// por processo ligeiro autonomo, usando canais de objetos.
	//
	// 2.
	// Os jogadores humanos não bloquearão se forem encaminhados para
	// uma célula ocupada por um obstáculo ou outra cobra: ignorarão apenas
	// o movimento.
	//
	// 3.
	// Os clientes remotos devem receber 2 argumentos de execucão: endereço
	// e porto da aplicacão principal.
	//
	// ------------------------------------------------------------------------

	//
	// *resumo da lógica que eu fiz:
	//
	// thread 1 -> cria o cliente e envia ciclicamente a board pra ele
	//
	// função startServing() da classe Server -> lança a thread 1
	//
	// (startServing() -> thread 1 )
	//

	public Server(SnakeGui game) {
		prepareGame();

	}

	public void prepareGame() {
		board = new LocalBoard();
		game = new SnakeGui(board, 600, 0);
		game.init();

	}

	public void startServing() throws IOException {
		System.out.println("Server started");
		ServerSocket ss = new ServerSocket(PORTO);
		System.out.println("Server socket at port " + PORTO);
		try {
			while (true) {

				System.out.println("Server waiting");
				Socket socket = ss.accept();

				// aceita clientes
				// inicia a thread q liga o cliente e manda a board pra ele (thread 1)

				new ServerDealWithClientThread(socket).start();

			}
		} finally {
			ss.close();
		}

	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//
//-----------------------------------------------thread 1-----------------------------------------------------------------	
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	// é necessario criar uma thread para cada cliente que se liga.
	public class ServerDealWithClientThread extends Thread {

		private Socket socket;
		private BufferedReader in;
		private ObjectOutputStream out;
		LinkedList<String> info = new LinkedList<String>();
		private String infoClient;

		public ServerDealWithClientThread(Socket socket) throws IOException {
			this.socket = socket;
			doConnections(socket);
			Information info = new Information(board.getSnakes(), board.getCells());
			out.writeObject(info);

		}

		// Cria ligações input/output com o server
		void doConnections(Socket socket) throws IOException {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new ObjectOutputStream(socket.getOutputStream());
		}

		public void closeConnections() {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void serve() {
			// envi cicliacamente RemoteBoard para o cliente

			while (true) {
				if (notFunctional) {
					// System.out.println("Server sending board / setChange");
				} else {
					try {

						Information information = new Information(board.getSnakes(), board.getCells());
						out.writeObject(information);
						Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);

					} catch (IOException | InterruptedException e) {
						// e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void run() {


			new ServerDealWithClientInfoThread().start();

			while (!ended) {
				try {

					// envia a info ciclicamente
					serve();

				} finally {
					closeConnections();
				}

			}
		}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//
//-----------------------------------------------thread 2 ----------------------------------------------------
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		public class ServerDealWithClientInfoThread extends Thread {

			private ClientHumanSnake playerSnake;

			public ServerDealWithClientInfoThread() {
				playerSnake = new ClientHumanSnake(id, board);
				board.addSnake(playerSnake);
				for (Snake s : board.getSnakes()) {
					if (s.equals(playerSnake))
						s.start();
				}
			}

			public void serve() {

				try {
					infoClient = in.readLine();
					// LEFT 7
					// UP 8
					// RIGHT 9
					// DOWN 0
					String lastKey = infoClient.substring(infoClient.length() - 2, infoClient.length());
					switch (lastKey) {
					case "37":
						try {
							playerSnake.move(playerSnake.getHeadCellLeft());
							lastKey = "LEFT";
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					case "38":
						try {
							playerSnake.move(playerSnake.getHeadCellAbove());
							lastKey = "UP";
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					case "39":
						try {
							playerSnake.move(playerSnake.getHeadCellRight());
							lastKey = "RIGHT";
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					case "40":
						try {
							playerSnake.move(playerSnake.getHeadCellBelow());
							lastKey = "DOWN";
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}

					System.out.println("lastKey " + lastKey);

				} catch (IOException e) {
				}

			}

			@Override
			public void run() {

				while (!ended) {
					serve();
				}

			}
		}

	}

	public class ClientHumanSnake extends HumanSnake {
		public ClientHumanSnake(int id, Board board) {
			super(id, board);
		
		}

		public void checkEnd() {
			if(board.finished())
				ended=true;
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

			while (!getBoard().finished()) {
				try {
					checkEnd();
					Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
					
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}

		}
	}
}
