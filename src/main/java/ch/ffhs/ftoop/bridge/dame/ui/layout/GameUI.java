package ch.ffhs.ftoop.bridge.dame.ui.layout;

import ch.ffhs.ftoop.bridge.dame.game.Game;
import ch.ffhs.ftoop.bridge.dame.game.GameAlreadyStartedException;
import ch.ffhs.ftoop.bridge.dame.game.PlayersUsingSamePieceColorException;
import ch.ffhs.ftoop.bridge.dame.game.actor.Computer;
import ch.ffhs.ftoop.bridge.dame.game.actor.NoValidComputerMoveFoundException;
import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.Board;
import ch.ffhs.ftoop.bridge.dame.game.board.BoardPosition;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardPositionException;
import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.move.InvalidMoveException;
import ch.ffhs.ftoop.bridge.dame.game.move.Move;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveFinder;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveRule;
import ch.ffhs.ftoop.bridge.dame.game.move.MoveRuleValidator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.ALERT_GENERAL_TITLE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_BORDER;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_BORDER_WIDTH;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_CURRENT_PLAYER_DESCRIPTION;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_CURRENT_PLAYER_DESCRIPTION_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_CURRENT_PLAYER_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_SHADOW_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_SHADOW_OFFSET;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_SHADOW_OPACITY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_SHADOW_RADIUS;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_PLAYER_SCORE_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_ATTEMPTED_INVALID_MOVE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_COMPUTER_COULD_NOT_FIND_VALID_TURN;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_GAME_ENDED_PLAYER_LOST_BODY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_GAME_ENDED_PLAYER_WON_BODY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_GAME_ENDED_TITLE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_SELECTED_PIECE_HAS_NO_POSSIBLE_MOVES;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_DARK_NORMAL_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_LIGHT_NORMAL_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.SPACER;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.TILE_PIECE_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.TILE_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.WINDOW_BACKGROUND_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.backgroundFillFor;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.colorFor;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.imageFromClassLoader;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.showConfirmation;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.showErrorAlert;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.showInformationAlert;
import static com.google.common.base.Preconditions.checkNotNull;

public class GameUI extends BorderPane {
    private static final Logger logger = LogManager.getLogger(GameUI.class);

    private final Game game;
    private final Player player;
    private final Computer computer;
    private final boolean showDebugAids;

    private Label currentPlayerLabel;
    private Label darkScoreLabel;
    private Label lightScoreLabel;

    private GridPane gameBoard;

    private Tile selectedTile;

    public GameUI(Game game, Player player, Computer computer, boolean showDebugAids) {
        checkNotNull(game);
        checkNotNull(player);
        checkNotNull(computer);

        this.game = game;
        this.player = player;
        this.computer = computer;
        this.showDebugAids = showDebugAids;
        this.selectedTile = null;

        this.setup();
        this.registerGameEventObservers();
    }

    private void setup() {
        this.setPadding(new Insets(2 * SPACER, 4 * SPACER, 4 * SPACER, 4 * SPACER));
        this.setBackground(new Background(backgroundFillFor(WINDOW_BACKGROUND_COLOR)));

        this.currentPlayerLabel = this.createCurrentPlayerLabel();
        this.darkScoreLabel = this.createScoreLabel();
        this.lightScoreLabel = this.createScoreLabel();
        this.gameBoard = this.createGameBoard(this.game.getBoard());

        this.setTop(this.createInformationArea());
        this.setCenter(this.gameBoard);
    }

    private void registerGameEventObservers() {
        this.game.registerGameStartedObserver(this::handleGameStarted);
        this.game.registerTurnFinishedObserver(this::handleTurnFinished);
        this.game.registerGameEndedObserver(this::handleGameEnded);
    }

    private Label createCurrentPlayerLabel() {
        Label label = new Label("-");
        label.setFont(Font.font(GAME_CURRENT_PLAYER_TEXT_SIZE));
        label.setTextAlignment(TextAlignment.CENTER);

        return label;
    }

    private Label createScoreLabel() {
        Label label = new Label("0");
        label.setFont(Font.font(GAME_PLAYER_SCORE_TEXT_SIZE));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(TILE_PIECE_SIZE);
        label.setPrefHeight(TILE_PIECE_SIZE);
        label.setTextFill(colorFor(GAME_PLAYER_SCORE_TEXT_COLOR));

        DropShadow shadow = new DropShadow();
        shadow.setColor(colorFor(GAME_PLAYER_SCORE_TEXT_SHADOW_COLOR, GAME_PLAYER_SCORE_TEXT_SHADOW_OPACITY));
        shadow.setRadius(GAME_PLAYER_SCORE_TEXT_SHADOW_RADIUS);
        shadow.setOffsetY(GAME_PLAYER_SCORE_TEXT_SHADOW_OFFSET);
        shadow.setOffsetX(0);
        shadow.setBlurType(BlurType.GAUSSIAN);

        label.setEffect(shadow);

        return label;
    }

