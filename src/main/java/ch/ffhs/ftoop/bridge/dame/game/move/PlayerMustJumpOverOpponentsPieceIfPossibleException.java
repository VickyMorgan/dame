package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that a neighbouring piece must be captured, if there is the chance to do so.
 */
public class PlayerMustJumpOverOpponentsPieceIfPossibleException extends InvalidMoveException {
    public PlayerMustJumpOverOpponentsPieceIfPossibleException(String message) {
        super(message);
    }
}
