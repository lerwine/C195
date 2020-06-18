package scheduler.fx;

import scheduler.model.ui.CountryModel;
import scheduler.events.CountryEvent;

public class CountryEditTableCellFactory extends ItemEditTableCellFactory<CountryModel, CountryEvent> {

    @Override
    protected CountryModel.Factory getFactory() {
        return CountryModel.FACTORY;
    }

}
