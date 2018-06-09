package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.board.TileColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_BE_PLACED_ON_DARK_TILE;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_BE_PLACED_ON_FREE_TILE;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_MOVE;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_MOVE_CORRECT_DISTANCE;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_MOVE_DIAGONALLY;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_MOVE_FORWARD;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_ONLY_JUMP_OVER_OPPONENT_PIECES;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PIECE_MUST_STILL_BE_ON_BOARD;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PLAYER_MUST_PLAY_OWN_PIECES;
import static ch.ffhs.ftoop.bridge.dame.game.move.MoveRule.PLAYER_MUST_PLAY_PIECE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The central piece, that is used to validate moves and differentiate between invalid and valid ones; it
 * implements all the games rules.
 */
public abstract class MoveRuleValidator {
    private static final Logger logger = LogManager.getLogger(MoveRuleValidator.class);

    private MoveRuleValidator() {
    }

    /**
     * Validates a given move against all enabled game rules.
     *
     * @param board        The current state of the board.
     * @param move         The move to test the validity for.
     * @param enabledRules A list of all enabled rules.
     * @throws InvalidMoveException          Thrown if the given move is invalid (according to the the enabled rules)
     * @throws InvalidBoardPositionException Thrown if the given move includes positions that are invalid.
     */
    public static void validateMove(Board board, Move move, List<MoveRule> enabledRules) throws InvalidMoveException, InvalidBoardPositionException {
        checkNotNull(board);
        checkNotNull(move);
        checkNotNull(enabledRules);

        logger.debug("Enabled rules: {}", enabledRules);

        Tile fromTile = board.getTile(move.getFrom());
        Tile toTile = board.getTile(move.getTo());

        if (isRuleEnabled(PLAYER_MUST_PLAY_PIECE, enabledRules)
                && !isMovingPiece(move.getPiece())) {

            String message = "A move must always include a piece";
            logger.debug(message);

            throw new PlayerMustAlwaysMovePieceException(message);
        }

        if (isRuleEnabled(PIECE_MUST_MOVE, enabledRules)
                && !isActuallyMoving(move)) {
            String message = "The move must move a piece from one position to another and cannot just stay in the same spot";
            logger.debug(message);

            throw new PieceMustMoveToNewPositionException(message);
        }

        if (isRuleEnabled(PIECE_MUST_STILL_BE_ON_BOARD, enabledRules)
                && !isPlayerPieceOnTile(fromTile, move.getPlayer())) {
            String message = "Only pieces that are actually (still) on the board can be played";
            logger.debug(message);

            throw new PieceMustBeOnBoard(message);
        }

        if (isRuleEnabled(PLAYER_MUST_PLAY_OWN_PIECES, enabledRules)
                && !isPieceAndPlayersColorMatching(move.getPiece(), move.getPlayer())) {
            String message = String.format("This player can only play %s pieces", move.getPlayer().getColor());
            logger.debug(message);

            throw new PlayerCanOnlyPlayPieceOfTheirColorException(message);
        }

        if (isRuleEnabled(PIECE_MUST_BE_PLACED_ON_DARK_TILE, enabledRules)
                && !isDarkTile(toTile)) {
            String message = "Pieces can only be moved on dark tiles";
            logger.debug(message);

            throw new PieceCanOnlyBeMovedToDarkTileException(message);
        }

        if (isRuleEnabled(PIECE_MUST_BE_PLACED_ON_FREE_TILE, enabledRules)
                && !isTileFree(toTile)) {
            String message = "Pieces can only be moved onto free tiles";
            logger.debug(message);

            throw new PieceCanOnlyBeMovedOnAFreeTileException(message);
        }

        if (isRuleEnabled(PIECE_MUST_MOVE_FORWARD, enabledRules)
                && !isMovingForward(move)) {
            String message = "Pieces can, unless they are king, be moved in one direction. Downwards if it is a dark piece, upwards if it is light";
            logger.debug(message);

            throw new PieceMustMoveForwardException(message);
        }

        if (isRuleEnabled(PIECE_MUST_MOVE_DIAGONALLY, enabledRules)
                && !isMovingDiagonally(move)) {
            String message = "Pieces can only move diagonally from field to field";
            logger.debug(message);

            throw new PieceMustMoveDiagonallyException(message);
        }

        List<Tile> jumpedTilesWithPieces = board.findTilesBetween(move.getFrom(), move.getTo()).stream()
                .filter(Tile::isOccupied)
                .collect(Collectors.toList());
        logger.debug("Jumped tiles between {} and {}: {}", move.getFrom(), move.getTo(), jumpedTilesWithPieces);

        // Check if the move is jumping over other pieces
        if (jumpedTilesWithPieces.size() > 0) {
            validateJumpingMove(board, move, jumpedTilesWithPieces, enabledRules);
        } else {
            validateNonJumpingMove(board, move, enabledRules);
        }
    }

