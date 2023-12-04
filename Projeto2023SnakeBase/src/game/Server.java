package game;

import environment.Board;
import environment.GameState;
import environment.LocalBoard;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    // TODO
    private ServerSocket server; // ServerSocket to listen for incoming connections.
    private final LocalBoard localBoard; // The local board that maintains the game state.
    private final List<ConnectionHandler> activeConnections = new ArrayList<>(); // List of active client connections.
    private final List<ConnectionHandler> closedConnections = new ArrayList<>();; // List of closed connections.
    private final List<ConnectionHandler> incomingConnections = new ArrayList<>();; // List of new incoming connections.

    // Thread for broadcasting game state updates to all active clients.
    private final Thread multicastThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!localBoard.isFinished()) {
                try {
                    // Delay between broadcasts.
                    Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
                    // Update the lists of connections.
                    synchronized (incomingConnections) {
                        activeConnections.addAll(incomingConnections);
                        incomingConnections.clear();
                    }
                    System.out.println("Active connections: " + activeConnections.size());
                    // Get the current game state for broadcasting.
                    GameState gameState = new GameState(localBoard.getCells(), localBoard.getSnakes());
                    // Broadcast the game state to each client.
                    for (ConnectionHandler connection : activeConnections) {
                        if (!connection.connection.isClosed()) {
                            connection.broadcastGameState(gameState);
                        }
                    }
                    // Update the lists of connections.
                    synchronized (closedConnections) {
                        activeConnections.removeAll(closedConnections);
                        closedConnections.clear();
                    }
                } catch (InterruptedException e) {
                    System.out.println("MulticastThread interrupted: " + e.getMessage() + ".");
                }
            }
        }
    });

    public Server(LocalBoard localBoard) {
        this.localBoard = localBoard;
    }

    // Starts the server and listens for incoming client connections.
    public void run() {
        try {
            // Create a server socket bound to port 12345.
            server = new ServerSocket(12345);
            multicastThread.start();
            System.out.println("The server is running.");
            // Continuously listen for client connections as long as the server is not closed.
            while (!server.isClosed()) {
                try {
                    waitForConnection();
                } catch (IOException e) {
                    System.out.println("Exception handling client connection: " + e.getMessage() + ".");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage() + ".");
        }
    }

    // Waits for a client to connect and creates a handler to manage the connection.
    private void waitForConnection() throws IOException {
        System.out.println("Waiting for connection.");
        Socket connection = server.accept(); // Accept the incoming connection.
        ConnectionHandler connectionHandler = new ConnectionHandler(connection);
        connectionHandler.start(); // Start the thread to process the connection.
        System.out.println("New connection to client " + connection.getPort() + ". " + connection.getInetAddress().getHostAddress());
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
                getStreams();
                synchronized (incomingConnections) {
                    incomingConnections.add(ConnectionHandler.this);
                }
                while (!connection.isClosed()) { // ATENÇÃO NÃO ESTÁ A RECONHECER SE A LIGAÇÃO ESTÁ FECHADA.
                    processConnection();
                }
            } catch (SocketException e) {
                System.out.println("Connection closed by server.");
            } catch (IOException e) {
                System.out.println("IOException in ConnectionHandler: " + e.getMessage() + ".");
            } finally {
                closeConnection(); // Close the connection when done. // ATENÇÃO NÃO ESTÁ A SER EXECUTADA.
            }
        }

        // Sets up the I/O streams for communication with the client.
        private void getStreams() throws IOException {
            out = new ObjectOutputStream(connection.getOutputStream()); // Output stream.
            in = new Scanner(connection.getInputStream()); // Input stream.
        }

        // Sends the updated game state to the clients.
        public void broadcastGameState(GameState gameState) {
            try {
                out.reset(); // Reset the ObjectOutputStream to ensure no stale objects are sent.
                out.writeObject(gameState); // Send the game state to the client.
                out.flush(); // Flush the stream to ensure the data is sent.
                System.out.println("Sending the game state to client " + connection.getPort() + ".");
            } catch (IOException e) {
                System.out.println("Error sending game state to client " + connection.getPort() + ". " + e.getMessage() + ".");
                // closeConnection(); // ATENÇÃO NÃO DEVERIA SER LANÇADA AQUI.
            }
        }

        // Handles communication with the client.
        private void processConnection() throws IOException {
            /*
            if (in.hasNextLine()) {
                String command = in.nextLine();
                System.out.println("Received command: " + command);
                // Process command here...
            }
             */
        }

        // Closes the client connection and associated streams.
        private void closeConnection() {
            synchronized (closedConnections) {
                closedConnections.add(ConnectionHandler.this);
            }
            try {
                if (out != null) out.close(); // Close the output stream.
                if (in != null) in.close(); // Close the input stream.
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (IOException e) {
                System.out.println("IOException on closing connection: " + e.getMessage());
            }
        }
    }
}