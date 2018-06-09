package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.GameConfig;
import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardDimensionsException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidNumberOfPiecesPerPlayerException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition.from;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MoveFinderTest {
    private static final Logger logger = LogManager.getLogger(MoveFinderTest.class);

    @Test
    public void testFindValidMovesWithNormalPieceAndNoNeighbouringOpponentPieces() throws InvalidBoardPositionException, InvalidBoardDimensionsException, ConfigurationException {
        GameConfig config = new GameConfig("game-test-no-compulsory-jump.properties");
        Board board = new Board(config.getNumberOfRowsAndColumnsOfBoard());

        BoardPosition positionToFindMovesFrom = from(1, 0);
        Player player = new Player("Test", PieceColor.DARK);

        Piece piece = Piece.dark();
        board.setPiece(piece, positionToFindMovesFrom);
        logger.debug("Board:\n{}", board);

        /*
         * Valid moves are:
         * 1) Forward, diagonally to left: Position 0, 1
         * 2) Forward, diagonally to right: Position 2, 1
         */

        List<BoardPosition> expectedValidMovesPositions = Arrays.asList(
                from(0, 1),
                from(2, 1)
        );

        List<Move> actualValidMoves = MoveFinder.findValidMoves(board, player, piece, positionToFindMovesFrom, config.getEnabledRules());

        this.visualizeValidMoves(board, actualValidMoves);
        assertEquals(expectedValidMovesPositions.size(), actualValidMoves.size());
        assertThat(expectedValidMovesPositions, containsInAnyOrder(
                actualValidMoves.stream().map(Move::getTo).toArray()
        ));
    }

    private void visualizeValidMoves(Board board, List<Move> listOfMoves) {
        logger.debug("Valid Moves:");
        listOfMoves.stream()
                .map(Move::toString)
                .forEach(move -> logger.debug("\t {}", move));

        listOfMoves.forEach(move -> {
            try {
                board.setPiece(Piece.light(), move.getTo());
            } catch (InvalidBoardPositionException e) {
                e.printStackTrace();
            }
        });

        logger.debug("Board:\n{}", board);

    }

    @Test
    public void testFindValidMovesWithKingPieceAndNoNeighbouringOpponentPieces() throws InvalidBoardPositionException, ConfigurationException, InvalidBoardDimensionsException {
        GameConfig config = new GameConfig("game-test-no-compulsory-jump.properties");
        Board board = new Board(config.getNumberOfRowsAndColumnsOfBoard());

        BoardPosition positionToFindMovesFrom = from(1, 2);
        Player player = new Player("Test", PieceColor.DARK);

        Piece piece = Piece.dark();
        piece.setKing(true);
        board.setPiece(piece, positionToFindMovesFrom);
        logger.debug("Board:\n{}", board);

        /*
         * Valid moves are:
         * 1) Forward, diagonally to left: Position 0, 3
         * 2) Forward, diagonally to right: Position 2, 3
         * 3) Backwards, diagonally left: Position 1, 0
         * 4) Backwards, diagonally right: Position 2, 1
         */

        List<BoardPosition> expectedValidMovesPositions = Arrays.asList(
                from(0, 3),
                from(2, 3),
                from(0, 1),
                from(2, 1)
        );

        List<Move> actualValidMoves = MoveFinder.findValidMoves(board, player, piece, positionToFindMovesFrom, config.getEnabledRules());

        this.visualizeValidMoves(board, actualValidMoves);
        assertEquals(expectedValidMovesPositions.size(), actualValidMoves.size());
        assertThat(expectedValidMovesPositions, containsInAnyOrder(
                actualValidMoves.stream().map(Move::getTo).toArray()
        ));
    }

    @Test
    public void testFindValidMovesWithNormalPieceAndASingleNeighbouringOpponentPieces() throws InvalidBoardPositionException, ConfigurationException, InvalidBoardDimensionsException {
        GameConfig config = new GameConfig("game-test.properties");
        Board board = new Board(config.getNumberOfRowsAndColumnsOfBoard());

        board.setPiece(Piece.light(), BoardPosition.from(2, 1));

        BoardPosition positionToFindMovesFrom = from(1, 0);
        Player player = new Player("Test", PieceColor.DARK);

        Piece piece = Piece.dark();
        board.setPiece(piece, positionToFindMovesFrom);

        logger.debug("Board:\n{}", board);

        /*
         * Valid moves are:
         * 1) Forward, diagonally to left: Position 0, 1
         * 2) Forward, diagonally to right, jumping over piece at 2, 1: Position 3, 2
         */

        List<BoardPosition> expectedValidMovesPositions = Collections.singletonList(
                // from(0, 1) // Not possible as forbidden due to the compulsory jump rule
                from(3, 2)
        );

        List<Move> actualValidMoves = MoveFinder.findValidMoves(board, player, piece, positionToFindMovesFrom, config.getEnabledRules());

        this.visualizeValidMoves(board, actualValidMoves);
        assertEquals(expectedValidMovesPositions.size(), actualValidMoves.size());
        assertThat(expectedValidMovesPositions, containsInAnyOrder(
                actualValidMoves.stream().map(Move::getTo).toArray()
        ));
    }

    @Test
    public void testFindValidMovesWithNormalPieceAndMultipleNeighbouringOpponentPiecesInRow() throws InvalidBoardPositionException, ConfigurationException, InvalidBoardDimensionsException {
        GameConfig config = new GameConfig("game-test.properties");
        Board board = new Board(config.getNumberOfRowsAndColumnsOfBoard());

        board.setPiece(Piece.light(), BoardPosition.from(2, 1));
        board.setPiece(Piece.light(), BoardPosition.from(4, 3));

        BoardPosition positionToFindMovesFrom = from(1, 0);
        Player player = new Player("Test", PieceColor.DARK);

        Piece piece = Piece.dark();
        board.setPiece(piece, positionToFindMovesFrom);

        logger.debug("Board:\n{}", board);

        /*
         * Valid moves are:
         * 1) Forward, diagonally to right, jumping over piece at 2, 1: Position 3, 2
         * 2) Forward, diagonally to right, jumping over piece at 2, 1 and over 4,3: Position 5,4
         */

        List<BoardPosition> expectedValidMovesPositions = Arrays.asList(
                from(3, 2),
                from(5, 4)
        );

        List<Move> actualValidMoves = MoveFinder.findValidMoves(board, player, piece, positionToFindMovesFrom, config.getEnabledRules());

        this.visualizeValidMoves(board, actualValidMoves);
        assertEquals(expectedValidMovesPositions.size(), actualValidMoves.size());
        assertThat(expectedValidMovesPositions, containsInAnyOrder(
                actualValidMoves.stream().map(Move::getTo).toArray()
        ));
    }

    @Test
    public void testFindValidMovesWithNoPossibleMove() throws InvalidBoardPositionException, InvalidBoardDimensionsException, ConfigurationException {
        GameConfig config = new GameConfig("game-test.properties");
        Board board = new Board(config.getNumberOfRowsAndColumnsOfBoard());


        // No pieces, so no possibility to jump!
        BoardPosition positionToFindMovesFrom = from(1, 0);
        Player player = new Player("Test", PieceColor.DARK);

        Piece piece = Piece.dark();
        board.setPiece(piece, positionToFindMovesFrom);

        logger.debug("Board:\n{}", board);

        /*
         * Valid moves are:
         * - None
         */

        List<BoardPosition> expectedValidMovesPositions = Collections.emptyList();
        List<Move> actualValidMoves = MoveFinder.findValidMoves(board, player, piece, positionToFindMovesFrom, config.getEnabledRules());

        this.visualizeValidMoves(board, actualValidMoves);
        assertEquals(expectedValidMovesPositions.size(), actualValidMoves.size());
        assertThat(expectedValidMovesPositions, containsInAnyOrder(
                actualValidMoves.stream().map(Move::getTo).toArray()
        ));
    }


}