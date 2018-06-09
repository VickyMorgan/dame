package ch.ffhs.ftoop.bridge.dame.game;

import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardDimensionsException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidNumberOfPiecesPerPlayerException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.move.InvalidMoveException;
import ch.ffhs.ftoop.bridge.dame.game.move.Move;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition.from;

public class GameTest {
    private static final Logger logger = LogManager.getLogger(GameTest.class);
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private GameConfig config;

    @Before
    public void setup() throws ConfigurationException {
        this.config = new GameConfig("game-test-no-compulsory-jump.properties");
    }

    @Test
    public void testPlayACompleteGame() throws InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException, PlayersUsingSamePieceColorException, GameAlreadyStartedException, InvalidMoveException, InvalidBoardPositionException {
        Game game = new Game(this.config);
        Player player1 = new Player("Player 1", PieceColor.DARK);
        Player player2 = new Player("Player 2", PieceColor.LIGHT);

        game.start(player1, player2);
        logger.info("Initial Board:\n{}", game.getBoard());

        /*
         * The following code plays a complete game of checkers, where dark wins.
         * The moves have been taken from http://www.bobnewell.net/nucleus/checkers.php?itemid=130
         * The White Dyke is a well known opening strategy in checkers.
         *
         * Moves
         * 11-15 22-18
         * 15-22 25-18
         * 12-16 29-25
         * 9-13 18-14
         * 10-17 21-14
         * 16-20 24-19
         * 6-10 25-21
         * 10-17 21-14
         * 2-6 30-25
         * 6-10 25-21
         * 10-17 21-14
         * 1-6 Black Wins
         */

        // @formatter:off
        // 11-15 22-18
        Move d1 = Move.from(player1, this.pieceFrom(game.getBoard(), from(5, 2)), from(5, 2), from(4, 3));
        this.playMove(game, d1);
        Move l1 = Move.from(player2, this.pieceFrom(game.getBoard(), from(2, 5)), from(2, 5), from(3, 4));
        this.playMove(game, l1);

        // 15-22 25-18
        Move d2 = Move.from(player1, this.pieceFrom(game.getBoard(), from(4, 3)), from(4, 3), from(2, 5));
        this.playMove(game, d2);
        Move l2 = Move.from(player2, this.pieceFrom(game.getBoard(), from(1, 6)), from(1, 6), from(3, 4));
        this.playMove(game, l2);

        // 12-16 29-25
        Move d3 = Move.from(player1, this.pieceFrom(game.getBoard(), from(7, 2)), from(7, 2), from(6, 3));
        this.playMove(game, d3);
        Move l3 = Move.from(player2, this.pieceFrom(game.getBoard(), from(0, 7)), from(0, 7), from(1, 6));
        this.playMove(game, l3);

        // 9-13 18-14
        Move d4 = Move.from(player1, this.pieceFrom(game.getBoard(), from(1, 2)), from(1, 2), from(0, 3));
        this.playMove(game, d4);
        Move l4 = Move.from(player2, this.pieceFrom(game.getBoard(), from(3, 4)), from(3, 4), from(2, 3));
        this.playMove(game, l4);

        // 10-17 21-14
        Move d5 = Move.from(player1, this.pieceFrom(game.getBoard(), from(3, 2)), from(3, 2), from(1, 4));
        this.playMove(game, d5);
        Move l5 = Move.from(player2, this.pieceFrom(game.getBoard(), from(0, 5)), from(0, 5), from(2, 3));
        this.playMove(game, l5);

        // 16-20 24-19
        Move d6 = Move.from(player1, this.pieceFrom(game.getBoard(), from(6, 3)), from(6, 3), from(7, 4));
        this.playMove(game, d6);
        Move l6 = Move.from(player2, this.pieceFrom(game.getBoard(), from(6, 5)), from(6, 5), from(5, 4));
        this.playMove(game, l6);

        // 6-10 25-21
        Move d7 = Move.from(player1, this.pieceFrom(game.getBoard(), from(2, 1)), from(2, 1), from(3, 2));
        this.playMove(game, d7);
        Move l7 = Move.from(player2, this.pieceFrom(game.getBoard(), from(1, 6)), from(1, 6), from(0, 5));
        this.playMove(game, l7);

        // 10-17 21-14
        Move d8 = Move.from(player1, this.pieceFrom(game.getBoard(), from(3, 2)), from(3, 2), from(1, 4));
        this.playMove(game, d8);
        Move l8 = Move.from(player2, this.pieceFrom(game.getBoard(), from(0, 5)), from(0, 5), from(2, 3));
        this.playMove(game, l8);

        // 2-6 30-25
        Move d9 = Move.from(player1, this.pieceFrom(game.getBoard(), from(3, 0)), from(3, 0), from(2, 1));
        this.playMove(game, d9);
        Move l9 = Move.from(player2, this.pieceFrom(game.getBoard(), from(2, 7)), from(2, 7), from(1, 6));
        this.playMove(game, l9);

        // 6-10 25-21
        Move d10 = Move.from(player1, this.pieceFrom(game.getBoard(), from(2, 1)), from(2, 1), from(3, 2));
        this.playMove(game, d10);
        Move l10 = Move.from(player2, this.pieceFrom(game.getBoard(), from(1, 6)), from(1, 6), from(0, 5));
        this.playMove(game, l10);

        // 10-17 21-14
        Move d11 = Move.from(player1, this.pieceFrom(game.getBoard(), from(3, 2)), from(3, 2), from(1, 4));
        this.playMove(game, d11);
        Move l11 = Move.from(player2, this.pieceFrom(game.getBoard(), from(0, 5)), from(0, 5), from(2, 3));
        this.playMove(game, l11);

        // 1-6
        Move d12 = Move.from(player1, this.pieceFrom(game.getBoard(), from(1, 0)), from(1, 0), from(2, 1));
        this.playMove(game, d12);

        // Black wins!
        // @formatter:on
    }

