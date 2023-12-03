package game;

import environment.Board;
import environment.LocalBoard;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // TODO
    private ServerSocket server; // ServerSocket to listen for incoming connections.
    private final LocalBoard localBoard; // The local board that maintains the game state.
    private final ExecutorService clientExecutor; // Thread pool for handling client connections.
    private final ConcurrentHashMap<String, ConnectionHandler> activeConnections; // Map to track active client connections.

    public Server(LocalBoard localBoard) {
        this.localBoard = localBoard;
        this.clientExecutor = Executors.newCachedThreadPool();
        this.activeConnections = new ConcurrentHashMap<>();
    }

    // Starts the server and listens for incoming client connections.
    public void run() {
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
        // Create a unique key based on client's IP and port.
        String clientKey = connection.getInetAddress().getHostAddress() + ":" + connection.getPort();

        // Check if the client is not already connected or if its handler thread is not alive.
        if (!activeConnections.containsKey(clientKey) || !activeConnections.get(clientKey).isAlive()) {
            ConnectionHandler handler = new ConnectionHandler(connection); // Create a new connection handler.
            activeConnections.put(clientKey, handler); // Store handler in active connections map.
            clientExecutor.execute(handler); // Execute the handler in the thread pool.
            System.out.println("[new connection] " + clientKey);
        }
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
                // On completion or disconnection, remove from activeConnections.
                String clientKey = connection.getInetAddress().getHostAddress() + ":" + connection.getPort();
                activeConnections.remove(clientKey); // Remove the connection from active connections map.
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
            /*
            if (in.hasNext()) {
                String command = in.nextLine();
                System.out.println("Received command: " + command);

            }
             */
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