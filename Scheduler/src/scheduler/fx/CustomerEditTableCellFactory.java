package scheduler.fx;

import scheduler.model.ui.CustomerModel;
import scheduler.view.event.CustomerEvent;

public class CustomerEditTableCellFactory extends ItemEditTableCellFactory<CustomerModel, CustomerEvent> {

    @Override
    protected CustomerModel.Factory getFactory() {
        return CustomerModel.FACTORY;
    }

}
