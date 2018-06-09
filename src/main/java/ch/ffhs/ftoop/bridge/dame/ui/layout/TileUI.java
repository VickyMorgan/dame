package ch.ffhs.ftoop.bridge.dame.ui.layout;

import ch.ffhs.ftoop.bridge.dame.game.board.Piece;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.game.board.Tile;
import ch.ffhs.ftoop.bridge.dame.game.board.TileColor;
import ch.ffhs.ftoop.bridge.dame.ui.UIConstants;
import ch.ffhs.ftoop.bridge.dame.ui.handler.TileClickedHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_TILE_DEBUG_BACKGROUND_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_TILE_DEBUG_OPACITY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_TILE_DEBUG_TEXT_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.GAME_BOARD_TILE_DEBUG_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_DARK_KING_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_DARK_NORMAL_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_LIGHT_KING_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_LIGHT_NORMAL_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_SELECTED_MARKER_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.PIECE_VALID_MOVE_MARKER_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.SPACER;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.TILE_BACKGROUND_COLOR_DARK;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.TILE_BACKGROUND_COLOR_LIGHT;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.TILE_PIECE_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.backgroundFillFor;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.colorFor;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.imageFromClassLoader;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.transparentBackgroundFill;
import static com.google.common.base.Preconditions.checkNotNull;

public class TileUI extends StackPane {
    private static final Logger logger = LogManager.getLogger(TileUI.class);

    private final Tile tile;
    private final List<TileClickedHandler> tileClickedHandlers = new ArrayList<>();
    private boolean interactable;
    private boolean moveCandidate = false;
    private boolean selected = false;
    private boolean showDebugAids = false;

    private Label positionLabel;
    private Label interactableLabel;

    private Button pieceButton;
    private ImageView moveCandidateIndicator;
    private ImageView selectedIndicator;

    public TileUI(Tile tile, boolean interactable) {
        checkNotNull(tile);
        this.tile = tile;
        this.interactable = interactable;

        this.setup();
    }

    private void setup() {
        this.setMinWidth(UIConstants.TILE_SIZE);
        this.setMinHeight(UIConstants.TILE_SIZE);
        this.setAlignment(Pos.CENTER);

        this.pieceButton = this.createPieceButton();
        this.selectedIndicator = this.createSelectedIndicator();
        this.moveCandidateIndicator = this.createMoveCandidateIndicator();

        this.getChildren().addAll(moveCandidateIndicator, selectedIndicator, pieceButton);

        // Debug aids
        this.positionLabel = this.createDebugLabel();
        this.positionLabel.setVisible(this.showDebugAids);
        setAlignment(this.positionLabel, Pos.TOP_LEFT);

        this.interactableLabel = this.createDebugLabel();
        this.interactableLabel.setVisible(this.showDebugAids);
        setAlignment(this.interactableLabel, Pos.TOP_RIGHT);

        this.getChildren().addAll(this.positionLabel, this.interactableLabel);

        this.render();
    }

    private Button createPieceButton() {
        Button button = new Button();
        button.setMinWidth(TILE_PIECE_SIZE);
        button.setMinHeight(TILE_PIECE_SIZE);
        button.setAlignment(Pos.CENTER);
        button.setOnMouseClicked(event -> this.handlePieceButtonClicked());
        button.setBackground(new Background(transparentBackgroundFill()));

        return button;
    }

    private ImageView createMoveCandidateIndicator() {
        return this.createIndicator(PIECE_VALID_MOVE_MARKER_FILENAME);
    }

    private ImageView createSelectedIndicator() {
        return this.createIndicator(PIECE_SELECTED_MARKER_FILENAME);
    }

    private ImageView createIndicator(String filename) {
        ImageView indicator = new ImageView(imageFromClassLoader(this.getClass(), filename));
        indicator.setFitWidth(TILE_PIECE_SIZE);
        indicator.setFitHeight(TILE_PIECE_SIZE);
        indicator.setVisible(false);

        return indicator;
    }

    private Label createDebugLabel() {
        Label label = new Label();
        label.setPadding(new Insets(SPACER / 4));
        label.setFont(Font.font(GAME_BOARD_TILE_DEBUG_TEXT_SIZE));
        label.setBackground(new Background(backgroundFillFor(GAME_BOARD_TILE_DEBUG_BACKGROUND_COLOR)));
        label.setTextFill(colorFor(GAME_BOARD_TILE_DEBUG_TEXT_COLOR));
        label.setOpacity(GAME_BOARD_TILE_DEBUG_OPACITY);

        return label;
    }

    public void render() {
        logger.debug("Rendering tile {}", this.tile);

        TileColor color = this.tile.getColor();
        String backgroundColor = (color == TileColor.DARK) ? TILE_BACKGROUND_COLOR_DARK : TILE_BACKGROUND_COLOR_LIGHT;
        this.setBackground(new Background(backgroundFillFor(backgroundColor)));

        if (this.tile.isOccupied()) {
            Piece piece = this.tile.getPiece();
            this.pieceButton.setGraphic(this.getPieceImage(piece));
        } else {
            this.pieceButton.setGraphic(null);
        }

        this.moveCandidateIndicator.setVisible(moveCandidate);
        this.selectedIndicator.setVisible(selected);

        // Debug aids
        this.positionLabel.setText(String.format("%d / %d", this.tile.getPosition().getCol(), this.tile.getPosition().getRow()));
        this.positionLabel.setVisible(this.showDebugAids);

        this.interactableLabel.setText(this.interactable ? "✓" : "⚠");
        this.interactableLabel.setVisible(this.showDebugAids);
    }

    private void handlePieceButtonClicked() {
        /*
         * If the tile is not interactable, eg. when it contains a piece which
         * does not belong to the player, ignore the clicks.
         */
        if (!interactable) {
            return;
        }

        this.notifyTileClickedHandlers();
    }

    private ImageView getPieceImage(Piece piece) {
        ImageView view = new ImageView(imageFromClassLoader(this.getClass(), this.getPieceImageFileName(piece)));
        view.setFitWidth(TILE_PIECE_SIZE);
        view.setFitHeight(TILE_PIECE_SIZE);
        view.setSmooth(true);

        return view;
    }

    private void notifyTileClickedHandlers() {
        this.tileClickedHandlers.forEach(handler -> handler.onTileControlClicked(this.tile));
    }

    private String getPieceImageFileName(Piece piece) {
        if (piece.getColor() == PieceColor.DARK) {
            return (piece.isKing()) ? PIECE_DARK_KING_FILENAME : PIECE_DARK_NORMAL_FILENAME;
        }

        return (piece.isKing()) ? PIECE_LIGHT_KING_FILENAME : PIECE_LIGHT_NORMAL_FILENAME;
    }


    public void registerTileClickedHandler(TileClickedHandler handler) {
        checkNotNull(handler);
        tileClickedHandlers.add(handler);
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public void setMoveCandidate(boolean moveCandidate) {
        this.moveCandidate = moveCandidate;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setShowDebugAids(boolean showDebugAids) {
        this.showDebugAids = showDebugAids;
    }

    public Tile getTile() {
        return this.tile;
    }
}
