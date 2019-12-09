package controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumnBase;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;
import scheduler.App;

/**
 *
 * @author Leonard T. Erwine
 */
public abstract class ControllerBase implements Initializable {
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Class<?> c = getClass();
        Class<ResourceName> r = ResourceName.class;
        if (r.isAnnotationPresent(r)) {
            String resourceName = ((ResourceName)r.getAnnotation(r)).value();
            if (resourceName == null || resourceName.trim().isEmpty())
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Resource name annotation is empty");
            else {
                Class<ResourceKey> k = ResourceKey.class;
                ResourceBundle currentRb = ResourceBundle.getBundle(resourceName, App.getCurrentLocale());
                Arrays.stream(c.getFields()).forEach((Field f) -> {
                    if (f.isAnnotationPresent(k)) {
                        String key = ((ResourceKey)f.getAnnotation(k)).value();
                        if (currentRb.containsKey(key)) {
                            try {
                                Object value = f.get(this);
                                if (value == null)
                                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Field \"%s\" is null", f.getName());
                                else {
                                    String text = currentRb.getString(key);
                                    if (value instanceof Labeled)
                                        ((Labeled)value).setText(text);
                                    else if (value instanceof MenuItem)
                                        ((MenuItem)value).setText(text);
                                    else if (value instanceof TableColumnBase<?,?>)
                                        ((TableColumnBase<?,?>)value).setText(text);
                                    else
                                        Logger.getLogger(getClass().getName()).log(Level.WARNING, "Field \"%s\" value type %s is not supported",
                                            new Object[] { f.getName(), value.getClass().getName() });
                                }
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            }
                            
                        } else
                            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Cannot find resource key \"%s\"", key);
                    }
                });
            }
        } else
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No resource name annotation present");
    }
}