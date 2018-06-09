package ch.ffhs.ftoop.bridge.dame.game.board;

/**
 * A tile is a sub-component of the board and models a square on said board. It knows about it's location and also what piece is on it.
 */
public class Tile {
    private final BoardPosition position;
    private final TileColor color;
    private Piece piece;

    public Tile(BoardPosition position, TileColor color) {
        this(position, color, null);
    }

    public Tile(BoardPosition position, TileColor color, Piece piece) {
        this.position = position;
        this.color = color;
        this.piece = piece;
    }

    public BoardPosition getPosition() {
        return position;
    }

    public boolean isOccupied() {
        return this.piece != null;
    }

    public TileColor getColor() {
        return color;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "position=" + position +
                ", color=" + color +
                ", piece=" + piece +
                '}';
    }
}
