package game;

import environment.Board;
import environment.GameState;
import environment.LocalBoard;
import remote.Direction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    // TODO
    private ServerSocket serverSocket; // ServerSocket to listen for incoming connections.
    private final LocalBoard localBoard; // The local board that maintains the game state.
    private final Map<Integer, ConnectionHandler> connections = new HashMap<>(); // Maps client ports to their respective connection handlers.

    // Thread for broadcasting game state updates to all active clients.
    private final Thread broadcastThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!localBoard.isFinished()) {
                try {
                    // Delay between broadcasts.
                    Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
                    // Get the current game state for broadcasting.
                    GameState gameState = new GameState(localBoard.getCells(), localBoard.getSnakes(), localBoard.isFinished());
                    HashMap<Integer, ConnectionHandler> copyOfConnections = new HashMap<>(connections);
                    System.out.println("Active connections: " + copyOfConnections.size());
                    // Broadcast the game state to each client.
                    for (ConnectionHandler connection : copyOfConnections.values()) {
                        if (!connection.socket.isClosed() && connection.isOutputStreamInitialized) {
                            connection.broadcastGameState(gameState);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("BroadcastThread interrupted: " + e.getMessage() + ".");
                }
            }
            try {
                Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
                broadcastLastGameState(); // Broadcast the final game state at the end.
            } catch (InterruptedException e) {
                System.out.println("BroadcastThread interrupted: " + e.getMessage() + ".");
            }
        }

        // Broadcasts the final game state to all clients.
        private void broadcastLastGameState() {
            // Get the current game state for broadcasting.
            GameState gameState = new GameState(localBoard.getCells(), localBoard.getSnakes(), localBoard.isFinished());
            // Broadcast the game state to each client.
            HashMap<Integer, ConnectionHandler> connectionsCopy = new HashMap<>(connections);
            for (ConnectionHandler connection : connectionsCopy.values()) {
                if (!connection.socket.isClosed() && connection.isOutputStreamInitialized) {
                    connection.broadcastGameState(gameState);
                }
            }
        }
    });

    public Server(LocalBoard localBoard) {
        this.localBoard = localBoard;
    }

    // Starts the server and listens for incoming client connections.
    public void runServer() {
        try {
            serverSocket = new ServerSocket(12345); // Create a server socket bound to port 12345.
            broadcastThread.start(); // Start multicasting.
            System.out.println("The server is running.");
            while (!serverSocket.isClosed()) { // Continuously listen for client connections as long as the server is not closed.
                try {
                    waitForConnection();
                } catch (IOException e) {
                    System.err.println("Exception handling client connection: " + e.getMessage() + ".");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage() + ".");
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Could not close the server: " + e.getMessage() + ".");
                }
            }
        }
    }

    // Waits for a client to connect and creates a handler to manage the connection.
    private void waitForConnection() throws IOException {
        System.out.println("Waiting for connection.");
        Socket socket = serverSocket.accept(); // Accept the incoming connection.
        ConnectionHandler connectionHandler = new ConnectionHandler(socket);
        connectionHandler.start(); // Start the thread to process the connection.
        System.out.println("New connection to client " + socket.getPort() + ". " + socket.getInetAddress().getHostAddress());
    }

    // Inner class to handle client connections.
    private class ConnectionHandler extends Thread {
        private final Socket socket; // Socket representing the client connection.
        private ObjectOutputStream out; // Stream for sending data to the client.
        private Scanner in; // Stream for receiving data from the client.
        private final HumanSnake snake; // Represents the snake controlled by the client connected through this handler.
        private volatile boolean isInputStreamInitialized = false;
        private volatile boolean isOutputStreamInitialized = false;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
            this.snake = new HumanSnake(socket.getPort(), localBoard);
            addSnake(snake);
            synchronized (connections) {
                connections.put(socket.getPort(), this); // Add this connection handler to the map.
            }
        }

        // Checks if input and output streams are initialized.
        public boolean isInputStreamInitialized() {
            return isInputStreamInitialized;
        }

        public boolean isOutputStreamInitialized() {
            return isOutputStreamInitialized;
        }

        // Adds a new HumanSnake to the game when a client connection is established.
        private void addSnake(HumanSnake snake) {
            try {
                localBoard.addSnake(snake); // Add the snake to the local board.
                snake.start(); // Start the snake's movement and logic.
                Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
                localBoard.setChanged();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        // Removes the associated HumanSnake from the game.
        private void removeSnake() {
            localBoard.getSnakes().remove(snake); // Remove the snake from the board.
            snake.getCells().forEach(cell -> cell.release()); // Release the cells occupied by the snake.
            localBoard.setChanged();
        }

        @Override
        public void run() {
            try {
                getStreams();
                isInputStreamInitialized = true;
                isOutputStreamInitialized = true;
                while (!socket.isClosed()) {
                    processConnection();
                }
            } catch (SocketException e) {
                System.err.println("Connection closed by server.");
            } catch (IOException e) {
                System.err.println("Exception in ConnectionHandler: " + e.getMessage() + ".");
            } finally {
                closeConnection(); // Close the connection when done.
            }
        }

        // Sets up the I/O streams for communication with the client.
        private void getStreams() throws IOException {
            out = new ObjectOutputStream(socket.getOutputStream()); // Output stream.
            in = new Scanner(socket.getInputStream()); // Input stream.
        }

        // Handles communication with the client.
        private void processConnection() throws IOException {
            try {
                if (in.hasNextLine()) {
                    processClientInput();
                }
            } catch (SocketException e) {
                System.err.println("Client " + socket.getPort() + " disconnected.");
            } catch (IOException e) {
                System.err.println("IO Exception in ConnectionHandler: " + e.getMessage());
            }
        }

        // Closes the client connection and associated streams.
        private void closeConnection() {
            synchronized (connections) {
                connections.remove(socket.getPort()); // Remove this connection handler from the map.
            }
            try {
                if (out != null) out.close(); // Close the output stream.
                if (in != null) in.close(); // Close the input stream.
                if (!socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("Exception on closing connection: " + e.getMessage() + ".");
            } finally {
                removeSnake();
            }
        }

        // Sends the updated game state to the clients.
        public void broadcastGameState(GameState gameState) {
            if (out == null) {
                System.err.println("Output stream not initialized for client " + socket.getPort() + ".");
                return;
            }
            try {
                out.reset(); // Reset the ObjectOutputStream to ensure no stale objects are sent.
                out.writeObject(gameState); // Send the game state to the client.
                out.flush(); // Flush the stream to ensure the data is sent.
                System.out.println("Sending the game state to client " + socket.getPort() + ".");
            } catch (IOException e) {
                System.err.println("Exception on sending game state to client " + socket.getPort() + ". " + e.getMessage() + ".");
                closeConnection(); // Close the connection if an error occurs while broadcasting.
            }
        }

        // Processes incoming commands from the client.
        private void processClientInput() throws IOException {
            String directionInput = in.nextLine();
            try {
                Direction direction = Direction.valueOf(directionInput);
                snake.setDirection(direction);
                System.out.println("Received direction from client " + socket.getPort() + ": " + directionInput);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid direction received from client " + socket.getPort() + ".");
            }
        }
    }
}