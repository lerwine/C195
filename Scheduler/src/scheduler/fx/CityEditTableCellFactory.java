package scheduler.fx;

import scheduler.model.ui.CityModel;
import scheduler.view.event.CityEvent;

public class CityEditTableCellFactory extends ItemEditTableCellFactory<CityModel, CityEvent> {

    @Override
    protected CityModel.Factory getFactory() {
        return CityModel.FACTORY;
    }

}
