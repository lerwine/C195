package scheduler.fx;

import scheduler.dao.CountryDAO;
import scheduler.events.CountryOpRequestEvent;
import scheduler.model.ui.CountryModel;

public class CountryEditTableCellFactory extends ItemEditTableCellFactory<CountryDAO, CountryModel, CountryOpRequestEvent> {

    @Override
    public CountryModel.Factory getFactory() {
        return CountryModel.FACTORY;
    }

}
