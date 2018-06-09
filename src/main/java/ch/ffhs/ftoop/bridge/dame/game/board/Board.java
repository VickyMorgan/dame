package ch.ffhs.ftoop.bridge.dame.game.board;

import ch.ffhs.ftoop.bridge.dame.game.move.MoveType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition.from;

/**
 * Models the board of the game and contains convenience methods for common operations on the game like calculating the distance between multiple tiles or finding neighbours of a given tile.
 */
public class Board {

    private static final Logger logger = LogManager.getLogger(Board.class);
    private int numberOfRowsAndColumns;
    private Tile[][] tiles; // [row][col]

    /**
     * Instantiates a new board of the given size. The size (height, width) must be an even number.
     *
     * @param numberOfRowsAndColumns The number of rows, the size of the board.
     * @throws InvalidBoardDimensionsException If the given board size is invalid.
     */
    public Board(int numberOfRowsAndColumns) throws InvalidBoardDimensionsException {
        this.checkBoardDimensions(numberOfRowsAndColumns);
        this.numberOfRowsAndColumns = numberOfRowsAndColumns;

        this.initializeCheckerboard();
    }

    private void checkBoardDimensions(int numberOfRowsAndColumns) throws InvalidBoardDimensionsException {
        if (numberOfRowsAndColumns <= 0) {
            throw new InvalidBoardDimensionsException("The number of rows and columns must be greater than 0!");
        }

        if (numberOfRowsAndColumns % 2 != 0) {
            throw new InvalidBoardDimensionsException("The number of rows and columns must be an even number!");
        }
    }

    private void initializeCheckerboard() {
        logger.info("Initialising checkerboard");
        this.tiles = new Tile[this.numberOfRowsAndColumns][this.numberOfRowsAndColumns];

        // Create checkerboard pattern
        for (int row = 0; row < this.numberOfRowsAndColumns; row++) {
            for (int col = 0; col < this.numberOfRowsAndColumns; col++) {
                // On even rows and odd columns as well as on odd rows with even columns
                // place a light tile, if both are even or both are odd, place a dark one
                // This creates the checkerboard pattern
                TileColor tileColor = (row % 2 == 0 && col % 2 != 0 || row % 2 != 0 && col % 2 == 0)
                        ? TileColor.DARK
                        : TileColor.LIGHT;
                this.tiles[row][col] = new Tile(BoardPosition.from(col, row), tileColor, Piece.NONE);
            }
        }
    }

    private void checkNumberOfPiecesPerPlayer(int numberOfPiecesPerPlayer) throws InvalidNumberOfPiecesPerPlayerException {
        if (numberOfPiecesPerPlayer <= 0) {
            throw new InvalidNumberOfPiecesPerPlayerException("The number of pieces per player  must be greater than 0!");
        }

        if (numberOfPiecesPerPlayer % 2 != 0) {
            throw new InvalidNumberOfPiecesPerPlayerException("Number of pieces per player must be an even number!");
        }
    }

    /**
     * Clears the board of all the pieces.
     */
    public void clear() {
        logger.debug("Resetting board");

        for (int row = 0; row < this.numberOfRowsAndColumns; row++) {
            for (int col = 0; col < this.numberOfRowsAndColumns; col++) {
                try {
                    BoardPosition position = from(col, row);
                    this.setPiece(Piece.NONE, position);
                } catch (InvalidBoardPositionException e) {
                    logger.error("Encountered exception whilst clearing board", e);
                }
            }
        }
    }

    /**
     * Sets a piece on the board at the given position.
     *
     * @param piece    The piece to set on the board.
     * @param position The position on which to set the piece.
     * @throws InvalidBoardPositionException Thrown if the given position is invalid.
     */
    public void setPiece(Piece piece, BoardPosition position) throws InvalidBoardPositionException {
        this.checkValidPosition(position);

        logger.debug("Setting piece {} at {}", piece, position);
        this.tiles[position.getRow()][position.getCol()].setPiece(piece);
    }

    private void checkValidPosition(BoardPosition position) throws InvalidBoardPositionException {
        if (!this.isRowWithinBounds(position)) {
            throw new InvalidBoardPositionException(String.format("Row %d is outside the board!", position.getRow()));
        }

        if (!this.isColWithinBounds(position)) {
            throw new InvalidBoardPositionException(String.format("Column %d is outside the board!", position.getRow()));
        }
    }

