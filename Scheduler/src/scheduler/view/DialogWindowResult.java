package scheduler.view;

import java.util.function.Supplier;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public interface DialogWindowResult<T> extends Supplier<T> {
    Throwable getFault();
}
