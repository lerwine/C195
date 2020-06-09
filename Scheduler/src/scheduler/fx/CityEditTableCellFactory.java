package scheduler.fx;

import scheduler.model.ui.CityModel;

public class CityEditTableCellFactory extends ItemEditTableCellFactory<CityModel> {

    @Override
    protected CityModel.Factory getFactory() {
        return CityModel.FACTORY;
    }

}
