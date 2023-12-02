package environment;

import game.GameElement;
import game.Snake;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private final List<GameElement> gameElements;
    private final List<Snake> snakes;

    public GameState(List<GameElement>  gameElements, List<Snake> snakes) {
        this.gameElements = gameElements;
        this.snakes = snakes;
    }

    public List<GameElement> getGameElements() {
        return gameElements;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }
}
