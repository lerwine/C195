package scheduler.dao.filter;

import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.UserDAO;
import scheduler.model.ui.EntityModel;
import scheduler.model.ui.CustomerModel;
import scheduler.model.ui.UserModel;

/**
 * Compares {@link DataAccessObject} and {@link EntityModel} to string values.
 *
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link EntityModel} object.
 @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ItemStringComparer<T extends DataAccessObject, U extends EntityModel<T>> extends ItemValueComparer<T, U, String> {

    public static final ItemStringComparer<CustomerDAO, CustomerModel> CUSTOMER_NAME = new ItemStringComparer<CustomerDAO, CustomerModel>() {
        @Override
        public String get(CustomerDAO dao) {
            return dao.getName();
        }

        @Override
        public String get(CustomerModel model) {
            return model.getName();
        }
    };
    
    public static ItemStringComparer<UserDAO, UserModel> USER_NAME = new ItemStringComparer<UserDAO, UserModel>() {
        @Override
        public String get(UserDAO dao) {
            return dao.getUserName();
        }

        @Override
        public String get(UserModel model) {
            return model.getUserName();
        }
    };
    
    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a {@link DataAccessObject} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataAccessObject} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, String value) {
        String t = get(dao);
        return (null == t) ? ((null == value) ? 0 : -1) : ((null != value) ? t.compareTo(value) : 1);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param model The target {@link EntityModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link EntityModel} object is equal to {@code value}. If the value associated with a {@link EntityModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link EntityModel} object is greater than {@code value}.
     */
    default int compareTo(U model, String value) {
        String t = get(model);
        return (null == t) ? ((null == value) ? 0 : -1) : ((null != value) ? t.compareTo(value) : 1);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object is equal to another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object is starts with {@code value}; otherwise, {@code false}.
     */
    default boolean startsWith(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object starts with another value.
     *
     * @param model The target {@link EntityModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object starts with {@code value}; otherwise, {@code false}.
     */
    default boolean startsWith(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object ends with another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object ends with {@code value}; otherwise, {@code false}.
     */
    default boolean endsWith(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.endsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object ends with another value.
     *
     * @param model The target {@link EntityModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object ends with {@code value}; otherwise, {@code false}.
     */
    default boolean endsWith(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object contains another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object contains {@code value}; otherwise, {@code false}.
     */
    default boolean contains(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.contains(s);
    }

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object contains another value.
     *
     * @param model The target {@link EntityModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object contains {@code value}; otherwise, {@code false}.
     */
    default boolean contains(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.contains(s);
    }

}
