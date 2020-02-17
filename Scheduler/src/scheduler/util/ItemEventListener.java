package scheduler.util;

import java.util.EventListener;
import java.util.function.Consumer;

/**
 * Listens for {@link DaoEventManager}s.
 * @author lerwi
 * @param <T> The type of data access object.
 */
@FunctionalInterface
public interface ItemEventListener<T extends ItemEventObject<?>> extends EventListener, Consumer<T> {
}