    private Piece pieceFrom(Board board, BoardPosition position) throws InvalidBoardPositionException {
        Tile tile = board.getTile(position);

        if (!tile.isOccupied()) {
            throw new IllegalArgumentException(String.format("The tile at position %s does not have a piece!", tile.getPosition()));
        }

        return tile.getPiece();
    }

    private void playMove(Game game, Move move) throws InvalidMoveException, InvalidBoardPositionException {
        logger.info("Move {} from {} to {}", move.getPiece().getColor(), move.getFrom(), move.getTo());
        game.doMove(move);
        logger.info("Pieces left: DARK: {}, LIGHT: {}, Current Player: {}",
                game.getBoard().getPieces(PieceColor.DARK).size(),
                game.getBoard().getPieces(PieceColor.LIGHT).size(),
                game.getCurrentPlayer());
        logger.info("Board:\n{}", game.getBoard());
    }

    @Test
    public void testVerifyPlayersUsingDifferentColorsWithDifferentColors() throws PlayersUsingSamePieceColorException, GameAlreadyStartedException, InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        Game game = new Game(this.config);
        Player player1 = new Player("Player 1", PieceColor.DARK);
        Player player2 = new Player("Player 2", PieceColor.DARK);

        this.thrown.expect(PlayersUsingSamePieceColorException.class);
        game.start(player1, player2);
    }

    @Test
    public void testVerifyNewGameByStartingGameTwice() throws PlayersUsingSamePieceColorException, GameAlreadyStartedException, InvalidNumberOfPiecesPerPlayerException, InvalidBoardDimensionsException {
        Game game = new Game(this.config);
        Player player1 = new Player("Player 1", PieceColor.DARK);
        Player player2 = new Player("Player 2", PieceColor.LIGHT);

        game.start(player1, player2);

        this.thrown.expect(GameAlreadyStartedException.class);
        game.start(player1, player2);
    }


}
