package scheduler.fx;

import scheduler.model.ui.CustomerModel;

public class CustomerEditTableCellFactory extends ItemEditTableCellFactory<CustomerModel> {

    @Override
    protected CustomerModel.Factory getFactory() {
        return CustomerModel.FACTORY;
    }

}
