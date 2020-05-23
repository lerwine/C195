package scheduler.dao;

import scheduler.model.User;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IUserDAO extends DbObject, User {

    public static <T extends IUserDAO> T assertValidUser(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Customer has already been deleted");
        }
        if (Values.isNullWhiteSpaceOrEmpty(target.getUserName())) {
            throw new IllegalStateException("User name not defined");
        }
        if (target instanceof UserDAO && Values.isNullWhiteSpaceOrEmpty(((UserDAO)target).getPassword())) {
            throw new IllegalStateException("Password not defined");
        }
        return target;
    }

}
