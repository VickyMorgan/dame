package ch.ffhs.ftoop.bridge.dame.ui.layout;

import ch.ffhs.ftoop.bridge.dame.ui.handler.GameStartClickedHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.APPLICATION_ABOUT;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.APPLICATION_ICON_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.APPLICATION_NAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_ABOUT_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_LOGO_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_NAME_TEXT_SIZE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_BACKGROUND_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_BACKGROUND_COLOR_HOVER;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_HEIGHT;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_TEXT;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_TEXT_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MENU_START_BUTTON_WIDTH;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.SPACER;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.WINDOW_BACKGROUND_COLOR;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.backgroundFillFor;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.imageFromClassLoader;
import static com.google.common.base.Preconditions.checkNotNull;

public class MenuUI extends VBox {

    private final List<GameStartClickedHandler> gameStartClickedHandlers = new ArrayList<>();

    public MenuUI() {
        this.setup();
    }

    private void setup() {
        this.setSpacing(SPACER);
        this.setPadding(new Insets(4 * SPACER));
        this.setBackground(new Background(backgroundFillFor(WINDOW_BACKGROUND_COLOR)));
        this.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(APPLICATION_NAME);
        nameLabel.setFont(Font.font(MENU_NAME_TEXT_SIZE));

        ImageView logo = new ImageView(imageFromClassLoader(this.getClass(), APPLICATION_ICON_FILENAME));
        logo.setFitHeight(MENU_LOGO_SIZE);
        logo.setFitWidth(MENU_LOGO_SIZE);

        Button startButton = new Button(MENU_START_BUTTON_TEXT);
        startButton.setPadding(new Insets(SPACER));
        startButton.setPrefSize(MENU_START_BUTTON_WIDTH, MENU_START_BUTTON_HEIGHT);
        startButton.setTextFill(Color.web(MENU_START_BUTTON_TEXT_COLOR));
        startButton.setBackground(new Background(backgroundFillFor(MENU_START_BUTTON_BACKGROUND_COLOR)));
        startButton.setOnMouseEntered(e -> startButton.setBackground(new Background(backgroundFillFor(MENU_START_BUTTON_BACKGROUND_COLOR_HOVER))));
        startButton.setOnMouseExited(e -> startButton.setBackground(new Background(backgroundFillFor(MENU_START_BUTTON_BACKGROUND_COLOR))));
        startButton.setOnMouseClicked(event -> this.notifyGameStartClickedHandlers());
        setMargin(startButton, new Insets(2 * SPACER, 0, 0, 0));

        Label aboutLabel = new Label(APPLICATION_ABOUT);
        aboutLabel.setWrapText(true);
        aboutLabel.setFont(Font.font(MENU_ABOUT_TEXT_SIZE));
        aboutLabel.setTextAlignment(TextAlignment.CENTER);

        this.getChildren().addAll(logo, nameLabel, aboutLabel, startButton);
    }

    public void registerGameStartClickedHandler(GameStartClickedHandler handler) {
        checkNotNull(handler);
        gameStartClickedHandlers.add(handler);
    }

    private void notifyGameStartClickedHandlers() {
        this.gameStartClickedHandlers.forEach(GameStartClickedHandler::onGameStartClicked);
    }

}
