package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.GameConfig;
import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardDimensionsException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.board.TileColor;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveRuleValidatorTest {

    private static final Logger logger = LogManager.getLogger(MoveRuleValidatorTest.class);
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private Board board;

    @Before
    public void setup() throws InvalidBoardDimensionsException, ConfigurationException {
        GameConfig config = new GameConfig("game-test.properties");
        this.board = new Board(config.getNumberOfRowsAndColumnsOfBoard());
    }

    @Test
    public void tesIsMovingPiece() {
        Piece piece = Piece.dark();
        assertTrue(MoveRuleValidator.isMovingPiece(piece));
    }

    @Test
    public void tesIsMovingPieceWhenPieceNull() {
        assertFalse(MoveRuleValidator.isMovingPiece(null));
    }

    @Test
    public void testIsActuallyMovingWithoutMove() {
        BoardPosition from = BoardPosition.from(1, 1);
        BoardPosition to = BoardPosition.from(1, 1);
        Move move = Move.from(this.player(), Piece.dark(), from, to);
        assertFalse(MoveRuleValidator.isActuallyMoving(move));
    }

    private Player player() {
        return new Player("Player", PieceColor.DARK);
    }

    @Test
    public void testIsActuallyMovingWithMove() {
        BoardPosition from = BoardPosition.from(1, 1);
        BoardPosition to = BoardPosition.from(2, 2);
        Move move = Move.from(this.player(), Piece.dark(), from, to);
        assertTrue(MoveRuleValidator.isActuallyMoving(move));
    }

    @Test
    public void testIsPlayerPieceOnTileWithPieceBeingThere() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.dark(), BoardPosition.from(1, 0));
        Tile tile = this.board.getTile(BoardPosition.from(1, 0));
        Player player = new Player("Player", PieceColor.DARK);

        assertTrue(MoveRuleValidator.isPlayerPieceOnTile(tile, player));
    }

    @Test
    public void testIsPlayerPieceOnTileWithPieceMissing() throws InvalidBoardPositionException {
        Tile tile = this.board.getTile(BoardPosition.from(1, 0));
        Player player = new Player("Player", PieceColor.DARK);

        assertFalse(MoveRuleValidator.isPlayerPieceOnTile(tile, player));
    }

    @Test
    public void testIsPieceAndPlayersColorMatchingWithMatchingColors() {
        Player player = new Player("Player", PieceColor.DARK);
        Piece piece = Piece.forColor(PieceColor.DARK);

        assertTrue(MoveRuleValidator.isPieceAndPlayersColorMatching(piece, player));
    }

    @Test
    public void testIsPieceAndPlayersColorMatchingWithMismatchingColors() {
        Player player = new Player("Player", PieceColor.DARK);
        Piece piece = Piece.forColor(PieceColor.LIGHT);

        assertFalse(MoveRuleValidator.isPieceAndPlayersColorMatching(piece, player));
    }

    @Test
    public void testIsDarkTileWithDarkTile() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.DARK);
        assertTrue(MoveRuleValidator.isDarkTile(tile));
    }

    @Test
    public void testIsDarkTileWithLightTile() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.LIGHT);
        assertFalse(MoveRuleValidator.isDarkTile(tile));
    }

    @Test
    public void testIsTileFreeWithTileFree() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.DARK, Piece.NONE);
        assertTrue(MoveRuleValidator.isTileFree(tile));
    }

    @Test
    public void testIsTileFreeWithTileOccupied() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.DARK, Piece.dark());
        assertFalse(MoveRuleValidator.isTileFree(tile));
    }

    @Test
    public void testIsMovingForwardWithDarkPiece() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(1, 1));
        assertTrue(MoveRuleValidator.isMovingForward(move));
    }

    @Test
    public void testIsMovingForwardWithDarkPieceMovingBackwards() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 1), BoardPosition.from(0, 0));
        assertFalse(MoveRuleValidator.isMovingForward(move));
    }

    @Test
    public void testIsMovingForwardWithLightPieceMovingForward() {
        Move move = Move.from(this.player(), Piece.light(), BoardPosition.from(8, 8), BoardPosition.from(7, 7));
        assertTrue(MoveRuleValidator.isMovingForward(move));
    }

    @Test
    public void testIsMovingForwardWithLightPieceMovingBackwards() {
        Move move = Move.from(this.player(), Piece.light(), BoardPosition.from(7, 7), BoardPosition.from(8, 8));
        assertFalse(MoveRuleValidator.isMovingForward(move));
    }

    @Test
    public void testIsMovingForwardsWithKing() {
        Piece piece = Piece.dark();
        piece.setKing(true);

        Move move = Move.from(this.player(), piece, BoardPosition.from(0, 0), BoardPosition.from(1, 0));
        assertTrue(MoveRuleValidator.isMovingForward(move));
    }

    @Test
    public void testIsMovingDiagonallyWhenMovingDiagonally() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(7, 7), BoardPosition.from(8, 8));
        assertTrue(MoveRuleValidator.isMovingDiagonally(move));
    }

    @Test
    public void testIsMovingDiagonallyWhenMovingStraight() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(8, 7), BoardPosition.from(8, 8));
        assertFalse(MoveRuleValidator.isMovingDiagonally(move));
    }

    @Test
    public void testIsMovingDiagonallyWhenMovingStraightAsKing() {
        Piece piece = Piece.dark();
        piece.setKing(true);

        Move move = Move.from(this.player(), piece, BoardPosition.from(8, 7), BoardPosition.from(8, 8));
        // Not even king can move straight
        assertFalse(MoveRuleValidator.isMovingDiagonally(move));
    }

    @Test
    public void testIsCapturingOpponentsPieceWhenPieceInPath() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(2, 2));

        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(3, 3));
        assertTrue(MoveRuleValidator.isCapturingOpponentsPiece(this.board, move));
    }

    @Test
    public void testIsCapturingOpponentsPieceWhenNoPieceInPath() throws InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(3, 3));
        assertFalse(MoveRuleValidator.isCapturingOpponentsPiece(this.board, move));
    }

    @Test
    public void testIsMovingCorrectWhenNotCapturing() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(1, 1));
        assertTrue(MoveRuleValidator.isMovingCorrectDistance(this.board, move, false));
    }

    @Test
    public void testIsMovingCorrectWhenCapturing() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(4, 4));
        assertTrue(MoveRuleValidator.isMovingCorrectDistance(this.board, move, true));
    }

    @Test
    public void testIsMovingCorrectWhenMultiple() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(2, 2));
        assertTrue(MoveRuleValidator.isMovingCorrectDistance(this.board, move, true));
    }

    @Test
    public void testIsMovingCorrectWhenMovingMultiple() {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(5, 4));
        assertTrue(MoveRuleValidator.isMovingCorrectDistance(this.board, move, true));
    }

    @Test
    public void testIsJumpingOverPieces() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(2, 1));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(3, 2));

        assertTrue(MoveRuleValidator.isJumpingOverPieces(this.board, move));
    }

    @Test
    public void testIsJumpingOnlyOverOpponentsPiecesWithOpponentsPiece() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(2, 1));

        BoardPosition from = BoardPosition.from(1, 0);
        BoardPosition to = BoardPosition.from(3, 2);
        Move move = Move.from(this.player(), Piece.dark(), from, to);

        List<Tile> tilesBetween = this.board.findTilesBetween(from, to);
        assertTrue(MoveRuleValidator.isJumpingOnlyOverOpponentsPieces(tilesBetween, move));
    }

    @Test
    public void testIsJumpingOnlyOverOpponentsPiecesWithOwnPiece() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.dark(), BoardPosition.from(2, 1));

        BoardPosition from = BoardPosition.from(1, 0);
        BoardPosition to = BoardPosition.from(3, 2);
        Move move = Move.from(this.player(), Piece.dark(), from, to);

        List<Tile> tilesBetween = this.board.findTilesBetween(from, to);
        assertFalse(MoveRuleValidator.isJumpingOnlyOverOpponentsPieces(tilesBetween, move));
    }

    @Test
    public void testIsAbleToJumpOverAnyDirectNeighbourWhenActuallyAble() throws InvalidBoardPositionException {
        this.board.setPiece(Piece.dark(), BoardPosition.from(2, 1)); // bottom right of "from"
        logger.debug("Board\n{}", this.board);

        BoardPosition from = BoardPosition.from(1, 0);
        BoardPosition to = BoardPosition.from(0, 1); // Moving to bottom left
        Move move = Move.from(this.player(), Piece.dark(), from, to);

        assertTrue(MoveRuleValidator.isAbleToJumpOverAnyDirectNeighbour(this.board, move));
    }

    @Test
    public void testIsAbleToJumpOverAnyDirectNeighbourWhenReallyUnable() throws InvalidBoardPositionException {
        // No pieces on board at all...

        BoardPosition from = BoardPosition.from(1, 0);
        BoardPosition to = BoardPosition.from(0, 1); // Moving to bottom left
        Move move = Move.from(this.player(), Piece.dark(), from, to);

        assertFalse(MoveRuleValidator.isAbleToJumpOverAnyDirectNeighbour(this.board, move));
    }

    @Test
    public void testIsAbleToHoldPieceWithDarkTile() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.DARK);
        assertTrue(MoveRuleValidator.isAbleToHoldPiece(tile));
    }

    @Test
    public void testIsAbleToHoldPieceWithLightTile() {
        Tile tile = new Tile(BoardPosition.from(0, 0), TileColor.LIGHT);
        assertFalse(MoveRuleValidator.isAbleToHoldPiece(tile));
    }

    @Test
    public void testValidateMoveThrowingPlayerMustAlwaysMovePieceException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.NONE, BoardPosition.from(0, 0), BoardPosition.from(1, 1));

        this.thrown.expect(PlayerMustAlwaysMovePieceException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PLAYER_MUST_PLAY_PIECE));
    }

    @Test
    public void testValidateMoveThrowingPieceMustMoveToNewPositionException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 0), BoardPosition.from(0, 0));

        this.thrown.expect(PieceMustMoveToNewPositionException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE));
    }

    @Test
    public void testValidateMoveThrowingPieceMustBeOnBoard() throws InvalidMoveException, InvalidBoardPositionException {
        Piece setPiece = Piece.dark();
        this.board.setPiece(setPiece, BoardPosition.from(1, 1));

        Piece playedPiece = Piece.dark(); // different piece than set
        playedPiece.setKing(true);
        Move move = Move.from(this.player(), playedPiece, BoardPosition.from(0, 0), BoardPosition.from(0, 0));

        this.thrown.expect(PieceMustBeOnBoard.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_STILL_BE_ON_BOARD));
    }

    @Test
    public void testValidateMoveThrowingPlayerCanOnlyPlayPieceOfTheirColorException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.light(), BoardPosition.from(0, 0), BoardPosition.from(0, 0));

        this.thrown.expect(PlayerCanOnlyPlayPieceOfTheirColorException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PLAYER_MUST_PLAY_OWN_PIECES));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyBeMovedToDarkTileException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(1, 1));

        this.thrown.expect(PieceCanOnlyBeMovedToDarkTileException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_BE_PLACED_ON_DARK_TILE));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyBeMovedOnAFreeTileException() throws InvalidMoveException, InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(1, 1));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(1, 1));

        this.thrown.expect(PieceCanOnlyBeMovedOnAFreeTileException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_BE_PLACED_ON_FREE_TILE));
    }

    @Test
    public void testValidateMoveThrowingPieceMustMoveForwardException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 1), BoardPosition.from(1, 0));

        this.thrown.expect(PieceMustMoveForwardException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE_FORWARD));
    }

    @Test
    public void testValidateMoveThrowingPieceMustMoveDiagonallyException() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(0, 1), BoardPosition.from(0, 2));

        this.thrown.expect(PieceMustMoveDiagonallyException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE_DIAGONALLY));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyMoveASetDistanceExceptionMovingJumpingTooFar() throws InvalidMoveException, InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(2, 1));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(4, 3));

        this.thrown.expect(PieceCanOnlyMoveASetDistanceException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE_CORRECT_DISTANCE));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyMoveASetDistanceExceptionDoubleJumpingWithoutAbilityToLand() throws InvalidMoveException, InvalidBoardPositionException {
        this.board.setPiece(Piece.light(), BoardPosition.from(2, 1));
        this.board.setPiece(Piece.light(), BoardPosition.from(3, 2));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(4, 3));

        this.thrown.expect(PieceCanOnlyMoveASetDistanceException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE_CORRECT_DISTANCE));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyJumpOverOpponentsPieces() throws InvalidMoveException, InvalidBoardPositionException {
        this.board.setPiece(Piece.dark(), BoardPosition.from(2, 1));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(3, 2));

        this.thrown.expect(PieceCanOnlyJumpOverOpponentsPieces.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_ONLY_JUMP_OVER_OPPONENT_PIECES));
    }

    @Test
    public void testValidateMoveThrowingPlayerMustJumpOverOpponentsPieceIfPossibleException() throws InvalidMoveException, InvalidBoardPositionException {
        this.board.setPiece(Piece.dark(), BoardPosition.from(2, 1));
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(0, 1));

        this.thrown.expect(PlayerMustJumpOverOpponentsPieceIfPossibleException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY));
    }

    @Test
    public void testValidateMoveThrowingPieceCanOnlyMoveASetDistanceExceptionWhenNotJumpingAndMovingTooFar() throws InvalidMoveException, InvalidBoardPositionException {
        Move move = Move.from(this.player(), Piece.dark(), BoardPosition.from(1, 0), BoardPosition.from(5, 4));

        this.thrown.expect(PieceCanOnlyMoveASetDistanceException.class);
        MoveRuleValidator.validateMove(this.board, move, Collections.singletonList(MoveRule.PIECE_MUST_MOVE_CORRECT_DISTANCE));
    }
}