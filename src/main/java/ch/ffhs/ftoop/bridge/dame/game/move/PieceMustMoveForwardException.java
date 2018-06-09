package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that the piece must travel forwards on the board.
 */
public class PieceMustMoveForwardException extends InvalidMoveException {
    public PieceMustMoveForwardException(String message) {
        super(message);
    }
}
