package ch.ffhs.ftoop.bridge.dame.game.board;

import java.util.Objects;

/**
 * A BoardPosition encapsulates rows and columns, and is used to address tiles on the board.
 */
public class BoardPosition {

    private final int col;
    private final int row;

    private BoardPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public static BoardPosition from(int col, int row) {
        return new BoardPosition(col, row);
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    @Override
    public String toString() {
        return String.format("(%d/%d)", this.col, this.row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        BoardPosition position = (BoardPosition) o;
        return col == position.col &&
                row == position.row;
    }

    @Override
    public int hashCode() {

        return Objects.hash(col, row);
    }
}
