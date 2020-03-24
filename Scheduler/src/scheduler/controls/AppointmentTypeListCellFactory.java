package scheduler.controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.dao.AppointmentType;

/**
 *
 * @author Leonard T. Erwine
 * @param <T> The type of the item represented by the {@link ListView}.
 */
public class AppointmentTypeListCellFactory<T extends AppointmentType> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new AppointmentTypeListCell<>();
    }
}
