package ch.ffhs.ftoop.bridge.dame.game.board;

/**
 * Indicates, that an invalid board position was used. This could eg. be that the given position was outside the board.
 */
public class InvalidBoardPositionException extends Exception {

  public InvalidBoardPositionException(String message) {
    super(message);
  }
}
