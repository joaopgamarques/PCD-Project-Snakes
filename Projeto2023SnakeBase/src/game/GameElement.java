package game;

import environment.BoardPosition;
import java.io.Serializable;

public abstract class GameElement implements Serializable {

    public abstract BoardPosition getCurrentPosition();

    public abstract void setCurrentPosition(BoardPosition position);
}