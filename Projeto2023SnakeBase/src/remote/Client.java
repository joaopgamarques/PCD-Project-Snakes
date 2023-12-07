package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import environment.GameState;

import game.Goal;
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
	private final RemoteBoard remoteBoard; // Remote board that will reflect the game state from the server.

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
			Object object = in.readObject(); // Reads an object from the input stream.
			GameState gameState = (GameState)object; // Casts the received object to GameState.
			remoteBoard.setChanged(gameState); // Updates the remote board with the new game state.
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

	// Main method for starting the client with a specific server address and port.
	public static void main(String[] args) throws UnknownHostException {
		// TODO
		// Initializes a new instance of the RemoteBoard class, which represents the game state as seen from the client's perspective.
		RemoteBoard board = new RemoteBoard();
		// Creates a new SnakeGui object, which is the graphical user interface for the game, passing the remote board and window position.
		SnakeGui game = new SnakeGui(board,1000,0);
		game.init(); // Initializes the game user interface, making it visible and ready for interaction.
		// Constructs a new Client instance, connecting to the server at the specified IP address ("localhost") and port number (12345).
		// The client will interact with the remote board.
		Client client = new Client(InetAddress.getByName("localhost"), 12345, board);
		// Starts the client's operation, which involves connecting to the server, receiving game state updates,
		// and updating the remote board accordingly.
		client.run();
	}
}