package scheduler.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface WeakChangeHandlingReference<T> {
    ChangeListener<T> getListener();
    WeakChangeListener<T> getWeakListener();
    
    public static <T> WeakChangeHandlingReference<T> of (ChangeListener<T> listener) {
        return new WeakChangeHandlingReference<T>() {
            private final WeakChangeListener<T> weakListener = new WeakChangeListener<>(listener);
            @Override
            public ChangeListener<T> getListener() {
                return listener;
            }

            @Override
            public WeakChangeListener<T> getWeakListener() {
                return weakListener;
            }
            
        };
    }
}
