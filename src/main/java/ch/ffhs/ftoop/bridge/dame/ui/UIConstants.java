package ch.ffhs.ftoop.bridge.dame.ui;

public final class UIConstants {

    public static final String APPLICATION_NAME = "Dame";
    public static final String APPLICATION_ABOUT = "Ein Spiel von Linda Hess, \nSebastian Hutter & Simon Albrecht";
    public static final String APPLICATION_ICON_FILENAME = "assets/icon.png";

    public static final String WINDOW_BACKGROUND_COLOR = "#f7f1e3";

    public static final int SPACER = 10;
    public static final int TILE_SIZE = 64;
    public static final int TILE_PIECE_SIZE = 50;

    public static final String TILE_BACKGROUND_COLOR_LIGHT = "#f7f1e3";
    public static final String TILE_BACKGROUND_COLOR_DARK = "#696660";

    public static final String PIECE_DARK_NORMAL_FILENAME = "assets/pieces/dark.png";
    public static final String PIECE_DARK_KING_FILENAME = "assets/pieces/dark-king.png";
    public static final String PIECE_LIGHT_NORMAL_FILENAME = "assets/pieces/light.png";
    public static final String PIECE_LIGHT_KING_FILENAME = "assets/pieces/light-king.png";
    public static final String PIECE_VALID_MOVE_MARKER_FILENAME = "assets/pieces/valid-move.png";
    public static final String PIECE_SELECTED_MARKER_FILENAME = "assets/pieces/selected.png";

    public static final int MENU_LOGO_SIZE = 160;
    public static final String MENU_START_BUTTON_TEXT = "Spiel Starten!";
    public static final String MENU_START_BUTTON_BACKGROUND_COLOR = "#696660";
    public static final String MENU_START_BUTTON_BACKGROUND_COLOR_HOVER = "#393835";
    public static final String MENU_START_BUTTON_TEXT_COLOR = "#ffffff";
    public static final int MENU_START_BUTTON_HEIGHT = 40;
    public static final int MENU_START_BUTTON_WIDTH = 200;
    public static final int MENU_NAME_TEXT_SIZE = 30;
    public static final int MENU_ABOUT_TEXT_SIZE = 12;

    public static final String GAME_BOARD_BORDER = "#393835";
    public static final int GAME_BOARD_BORDER_WIDTH = 4;

    public static final int GAME_CURRENT_PLAYER_TEXT_SIZE = 26;
    public static final String GAME_CURRENT_PLAYER_DESCRIPTION = "Am Zug";
    public static final int GAME_CURRENT_PLAYER_DESCRIPTION_TEXT_SIZE = 14;

    public static final int GAME_PLAYER_SCORE_TEXT_SIZE = 18;
    public static final String GAME_PLAYER_SCORE_TEXT_COLOR = "#f7f1e3";
    public static final String GAME_PLAYER_SCORE_TEXT_SHADOW_COLOR = "#000000";
    public static final double GAME_PLAYER_SCORE_TEXT_SHADOW_OPACITY = 0.5;
    public static final int GAME_PLAYER_SCORE_TEXT_SHADOW_RADIUS = 2;
    public static final int GAME_PLAYER_SCORE_TEXT_SHADOW_OFFSET = 1;

    public static final String GAME_BOARD_TILE_DEBUG_TEXT_COLOR = "#ffffff";
    public static final int GAME_BOARD_TILE_DEBUG_TEXT_SIZE = 10;
    public static final String GAME_BOARD_TILE_DEBUG_BACKGROUND_COLOR = "#ff0000";
    public static final double GAME_BOARD_TILE_DEBUG_OPACITY = 0.25;

    public static final String ALERT_GENERAL_TITLE = "Mitteilung";
    public static final String ALERT_EXCEPTION_TITLE = "Fehler";
    public static final String ALERT_EXCEPTION_BODY = "Ein schwerwiegender Fehler ist aufgetreten:";
    public static final String ALERT_UNCAUGHT_EXCEPTION_TITLE = "Unbehandelter Fehler";
    public static final String ALERT_UNCAUGHT_EXCEPTION_BODY = "Ein unbehandelter, schwerwiegender Fehler is aufgetreten:";

    public static final String ALERT_EXCEPTION_STACKTRACE_LABEL = "Stacktrace:";

    public static final String MESSAGE_SELECTED_PIECE_HAS_NO_POSSIBLE_MOVES = "Für den gewählten Stein gibt es keine gültigen Züge.\nBitte wählen Sie erneut.";
    public static final String MESSAGE_ATTEMPTED_INVALID_MOVE = "Ein ungültiger Spielzug wurde gespielt.\nBitte versuchen Sie es erneut.";
    public static final String MESSAGE_GAME_ENDED_TITLE = "Spiel Beendet";
    public static final String MESSAGE_GAME_ENDED_PLAYER_WON_BODY = "Glückwunsch, Sie haben das Spiel mit %d zu %d gewonnen!\n\nMöchten Sie ein neues Spiel starten?";
    public static final String MESSAGE_GAME_ENDED_PLAYER_LOST_BODY = "Schade, leider haben Sie das Spiel %d zu %d gegen den Computer verloren.\n\nMöchten Sie ein neues Spiel starten?";
    public static final String MESSAGE_COMPUTER_COULD_NOT_FIND_VALID_TURN = "Der Computer konnte kein gültigen Zug finde.\n\nMöchten Sie ein neues Spiel starten?";

    public static final String MESSAGE_COULD_NOT_START_GAME_TITLE = "Fehler beim Starten";
    public static final String MESSAGE_COULD_NOT_START_GAME_BODY = "Das Spiel konnte nicht gestartet werden!\nBitte starten sie das Programm erneut.";

    private UIConstants() {
    }

}
