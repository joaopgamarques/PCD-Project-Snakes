package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import environment.GameState;
import gui.SnakeGui;

/** Remore client, only for part II
 *
 * @author luismota
 *
 */

public class Client {
	private Socket connection; // Client socket for communicating with the server.
	private final InetAddress server; // IP address of the server.
	private final int port; // Port number of the server.
	private ObjectInputStream in; // Stream for receiving data from the server.
	private PrintWriter out; // Stream for sending data to the server.
	private final RemoteBoard remoteBoard;

	public Client(InetAddress server, int port, RemoteBoard remoteBoard) {
		this.server = server;
		this.port = port;
		this.remoteBoard = remoteBoard;
		remoteBoard.setClient(this);
	}

	public PrintWriter getPrintWriter() {
		return out;
	}

	// Runs the client to connect to the server and update the game state.
	public void run() {
		try {
			connectToServer(); // Establish a connection to the server.
			getStreams(); // Setup I/O streams.
			// Continuously process game updates from the server.
			while (!connection.isClosed()) {
				processConnection(); // Process the incoming game state.
			}
		} catch (SocketException e) {
			System.out.println("Connection closed by server.");
		} catch (IOException e) {
			System.err.println("Client error: " + e.getMessage() + ".");
		} finally {
			closeConnection(); // Close the connection when done or on error.
		}
	}

	// Establishes a connection to the server.
	private void connectToServer() throws IOException {
		connection = new Socket(server, port);
		System.out.println("Connected to server at " + server + ".");
	}

	// Sets up the I/O streams for communication with the server.
	private void getStreams() throws IOException {
		in = new ObjectInputStream(connection.getInputStream()); // Input stream.
		out = new PrintWriter(connection.getOutputStream(), true); // Output stream.
	}

	// Handles communication with the server.
	private void processConnection() throws IOException {
		try {
			Object object = in.readObject();
			GameState gameState = (GameState)object;
			remoteBoard.setChanged(gameState);
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found: " + e.getMessage() + ".");
		}
	}

	// Closes the I/O streams and the socket.
	private void closeConnection() {
		try {
			if (in != null) in.close(); // Close the input stream.
			if (out != null) out.close(); // Close the output stream.
			if (connection != null) connection.close();
			System.out.println("Connection closed.");
		} catch (IOException e) {
			System.err.println("Error on closing connection: " + e.getMessage() + ".");
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		// TODO
		RemoteBoard board = new RemoteBoard();
		SnakeGui game = new SnakeGui(board,1000,0);
		game.init();
		Client client = new Client(InetAddress.getByName("localhost"), 12345, board);
		client.run();
	}
}