package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
@FunctionalInterface
public interface ItemEventListener<T extends ItemEvent<?>> extends java.util.EventListener {

    void handle(T event);
}
