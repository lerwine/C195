package scheduler.fx;

import scheduler.model.ui.CountryModel;
import events.CountryEvent;

public class CountryEditTableCellFactory extends ItemEditTableCellFactory<CountryModel, CountryEvent> {

    @Override
    protected CountryModel.Factory getFactory() {
        return CountryModel.FACTORY;
    }

}
