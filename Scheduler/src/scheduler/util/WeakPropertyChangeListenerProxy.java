package scheduler.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class WeakPropertyChangeListenerProxy implements PropertyChangeListener {

    private WeakReference<PropertyChangeListener> listener;
    private WeakReference<IPropertyBindable> source;

    WeakPropertyChangeListenerProxy(IPropertyBindable source, PropertyChangeListener listener) {
        this.listener = new WeakReference<>(Objects.requireNonNull(listener));
        this.source = new WeakReference<>(Objects.requireNonNull(source));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        IPropertyBindable s = source.get();
        PropertyChangeListener pcl = listener.get();
        if (null != s) {
            if (null != pcl) {
                pcl.propertyChange(evt);
                return;
            }
            s.removePropertyChangeListener(this);
            source.clear();
        } else if (null != pcl) {
            listener.clear();
        }
    }

}
