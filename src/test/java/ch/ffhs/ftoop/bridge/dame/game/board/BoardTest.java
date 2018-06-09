package ch.ffhs.ftoop.bridge.dame.game.board;

import ch.ffhs.ftoop.bridge.dame.game.GameConfig;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition.from;
import static ch.ffhs.ftoop.bridge.dame.game.board.Piece.dark;
import static ch.ffhs.ftoop.bridge.dame.game.board.Piece.light;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BoardTest {
    private static final Logger logger = LogManager.getLogger(BoardTest.class);
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private GameConfig config;

    @Before
    public void setup() throws ConfigurationException {
        this.config = new GameConfig("game-test.properties");
    }

    @Test
    public void testBoardConstructorAndVerifyCheckerboard() throws InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        logger.debug("Board:\n{}", board);

        // @formatter:off
        List<BoardPosition> expectedPositionsOfDarkTiles = Arrays.asList(
                from(1, 0), from(3, 0), from(5, 0), from(7, 0),
                from(0, 1), from(2, 1), from(4, 1), from(6, 1),
                from(1, 2), from(3, 2), from(5, 2), from(7, 2),
                from(0, 3), from(2, 3), from(4, 3), from(6, 3),
                from(1, 4), from(3, 4), from(5, 4), from(7, 4),
                from(0, 5), from(2, 5), from(4, 5), from(6, 5),
                from(1, 6), from(3, 6), from(5, 6), from(7, 6),
                from(0, 7), from(2, 7), from(4, 7), from(6, 7)
        );
        // @formatter:on

        List<Tile> tiles = board.getTiles();
        assertEquals(this.config.getNumberOfRowsAndColumnsOfBoard() * this.config.getNumberOfRowsAndColumnsOfBoard(), tiles.size());

        List<BoardPosition> actualPositionOfDarkTiles = tiles.stream()
                .filter(t -> t.getColor() == TileColor.DARK)
                .map(Tile::getPosition)
                .collect(Collectors.toList());

        assertEquals((this.config.getNumberOfRowsAndColumnsOfBoard() * this.config.getNumberOfRowsAndColumnsOfBoard()) / 2, actualPositionOfDarkTiles.size());
        assertThat(expectedPositionsOfDarkTiles, containsInAnyOrder(actualPositionOfDarkTiles.toArray()));
    }

    @Test
    public void testPopulateWithInitialPieces() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());

        // @formatter:off
        // Dark is at the top
        List<BoardPosition> expectedPositionsOfDarkPieces = Arrays.asList(
                from(1, 0), from(3, 0), from(5, 0), from(7, 0),
                from(0, 1), from(2, 1), from(4, 1), from(6, 1),
                from(1, 2), from(3, 2), from(5, 2), from(7, 2)
        );
        // @formatter:on

        // @formatter:off
        // Dark is at the bottom
        List<BoardPosition> expectedPositionsOfLightPieces = Arrays.asList(
                from(0, 5), from(2, 5), from(4, 5), from(6, 5),
                from(1, 6), from(3, 6), from(5, 6), from(7, 6),
                from(0, 7), from(2, 7), from(4, 7), from(6, 7)
        );
        // @formatter:on

        board.populateWithInitialPieces(this.config.getNumberOfPiecesPerPlayer());
        logger.debug("Board:\n{}", board);

        // Check the number of pieces
        List<Piece> darkPieces = board.getPieces(PieceColor.DARK);
        List<Piece> lightPieces = board.getPieces(PieceColor.LIGHT);
        assertEquals(this.config.getNumberOfPiecesPerPlayer() * 2, (darkPieces.size() + lightPieces.size()));

        // Check the positions of the pieces
        List<Tile> tiles = board.getTiles();

        assertThat(expectedPositionsOfDarkPieces, containsInAnyOrder(tiles.stream()
                .filter(Tile::isOccupied)
                .filter(tile -> tile.getPiece().getColor() == PieceColor.DARK)
                .map(Tile::getPosition).toArray()));

        assertThat(expectedPositionsOfLightPieces, containsInAnyOrder(tiles.stream()
                .filter(Tile::isOccupied)
                .filter(tile -> tile.getPiece().getColor() == PieceColor.LIGHT)
                .map(Tile::getPosition).toArray()));

    }

    @Test
    public void testCheckBoardDimensionsWithNegativeNumberOfColumns() throws InvalidBoardDimensionsException {
        thrown.expect(InvalidBoardDimensionsException.class);
        new Board(-1);
    }

    @Test
    public void testCheckBoardDimensionsWithZeroNumberOfColumns() throws InvalidBoardDimensionsException {
        thrown.expect(InvalidBoardDimensionsException.class);
        new Board(0);
    }

    @Test
    public void testCheckBoardDimensionsWithOddNumberOfColumns() throws InvalidBoardDimensionsException {
        thrown.expect(InvalidBoardDimensionsException.class);
        new Board(7);
    }

    @Test
    public void testCheckBoardDimensionsWithEvenNumberOfColumns() throws InvalidBoardDimensionsException {
        new Board(8);
    }

    @Test
    public void testCheckNumberOfPiecesPerPlayerWithNegativeNumberOfPieces() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        thrown.expect(InvalidNumberOfPiecesPerPlayerException.class);
        Board board = new Board(2);
        board.populateWithInitialPieces(-1);
    }

    @Test
    public void testCheckNumberOfPiecesPerPlayerWithZeroNumberOfPieces() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        thrown.expect(InvalidNumberOfPiecesPerPlayerException.class);
        Board board = new Board(2);
        board.populateWithInitialPieces(0);
    }

    @Test
    public void testCheckNumberOfPiecesPerPlayerWithOddNumberOfPieces() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        thrown.expect(InvalidNumberOfPiecesPerPlayerException.class);
        Board board = new Board(2);
        board.populateWithInitialPieces(1);
    }

    @Test
    public void testCheckNumberOfPiecesPerPlayerWithEvenNumberOfPieces() throws InvalidBoardDimensionsException, InvalidNumberOfPiecesPerPlayerException {
        Board board = new Board(2);
        board.populateWithInitialPieces(2);

    }

    @Test
    public void testCalculateDistanceBetweenHorizontalPoints() throws InvalidBoardDimensionsException, InvalidNumberOfPiecesPerPlayerException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        board.populateWithInitialPieces(this.config.getNumberOfPiecesPerPlayer());
        BoardPosition from = from(0, 0);
        BoardPosition to = from(3, 0);

        assertEquals(3, board.calculateDistanceBetween(from, to));
    }

    @Test
    public void testCalculateDistanceBetweenVerticalPoints() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition from = from(0, 0);
        BoardPosition to = from(0, 3);

        assertEquals(3, board.calculateDistanceBetween(from, to));
    }

    @Test
    public void testCalculateDistanceBetweenDiagonalPointsWithOneSingleStep() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition from = from(0, 0);
        BoardPosition to = from(1, 1);

        assertEquals(1, board.calculateDistanceBetween(from, to));
    }

    @Test
    public void testCalculateDistanceBetweenDiagonalPointsWithMultipleSteps() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition from = from(0, 0);
        BoardPosition to = from(3, 3);

        assertEquals(3, board.calculateDistanceBetween(from, to));
    }

    @Test
    public void testFindTilesBetweenTwoDiagonalPointsToRight() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());

        BoardPosition from = from(3, 0);
        BoardPosition to = from(5, 2);

        // Debug TODO
        board.setPiece(Piece.light(), from);
        board.setPiece(Piece.light(), to);
        board.setPiece(light(), from(4, 1));

        logger.debug("Board:\n{}", board);

        List<Tile> tilesBetween = board.findTilesBetween(from, to);
        assertEquals(1, tilesBetween.size());

        Tile betweenTile = tilesBetween.get(0);
        assertEquals(1, betweenTile.getPosition().getRow());
        assertEquals(4, betweenTile.getPosition().getCol());
    }

    @Test
    public void testFindTilesBetweenTwoDiagonalPointsToLeft() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());

        // Move from top left to bottom right, over two tiles
        BoardPosition from = from(3, 0);
        BoardPosition to = from(1, 2);

        // Debug TODO
        board.setPiece(Piece.light(), from);
        board.setPiece(Piece.light(), to);
        board.setPiece(light(), from(2, 1));

        logger.debug("Board:\n{}", board);

        List<Tile> tilesBetween = board.findTilesBetween(from, to);
        assertEquals(1, tilesBetween.size());

        Tile betweenTile = tilesBetween.get(0);
        assertEquals(1, betweenTile.getPosition().getRow());
        assertEquals(2, betweenTile.getPosition().getCol());
    }

    @Test
    public void testFindTilesBetweenToHorizontalPoints() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());

        BoardPosition from = from(0, 0);
        BoardPosition to = from(3, 0);

        List<Tile> tilesBetween = board.findTilesBetween(from, to);
        assertEquals(2, tilesBetween.size());

        Tile firstTile = tilesBetween.get(0);
        assertEquals(0, firstTile.getPosition().getRow());
        assertEquals(1, firstTile.getPosition().getCol());

        Tile secondTile = tilesBetween.get(1);
        assertEquals(0, secondTile.getPosition().getRow());
        assertEquals(2, secondTile.getPosition().getCol());
    }

    @Test
    public void testCheckPositionWithPositionOutsideBoard() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());

        thrown.expect(InvalidBoardPositionException.class);
        board.setPiece(dark(), from(42, 42));
    }

    @Test
    public void testIsKingsRowWithDarkPieceAtKingsRow() throws InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition positionOnKingsRow = BoardPosition.from(0, this.config.getNumberOfRowsAndColumnsOfBoard() - 1);

        assertTrue(board.isKingsRow(positionOnKingsRow, PieceColor.DARK));
    }

    @Test
    public void testIsKingsRowWithLightPieceAtKingsRow() throws InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition positionOnKingsRow = BoardPosition.from(0, 0);

        assertTrue(board.isKingsRow(positionOnKingsRow, PieceColor.LIGHT));
    }

    @Test
    public void testIsKingsRowWithDarkPieceNotOnKingsRow() throws InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        BoardPosition positionOnKingsRow = BoardPosition.from(0, 4);

        assertFalse(board.isKingsRow(positionOnKingsRow, PieceColor.DARK));
    }

    @Test
    public void testGetPiecesForColor() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        board.populateWithInitialPieces(this.config.getNumberOfPiecesPerPlayer());

        List<Piece> pieces = board.getPieces(PieceColor.DARK);
        assertEquals(this.config.getNumberOfPiecesPerPlayer(), pieces.size());
    }

    @Test
    public void testSetPiece() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        Piece piece = Piece.dark();
        BoardPosition position = BoardPosition.from(0, 0);

        board.setPiece(piece, position);
        assertEquals(piece, board.getTile(position).getPiece());
    }

    @Test
    public void testRemovePiece() throws InvalidBoardPositionException, InvalidBoardDimensionsException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        Piece piece = Piece.dark();
        BoardPosition position = BoardPosition.from(0, 0);
        board.setPiece(piece, position);

        board.removePiece(position);
        assertFalse(board.getTile(position).isOccupied());
    }

    @Test
    public void testFindNeighbouringTiles() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        logger.debug("\n{}", board);

        BoardPosition from = BoardPosition.from(1, 1);
        List<BoardPosition> expectedNeighbouringPositions = Arrays.asList(
                BoardPosition.from(1, 0),
                BoardPosition.from(2, 0),
                BoardPosition.from(2, 1),
                BoardPosition.from(2, 2),
                BoardPosition.from(1, 2),
                BoardPosition.from(0, 2),
                BoardPosition.from(0, 1),
                BoardPosition.from(0, 0)
        );

        List<Tile> neighbouringTiles = board.findNeighbouringTiles(from);
        List<BoardPosition> actualNeighbouringPositions = neighbouringTiles.stream()
                .map(Tile::getPosition)
                .collect(Collectors.toList());

        assertEquals(expectedNeighbouringPositions.size(), actualNeighbouringPositions.size());
        assertThat(expectedNeighbouringPositions, containsInAnyOrder(actualNeighbouringPositions.toArray()));

    }

    @Test
    public void testFindNeighbouringTilesWithSomeOutOfBounds() throws InvalidBoardDimensionsException, InvalidBoardPositionException {
        Board board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        logger.debug("\n{}", board);

        BoardPosition from = BoardPosition.from(0, 0);
        List<BoardPosition> expectedNeighbouringPositions = Arrays.asList(
                BoardPosition.from(1, 0),
                BoardPosition.from(1, 1),
                BoardPosition.from(0, 1)
        );

        List<Tile> neighbouringTiles = board.findNeighbouringTiles(from);
        List<BoardPosition> actualNeighbouringPositions = neighbouringTiles.stream()
                .map(Tile::getPosition)
                .collect(Collectors.toList());

        assertEquals(expectedNeighbouringPositions.size(), actualNeighbouringPositions.size());
        assertThat(expectedNeighbouringPositions, containsInAnyOrder(actualNeighbouringPositions.toArray()));
    }

}