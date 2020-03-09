package scheduler.util;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import scheduler.AppResources;
import scheduler.view.ErrorDialogDetailController;

/**
 *
 * @author erwinel
 */
public class Alerts {

    public static final String CSS_CLASS_FORMCONTROLLABEL = "formControlLabel";

    public static Optional<ButtonType> showErrorAlert(String title, Node content, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
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

    public static Optional<ButtonType> logAndAlertError(Logger logger, Class<?> sourceClass, String sourceMethod, String logMessage, Throwable error, ButtonType... buttons) {
        logger.logp(Level.SEVERE, sourceClass.getName(), sourceMethod, logMessage, error);
        if (null == buttons || buttons.length == 0) {
            buttons = new ButtonType[]{ButtonType.OK};
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORDETAILS), buttons);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORTITLE));
        try {
            alert.getDialogPane().setExpandableContent(ErrorDialogDetailController.load(error, logMessage));
        } catch (IOException ex) {
            Logger.getLogger(Alerts.class.getName()).logp(Level.SEVERE, Alerts.class.getName(), "logAndAlert", "Error loading exception detail", ex);
        }
        return alert.showAndWait();
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param headerText The header of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param error The error to show as the details.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String title, String headerText, String contentText, Throwable error, ButtonType... buttons) {
        if (null == buttons || buttons.length == 0) {
            buttons = new ButtonType[]{ButtonType.OK};
        }
        Alert alert;
        if (Values.isNullWhiteSpaceOrEmpty(headerText)) {
            if (null == error) {
                alert = new Alert(Alert.AlertType.ERROR, Values.nonWhitespaceOrDefault(contentText, "Content text cannot be empty"), buttons);
            } else {
                alert = new Alert(Alert.AlertType.ERROR, Values.nonWhitespaceOrDefault(contentText,
                        () -> AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORDETAILS)), buttons);
            }
        } else {
            alert = new Alert(Alert.AlertType.ERROR, Values.nonWhitespaceOrDefault(contentText,
                    () -> AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORDETAILS)), buttons);
            alert.setHeaderText(headerText);
        }
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(Values.nonWhitespaceOrDefault(title, () -> AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORTITLE)));
        if (null != error) {
            try {
                alert.getDialogPane().setExpandableContent(ErrorDialogDetailController.load(error, null));
            } catch (IOException ex) {
                Logger.getLogger(Alerts.class.getName()).logp(Level.SEVERE, Alerts.class.getName(), "logAndAlert", "Error loading exception detail", ex);
            }
        }
        return alert.showAndWait();
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param text The header of the {@link Alert} dialog if {@code error} is {@code null} or whitespace; otherwise, the content.
     * @param error The error to show as the details.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String title, String text, Throwable error, ButtonType... buttons) {
        return showErrorAlert(title, text, null, error, buttons);
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param text The header of the {@link Alert} dialog if {@code error} is {@code null} or whitespace; otherwise, the content.
     * @param error The error to show as the details.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String text, Throwable error, ButtonType... buttons) {
        return showErrorAlert(null, text, error, buttons);
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param error The error to show as the details.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(Throwable error, ButtonType... buttons) {
        return showErrorAlert(null, AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORHEADING),
                AppResources.getResourceString(AppResources.RESOURCEKEY_UNEXPECTEDERRORDETAILS), Objects.requireNonNull(error, "Error cannot be null"), buttons);
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param headerText The header of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String title, String headerText, String contentText, ButtonType... buttons) {
        return showErrorAlert(title, headerText, contentText, null, buttons);
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String title, String contentText, ButtonType... buttons) {
        return showErrorAlert(title, null, contentText, null, buttons);
    }

    /**
     * Shows an {@link Alert.AlertType#ERROR} {@link Alert} dialog.
     *
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showErrorAlert(String contentText, ButtonType... buttons) {
        return showErrorAlert(null, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param headerText The header of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(String title, String headerText, String contentText, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.WARNING, Values.requireNonWhitespace(contentText, "Content text cannot be empty"), buttons);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(Values.nonWhitespaceOrDefault(title, () -> AppResources.getResourceString(AppResources.RESOURCEKEY_WARNING)));
        if (Values.isNullWhiteSpaceOrEmpty(headerText)) {
            alert.setHeaderText(headerText);
        }
        return alert.showAndWait();
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param title The title of the {@link Alert} dialog.
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(String title, String contentText, ButtonType... buttons) {
        return showWarningAlert(title, null, contentText, buttons);
    }

    /**
     * Shows a {@link Alert.AlertType#WARNING} {@link Alert} dialog.
     *
     * @param contentText The message to show in the {@link Alert} dialog content area.
     * @param buttons The types of buttons to be displayed in the {@link Alert} dialog. Defaults to {@link ButtonType#OK} if no button types are specified.
     * @return An {@link Optional} that contains the value of {@link Alert#resultProperty()}.
     */
    public static Optional<ButtonType> showWarningAlert(String contentText, ButtonType... buttons) {
        return showWarningAlert(null, contentText, buttons);
    }
}
