package scheduler.util;

import java.util.EventObject;

/**
 *
 * @author lerwi
 * @param <T>
 */
public class ItemEvent<T> extends EventObject {

    private final T target;

    public final T getTarget() {
        return target;
    }

    public ItemEvent(Object source, T target) {
        super(source);
        this.target = target;
    }
}
