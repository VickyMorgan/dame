package ch.ffhs.ftoop.bridge.dame;

import ch.ffhs.ftoop.bridge.dame.game.Game;
import ch.ffhs.ftoop.bridge.dame.game.GameAlreadyStartedException;
import ch.ffhs.ftoop.bridge.dame.game.GameConfig;
import ch.ffhs.ftoop.bridge.dame.game.PlayersUsingSamePieceColorException;
import ch.ffhs.ftoop.bridge.dame.game.actor.Computer;
import ch.ffhs.ftoop.bridge.dame.game.actor.Player;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidBoardDimensionsException;
import ch.ffhs.ftoop.bridge.dame.game.board.InvalidNumberOfPiecesPerPlayerException;
import ch.ffhs.ftoop.bridge.dame.game.board.PieceColor;
import ch.ffhs.ftoop.bridge.dame.ui.UIConstants;
import ch.ffhs.ftoop.bridge.dame.ui.layout.GameUI;
import ch.ffhs.ftoop.bridge.dame.ui.layout.MenuUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.ALERT_UNCAUGHT_EXCEPTION_BODY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.ALERT_UNCAUGHT_EXCEPTION_TITLE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.APPLICATION_ICON_FILENAME;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_COULD_NOT_START_GAME_BODY;
import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.MESSAGE_COULD_NOT_START_GAME_TITLE;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.imageFromClassLoader;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.showErrorAlert;
import static ch.ffhs.ftoop.bridge.dame.ui.UIUtils.showException;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private Player player;
    private Computer computer;

    private GameConfig config;
    private Game game;

    public Main() {
        try {
            this.config = new GameConfig();
            this.game = new Game(config);
            this.player = new Player(config.getHumanPlayerName(), PieceColor.DARK);
            this.computer = new Computer(config.getComputerPlayerName(), PieceColor.LIGHT, config.getComputerMaxNumberOfAttemptsWhenFindingValidMove());
        } catch (ConfigurationException | InvalidNumberOfPiecesPerPlayerException | InvalidBoardDimensionsException e) {
            logger.error("Could not start game", e);
            showErrorAlert(MESSAGE_COULD_NOT_START_GAME_TITLE, MESSAGE_COULD_NOT_START_GAME_BODY, Platform::exit);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Register general exception handler
        // https://stackoverflow.com/questions/26361559/general-exception-handling-in-javafx-8
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            logger.fatal("Uncaught exception thrown", e);
            showException(ALERT_UNCAUGHT_EXCEPTION_TITLE, ALERT_UNCAUGHT_EXCEPTION_BODY, e, () -> {
                logger.info("Quitting application");
                Platform.exit();
            });
        });

        GameUI gameUI = new GameUI(this.game, this.player, this.computer, this.config.isShowDebugAids());
        Scene gameScene = new Scene(gameUI);

        MenuUI menuUI = new MenuUI();
        menuUI.registerGameStartClickedHandler(() -> this.handleGameStartClicked(gameUI, gameScene, primaryStage));
        Scene menuScene = new Scene(menuUI);

        primaryStage.setTitle(UIConstants.APPLICATION_NAME);
        primaryStage.getIcons().add(imageFromClassLoader(this.getClass(), APPLICATION_ICON_FILENAME));
        primaryStage.setOnCloseRequest(e -> {
            // Run in main thread
            Platform.runLater(() -> {
                logger.info("Exiting application");
                Platform.exit();
            });
        }); // Exit when main window is closed
        primaryStage.setResizable(false);
        primaryStage.setScene(menuScene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        logger.info("Application started!");

        if (!this.config.isShowMenuScreen()) {
            this.handleGameStartClicked(gameUI, gameScene, primaryStage);
        }
    }

    private void handleGameStartClicked(GameUI gameUI, Scene gameScene, Stage primaryStage) {
        try {
            gameUI.startGame();

            logger.info("Switching to game scene...");
            primaryStage.setScene(gameScene);
            primaryStage.centerOnScreen();
        } catch (PlayersUsingSamePieceColorException | GameAlreadyStartedException e) {
            logger.error("Could not start game", e);
            showErrorAlert(MESSAGE_COULD_NOT_START_GAME_TITLE, MESSAGE_COULD_NOT_START_GAME_BODY, Platform::exit);
        }

    }
}
