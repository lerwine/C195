package scheduler.observables;

import javafx.beans.binding.Binding;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The object type.
 */
public interface BindingProperty<T> extends ReadOnlyProperty<T>, Binding<T> {

}
