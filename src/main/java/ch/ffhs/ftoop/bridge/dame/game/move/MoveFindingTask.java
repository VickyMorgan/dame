package ch.ffhs.ftoop.bridge.dame.game.move;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Encapsulates the small piece of work of the Move Finder. The Task searches the valid moves for a single, give position on the board.
 */
public class MoveFindingTask implements Callable<Optional<Move>> {
    private static final Logger logger = LogManager.getLogger(MoveFindingTask.class);

    private final Board board;
    private final Player player;
    private final Piece piece;
    private final BoardPosition from;
    private final BoardPosition to;
    private final List<MoveRule> enabledRules;

    public MoveFindingTask(Board board, Player player, Piece piece, BoardPosition from, BoardPosition to, List<MoveRule> enabledRules) {
        this.board = board;
        this.player = player;
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.enabledRules = enabledRules;
    }

    @Override
    public Optional<Move> call() {
        logger.debug("Finding move from {} to {} for {} with {}", this.from, this.to, this.player, this.piece);

        try {
            Move move = Move.from(this.player, this.piece, this.from, this.to);
            MoveRuleValidator.validateMove(this.board, move, enabledRules);
            return Optional.of(move);
        } catch (InvalidMoveException | InvalidBoardPositionException e) {
            logger.debug("Move {} from {} for {} with {} not valid due to", this.from, this.to, this.player, this.piece, e);
            return Optional.empty();
        }
    }
}
