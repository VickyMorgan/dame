package ch.ffhs.ftoop.bridge.dame.game.board;

/**
 * A piece is the central playing object of the game. It is moved across the board and can become a "king" (a "Dame").
 */
public class Piece {
    /**
     * Convenience constant, that denotes if there is no piece on the tile.
     */
    public static final Piece NONE = null;

    private final PieceColor color;

    /**
     * Denotes, whether this piece is a king, a "Dame" piece, which is allowed to move freely across the board
     * after the player has reached the last row.
     */
    private boolean isKing = false;

    private Piece(PieceColor color) {
        this.color = color;
    }

    public static Piece forColor(PieceColor color) {
        if (color == PieceColor.DARK) {
            return new Piece(PieceColor.DARK);
        }

        return new Piece(PieceColor.LIGHT);
    }

    public static Piece light() {
        return forColor(PieceColor.LIGHT);
    }

    public static Piece dark() {
        return forColor(PieceColor.DARK);
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public PieceColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                ", isKing=" + isKing +
                '}';
    }
}
