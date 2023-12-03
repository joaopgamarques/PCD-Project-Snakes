package remote;

public enum Direction {
    LEFT("LEFT"), RIGHT("RIGHT"), UP("UP"), DOWN("DOWN");

    private final String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return direction;
    }
}