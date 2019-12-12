package controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
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
import model.annotations.ResourceFormatArgs;

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
        Class<?> currentClass = getClass();
        Class<ResourceName> resourceNameClass = ResourceName.class;
        if (currentClass.isAnnotationPresent(resourceNameClass)) {
            String resourceName = ((ResourceName)currentClass.getAnnotation(resourceNameClass)).value();
            if (resourceName == null || resourceName.trim().isEmpty())
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Resource name annotation is empty");
            else {
                Class<ResourceKey> resourceKeyClass = ResourceKey.class;
                Class<ResourceFormatArgs> resourceFormatClass = ResourceFormatArgs.class;
                final ResourceBundle defaultBundle = ResourceBundle.getBundle(resourceName, App.getCurrentLocale());
                
                String bc = ControllerBase.class.getName();
                final HashMap<String, ResourceBundle> loadedBundles = new HashMap<>();
                loadedBundles.put(resourceName, defaultBundle);
                do {
                    Arrays.stream(currentClass.getDeclaredFields()).forEach((Field field) -> {
                        if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || !field.isAnnotationPresent(resourceKeyClass))
                            return;
                        String key = ((ResourceKey)field.getAnnotation(resourceKeyClass)).value();
                        ResourceBundle currentBundle = defaultBundle;
                        if (field.isAnnotationPresent(resourceNameClass)) {
                            String rbName = ((ResourceName)getClass().getAnnotation(resourceNameClass)).value();
                            if (rbName != null && !rbName.trim().isEmpty()) {
                                if (loadedBundles.containsKey(rbName))
                                    currentBundle = loadedBundles.get(rbName);
                                else {
                                    currentBundle = ResourceBundle.getBundle(rbName, App.getCurrentLocale());
                                    loadedBundles.put(rbName, currentBundle);
                                }
                            }
                        }
                        if (currentBundle.containsKey(key)) {
                            try {
                                boolean wasAccessible = field.isAccessible();
                                if (!wasAccessible)
                                    field.setAccessible(true);
                                Object value;
                                try { value = field.get(this); }
                                finally {
                                    if (!wasAccessible)
                                        field.setAccessible(false);
                                }
                                if (value == null)
                                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Field \"%s\" is null", field.getName());
                                else {
                                    String text = currentBundle.getString(key);
                                    if (field.isAnnotationPresent(resourceFormatClass)) {
                                        String[] args = ((ResourceFormatArgs)field.getAnnotation(resourceFormatClass)).value();
                                        if (args != null && args.length > 0) {
                                            Object[] a = new Object[args.length];
                                            for (int i = 0; i < args.length; i++) {
                                                if (currentBundle.containsKey(args[i]))
                                                    a[i] = currentBundle.getString(args[i]);
                                            }
                                            text = String.format(text, a);
                                        }
                                    }
                                    if (value instanceof Labeled)
                                        ((Labeled)value).setText(text);
                                    else if (value instanceof MenuItem)
                                        ((MenuItem)value).setText(text);
                                    else if (value instanceof TableColumnBase<?,?>)
                                        ((TableColumnBase<?,?>)value).setText(text);
                                    else
                                        Logger.getLogger(getClass().getName()).log(Level.WARNING, "Field \"%s\" value type %s is not supported",
                                            new Object[] { field.getName(), value.getClass().getName() });
                                }
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            }
                        } else
                            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Cannot find resource key \"%s\"", key);
                    });
                    currentClass = currentClass.getSuperclass();
                } while (!bc.equals(currentClass.getName()));
            }
        } else
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No resource name annotation present");
    }
}