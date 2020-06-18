package scheduler.fx;

import scheduler.model.ui.AppointmentModel;
import scheduler.events.AppointmentEvent;

public class AppointmentEditTableCellFactory extends ItemEditTableCellFactory<AppointmentModel, AppointmentEvent> {

    @Override
    protected AppointmentModel.Factory getFactory() {
        return AppointmentModel.FACTORY;
    }

}
