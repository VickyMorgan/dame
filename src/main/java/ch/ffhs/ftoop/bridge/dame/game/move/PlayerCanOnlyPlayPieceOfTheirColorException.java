package ch.ffhs.ftoop.bridge.dame.game.move;

/**
 * Indicates, that the player is only allowed to play pieces of their own color.
 */
public class PlayerCanOnlyPlayPieceOfTheirColorException extends InvalidMoveException {

    public PlayerCanOnlyPlayPieceOfTheirColorException(String message) {
        super(message);
    }
}
