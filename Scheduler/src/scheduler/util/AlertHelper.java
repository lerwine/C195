package scheduler.util;

import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;

/**
 * Utility class for alerts and logging.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AlertHelper {

    public static final String CSS_CLASS_FORMCONTROLLABEL = "formControlLabel";

    public static Optional<ButtonType> showErrorAlert(Window parent, String title, Node content, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        if (null != parent) {
            alert.initOwner(parent);
        }
        alert.getDialogPane().setContent(content);
        if (null != buttons && buttons.length > 0) {
            for (ButtonType btnType : buttons) {
                alert.getButtonTypes().addAll(btnType);
            }
        } else {
            alert.getButtonTypes().add(ButtonType.OK);
        }
        return alert.showAndWait();
    }

    public static Optional<ButtonType> showErrorAlert(Window parent, String title, String content, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, buttons);
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        if (null != parent) {
            alert.initOwner(parent);
        }

        return alert.showAndWait();
    }

    public static Optional<ButtonType> showErrorAlert(String title, Node content, ButtonType... buttons) {
        return showErrorAlert((Window) null, title, content, buttons);
    }

    public static Optional<ButtonType> showErrorAlert(String title, String content, ButtonType... buttons) {
        return showErrorAlert((Window) null, title, content, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param logger The {@link Logger} to log the warning to. This can be {@code null} if you do not want to write to a log.
     * @param title The title of the {@link Alert} dialog.
     * @param headerText The header of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, Logger logger, String title, String headerText, String contentText, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.WARNING, Values.requireNonWhitespace(contentText, "Content text cannot be empty"), buttons);
        alert.initStyle(StageStyle.UTILITY);
        if (null != parent) {
            alert.initOwner(parent);
        }
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(Values.nonWhitespaceOrDefault(title, () -> AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_WARNING)));
        if (Values.isNullWhiteSpaceOrEmpty(headerText)) {
            alert.setHeaderText(headerText);
        }
        return alert.showAndWait();
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param logger The {@link Logger} to log the warning to. This can be {@code null} if you do not want to write to a log.
     * @param title The title of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, Logger logger, String title, String contentText, ButtonType... buttons) {
        return showWarningAlert(parent, logger, title, null, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param title The title of the {@link Alert} dialog.
     * @param headerText The header of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, String title, String headerText, String contentText, ButtonType... buttons) {
        return showWarningAlert(parent, null, title, headerText, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param logger The {@link Logger} to log the warning to. This can be {@code null} if you do not want to write to a log.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, Logger logger, String contentText, ButtonType... buttons) {
        return showWarningAlert(parent, logger, null, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param title The title of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, String title, String contentText, ButtonType... buttons) {
        return showWarningAlert(parent, title, null, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param parent The parent {@link Window} for the displayed {@link Alert}. This should only be {@code null} if you are not able to determine a parent window to use.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(Window parent, String contentText, ButtonType... buttons) {
        return showWarningAlert(parent, (String) null, contentText, buttons);
    }

}
