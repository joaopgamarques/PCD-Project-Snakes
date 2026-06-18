# Snakes — Concurrent & Distributed Multiplayer Snake Game

A multiplayer Snake game built in Java that demonstrates concurrent and distributed programming concepts. Features AI-controlled snakes, moving obstacles, a local GUI, and a client-server architecture for remote play.

Developed for the Concurrent and Distributed Programming (PCD) course at ISCTE.

## Gameplay

- **Automatic snakes** navigate a 30x30 grid, pathfinding towards a shared goal
- **Human players** connect remotely via a client and control a snake with arrow keys
- **Obstacles** move independently across the board (up to 3 moving simultaneously)
- **Goals** spawn at random positions; capturing one grows the snake and increments the goal's value (max 10)
- Snakes compete for the same goal — the game ends when a completion condition is met

## Architecture

The project is split into four packages reflecting its concurrent and distributed design:

```text
src/
├── gui/ # Presentation layer
│ ├── Main.java # Entry point — creates LocalBoard, GUI, and Server
│ ├── SnakeGui.java # Swing GUI (800x800), Observer pattern for board updates
│ └── BoardComponent.java # Custom JComponent for rendering the grid
│
├── environment/ # Shared game state
│ ├── Board.java # Abstract — 30x30 cell grid, snake/obstacle/goal management
│ ├── LocalBoard.java # Concrete board with thread pool for obstacle movers
│ ├── Cell.java # Thread-safe cell with ReentrantLock + Condition
│ ├── BoardPosition.java # Grid coordinate
│ └── GameState.java # Serializable snapshot (cells, snakes, finished flag)
│
├── game/ # Game entities (each runs in its own thread)
│ ├── Snake.java # Abstract — Thread subclass, movement, growth, initial positioning
│ │ ├── AutomaticSnake.java # AI pathfinding towards goal, random fallback when trapped
│ │ └── HumanSnake.java # Remote-controlled via ReentrantLock + Condition (producer-consumer)
│ ├── Goal.java # Capturable target with incrementing value
│ ├── Obstacle.java # Movable barrier (3 moves each, 2s interval)
│ ├── ObstacleMover.java # Thread that drives obstacle movement
│ ├── GameElement.java # Base class for all board elements
│ └── Server.java # Socket server for remote clients
│
└── remote/ # Client-side networking
├── Client.java # Connects to server, receives GameState via ObjectInputStream
├── RemoteBoard.java # Board subclass — renders server state, sends key input
└── Direction.java # Enum: UP, DOWN, LEFT, RIGHT
```

### Concurrency Model

| Concept | Implementation |
|---------|---------------|
| **Snake threads** | Each `Snake` extends `Thread` and runs its own movement loop |
| **Obstacle thread pool** | `LocalBoard` uses an `ExecutorService` with 3 simultaneous `ObstacleMover` threads |
| **Cell locking** | `Cell` uses `ReentrantLock` + `Condition` — snakes block on `request()` until a cell is free |
| **Deadlock prevention** | `Cell.goalCaptureAndMoveHandler()` acquires locks in position order to prevent circular waits |
| **Human input sync** | `HumanSnake` uses a `ReentrantLock` + `Condition` (producer-consumer) to bridge network input and game loop |
| **Game state broadcast** | `Server` runs a dedicated thread that periodically serializes `GameState` to all connected clients |
| **Observer pattern** | `Board` extends `Observable`; `SnakeGui` observes and repaints on every state change |

### Client-Server Model
```text
┌─────────────┐ TCP :12345 ┌─────────────────┐
│ Client │ ◄──── GameState (obj) ──── │ Server │
│ RemoteBoard │ ───── Direction (text) ──► │ ConnectionHandler│
│ SnakeGui │ │ LocalBoard │
└─────────────┘ └─────────────────┘
```


- **Server** (`Main.java`): Creates a `LocalBoard`, launches the GUI, starts accepting client connections
- **Client** (`Client.java`): Connects to the server, receives serialized `GameState` objects, renders them on a `RemoteBoard`
- **Input**: Arrow key presses are converted to `Direction` enums and sent as text over the socket
- **State sync**: The server broadcasts the full board state at regular intervals (`REMOTE_CONNECTION_SETUP_DELAY`)

## Controls

| Key | Action |
|-----|--------|
| Arrow keys | Move the human-controlled snake (remote client only) |
| Reset button | Interrupt and re-randomize automatic snake directions (local GUI only) |

## Configuration

Key constants defined across the codebase:

| Constant | Value | Location |
|----------|-------|----------|
| Grid size | 30 x 30 | `Board.java` |
| Snakes | 2 | `LocalBoard.java` |
| Obstacles | 25 | `LocalBoard.java` |
| Simultaneous movers | 3 | `LocalBoard.java` |
| Obstacle moves | 3 per obstacle | `Obstacle.java` |
| Obstacle interval | 2000 ms | `Obstacle.java` |
| Player move interval | 200 ms | `Board.java` |
| Server port | 12345 | `Server.java` |
| Max goal value | 10 | `Goal.java` |

## Prerequisites

- Java 8+
- IntelliJ IDEA or Eclipse (project files included for both)

## Running

### Local Mode (with server)

Run `gui.Main` — starts the local board, GUI, and server:
```bash
javac -d out src/**/*.java
java -cp out gui.Main
```
Remote Client
Run remote.Client to connect to a running server:
```bash
java -cp out remote.Client
```
Connects to localhost:12345 by default.

## Authors
- Joao Marques
- Tiago Lobo
