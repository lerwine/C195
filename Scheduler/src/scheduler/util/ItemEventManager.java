package scheduler.util;

/**
 * Manages a chain of {@link ItemEvent} listeners.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of event object.
 */
public class ItemEventManager<T extends ItemEvent<?>> {

    private Link first = null;
    private Link last = null;

    /**
     * Fires an event on all listeners in the event listener chain.
     *
     * @param event The event being raised.
     */
    public final void fireEvent(T event) {
        Link l = last;
        if (null != l) {
            l.fireEvent(event);
        }
    }

    /**
     * Adds a {@link ItemEventListener} to the event listener chain.
     *
     * @param listener The {@link ItemEventListener} to add.
     * @return {@code true} if the {@code listener} was added to the event listener chain; otherwise, {@code false} if {@code listener} was null or it
     * had already been added.
     */
    public final synchronized boolean addListener(ItemEventListener<T> listener) {
        if (null == listener) {
            return false;
        }
        for (Link h = first; null != h; h = h.next) {
            if (h.listener.equals(listener)) {
                return false;
            }
        }
        if (null == (last = new Link(listener)).previous) {
            first = last;
        } else {
            last.previous.next = last;
        }
        return true;
    }

    /**
     * Removes a {@link ItemEventListener} from the event listener chain.
     *
     * @param listener The {@link ItemEventListener} to remove.
     * @return {@code true} if the {@code listener} was removed to the event listener chain; otherwise, {@code false} if {@code listener} was null or
     * it was not part of the event listener chain.
     */
    public final synchronized boolean removeListener(ItemEventListener<T> listener) {
        if (null == listener) {
            return false;
        }
        for (Link h = first; null != h; h = h.next) {
            if (h.listener.equals(listener)) {
                if (null != h.previous) {
                    if (null == (h.previous.next = h.next)) {
                        last = h.previous;
                    }
                } else if (null == (first = h.next)) {
                    last = null;
                } else {
                    first.previous = null;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether a {@link ItemEventListener} has already been added to the event listener chain.
     *
     * @param listener The {@link ItemEventListener} to check.
     * @return {@code true} if the {@code listener} has already been added to the event listener chain; otherwise, {@code false}.
     */
    public final synchronized boolean isListening(ItemEventListener<T> listener) {
        if (null == listener) {
            return false;
        }
        for (Link h = first; null != h; h = h.next) {
            if (h.listener.equals(listener)) {
                return true;
            }
        }
        return false;
    }

    // Represents a link in an event listener chain.
    private class Link {

        private Link previous;
        private Link next = null;
        private final ItemEventListener<T> listener;

        Link(ItemEventListener<T> listener) {
            this.listener = listener;
            previous = last;
        }

        /**
         * Fires an event for the current and preceding listeners. Invokes the preceding listeners first, and then the current one.
         *
         * @param event The event being fired.
         */
        private void fireEvent(T event) {
            // Save the previous link in case any of the listeners modify the listener chain.
            Link p = previous;
            try {
                if (null != p) {
                    p.fireEvent(event);
                }
            } finally {
                listener.handle(event);
            }
        }
    }
}
