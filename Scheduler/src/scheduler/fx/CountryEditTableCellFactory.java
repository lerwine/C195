package scheduler.fx;

import scheduler.model.ui.CountryModel;
import scheduler.view.event.CountryEvent;

public class CountryEditTableCellFactory extends ItemEditTableCellFactory<CountryModel, CountryEvent> {

    @Override
    protected CountryModel.Factory getFactory() {
        return CountryModel.FACTORY;
    }

}
