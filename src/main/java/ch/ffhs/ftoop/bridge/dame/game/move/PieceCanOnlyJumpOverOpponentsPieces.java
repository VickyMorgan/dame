package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a piece can only jump over opponents pieces.
 */
public class PieceCanOnlyJumpOverOpponentsPieces extends InvalidMoveException {
    public PieceCanOnlyJumpOverOpponentsPieces(String message) {
        super(message);
    }
}
