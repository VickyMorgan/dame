package ch.ffhs.ftoop.bridge.dame.game;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardDimensionsException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidNumberOfPiecesPerPlayerException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.move.InvalidMoveException;
import ch.ffhs.ftoop.bridge.dame.game.move.Move;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveFinder;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveRuleValidator;
import ch.ffhs.ftoop.bridge.dame.game.observer.GameEndedObserver;
import ch.ffhs.ftoop.bridge.dame.game.observer.GameStartedObserver;
import ch.ffhs.ftoop.bridge.dame.game.observer.TurnFinishedObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the game engine, that implements the "Dame"/"Checkers" game with all it's rules. It encapsulates
 * all the games logic and flow, stores it's state and encapsulates all the interaction between the player and the board and the pieces.
 */
public class Game {
    private static final Logger logger = LogManager.getLogger(Game.class);

    private final GameConfig config;
    private final Board board;
    private final List<GameStartedObserver> gameStartedObservers = new ArrayList<>();
    private final List<GameEndedObserver> gameEndedObservers = new ArrayList<>();
    private final List<TurnFinishedObserver> turnFinishedObservers = new ArrayList<>();
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    /**
     * Setups a new game with the given config.
     *
     * @param config The games config.
     * @throws InvalidNumberOfPiecesPerPlayerException Thrown when an invalid number of pieces per player has been chosen.
     * @throws InvalidBoardDimensionsException         Thrown when an invalid dimension for the board has ben chosen.
     */
    public Game(GameConfig config) throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        this.config = config;
        this.board = new Board(this.config.getNumberOfRowsAndColumnsOfBoard());
        this.board.populateWithInitialPieces(this.config.getNumberOfPiecesPerPlayer());
    }

    /**
     * Starts a new game between two players
     *
     * @param player1 The first player.
     * @param player2 The second player.
     * @throws PlayersUsingSamePieceColorException Thrown when both players are using the same color of pieces.
     * @throws GameAlreadyStartedException         Thrown when the game has already been started and is currently ongoing.
     */
    public void start(Player player1, Player player2) throws PlayersUsingSamePieceColorException, GameAlreadyStartedException {
        this.verifyNewGame();
        this.verifyPlayersUsingDifferentColors(player1, player2);

        logger.info("Starting game with {} as Player 1 and {} as Player 2", player1, player2);

        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = this.findPlayerWithColor(PieceColor.DARK); // Dark starts

        this.notifyGameStartedObservers();
    }

    private void verifyNewGame() throws GameAlreadyStartedException {
        if (this.currentPlayer != null) {
            throw new GameAlreadyStartedException("The game was already started");
        }
    }

    private void verifyPlayersUsingDifferentColors(Player player1, Player player2) throws PlayersUsingSamePieceColorException {
        if (player1.getColor() == player2.getColor()) {
            throw new PlayersUsingSamePieceColorException(String.format("Both players are using %s as their piece color. Players must use different colors!", player1.getColor()));
        }
    }

    private Player findPlayerWithColor(PieceColor color) {
        if (this.player1.getColor() == color) {
            return player1;
        }

        return player2;
    }

    private void notifyGameStartedObservers() {
        this.gameStartedObservers.forEach(GameStartedObserver::onGameStarted);
    }

    /**
     * Restarts the game.
     */
    public void restart() {
        this.board.clear();

        try {
            this.board.populateWithInitialPieces(this.config.getNumberOfPiecesPerPlayer());
        } catch (InvalidNumberOfPiecesPerPlayerException e) {
            logger.error("Could not populate the board with initial pieces", e);
        }

        logger.info("Restarting game with {} as Player 1 and {} as Player 2", this.player1, this.player2);

        this.player1.setScore(0);
        this.player2.setScore(0);
        this.currentPlayer = this.findPlayerWithColor(PieceColor.DARK); // Dark starts again

        this.notifyGameStartedObservers();
    }

    /**
     * Plays a move of the game. The move is checked against all the games rules.
     *
     * @param move The move to be played in the game.
     * @throws InvalidBoardPositionException Thrown when an invalid position on the board has been used.
     * @throws InvalidMoveException          Thrown when the move violates any of the enabled game's rules.
     */
    public void doMove(Move move) throws InvalidBoardPositionException, InvalidMoveException {
        MoveRuleValidator.validateMove(this.board, move, this.config.getEnabledRules());
        logger.debug("{} playing {}", this.currentPlayer, move);

        // Find all the opponents tiles between the current position and the new position
        // to find out if the move captures ("kills") an opponents piece
        List<Tile> opponentTiles = this.board.findTilesBetween(move.getFrom(), move.getTo()).stream()
                .filter(Tile::isOccupied)
                .filter(t -> t.getPiece().getColor() != this.currentPlayer.getColor())
                .collect(Collectors.toList());

        /*
         * Moving a piece follows the following 4 steps:
         * 1) Remove current piece from the board -- the player has it in his hand so to say
         * 2) Check if the piece will become a king in it's target position
         * 2.1) if so, mark it as a king
         * 3) Place the piece down in it's new location
         * 4) Remove the opponents pieces, that have been captured by the move
         */
        // Remove the existing piece
        this.board.removePiece(move.getFrom());

        // Check, if player has reach "Kings Row", if so, it will become
        // a king in it's new position
        Piece piece = move.getPiece();
        if (this.board.isKingsRow(move.getTo(), move.getPiece().getColor())) {
            logger.info("{} has become king!", piece);
            piece.setKing(true);
        }

        // Set the piece down in it's target location
        this.board.setPiece(piece, move.getTo());

        // If opponent tiles were jumped over, pieces have been captured and will be removed from the game
        if (opponentTiles.size() > 0) {
            opponentTiles.forEach(tile -> {
                try {
                    this.board.removePiece(tile.getPosition());
                    this.currentPlayer.increaseScore();

                    logger.info("{} won piece on {}", tile.getPosition());
                } catch (InvalidBoardPositionException e) {
                    logger.error("Could not remove opponent's piece", e);
                }
            });
        }

        // Finish the turn
        if (!this.isGameOver()) {
            this.finishTurn();
        } else {
            this.wrapUpGame();
        }
    }

    private boolean isGameOver() {
        // If there are no pieces left, the game is over
        if (this.board.getPieces(PieceColor.DARK).size() == 0 || this.board.getPieces(PieceColor.LIGHT).size() == 0) {
            logger.info("Game finished as no pieces left for either player");
            return true;
        }

        /*
         * If compulsory jumps are enabled, the game can end sooner, when no piece can make any move.
         * So we need to check each occupied tile if it can make a move
         */
        if (this.config.isCompulsoryJumpRuleEnabled()) {
            boolean stillPossibleToMakeMoves = this.board.getTiles().stream()
                    .filter(Tile::isOccupied)
                    .anyMatch(tile -> {
                        Player owner = this.findPlayerWithColor(tile.getPiece().getColor());
                        List<Move> movesForTile = MoveFinder.findValidMoves(this.board, owner, tile.getPiece(), tile.getPosition(), this.config.getEnabledRules());
                        return movesForTile.size() > 0;
                    });

            if (!stillPossibleToMakeMoves) {
                logger.info("Game finished as both players cannot make any more moves");
                return true;
            }
        }

        return false;
    }

    private void finishTurn() {
        this.currentPlayer = this.findOpponent(this.currentPlayer);

        logger.info("Turn finished, current player is {}", this.currentPlayer);
        this.notifyTurnFinishedObservers();
    }

    private void wrapUpGame() {
        Player winner = this.currentPlayer;
        Player loser = this.findOpponent(winner);

        this.notifyGameEndedObservers(winner, loser);
    }

    private Player findOpponent(Player player) {
        if (player == this.player1) {
            return this.player2;
        }

        return this.player1;
    }

    private void notifyTurnFinishedObservers() {
        this.turnFinishedObservers.forEach(observer -> observer.onTurnFinished(this.currentPlayer));
    }

    private void notifyGameEndedObservers(Player winner, Player loser) {
        this.gameEndedObservers.forEach(observer -> observer.onGameEnded(winner, loser));
    }

    /**
     * Registers an observer for when the game has been started.
     *
     * @param observer The observer.
     */
    public void registerGameStartedObserver(GameStartedObserver observer) {
        checkNotNull(observer);
        this.gameStartedObservers.add(observer);
    }

    /**
     * Registers an observer for when the game has ended.
     *
     * @param observer The observer.
     */
    public void registerGameEndedObserver(GameEndedObserver observer) {
        checkNotNull(observer);
        this.gameEndedObservers.add(observer);
    }

    /**
     * Registers an observer for when the turn has finished.
     *
     * @param observer The observer.
     */
    public void registerTurnFinishedObserver(TurnFinishedObserver observer) {
        checkNotNull(observer);
        this.turnFinishedObservers.add(observer);
    }

    public GameConfig getConfig() {
        return config;
    }

    public Board getBoard() {
        return this.board;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}