package scheduler.view;

import java.util.function.Supplier;

/**
 *
 * @author lerwi
 */
public interface DialogWindowResult<T> extends Supplier<T> {
    Throwable getFault();
}
