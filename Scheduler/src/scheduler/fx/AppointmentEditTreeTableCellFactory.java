package scheduler.fx;

import scheduler.dao.AppointmentDAO;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.fx.AppointmentModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentEditTreeTableCellFactory extends ItemEditTreeTableCellFactory<AppointmentDAO, AppointmentModel, AppointmentOpRequestEvent> {

    @Override
    public AppointmentModel.Factory getFactory() {
        return AppointmentModel.FACTORY;
    }

}
