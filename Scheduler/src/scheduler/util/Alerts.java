package scheduler.util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/**
 *
 * @author erwinel
 */
public class Alerts {
    public static Optional<ButtonType> showErrorAlert(String title, String contentText, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText, buttons);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        return alert.showAndWait();
    }
    
    public static Optional<ButtonType> showWarningAlert(String title, String contentText, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.WARNING, contentText, buttons);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        return alert.showAndWait();
    }
}
