package scheduler.fx;

import scheduler.model.ui.AppointmentModel;

public class AppointmentEditTableCellFactory extends ItemEditTableCellFactory<AppointmentModel> {

    @Override
    protected AppointmentModel.Factory getFactory() {
        return AppointmentModel.FACTORY;
    }

}
