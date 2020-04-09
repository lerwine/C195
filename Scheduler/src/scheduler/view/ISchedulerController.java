package scheduler.view;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface ISchedulerController {

    URL getLocation();

    ResourceBundle getResources();

    /**
     * Gets a string from the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     *
     * @param key The key of the string to get.
     * @return A string from the {@link java.util.ResourceBundle} injected by the {@link javafx.fxml.FXMLLoader}.
     */
    default String getResourceString(String key) {
        return getResources().getString(key);
    }

}