    private GridPane createGameBoard(Board board) {
        GridPane gameBoard = new GridPane();
        gameBoard.setPrefWidth(board.getNumberOfRowsAndColumns() * TILE_SIZE);
        gameBoard.setPrefHeight(board.getNumberOfRowsAndColumns() * TILE_SIZE);
        gameBoard.setBorder(new Border(
                new BorderStroke(colorFor(GAME_BOARD_BORDER),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(GAME_BOARD_BORDER_WIDTH))
        ));

        for (Tile tile : board.getTiles()) {
            TileUI tileUI = new TileUI(tile, false);
            tileUI.setPrefWidth(TILE_SIZE);
            tileUI.setPrefHeight(TILE_SIZE);
            tileUI.setShowDebugAids(this.showDebugAids);
            tileUI.registerTileClickedHandler(this::handleTileClicked);

            gameBoard.add(tileUI, tile.getPosition().getCol(), tile.getPosition().getRow());
        }

        return gameBoard;
    }

    private BorderPane createInformationArea() {
        BorderPane informationArea = new BorderPane();
        informationArea.setPadding(new Insets(0, 0, 2 * SPACER, 0));

        // The current player information area
        VBox currentPlayerArea = this.createCurrentPlayerArea();

        // Player scores
        StackPane lightScoreArea = this.createPlayerScoreArea(PIECE_LIGHT_NORMAL_FILENAME, Pos.CENTER_LEFT);
        lightScoreArea.getChildren().add(this.lightScoreLabel);

        StackPane darkScoreArea = this.createPlayerScoreArea(PIECE_DARK_NORMAL_FILENAME, Pos.CENTER_RIGHT);
        darkScoreArea.getChildren().add(this.darkScoreLabel);

        informationArea.setCenter(currentPlayerArea);
        informationArea.setLeft(lightScoreArea);
        informationArea.setRight(darkScoreArea);

        return informationArea;
    }

    private VBox createCurrentPlayerArea() {
        VBox currentPlayerArea = new VBox();
        currentPlayerArea.setAlignment(Pos.CENTER);

        Label currentPlayerDescriptionLabel = new Label(GAME_CURRENT_PLAYER_DESCRIPTION);
        currentPlayerDescriptionLabel.setFont(Font.font(GAME_CURRENT_PLAYER_DESCRIPTION_TEXT_SIZE));

        currentPlayerArea.getChildren().addAll(currentPlayerDescriptionLabel, this.currentPlayerLabel);
        return currentPlayerArea;
    }

    private StackPane createPlayerScoreArea(String pieceFileName, Pos alignment) {
        StackPane area = new StackPane();
        area.setAlignment(alignment);
        area.setPrefWidth(TILE_PIECE_SIZE);
        area.setPrefHeight(TILE_PIECE_SIZE);

        ImageView pieceImage = new ImageView(imageFromClassLoader(this.getClass(), pieceFileName));
        pieceImage.setFitWidth(TILE_PIECE_SIZE);
        pieceImage.setFitHeight(TILE_PIECE_SIZE);

        area.getChildren().addAll(pieceImage);
        return area;
    }

    public void startGame() throws PlayersUsingSamePieceColorException, GameAlreadyStartedException {
        this.game.start(this.player, this.computer);

    }

    private void handleGameStarted() {
        logger.info("Game started");
        this.render();
    }

    private void render() {
        logger.debug("Rendering UI");
        Player currentPlayer = this.game.getCurrentPlayer();
        this.currentPlayerLabel.setText(currentPlayer.getName());

        this.renderGameBoard(currentPlayer);
        this.renderScores();
    }

    private void renderGameBoard(Player currentPlayer) {
        this.renderGameBoard(currentPlayer, new ArrayList<>());
    }

    private void renderScores() {
        if (this.game.getPlayer1().getColor() == PieceColor.DARK) {
            this.darkScoreLabel.setText(String.valueOf(this.game.getPlayer1().getScore()));
            this.lightScoreLabel.setText(String.valueOf(this.game.getPlayer2().getScore()));
        } else {
            this.lightScoreLabel.setText(String.valueOf(this.game.getPlayer1().getScore()));
            this.darkScoreLabel.setText(String.valueOf(this.game.getPlayer2().getScore()));
        }
    }

    private void renderGameBoard(Player currentPlayer, List<BoardPosition> potentialToPositions) {
        this.gameBoard.getChildren().stream()
                .filter(node -> (node instanceof TileUI))
                .map(node -> (TileUI) node)
                .forEach(tileUI -> {
                    Tile tile = tileUI.getTile();
                    boolean isInteractable = MoveRuleValidator.isAbleToHoldPiece(tile)
                            && (!tile.isOccupied() || this.tileContainsPlayerPiece(tile, currentPlayer));
                    boolean isMoveCandidate = potentialToPositions.contains(tile.getPosition());
                    boolean isSelected = tile.isOccupied() && tile.equals(this.selectedTile);

                    // Mark the tile as interactable, if it is free or contains a piece of the player
                    tileUI.setInteractable(isInteractable);
                    tileUI.setMoveCandidate(isMoveCandidate);
                    tileUI.setSelected(isSelected);
                    tileUI.render();
                });
    }

