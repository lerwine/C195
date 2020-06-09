package scheduler.fx;

import scheduler.model.ui.CountryModel;

public class CountryEditTableCellFactory extends ItemEditTableCellFactory<CountryModel> {

    @Override
    protected CountryModel.Factory getFactory() {
        return CountryModel.FACTORY;
    }

}
