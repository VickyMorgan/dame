package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that an invalid move on the board was attempted. A move is invalid, once it violates any of the configured game rules.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
