package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that the player must always do a move per turn and cannot "sit one out".
 */
public class PlayerMustAlwaysMovePieceException extends InvalidMoveException {
    public PlayerMustAlwaysMovePieceException(String message) {
        super(message);
    }
}
