package scheduler.model;

import scheduler.model.schema.AppointmentTable;


public class AppointmentRow extends TableRow<AppointmentTable> {

    @Override
    public AppointmentTable getSchema() {
        return AppointmentTable.INSTANCE;
    }
    
}
