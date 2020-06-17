package scheduler.fx;

import scheduler.model.ui.CustomerModel;
import events.CustomerEvent;

public class CustomerEditTableCellFactory extends ItemEditTableCellFactory<CustomerModel, CustomerEvent> {

    @Override
    protected CustomerModel.Factory getFactory() {
        return CustomerModel.FACTORY;
    }

}