    private static boolean isRuleEnabled(MoveRule rule, List<MoveRule> enabledRules) {
        return enabledRules.contains(rule);
    }

    /**
     * Checks, if the piece that is being moved, actually exists.
     *
     * @param piece The piece of the move.
     * @return Whether the piece is actually a piece.
     */
    static boolean isMovingPiece(Piece piece) {
        return piece != Piece.NONE;
    }

    /**
     * Checks, if the piece is being moved from one position to another and is not remaining in the same spot.
     *
     * @param move The move.
     * @return Whether the piece is being moved from one position to another or not.
     */
    static boolean isActuallyMoving(Move move) {
        return (!move.getFrom().equals(move.getTo()));
    }

    /**
     * Checks, if the given tile contains a piece of the player.
     *
     * @param tile   The tile to check.
     * @param player The player which should have one of their pieces on the tile.
     * @return Whether a piece of the player is on the tile or not.
     */
    static boolean isPlayerPieceOnTile(Tile tile, Player player) {
        return tile.isOccupied() && (tile.getPiece().getColor() == player.getColor());
    }

    /**
     * Checks, if the piece color and the color of the players pieces match.
     *
     * @param piece  The piece to check.
     * @param player The player used to confirm that the piece has the same color.
     * @return Whether the piece color and the color of the players pieces matches.
     */
    static boolean isPieceAndPlayersColorMatching(Piece piece, Player player) {
        return piece.getColor() == player.getColor();
    }

    /**
     * Checks if the given tile is a dark tile.
     *
     * @param tile The tile to test.
     * @return Whether the tile is a dark tile.
     */
    static boolean isDarkTile(Tile tile) {
        return tile.getColor() == TileColor.DARK;
    }

    /**
     * Checks if the given tile is free (does not have a piece on it).
     *
     * @param tile The tile to test.
     * @return Whether the tile is free and does not contain a piece.
     */
    static boolean isTileFree(Tile tile) {
        return !tile.isOccupied();
    }

    /**
     * Checks if the move is moving forward on the board (in the direction of the opponent). If the
     * piece being played is a king, the rule does not apply as kings can move forwards and backwards.
     *
     * @param move The move to test.
     * @return Whether the piece is moving forward when the piece is not a king. If it is a king this will always return true.
     */
    static boolean isMovingForward(Move move) {
        // The king, the "Dame" is allowed to move forwards and backwards
        if (move.getPiece().isKing()) {
            return true;
        }

        return isMovingForward(move.getFrom(), move.getTo(), move.getPiece());
    }

    /**
     * Checks if the piece is being moved diagonally.
     *
     * @param move The move to test.
     * @return Whether the piece is being moved diagonally from it's origin.
     */
    static boolean isMovingDiagonally(Move move) {
        return MoveType.determineType(move.getFrom(), move.getTo()) == MoveType.DIAGONAL;
    }

    private static void validateJumpingMove(Board board, Move move, List<Tile> jumpedTiles, List<MoveRule> enabledRules) throws PieceCanOnlyMoveASetDistanceException, PieceCanOnlyJumpOverOpponentsPieces, InvalidBoardPositionException {
        logger.debug("Is jumping over tiles: {}", jumpedTiles);

        if (isRuleEnabled(PIECE_MUST_MOVE_CORRECT_DISTANCE, enabledRules)
                && (!isMovingCorrectDistance(board, move, true)
                || !isAbleToLandBetweenJumps(board, move))) {
            String message = "Pieces can only move a distance an even distance when jumping over pieces";
            logger.debug(message);

            throw new PieceCanOnlyMoveASetDistanceException(message);
        }

        List<Tile> tilesJumpedOver = board.findTilesBetween(move.getFrom(), move.getTo());
        List<Tile> occupiedTilesJumpedOver = tilesJumpedOver.stream()
                .filter(Tile::isOccupied)
                .collect(Collectors.toList());

        if (isRuleEnabled(PIECE_MUST_ONLY_JUMP_OVER_OPPONENT_PIECES, enabledRules)
                && !isJumpingOnlyOverOpponentsPieces(occupiedTilesJumpedOver, move)) {
            String message = "Pieces can only jump over opponents pieces, not over players own pieces";
            logger.debug(message);

            throw new PieceCanOnlyJumpOverOpponentsPieces(message);
        }
    }

