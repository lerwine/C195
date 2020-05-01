package scheduler.dao;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ItemCountResult<T> {
    
    private final int count;
    private final T value;

    public int getCount() {
        return count;
    }

    public T getValue() {
        return value;
    }

    public ItemCountResult(T value, int count) {
        this.value = value;
        this.count = count;
    }
}
