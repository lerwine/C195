package scheduler.fx;

import scheduler.dao.AddressDAO;
import scheduler.events.AddressOpRequestEvent;
import scheduler.model.fx.AddressModel;

public class AddressEditTableCellFactory extends ItemEditTableCellFactory<AddressDAO, AddressModel, AddressOpRequestEvent> {

    @Override
    public AddressModel.Factory getFactory() {
        return AddressModel.FACTORY;
    }

}
