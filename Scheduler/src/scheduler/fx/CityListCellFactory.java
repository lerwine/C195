package scheduler.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.dao.PartialCityDAO;
import scheduler.model.fx.PartialCityModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class CityListCellFactory<T extends PartialCityModel<? extends PartialCityDAO>> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new CityListCell<>();
    }

}
