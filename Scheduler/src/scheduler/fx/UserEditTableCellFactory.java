package scheduler.fx;

import scheduler.model.ui.UserModel;
import scheduler.view.event.UserEvent;

public class UserEditTableCellFactory extends ItemEditTableCellFactory<UserModel, UserEvent> {

    @Override
    protected UserModel.Factory getFactory() {
        return UserModel.FACTORY;
    }

}
