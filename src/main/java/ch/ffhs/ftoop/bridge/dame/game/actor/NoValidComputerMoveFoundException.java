package ch.ffhs.ftoop.bridge.dame.game.actor;

/**
 * Indicates, that the computer opponent could not find a valid move.
 */
public class NoValidComputerMoveFoundException extends Exception {
    public NoValidComputerMoveFoundException(String message) {
        super(message);
    }
}
