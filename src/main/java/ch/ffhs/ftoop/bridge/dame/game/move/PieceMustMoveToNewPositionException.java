package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that the piece must move to a new position and cannot stay on its current position.
 */
public class PieceMustMoveToNewPositionException extends InvalidMoveException {
    public PieceMustMoveToNewPositionException(String message) {
        super(message);
    }
}
