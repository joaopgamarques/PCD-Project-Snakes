package game;

import environment.Board;
import environment.LocalBoard;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    // TODO
    private ServerSocket server; // ServerSocket to listen for incoming connections.
    private final LocalBoard localBoard; // The local board that maintains the game state.

    public Server(LocalBoard localBoard) {
        this.localBoard = localBoard;
    }

    // Starts the server and listens for incoming client connections.
    public void runServer() {
        try {
            // Create a server socket bound to port 12345.
            server = new ServerSocket(12345, 1);
            System.out.println("The server is running.");
            // Continuously listen for client connections as long as the server is not closed.
            while (!server.isClosed()) {
                try {
                    waitForConnection();
                } catch (IOException e) {
                    System.out.println("Exception handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        }
    }

    // Waits for a client to connect and creates a handler to manage the connection.
    private void waitForConnection() throws IOException {
        System.out.println("Waiting for connection.");
        Socket connection = server.accept(); // Accept the incoming connection.
        ConnectionHandler connectionHandler = new ConnectionHandler(connection);
        connectionHandler.start(); // Start the thread to process the connection.
        System.out.println("[new connection] " + connection.getInetAddress().getHostAddress());
    }

    // Inner class to handle client connections.
    private class ConnectionHandler extends Thread {
        private final Socket connection; // Socket representing the client connection.
        private ObjectOutputStream out; // Stream for sending data to the client.
        private Scanner in; // Stream for receiving data from the client.

        public ConnectionHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                // Continuously send updated game state to the client.
                while (!connection.isClosed() && !Thread.currentThread().isInterrupted()) {
                    getStreams();
                    processConnection();
                }
            } catch (SocketException e) {
                System.out.println("Connection closed by server.");
            } catch (IOException e) {
                 System.out.println("IOException in ConnectionHandler: " + e.getMessage());
            } finally {
                closeConnection(); // Close the connection when done.
            }
        }

        // Sets up the I/O streams for communication with the client.
        private void getStreams() throws IOException {
            out = new ObjectOutputStream(connection.getOutputStream()); // Output stream.
            in = new Scanner(connection.getInputStream()); // Input stream.
        }

        // Handles communication with the client.
        private void processConnection() throws IOException {
            out.writeObject(localBoard.getGameState()); // Send the game state to the client.
            out.flush(); // Flush the stream to ensure the data is sent.
            try {
                Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
            } catch (InterruptedException e) {
                System.out.println("ConnectionHandler interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        // Closes the client connection and associated streams.
        private void closeConnection() {
            try {
                if (out!= null) out.close(); // Close the output stream.
                if (in != null) in.close(); // Close the input stream.
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (IOException e) {
                System.out.println("IOException on closing connection: " + e.getMessage());
            }
        }
    }
}