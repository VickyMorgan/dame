package ch.ffhs.ftoop.bridge.dame.game.board;

/**
 * Indicates, that an invalid number of pieces (odd number or similar) was used to setup the board.
 */
public class InvalidNumberOfPiecesPerPlayerException extends Exception {

  public InvalidNumberOfPiecesPerPlayerException(String message) {
    super(message);
  }
}
