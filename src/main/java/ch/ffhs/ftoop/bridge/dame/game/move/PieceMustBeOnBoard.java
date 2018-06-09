package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a piece, that is still on the board (not yet captured) must be played.
 */
public class PieceMustBeOnBoard extends InvalidMoveException {
    public PieceMustBeOnBoard(String message) {
        super(message);
    }
}
