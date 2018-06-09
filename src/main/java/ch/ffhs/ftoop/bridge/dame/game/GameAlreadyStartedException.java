package ch.ffhs.ftoop.bridge.dame.game;

/**
 * Indicates that the game has already been started.
 */
public class GameAlreadyStartedException extends Exception {
    public GameAlreadyStartedException(String message) {
        super(message);
    }
}
