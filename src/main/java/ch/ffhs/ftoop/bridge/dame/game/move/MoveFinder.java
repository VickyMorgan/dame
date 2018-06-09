package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Allows finding valid moves for any given position on the board. It uses a multi-threaded programming model
 * to optimise the search for a move by parallalising and dividing the search into smaller chunks.
 */
public abstract class MoveFinder {
    private static final Logger logger = LogManager.getLogger(MoveFinder.class);

    private MoveFinder() {
    }

    /**
     * Finds all valid moves for the given player, piece and position on the board.
     * <p>
     * It does this by splitting and parallalising the work using threads.
     *
     * @param board        The board on which to find the valid moves for.
     * @param player       The player for which all valid moves should be found.
     * @param piece        The piece for which the valid moves should be determined.
     * @param from         The position (on the board) form which all the valid moves should be found.
     * @param enabledRules The game rules that are enabled that set the constraint on what a valid move means.
     * @return A list of valid moves or an empty list if there are none.
     */
    public static List<Move> findValidMoves(Board board, Player player, Piece piece, BoardPosition from, List<MoveRule> enabledRules) {
        logger.debug("Finding valid moves from {} for {} with {}", from, player, piece);

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Optional<Move>>> findMoveTasks;

        synchronized (board) {
            List<BoardPosition> toPositionsToValidate = board.getTiles().stream()
                    .map(Tile::getPosition)
                    .collect(Collectors.toList());

            findMoveTasks = toPositionsToValidate.stream()
                    // Submit the tasks to the thread pool for calculation, storing the future for
                    // result retrieval later on
                    .map(to -> executor.submit(new MoveFindingTask(board, player, piece, from, to, enabledRules)))
                    .collect(Collectors.toList());
        }

        List<Move> validMoves = findMoveTasks.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.warn("Task interrupted due to exception", e);
                        return Optional.<Move>empty();
                    }
                })
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        executor.shutdown();
        return validMoves;
    }
}
