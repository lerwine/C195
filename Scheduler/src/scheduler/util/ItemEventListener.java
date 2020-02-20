package scheduler.util;

/**
 *
 * @author lerwi
 * @param <T>
 */
@FunctionalInterface
public interface ItemEventListener<T extends ItemEvent<?>> extends java.util.EventListener {

    void handle(T event);
}
