package ch.ffhs.ftoop.bridge.dame.game.board;

/**
 * Indicate, that the board was attempted to be setup using invalid board dimensions.
 */
public class InvalidBoardDimensionsException extends Exception {

  public InvalidBoardDimensionsException(String message) {
    super(message);
  }
}