    private static void validateNonJumpingMove(Board board, Move move, List<MoveRule> enabledRules) throws InvalidBoardPositionException, PlayerMustJumpOverOpponentsPieceIfPossibleException, PieceCanOnlyMoveASetDistanceException {
        // Verify, that the player could actually not jump forwards over any opponents piece
        if (isRuleEnabled(PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY, enabledRules)) {
            if (isAbleToJumpOverAnyDirectNeighbour(board, move) || !hasOccupiedNeighbours(board, move)) {
                String message = "Jumping over neighbouring pieces is compulsory";
                logger.debug(message);
                logger.debug(move);

                throw new PlayerMustJumpOverOpponentsPieceIfPossibleException(message);
            }
        }


        if (isRuleEnabled(PIECE_MUST_MOVE_CORRECT_DISTANCE, enabledRules)
                && !isMovingCorrectDistance(board, move, false)) {
            String message = "Pieces can only move a distance of 1 tiles when not jump over pieces";
            logger.debug(message);

            throw new PieceCanOnlyMoveASetDistanceException(message);
        }
    }

    private static boolean isMovingForward(BoardPosition from, BoardPosition to, Piece piece) {
        int fromRow = from.getRow();
        int toRow = to.getRow();

        // Black pieces start at the top and can only move downwards
        if (piece.getColor() == PieceColor.DARK) {
            return toRow > fromRow;
        }

        // Light pieces can only move upwards
        return toRow < fromRow;
    }

    /**
     * Checks, if the piece is moving the correct distance. If the piece is not jumping over another piece,
     * it is only allowed to move a distance of 1 (a single tile). If the move is jumping (capturing) over another
     * piece, it has to move an even distance (1 step is onto the captured piece, another to land behind it). By checking
     * for even distance and not just to, a multi-jump-move where multiple pieces are captured can be covered.
     *
     * @param board       The board.
     * @param move        The move.
     * @param isCapturing If the move is capturing a piece or not.
     * @return Whether the piece is moving the correct distance for it's intention (capturing, non-capturing).
     */
    static boolean isMovingCorrectDistance(Board board, Move move, boolean isCapturing) {
        try {
            int distance = board.calculateDistanceBetween(move.getFrom(), move.getTo());

            /*
             * When capturing, also allow jumping over multiple pieces. When jumping over a piece,
             * the distance moved is always even (one step to the piece, one to the free tile).
             */
            if (isCapturing) {
                return distance % 2 == 0;

            } else {
                return distance == 1;
            }

        } catch (InvalidBoardPositionException e) {
            return false;
        }
    }

