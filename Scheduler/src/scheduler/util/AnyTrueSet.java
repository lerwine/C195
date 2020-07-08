package scheduler.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AnyTrueSet {

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(true);
    private Node first;
    private Node last;

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    public synchronized Node add(boolean initialValue) {
        Node result = new Node(this, initialValue);
        if (!initialValue && result.onValidationChanged(initialValue)) {
            valid.set(null == last);
        }
        return result;
    }

    public static class Node {

        private final SimpleBooleanProperty valid;
        private final AnyTrueSet parent;
        private Node previous;
        private Node next;

        private Node(AnyTrueSet parent, boolean initialValue) {
            this.parent = parent;
            valid = new SimpleBooleanProperty(initialValue);
            valid.addListener((observable, oldValue, newValue) -> {
                if (onValidationChanged(newValue)) {
                    parent.valid.set(null == parent.last);
                }
            });
        }

        private boolean onValidationChanged(boolean value) {
            synchronized (parent) {
                if (value) {
                    if (null == previous) {
                        if (null == (parent.first = next)) {
                            parent.last = null;
                            return true;
                        }
                        next = next.previous = null;
                    } else if (null == (previous.next = next)) {
                        previous = (parent.last = previous).next = null;
                    } else {
                        next.previous = previous;
                        next = previous = null;
                    }
                } else if (null != (previous = parent.last)) {
                    parent.last = previous.next = this;
                } else {
                    parent.first = parent.last = this;
                    return true;
                }
            }
            return false;
        }

        public boolean isValid() {
            return valid.get();
        }

        public void setValid(boolean value) {
            valid.set(value);
        }

        public BooleanProperty validProperty() {
            return valid;
        }

    }
}
