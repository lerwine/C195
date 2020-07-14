package scheduler.fx;

import scheduler.dao.UserDAO;
import scheduler.events.UserOpRequestEvent;
import scheduler.model.fx.UserModel;

public class UserEditTableCellFactory extends ItemEditTableCellFactory<UserDAO, UserModel, UserOpRequestEvent> {

    @Override
    public UserModel.Factory getFactory() {
        return UserModel.FACTORY;
    }

}
