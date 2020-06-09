package scheduler.fx;

import scheduler.model.ui.UserModel;

public class UserEditTableCellFactory extends ItemEditTableCellFactory<UserModel> {

    @Override
    protected UserModel.Factory getFactory() {
        return UserModel.FACTORY;
    }

}
