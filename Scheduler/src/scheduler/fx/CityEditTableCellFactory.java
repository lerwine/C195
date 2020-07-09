package scheduler.fx;

import scheduler.dao.CityDAO;
import scheduler.events.CityOpRequestEvent;
import scheduler.model.ui.CityModel;

public class CityEditTableCellFactory extends ItemEditTableCellFactory<CityDAO, CityModel, CityOpRequestEvent> {

    @Override
    public CityModel.Factory getFactory() {
        return CityModel.FACTORY;
    }

}