    private static boolean isAbleToLandBetweenJumps(Board board, Move move) throws InvalidBoardPositionException {
        // Check that every other piece is free
        List<Tile> allTilesBetween = board.findTilesBetween(move.getFrom(), move.getTo());
        logger.debug("Checking if move is able to land between jumped pieces");

        for (int i = 0; i < allTilesBetween.size(); i++) {

            // All the odd tiles should be free
            if (i % 2 == 1) {
                Tile tileWhichMustBeFree = allTilesBetween.get(i);

                if (tileWhichMustBeFree.isOccupied()) {
                    logger.debug("Tile which needed to be free is occupied: {}", tileWhichMustBeFree);
                    return false;
                }
                // All the even ones must be opponents pieces (of different colors than the played piece
            } else {
                Tile tileWhichMustHaveOpponentsPiece = allTilesBetween.get(i);

                if (!tileWhichMustHaveOpponentsPiece.isOccupied()
                        || tileWhichMustHaveOpponentsPiece.getPiece().getColor() == move.getPiece().getColor()) {
                    logger.debug("Tile which should contain opponents piece was free or had players one piece on it: {}", tileWhichMustHaveOpponentsPiece);
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * Checks if the move only jumps over opponents pieces.
     *
     * @param tilesJumpedOver The tiles which are being jumped/traveled across.
     * @param move            The move.
     * @return Whether only opponents pieces are in the path of the move.
     */
    static boolean isJumpingOnlyOverOpponentsPieces(List<Tile> tilesJumpedOver, Move move) {
        return tilesJumpedOver.stream()
                .filter(Tile::isOccupied)
                .allMatch(tile -> tile.getPiece().getColor() != move.getPlayer().getColor());
    }

    /**
     * Checks, if the played piece could potentially jump (capture) over any direct neighbour.
     *
     * @param board The board.
     * @param move  The move.
     * @return Whether the move could, instead of being a non-capuring move, capture a neighbouring piece.
     * @throws InvalidBoardPositionException Thrown when an invalid position on the board was used.
     */
    static boolean isAbleToJumpOverAnyDirectNeighbour(Board board, Move move) throws InvalidBoardPositionException {
        // Kings must not follow this rule as they are not required to jump over direct neighbours
        if (move.getPiece().isKing()) {
            return false;
        }

        List<Tile> occupiedNeighbouringTiles = board.findNeighbouringTiles(move.getFrom()).stream()
                .filter(Tile::isOccupied)
                .collect(Collectors.toList());

        // If there are no neighbours, no need to check neighbours' neighbours
        if (occupiedNeighbouringTiles.size() == 0) {
            return false;
        }

        // Check if any of the neighbours' neighbours in the same direction are free
        List<Tile> tilesReachableAfterJumpingOverNeighbour = occupiedNeighbouringTiles.stream()
                .flatMap(neighbour -> {
                    try {
                        // Find the neighbours free tiles
                        List<Tile> neighboursNeighbourFreeTiles = board.findNeighbouringTiles(neighbour.getPosition()).stream()
                                .filter(tile -> !tile.isOccupied())
                                .collect(Collectors.toList());

                        // Find all the neighbours free neighbours that are in the same direction as the neighbour from the tile
                        return neighboursNeighbourFreeTiles.stream()
                                .filter(neighboursFreeNeighbour -> MoveType.determineType(neighbour.getPosition(), neighboursFreeNeighbour.getPosition()) == MoveType.DIAGONAL);
                    } catch (InvalidBoardPositionException e) {
                        return new ArrayList<Tile>().stream();
                    }
                })
                .collect(Collectors.toList());

        return tilesReachableAfterJumpingOverNeighbour.size() > 0;
    }

    private static boolean hasOccupiedNeighbours(Board board, Move move) throws InvalidBoardPositionException {
        return board.findNeighbouringTiles(move.getFrom()).stream()
                .anyMatch(Tile::isOccupied);
    }

    /**
     * Checks if the move is jumping over (any, also own) pieces.
     *
     * @param board The board.
     * @param move  The move.
     * @return Whether there ar any pieces in the moves path.
     * @throws InvalidBoardPositionException Thrown when an invalid position on the board was used.
     */
    static boolean isJumpingOverPieces(Board board, Move move) throws InvalidBoardPositionException {
        List<Tile> tilesBetween = board.findTilesBetween(move.getFrom(), move.getTo());
        return tilesBetween.stream()
                .anyMatch(Tile::isOccupied);
    }

    /**
     * Checks, if the move will capture an opponents piece.
     *
     * @param board The board.
     * @param move  The move.
     * @return Whether the move is capturing an opponents piece.
     * @throws InvalidBoardPositionException Thrown when an invalid position on the board was used.
     */
    static boolean isCapturingOpponentsPiece(Board board, Move move) throws InvalidBoardPositionException {
        List<Tile> tilesBetween = board.findTilesBetween(move.getFrom(), move.getTo());
        return tilesBetween.stream()
                .filter(Tile::isOccupied)
                .anyMatch(t -> t.getPiece().getColor() != move.getPiece().getColor());
    }

    /**
     * Checks, whether the given tile is actually able to contain a piece. This enforces, that pieces can
     * only be placed on dark tiles.
     *
     * @param tile The tile to check.
     * @return Whether it is allowed to contain a piece or not.
     */
    public static boolean isAbleToHoldPiece(Tile tile) {
        return isDarkTile(tile);
    }
}
