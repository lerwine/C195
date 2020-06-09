package scheduler.fx;

import scheduler.model.ui.AddressModel;

public class AddressEditTableCellFactory extends ItemEditTableCellFactory<AddressModel> {

    @Override
    protected AddressModel.Factory getFactory() {
        return AddressModel.FACTORY;
    }

}
