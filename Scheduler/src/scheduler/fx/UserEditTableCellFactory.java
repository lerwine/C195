package scheduler.fx;

import scheduler.model.ui.UserModel;
import events.UserEvent;

public class UserEditTableCellFactory extends ItemEditTableCellFactory<UserModel, UserEvent> {

    @Override
    protected UserModel.Factory getFactory() {
        return UserModel.FACTORY;
    }

}
