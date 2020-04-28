package scheduler.view;

import java.util.function.Supplier;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface DialogWindowResult<T> extends Supplier<T> {
    Throwable getFault();
}