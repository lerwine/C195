package scheduler.dao;

import scheduler.model.User;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IUserDAO extends DbObject, User {

    public static <T extends IUserDAO> T assertValidUser(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Customer has already been deleted");
        }
        String userName = target.getUserName();
        if (userName.isEmpty()) {
            throw new IllegalStateException("User name not defined");
        }
        if (userName.length() > UserDAO.MAX_LENGTH_USERNAME) {
            throw new IllegalStateException("User name too long");
        }
        if (target instanceof UserDAO) {
            String password = ((UserDAO) target).getPassword();
            if (password.isEmpty()) {
                throw new IllegalStateException("Password not defined");
            }
            if (password.length() > UserDAO.MAX_LENGTH_PASSWORD) {
                throw new IllegalStateException("Password length too long");
            }
        }
        return target;
    }

}
