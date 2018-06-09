package ch.ffhs.ftoop.bridge.dame.game;

import ch.ffhs.ftoop.bridge.dame.game.move.MoveRule;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

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

/**
 * Models the games configuration. It includes configuration for the board, and the players, but most importantly all
 * of the enabled game rules.
 */
public class GameConfig {
    private static final Logger logger = LogManager.getLogger(GameConfig.class);
    private static final String DEFAULT_CONFIG_FILENAME = "game.properties";

    private int numberOfRowsAndColumnsOfBoard = 8;
    private int numberOfPiecesPerPlayer = 12;

    private String humanPlayerName = "Spieler";
    private String computerPlayerName = "Computer";
    private int computerMaxNumberOfAttemptsWhenFindingValidMove = 10;

    private boolean showMenuScreen = true;
    private boolean showDebugAids = false;

    private List<MoveRule> enabledRules = Arrays.asList(
            PLAYER_MUST_PLAY_OWN_PIECES,
            PLAYER_MUST_PLAY_PIECE,
            PIECE_MUST_MOVE,
            PIECE_MUST_STILL_BE_ON_BOARD,
            PIECE_MUST_BE_PLACED_ON_DARK_TILE,
            PIECE_MUST_BE_PLACED_ON_FREE_TILE,
            PIECE_MUST_MOVE_FORWARD,
            PIECE_MUST_MOVE_DIAGONALLY,
            PIECE_MUST_MOVE_CORRECT_DISTANCE,
            PIECE_MUST_ONLY_JUMP_OVER_OPPONENT_PIECES,
            PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY
    );

    public GameConfig() throws ConfigurationException {
        this(DEFAULT_CONFIG_FILENAME);
    }

    public GameConfig(String configFileName) throws ConfigurationException {
        this.loadConfiguration(configFileName);
    }

    private void loadConfiguration(String filename) throws ConfigurationException {
        logger.info("Loading game configuration {}", filename);

        Configurations configs = new Configurations();
        PropertiesConfiguration config = configs.properties(new File(filename));
        config.setListDelimiterHandler(new DefaultListDelimiterHandler('/'));

        this.numberOfRowsAndColumnsOfBoard = config.getInt("board.rows.cols", this.numberOfRowsAndColumnsOfBoard);
        this.numberOfPiecesPerPlayer = config.getInt("board.player.pieces", this.numberOfPiecesPerPlayer);
        this.humanPlayerName = config.getString("player.human.name", this.humanPlayerName);
        this.computerPlayerName = config.getString("player.computer.name", this.computerPlayerName);
        this.computerMaxNumberOfAttemptsWhenFindingValidMove = config.getInt("player.computer.max.attempts.finding.move", this.computerMaxNumberOfAttemptsWhenFindingValidMove);

        this.enabledRules = config.getList(MoveRule.class, "game.rules", this.enabledRules);
        logger.info("Enabled rules: {}", this.enabledRules);

        this.showMenuScreen = config.getBoolean("ui.show.menu.screen", this.showMenuScreen);
        this.showDebugAids = config.getBoolean("ui.show.debug.aids", this.showDebugAids);
    }

    public int getNumberOfRowsAndColumnsOfBoard() {
        return numberOfRowsAndColumnsOfBoard;
    }

    public int getNumberOfPiecesPerPlayer() {
        return numberOfPiecesPerPlayer;
    }

    public String getHumanPlayerName() {
        return humanPlayerName;
    }

    public String getComputerPlayerName() {
        return computerPlayerName;
    }

    public int getComputerMaxNumberOfAttemptsWhenFindingValidMove() {
        return computerMaxNumberOfAttemptsWhenFindingValidMove;
    }

    public List<MoveRule> getEnabledRules() {
        return enabledRules;
    }

    public boolean isShowMenuScreen() {
        return showMenuScreen;
    }

    public boolean isShowDebugAids() {
        return showDebugAids;
    }

    public boolean isCompulsoryJumpRuleEnabled() {
        return this.enabledRules.contains(PIECE_MUST_COMPULSORY_JUMP_IF_OPPONENT_PIECE_NEARBY);
    }
}
