/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import scheduler.model.schema.AppointmentTable;


public class AppointmentRow extends TableRow<AppointmentTable> {

    @Override
    public AppointmentTable getSchema() {
        return AppointmentTable.INSTANCE;
    }
    
}
