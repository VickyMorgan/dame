package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;

/**
 * Calculates what kind of type a move - in which direction it is played - a move is.
 */
public enum MoveType {
    NONE,
    HORIZONTAL,
    VERTICAL,
    DIAGONAL;

    /**
     * Determines the type of move for two given positions.
     *
     * @param from The position from which the type will be determined.
     * @param to   The position to which the type will be determined.
     * @return The type of the move.
     */
    public static MoveType determineType(BoardPosition from, BoardPosition to) {

        // Moving horizontally on same row
        if ((from.getRow() == to.getRow()) && (from.getCol() != to.getCol())) {
            return MoveType.HORIZONTAL;
        }

        // Moving vertically within same column
        else if ((from.getCol() == to.getCol()) && (from.getRow() != to.getRow())) {
            return MoveType.VERTICAL;

        } else {
            /*
             * Check if the two positions are actually diagonal via a 45 degree angle and not something else.
             * For this the trick with the linear equation slope m can be used -- if it is an integer, either 1 or -1
             * then it is safe to assume that they are directly diagonal.
             */
            double deltaY = to.getRow() - from.getRow();
            double deltaX = to.getCol() - from.getCol();
            double m = (deltaY / deltaX);

            // Check if the double is actually an integer, so => 1.0 == 1, 1.0001 != 1 and does not have decimals
            // and is either 1 or -1
            // https://stackoverflow.com/a/9909417
            if ((m % 1 == 0) && (m == 1 || m == -1)) {
                return MoveType.DIAGONAL;
            }

        }

        // No movement or totally invalid movement (eg moving one up, two to the right)
        return MoveType.NONE;
    }
}
