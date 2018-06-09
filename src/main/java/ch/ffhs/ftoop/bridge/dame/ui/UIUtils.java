package ch.ffhs.ftoop.bridge.dame.ui;

import ch.ffhs.ftoop.bridge.dame.ui.handler.AlertOptionClickedHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;

import static ch.ffhs.ftoop.bridge.dame.ui.UIConstants.ALERT_EXCEPTION_STACKTRACE_LABEL;

public final class UIUtils {
    private UIUtils() {}

    public static BackgroundFill backgroundFillFor(String color) {
        return new BackgroundFill(colorFor(color), CornerRadii.EMPTY, Insets.EMPTY);
    }

    public static BackgroundFill transparentBackgroundFill() {
        return new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
    }

    public static Image imageFromClassLoader(Class<?> clazz, String filename) {
        return new Image(clazz.getClassLoader().getResourceAsStream(filename));
    }

    public static Color colorFor(String color) {
        return Color.web(color,1.0);
    }

    public static Color colorFor(String color, double opacity) {
        return Color.web(color, opacity);
    }

    public static void showInformationAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION, () -> {});
    }

    public static void showErrorAlert(String title, String message) {
        showErrorAlert(title, message, () -> {});
    }

    public static void showErrorAlert(String title, String message, AlertOptionClickedHandler optionClickedHandler) {
        showAlert(title, message, Alert.AlertType.ERROR, optionClickedHandler);
    }

    @SuppressWarnings("WeakerAccess")
    public static void showAlert(String title, String message, Alert.AlertType type, AlertOptionClickedHandler optionClickedHandler) {
        // http://code.makery.ch/blog/javafx-dialogs-official/

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void showConfirmation(String title, String message, AlertOptionClickedHandler okClicked, AlertOptionClickedHandler cancelClicked) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            okClicked.onOptionClicked();
        } else {
            cancelClicked.onOptionClicked();
        }
    }

    public static void showException(String title, String body, Throwable e, AlertOptionClickedHandler optionClickedHandler) {
        // http://code.makery.ch/blog/javafx-dialogs-official/

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(String.format("%s\n%s", body, e.getMessage()));

        Label label = new Label(ALERT_EXCEPTION_STACKTRACE_LABEL);

        TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(e));
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        // Listen for the OK click
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (optionClickedHandler != null) {
                optionClickedHandler.onOptionClicked();
            }
        }


    }
}
