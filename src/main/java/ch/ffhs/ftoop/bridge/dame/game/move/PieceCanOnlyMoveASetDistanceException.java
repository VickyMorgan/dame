package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a piece can only travel a set distance on the board.
 */
public class PieceCanOnlyMoveASetDistanceException extends InvalidMoveException {
    public PieceCanOnlyMoveASetDistanceException(String message) {
        super(message);
    }
}
