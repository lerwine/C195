package scheduler.fx;

import scheduler.model.ui.AddressModel;
import scheduler.events.AddressEvent;

public class AddressEditTableCellFactory extends ItemEditTableCellFactory<AddressModel, AddressEvent> {

    @Override
    protected AddressModel.Factory getFactory() {
        return AddressModel.FACTORY;
    }

}
