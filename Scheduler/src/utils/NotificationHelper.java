/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/**
 * Helper class for displaying notification and confirmation popups.
 * @author webmaster
 */
public class NotificationHelper { 
    /**
     * Displays a notification dialog box.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText,
            String contentText, Alert.AlertType type) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty())
            alert = new Alert(type,
                    (headerText == null || headerText.trim().isEmpty()) ? headerText : rb.getString(headerText),
                    ButtonType.OK);
        else {
            alert = new Alert(type, rb.getString(contentText), ButtonType.OK);
            if (headerText != null && !headerText.trim().isEmpty())
                alert.setHeaderText(rb.getString(headerText));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        
        alert.showAndWait();
    }
    
    /**
     * Displays a notification dialog box.
     * The header text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param args          Arguments for header text format string.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText, Object[] args,
            String contentText, Alert.AlertType type) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty())
            alert = new Alert(type, String.format(rb.getString(headerText), args), ButtonType.OK);
        else {
            alert = new Alert(type, rb.getString(contentText), ButtonType.OK);
            alert.setHeaderText(String.format(rb.getString(headerText), args));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        
        alert.showAndWait();
    }
    
    /**
     * Displays a notification dialog box.
     * The content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content format of dialog box.
     * @param args          Arguments for content text format string.
     * @param type          Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText,
            String contentText, Object[] args, Alert.AlertType type) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty())
            alert = new Alert(type,
                    (headerText == null || headerText.trim().isEmpty()) ? headerText : rb.getString(headerText),
                    ButtonType.OK);
        else {
            alert = new Alert(type, rb.getString(contentText), ButtonType.OK);
            if (headerText != null && !headerText.trim().isEmpty())
                alert.setHeaderText(rb.getString(headerText));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        
        alert.showAndWait();
    }
    
    /**
     * Displays a notification dialog box.
     * The header and content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param headerArgs    Arguments for header text format string.
     * @param contentText   Resource key for content format of dialog box.
     * @param contentArgs   Arguments for content text format string.
     * @param type          Type of dialog box to display.
     */
    public static void showNotificationDialog(String title, String headerText, Object[] headerArgs,
            String contentText, Object[] contentArgs, Alert.AlertType type) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty())
            alert = new Alert(type, String.format(rb.getString(headerText), headerArgs), ButtonType.OK);
        else {
            alert = new Alert(type, rb.getString(contentText), ButtonType.OK);
            alert.setHeaderText(String.format(rb.getString(headerText), headerArgs));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        
        alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     * @param showCancel    Whether to show the 'Cancel' button as well as the 'Yes' and 'No' buttons.
     * @return An {@see Optional} value using {@see ButtonType.YES}, {@see ButtonType.NO} or {@see ButtonType.CANCEL}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText,
            String contentText, Alert.AlertType type, boolean showCancel) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty()) {
            if (headerText == null || headerText.trim().isEmpty())
                alert = (showCancel) ? new Alert(type, headerText, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                        new Alert(type, headerText, ButtonType.YES, ButtonType.NO);
            else
                alert = (showCancel) ? new Alert(type, rb.getString(headerText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                        new Alert(type, rb.getString(headerText), ButtonType.YES, ButtonType.NO);
        } else {
            alert = (showCancel) ? new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO);
            if (headerText != null && !headerText.trim().isEmpty())
                alert.setHeaderText(rb.getString(headerText));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * The header text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param args          Arguments for header text format string.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     * @param showCancel    Whether to show the 'Cancel' button as well as the 'Yes' and 'No' buttons.
     * @return {@see Optional} value using {@see ButtonType.YES}, {@see ButtonType.NO} or {@see ButtonType.CANCEL}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, Object[] args,
            String contentText, Alert.AlertType type, boolean showCancel) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().trim().isEmpty()) {
            alert = (showCancel) ? new Alert(type, String.format(rb.getString(headerText), args), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, String.format(rb.getString(headerText), args), ButtonType.YES, ButtonType.NO);
        } else {
            alert = (showCancel) ? new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(String.format(rb.getString(headerText), args));
        }
        if (title != null && !title.trim().trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * The content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content format of dialog box.
     * @param args          Arguments for content text format string.
     * @param type          Type of dialog box to display.
     * @param showCancel    Whether to show the 'Cancel' button as well as the 'Yes' and 'No' buttons.
     * @return An {@see Optional} value using {@see ButtonType.YES}, {@see ButtonType.NO} or {@see ButtonType.CANCEL}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText,
            String contentText, Object[] args, Alert.AlertType type, boolean showCancel) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().isEmpty()) {
            if (headerText == null || headerText.trim().isEmpty())
                alert = (showCancel) ? new Alert(type, headerText, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                        new Alert(type, headerText, ButtonType.YES, ButtonType.NO);
            else
                alert = (showCancel) ? new Alert(type, rb.getString(headerText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                        new Alert(type, rb.getString(headerText), ButtonType.YES, ButtonType.NO);
        } else {
            alert = (showCancel) ? new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO);
            if (headerText != null && !headerText.trim().isEmpty())
                alert.setHeaderText(rb.getString(headerText));
        }
        if (title != null && !title.trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * The header and content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param headerArgs    Arguments for header text format string.
     * @param contentText   Resource key for content format of dialog box.
     * @param contentArgs   Arguments for content text format string.
     * @param type          Type of dialog box to display.
     * @param showCancel    Whether to show the 'Cancel' button as well as the 'Yes' and 'No' buttons.
     * @return An {@see Optional} value using {@see ButtonType.YES}, {@see ButtonType.NO} or {@see ButtonType.CANCEL}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, Object[] headerArgs,
            String contentText, Object[] contentArgs, Alert.AlertType type, boolean showCancel) {
        ResourceBundle rb = scheduler.Context.getMessagesRB();
        Alert alert;
        if (contentText == null || contentText.trim().trim().isEmpty()) {
            alert = (showCancel) ? new Alert(type, String.format(rb.getString(headerText), headerArgs), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, String.format(rb.getString(headerText), headerArgs), ButtonType.YES, ButtonType.NO);
        } else {
            alert = (showCancel) ? new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL) : 
                    new Alert(type, rb.getString(contentText), ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(String.format(rb.getString(headerText), headerArgs));
        }
        if (title != null && !title.trim().trim().isEmpty())
            alert.setTitle(rb.getString(title));
        alert.initStyle(StageStyle.UTILITY);
        return alert.showAndWait();
    }
    
    /**
     * Displays a confirmation dialog box.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     * @return An {@see Optional} value using {@see ButtonType.YES} or {@see ButtonType.NO}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, contentText, type, false);
    }
    
    /**
     * Displays a confirmation dialog box.
     * The header text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param args          Arguments for header text format string.
     * @param contentText   Resource key for content of dialog box.
     * @param type          Type of dialog box to display.
     * @return An {@see Optional} value using {@see ButtonType.YES} or {@see ButtonType.NO}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, Object[] args, String contentText, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, args, contentText, type, false);
    }

    /**
     * Displays a confirmation dialog box.
     * The content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text for dialog box.
     * @param contentText   Resource key for content format of dialog box.
     * @param args          Arguments for content text format string.
     * @param type          Type of dialog box to display.
     * @return An {@see Optional} value using {@see ButtonType.YES} or {@see ButtonType.NO}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText, Object[] args, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, contentText, args, type, false);
    }
    
    /**
     * Displays a confirmation dialog box.
     * The header and content text is rendered by using {@link String.format(String,Object[])}.
     * @param title         Resource key for title of dialog box.
     * @param headerText    Resource key for header text format for dialog box.
     * @param headerArgs    Arguments for header text format string.
     * @param contentText   Resource key for content format of dialog box.
     * @param contentArgs   Arguments for content text format string.
     * @param type          Type of dialog box to display.
     * @return An {@see Optional} value using {@see ButtonType.YES} or {@see ButtonType.NO}.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, Object[] headerArgs, String contentText, Object[] contentArgs, Alert.AlertType type) {
        return showConfirmationDialog(title, headerText, headerArgs, contentText, contentArgs, type, false);
    }
}
