package scheduler.fx;

import scheduler.dao.CustomerDAO;
import scheduler.events.CustomerOpRequestEvent;
import scheduler.model.ui.CustomerModel;

public class CustomerEditTableCellFactory extends ItemEditTableCellFactory<CustomerDAO, CustomerModel, CustomerOpRequestEvent> {

    @Override
    protected CustomerModel.Factory getFactory() {
        return CustomerModel.FACTORY;
    }

}