    private boolean tileContainsPlayerPiece(Tile tile, Player player) {
        return tile.isOccupied() && tile.getPiece().getColor() == player.getColor();
    }

    private void handleTurnFinished(Player newCurrentPlayer) {
        this.selectedTile = null;
        this.render();

        if (this.isComputer(newCurrentPlayer)) {
            this.handleComputerTurn();
        }
    }

    private boolean isComputer(Player player) {
        return (player instanceof Computer);
    }

    private void handleComputerTurn() {
        try {
            logger.info("Computer is finding next move");
            List<MoveRule> enabledRules = this.game.getConfig().getEnabledRules();
            Move move = this.computer.findNextMove(this.game.getBoard(), enabledRules);

            logger.info("Computer selected move {}", move);
            this.game.doMove(move);

        } catch (NoValidComputerMoveFoundException | InvalidBoardPositionException | InvalidMoveException e) {
            logger.error("Computer could not find a valid move", e);
            showConfirmation(ALERT_GENERAL_TITLE, MESSAGE_COMPUTER_COULD_NOT_FIND_VALID_TURN, this::restart, this::quit);
        }
    }

    private void handleTileClicked(Tile tile) {
        logger.info("Tile clicked: {}", tile);
        logger.debug("Selected tile is {}", this.selectedTile);

        Player currentPlayer = this.game.getCurrentPlayer();

        // Player must select a tile from which to do the move
        if (this.selectedTile == null) {
            if (!tile.isOccupied()) {
                logger.debug("Selected an empty tile, player has to choose again");
                return;
            }

            this.selectedTile = tile;
            logger.debug("Selected tile is {}", this.selectedTile);

            Board board = this.game.getBoard();
            Piece piece = this.selectedTile.getPiece();
            BoardPosition from = this.selectedTile.getPosition();
            List<MoveRule> enabledRules = this.game.getConfig().getEnabledRules();

            /*
             * Find all candidates for the "to" position and render them on the board
             */
            List<BoardPosition> potentialToPositions = MoveFinder.findValidMoves(board, currentPlayer, piece, from, enabledRules).stream()
                    .map(Move::getTo)
                    .collect(Collectors.toList());
            logger.debug("Player has {} potential positions to move piece to", potentialToPositions.size());

            // If we cannot move with this piece, it cannot be selected and the player needs to select a new one
            if (potentialToPositions.size() == 0) {
                logger.warn("Player selected piece with no possible positions to move from, must select new one");
                showInformationAlert(ALERT_GENERAL_TITLE, MESSAGE_SELECTED_PIECE_HAS_NO_POSSIBLE_MOVES);

                this.selectedTile = null;
                this.render();
                return;
            }

            this.renderGameBoard(currentPlayer, potentialToPositions);

            // Player had chosen from which tile to move and now to which to move to
        } else {
            try {
                Piece piece = this.selectedTile.getPiece();
                BoardPosition from = this.selectedTile.getPosition();
                BoardPosition to = tile.getPosition();

                Move move = Move.from(currentPlayer, piece, from, to);
                this.game.doMove(move);
            } catch (InvalidBoardPositionException e) {
                logger.error("Could not do move as invalid board position has been selected", e);
            } catch (InvalidMoveException e) {
                logger.error("Attempted to do an invalid move", e);
                showErrorAlert(ALERT_GENERAL_TITLE, MESSAGE_ATTEMPTED_INVALID_MOVE);
            }
        }
    }

    private void handleGameEnded(Player winner, Player loser) {
        logger.info("Game has ended. Winner: {}, Loser: {}", winner, loser);

        String confirmationText;
        if (this.isComputer(winner)) {
            confirmationText = String.format(MESSAGE_GAME_ENDED_PLAYER_LOST_BODY, winner.getScore(), loser.getScore());
        } else {
            confirmationText = String.format(MESSAGE_GAME_ENDED_PLAYER_WON_BODY, winner.getScore(), loser.getScore());
        }

        showConfirmation(MESSAGE_GAME_ENDED_TITLE, confirmationText, () -> {
            logger.info("Player has requested a restart");
            this.restart();
        }, this::quit);
    }

    private void restart() {
        this.selectedTile = null;
        this.game.restart();

        Platform.runLater(this::render);
        logger.info("Game restarted");
    }

    private void quit() {
        logger.info("Player has quit the game");
        Platform.exit();
    }

}
