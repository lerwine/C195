package scheduler.fx;

import scheduler.dao.AddressDAO;
import scheduler.events.AddressOpRequestEvent;
import scheduler.model.ui.AddressModel;

public class AddressEditTableCellFactory extends ItemEditTableCellFactory<AddressDAO, AddressModel, AddressOpRequestEvent> {

    @Override
    protected AddressModel.Factory getFactory() {
        return AddressModel.FACTORY;
    }

}
