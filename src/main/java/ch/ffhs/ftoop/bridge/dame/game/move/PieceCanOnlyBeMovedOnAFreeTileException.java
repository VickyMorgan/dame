package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a piece can only be moved onto a free tile.
 */
public class PieceCanOnlyBeMovedOnAFreeTileException extends InvalidMoveException {

    public PieceCanOnlyBeMovedOnAFreeTileException(String message) {
        super(message);
    }
}
