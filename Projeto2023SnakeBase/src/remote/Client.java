package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import environment.Board;
import environment.GameState;
import environment.LocalBoard;
import gui.SnakeGui;

/** Remore client, only for part II
 *
 * @author luismota
 *
 */

public class Client {
	private Socket connection; // Client socket for communicating with the server.
	private final InetAddress serverName; // IP address of the server.
	private final int port; // Port number of the server.
	private ObjectInputStream in; // Input stream to receive game state from the server.
	private PrintWriter out; // Output stream.
	private final RemoteBoard remoteBoard;

	public Client(InetAddress byName, int port, RemoteBoard remoteBoard) {
		this.serverName = byName;
		this.port = port;
		this.remoteBoard = remoteBoard;
	}

	// Runs the client to connect to the server and update the game state.
	public void runClient() {
		try {
			connectToServer(); // Connect to the server.
			// Continuously process game updates from the server.
			while (!Thread.currentThread().isInterrupted()) {
				// connectToServer(); // Establish a connection to the server.
				getStreams(); // Setup I/O streams.
				processConnection(); // Process the incoming game state.
			}
		} catch (IOException e) {
			System.err.println("Client error: " + e.getMessage());
		} finally {
			closeConnection(); // Close the connection when done or on error.
		}
	}

	// Establishes a connection to the server.
	private void connectToServer() throws IOException {
		connection = new Socket(serverName, port);
		System.out.println("Connected to server at " + serverName + ".");
	}

	// Sets up the I/O streams for communication with the server.
	private void getStreams() throws IOException {
		try {
			// Input
			in = new ObjectInputStream(connection.getInputStream());
			GameState gameState = (GameState)in.readObject();
			remoteBoard.setChanged(gameState);
			// Output
			out = new PrintWriter(connection.getOutputStream(), true);
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Error processing connection: " + e.getMessage());
		}
	}

	// Handles communication with the server.
	private void processConnection() {

	}

	// Closes the I/O streams and the socket.
	private void closeConnection() {
		try {
			if (out!= null) out.close(); // Close the output stream.
			if (in != null) in.close(); // Close the input stream.
			if (connection != null) connection.close();
			System.out.println("Connection closed.");
		} catch (IOException e) {
			System.err.println("Error closing connection: " + e.getMessage());
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		// TODO
		RemoteBoard board = new RemoteBoard();
		SnakeGui game = new SnakeGui(board,1000,0);
		game.init();
		Client client = new Client(InetAddress.getByName("localhost"), 12345, board);
		client.runClient();
	}
}