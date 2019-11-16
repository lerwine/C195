/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/**
 *
 * @author webmaster
 */
public class NotificationHelper { 
    /**
     * Displays a notification dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText,
            String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type, contentText, ButtonType.OK);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     * @param showCancel Whether to show the 'Cancel' button as well as the
     *  'Yes' and 'No' buttons.
     * @return Optional&lt;ButtonType&gt; using ButtonType.YES, ButtonType.NO
     *  or ButtonType.CANCEL.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Alert.AlertType type, boolean showCancel) {
        Alert alert = (showCancel) ? new Alert(type, contentText, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                new Alert(type, contentText, ButtonType.YES, ButtonType.NO);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title Title of dialog box.
     * @param headerText Header text for dialog box.
     * @param contentText Content of dialog box.
     * @param type Type of dialog box to display.
     * @return Optional&lt;ButtonType&gt; using ButtonType.YES or ButtonType.NO.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, contentText, type, false);
    }
}
