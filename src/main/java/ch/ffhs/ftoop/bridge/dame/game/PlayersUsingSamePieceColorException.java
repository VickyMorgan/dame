package ch.ffhs.ftoop.bridge.dame.game;

/**
 * Indicates, that both players of the game are using the same color of pieces.
 */
public class PlayersUsingSamePieceColorException extends Exception {

    public PlayersUsingSamePieceColorException(String message) {
        super(message);
    }
}
