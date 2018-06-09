package ch.ffhs.ftoop.bridge.dame.game.actor;

import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.move.Move;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveFinder;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Implements a computer player, serving as an opponent to a human player.
 */
public class Computer extends Player {
    private static final Logger logger = LogManager.getLogger(Computer.class);

    private final int maxNumberOfAttemptsWhenFindingValidMove;

    public Computer(String name, PieceColor color, int maxNumberOfAttemptsWhenFindingValidMove) {
        super(name, color);
        this.maxNumberOfAttemptsWhenFindingValidMove = maxNumberOfAttemptsWhenFindingValidMove;
    }

    /**
     * Finds the next, valid move. The following strategy is used:
     * <ol>
     * <li>All tiles of the board, which are occupied (contain a piece) are determined</li>
     * <li>From the position of this tile, all valid moves are calculated</li>
     * <li>A valid move is then randomly selected</li>
     * <li>All tiles of the board, which are occupied (contain a piece) are determine. If there are no valid moves from the selected tile, the process is restarted over from 1.</li>
     * </ol>
     *
     * @param board        The board on which the move should be found.
     * @param enabledRules A list of rules that are enabled, used to determine what a valid move is.
     * @return The next move of the computer opponent.
     * @throws NoValidComputerMoveFoundException Thrown if no move could be found.
     */
    public Move findNextMove(Board board, List<MoveRule> enabledRules) throws NoValidComputerMoveFoundException {
        for (int i = 0; i < this.maxNumberOfAttemptsWhenFindingValidMove; i++) {
            List<Tile> availableTiles = this.findAvailableTiles(board);
            Tile selectedTile = this.selectRandomTile(availableTiles);

            List<Move> validMoves = MoveFinder.findValidMoves(board, this, selectedTile.getPiece(), selectedTile.getPosition(), enabledRules);
            if (validMoves.size() == 0) {
                logger.warn("Could not find any valid moves for computer to play, will try again ({}/{})", (i + 1), this.maxNumberOfAttemptsWhenFindingValidMove);
                continue;
            }

            return this.selectRandomMove(validMoves);
        }

        throw new NoValidComputerMoveFoundException("Could not find any valid moves for computer to play!");
    }

    private List<Tile> findAvailableTiles(Board board) {
        return board.getTiles().stream()
                .filter(Tile::isOccupied)
                .filter(tile -> tile.getPiece().getColor() == this.getColor())
                .collect(Collectors.toList());

    }

    private Tile selectRandomTile(List<Tile> tiles) {
        return tiles.get(ThreadLocalRandom.current().nextInt(0, tiles.size()));
    }

    private Move selectRandomMove(List<Move> moves) {
        return moves.get(ThreadLocalRandom.current().nextInt(0, moves.size()));

    }
}
