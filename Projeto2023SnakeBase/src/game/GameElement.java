package game;

import environment.BoardPosition;

import java.io.Serializable;

public abstract class GameElement implements Serializable {

    // Retrieves the current position of the game element on the board.
    public abstract BoardPosition getCurrentPosition();

    // Sets the current position of the game element on the board.
    public abstract void setCurrentPosition(BoardPosition position);
}