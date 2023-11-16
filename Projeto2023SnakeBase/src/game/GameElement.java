package game;

import environment.BoardPosition;

public abstract class GameElement{

    public abstract BoardPosition getCurrentPosition();

    public abstract void setCurrentPosition(BoardPosition position);
}