package remote;

public enum Direction {
    LEFT("LEFT"), // Represents the left direction.
    RIGHT("RIGHT"), // Represents the right direction.
    UP("UP"), // Represents the upward direction.
    DOWN("DOWN"); // Represents the downward direction.

    private final String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    // Retrieves the string representation of the direction.
    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return direction;
    }

    // Static method to check if a given string corresponds to a valid direction.
    public static boolean isDirection(String name) {
        for (Direction direction : values()) {
            if (direction.toString().toUpperCase().equals(name)) {
                return true;
            }
        }
        return false;
    }
}