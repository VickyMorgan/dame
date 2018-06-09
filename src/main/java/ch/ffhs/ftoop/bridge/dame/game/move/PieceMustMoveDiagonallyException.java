package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that the piece must be moved diagonally across the board.
 */
public class PieceMustMoveDiagonallyException extends InvalidMoveException {
    public PieceMustMoveDiagonallyException(String message) {
        super(message);
    }
}