    private boolean isRowWithinBounds(BoardPosition position) {
        return position.getRow() >= 0 && position.getRow() <= this.numberOfRowsAndColumns - 1;
    }

    private boolean isColWithinBounds(BoardPosition position) {
        return position.getCol() >= 0 && position.getCol() <= this.numberOfRowsAndColumns - 1;
    }

    /**
     * Removes the piece from the board at the given position. No check if there is actually a piece on the position is performed,
     * the tile is always emptied regardless.
     *
     * @param position The position on which the piece should be removed.
     * @throws InvalidBoardPositionException Thrown if the given position is invalid.
     */
    public void removePiece(BoardPosition position) throws InvalidBoardPositionException {
        this.checkValidPosition(position);

        logger.info("Removing piece from {}", position);
        this.tiles[position.getRow()][position.getCol()].setPiece(Piece.NONE);
    }

    /**
     * Places the initial pieces (game starting position) on the board. The dark pieces are positioned in the top rows, the light pieces on the rows at the bottom of the board.
     * The number of pieces per player mus be an even number and greater than 0.
     *
     * @param numberOfPiecesPerPlayer The number of pieces per player that will be placed.
     * @throws InvalidNumberOfPiecesPerPlayerException If the number of pieces per player is invalid.
     */
    public void populateWithInitialPieces(int numberOfPiecesPerPlayer) throws InvalidNumberOfPiecesPerPlayerException {
        this.checkNumberOfPiecesPerPlayer(numberOfPiecesPerPlayer);

        logger.info("Populating board with initial pieces");

        int numberOfPiecesPerRow = this.numberOfRowsAndColumns / 2; // every other field is dark
        int numberOfRowsWithPieces = numberOfPiecesPerPlayer / numberOfPiecesPerRow;

        for (int row = 0; row < this.numberOfRowsAndColumns; row++) {
            for (int col = 0; col < this.numberOfRowsAndColumns; col++) {
                try {
                    BoardPosition position = from(col, row);
                    Tile tile = this.getTile(position);

                    // Only place pieces on dark tiles
                    if (tile.getColor() != TileColor.DARK) {
                        continue;
                    }

                    // Dark pieces are on top, so if we are in the top third, start placing dark pieces
                    if (row < numberOfRowsWithPieces) {
                        this.setPiece(Piece.dark(), position);
                    }

                    // If we are in the bottom third, place light pieces
                    if (row > (this.numberOfRowsAndColumns - numberOfRowsWithPieces) - 1) {
                        this.setPiece(Piece.light(), position);
                    }
                } catch (InvalidBoardPositionException e) {
                    logger.error("Encountered an error while populating pieces on the board: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Returns a list of all the pieces of a given color that are still on the board.
     *
     * @param color The color of the pieces.
     * @return A list of pieces still on the board. If there are none, an empty list is returned.
     */
    public List<Piece> getPieces(PieceColor color) {
        return this.getTiles().stream()
                .filter(Tile::isOccupied)
                .map(Tile::getPiece)
                .filter(piece -> piece.getColor() == color)
                .collect(Collectors.toList());
    }

    /**
     * Returns all the tiles of the board.
     *
     * @return A list of all the tiles on the board.
     */
    public List<Tile> getTiles() {
        return Arrays.stream(this.tiles)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the distance between two positions on the board.
     *
     * @param from The position from which the position is calculated.
     * @param to   The position to which the position is calculated.
     * @return The distance between the two positions.
     * @throws InvalidBoardPositionException Thrown if any of the given positions was invalid.
     */
    public int calculateDistanceBetween(BoardPosition from, BoardPosition to) throws InvalidBoardPositionException {
        this.checkValidPosition(from);
        this.checkValidPosition(to);

        logger.debug("Calculating distance between {} and {} ", from, to);

        List<BoardPosition> positionsBetween = this.findPointsBetween(from, to);
        int distance = positionsBetween.size() + 1; // add the end position
        logger.debug("Distance between {} and {} is {}", from, to, distance);

        return distance;
    }

    private List<BoardPosition> findPointsBetween(BoardPosition from, BoardPosition to) throws InvalidBoardPositionException {
        this.checkValidPosition(from);
        this.checkValidPosition(to);

        MoveType moveType = MoveType.determineType(from, to);
        logger.debug("Finding positions between {} and {} with type {}", from, to, moveType);

        switch (moveType) {
            case VERTICAL:
                return this.findBoardPositionsBetweenTwoVerticalPoints(from, to);
            case HORIZONTAL:
                return this.findBoardPositionsBetweenTwoHorizontalPoints(from, to);
            case DIAGONAL:
                return this.findBoardPositionsBetweenToDiagonalPoints(from, to);
            default:
                return new ArrayList<>();
        }
    }

    private List<BoardPosition> findBoardPositionsBetweenTwoVerticalPoints(BoardPosition from, BoardPosition to) {
        List<BoardPosition> positions = new ArrayList<>();

        for (int row = Math.min(from.getRow(), to.getRow()); row < Math.max(from.getRow(), to.getRow()); row++) {
            BoardPosition position = from(from.getCol(), row);

            // Exclude from and start positions
            if (!position.equals(from) && !position.equals(to)) {
                positions.add(position);
            }
        }

        return positions;
    }

    private List<BoardPosition> findBoardPositionsBetweenTwoHorizontalPoints(BoardPosition from, BoardPosition to) {
        List<BoardPosition> positions = new ArrayList<>();

        for (int col = Math.min(from.getCol(), to.getCol()); col < Math.max(from.getCol(), to.getCol()); col++) {
            BoardPosition position = from(col, from.getRow());

            // Exclude from and start positions
            if (!position.equals(from) && !position.equals(to)) {
                positions.add(position);
            }
        }

        return positions;
    }

    private List<BoardPosition> findBoardPositionsBetweenToDiagonalPoints(BoardPosition from, BoardPosition to) {
        List<BoardPosition> positions = new ArrayList<>();

        /*
         * To calculate which positions are between to positions, linear equations can be used.
         * Basically, the two points are connected via a line, which can be expressed as the basic linear equation
         * f(x) := y = m*x + b, where m is the slope of the line, and b the height of which the Y-axis (vertical axis) is crossed.
         * Using this equation, all the possible function values (Y-values) can be calculated for all X-Values between
         * the X-value (Column) of the from position and the X-value of the to position (or vice versa).
         *
         * First, the slope m needs to be found by calculating it from the Y- and X-deltas. X-Values are the columns,
         * Y-values the rows.
         */

        int deltaX = from.getCol() - to.getCol();
        int deltaY = from.getRow() - to.getRow();
        int m = (deltaY / deltaX);

        /*
         * Now, calculate the Y-value of the point where the line crosses the Y-axis (at X = 0).
         *
         * The -1 * is used as there are two cases possible to be covered when calculating b
         * a) m < 0 => m*x negative => mx must be added to the other side
         * b) m > 0 => m*x is positive => mx must be subtracted from the other side
         */

        int b = from.getRow() + (-1 * (m * from.getCol()));

        /*
         * To make it possible to always iterate from the left (lowest X-value) to right (highest X-value), the lowest
         * as well as the highest X-values are determined. Always iterating from left to right, allows the step size
         * (last condition in for-statement) to be always +1.
         *
         * Then, all the Y-values can be calculated by solving the equation f(x) = y = m*x + b.
         */

        int xStart = Math.min(from.getCol(), to.getCol());
        int xEnd = Math.max(from.getCol(), to.getCol());

        logger.debug("Calculating f(x) = {}*x + {} in [{}, {}] to find diagonal points", m, b, xStart, xEnd);

        for (int x = xStart; x <= xEnd; x++) {

            int y = m * x + b;

            // Translate the Y value to the
            BoardPosition position = from(x, y); // Board is [row][col]

            // The start and end positions are excluded
            if (!position.equals(from) && !position.equals(to)) {
                positions.add(position);
            }
        }

        return positions;
    }

    /**
     * Finds all the tiles between two positions, excluding the tile beneath the from- and to-position.
     *
     * @param from The position from which the in-between tiles should be found.
     * @param to   The position to which the in-between tiles should be found.
     * @return A list of tile between the positions or an empty list if there are none.
     * @throws InvalidBoardPositionException Thrown if any of the positions are invalid.
     */
    public List<Tile> findTilesBetween(BoardPosition from, BoardPosition to) throws InvalidBoardPositionException {
        List<BoardPosition> positionsBetween = this.findPointsBetween(from, to);
        return this.getTiles().stream()
                .filter(tile -> positionsBetween.contains(tile.getPosition()))
                .collect(Collectors.toList());
    }

    /**
     * Checks, if a given position is on the "kings" row, which is the last row on either side of the board.
     * For the dark piece, this is the row at the bottom of the board, for light pieces, the top most row.
     *
     * @param position   The position which should be checked if it is on the kings row.
     * @param pieceColor The piece color for which this should be checked.
     * @return If the position is on the kings row.
     */
    public boolean isKingsRow(BoardPosition position, PieceColor pieceColor) {
        // Dark is at the top, must reach last (bottom) row
        if (pieceColor == PieceColor.DARK) {
            return position.getRow() == this.numberOfRowsAndColumns - 1;
        }

        return position.getRow() == 0;
    }

    /**
     * Finds all the neighbouring tiles for a given position, taking edge pieces or pieces around the boarder into account.
     *
     * @param position The position for which the neighbouring tiles should be found.
     * @return A list of neighbours or an empty list if there would be none.
     * @throws InvalidBoardPositionException Thrown if the position is invalid.
     */
    public List<Tile> findNeighbouringTiles(BoardPosition position) throws InvalidBoardPositionException {
        this.checkValidPosition(position);

        logger.debug("Finding neighbouring tiles of {}", position);

        List<BoardPosition> neighbouringPositions = Arrays.asList(
                BoardPosition.from(position.getCol(), position.getRow() + 1),           // North
                BoardPosition.from(position.getCol() + 1, position.getRow() + 1),   // North East
                BoardPosition.from(position.getCol() + 1, position.getRow()),           // East
                BoardPosition.from(position.getCol() + 1, position.getRow() - 1),   // South East
                BoardPosition.from(position.getCol(), position.getRow() - 1),           // South
                BoardPosition.from(position.getCol() - 1, position.getRow() - 1),   // South West
                BoardPosition.from(position.getCol() - 1, position.getRow()),            // West
                BoardPosition.from(position.getCol() - 1, position.getRow() + 1)    // North West
        );

        return neighbouringPositions.stream()
                // Check if we are within the boards bounds
                .filter(this::isColWithinBounds)
                .filter(this::isRowWithinBounds)
                .map(neighbour -> {
                    try {
                        return this.getTile(neighbour);
                    } catch (InvalidBoardPositionException e) {
                        logger.error("Encountered exception whilst finding neighbouring tiles", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns a tile from the board at the given position.
     *
     * @param position The position of the tile.
     * @return The tile on that position.
     * @throws InvalidBoardPositionException Thrown if the given position was invalid.
     */
    public Tile getTile(BoardPosition position) throws InvalidBoardPositionException {
        this.checkValidPosition(position);
        return this.tiles[position.getRow()][position.getCol()];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Draw the column numbers at the top
        builder.append("  "); // align with first number
        for (int col = 0; col < this.numberOfRowsAndColumns; col++) {
            builder.append(String.format("%2d ", col));
        }
        builder.append("\n");

        for (int row = 0; row < this.numberOfRowsAndColumns; row++) {
            // Draw the row numbers
            builder.append(String.format("%2d ", row));

            for (int col = 0; col < this.numberOfRowsAndColumns; col++) {
                Tile tile = this.tiles[row][col];

                TileColor tileColor = tile.getColor();
                String tileAsString = (tileColor == TileColor.LIGHT) ? "□" : "■";
                builder.append(tileAsString);

                // Tile has a piece on it
                if (tile.isOccupied()) {
                    Piece piece = tile.getPiece();
                    String pieceAsString = " "; // no piece
                    if (piece != null) {
                        if (piece.getColor() == PieceColor.DARK) {
                            if (piece.isKing()) {
                                pieceAsString = "★";
                            } else {
                                pieceAsString = "●";
                            }
                        } else {
                            if (piece.isKing()) {
                                pieceAsString = "☆";
                            } else {
                                pieceAsString = "○";
                            }
                        }

                    }

                    builder.append(pieceAsString);

                    // Tile is empty
                } else {
                    builder.append(" ");

                }

                builder.append(" ");

                if (col == this.numberOfRowsAndColumns - 1) {
                    builder.append("\n");
                }
            }

        }
        return builder.toString();
    }

    public int getNumberOfRowsAndColumns() {
        return numberOfRowsAndColumns;
    }
}
