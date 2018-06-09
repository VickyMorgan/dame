package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a piece can only be moved onto a dark tile.
 */
public class PieceCanOnlyBeMovedToDarkTileException extends InvalidMoveException {

    public PieceCanOnlyBeMovedToDarkTileException(String message) {
        super(message);
    }
}
