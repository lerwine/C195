package scheduler.fx;

import scheduler.dao.AppointmentDAO;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.fx.AppointmentModel;

public class AppointmentEditTableCellFactory extends ItemEditTableCellFactory<AppointmentDAO, AppointmentModel, AppointmentOpRequestEvent> {

    @Override
    public AppointmentModel.Factory getFactory() {
        return AppointmentModel.FACTORY;
    }

}
