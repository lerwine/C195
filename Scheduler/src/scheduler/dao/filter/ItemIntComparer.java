package scheduler.dao.filter;

import java.util.function.ToIntFunction;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.model.ModelHelper;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.AddressModel;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CityModel;
import scheduler.model.ui.CustomerModel;

/**
 * Compares {@link DataAccessObject} and {@link FxRecordModel} to integer values.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link FxRecordModel} object.
 */
public interface ItemIntComparer<T extends DataAccessObject, U extends FxRecordModel<T>> {

    public static <T extends DataAccessObject, U extends FxRecordModel<T>> ItemIntComparer<T, U> of(ToIntFunction<T> getDaoValue, ToIntFunction<U> getModelValue) {
        return new ItemIntComparer<T, U>() {
            @Override
            public int get(T dao) {
                return getDaoValue.applyAsInt(dao);
            }

            @Override
            public int get(U model) {
                return getModelValue.applyAsInt(model);
            }
        };
    }

    public static <T extends DataAccessObject, U extends FxRecordModel<T>> ItemIntComparer<T, U> forPrimaryKey() {
        return new ItemIntComparer<T, U>() {
            @Override
            public int get(T dao) {
                return dao.getPrimaryKey();
            }

            @Override
            public int get(U model) {
                return model.dataObject().getPrimaryKey();
            }
        };
    }

    public static final ItemIntComparer<AppointmentDAO, AppointmentModel> CUSTOMER_APPOINTMENTS = new ItemIntComparer<AppointmentDAO, AppointmentModel>() {
        @Override
        public int get(AppointmentDAO dao) {
            return dao.getCustomer().getPrimaryKey();
        }

        @Override
        public int get(AppointmentModel model) {
            return ModelHelper.getPrimaryKey(model.getCustomer());
        }
    };

    public static ItemIntComparer<AppointmentDAO, AppointmentModel> USER_APPOINTMENTS = new ItemIntComparer<AppointmentDAO, AppointmentModel>() {
        @Override
        public int get(AppointmentDAO dao) {
            return dao.getUser().getPrimaryKey();
        }

        @Override
        public int get(AppointmentModel model) {
            return ModelHelper.getPrimaryKey(model.getUser());
        }
    };

    public static ItemIntComparer<CustomerDAO, CustomerModel> ADDRESS_CUSTOMERS = new ItemIntComparer<CustomerDAO, CustomerModel>() {
        @Override
        public int get(CustomerDAO dao) {
            return dao.getAddress().getPrimaryKey();
        }

        @Override
        public int get(CustomerModel model) {
            return ModelHelper.getPrimaryKey(model.getAddress());
        }
    };

    public static ItemIntComparer<AddressDAO, AddressModel> CITY_ADDRESSES = new ItemIntComparer<AddressDAO, AddressModel>() {
        @Override
        public int get(AddressDAO dao) {
            return ModelHelper.getPrimaryKey(dao.getCity());
        }

        @Override
        public int get(AddressModel model) {
            return ModelHelper.getPrimaryKey(model.getCity());
        }
    };

    public static ItemIntComparer<CityDAO, CityModel> COUNTRY_CITIES = new ItemIntComparer<CityDAO, CityModel>() {
        @Override
        public int get(CityDAO dao) {
            return dao.getCountry().getPrimaryKey();
        }

        @Override
        public int get(CityModel model) {
            return ModelHelper.getPrimaryKey(model.getCountry());
        }
    };

    /**
     * Gets the integer value associated with the target {@link DataAccessObject}.
     *
     * @param dao The target {@link DataAccessObject}.
     * @return The integer value associated with the target {@link DataAccessObject}.
     */
    int get(T dao);

    /**
     * Gets the integer value associated with the target {@link FxRecordModel}.
     *
     * @param model The target {@link FxRecordModel}.
     * @return The integer value associated with the target {@link FxRecordModel}.
     */
    int get(U model);

    /**
     * Tests whether the value associated with a {@link DataAccessObject} object is equal to another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataAccessObject} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(T dao, int value) {
        return get(dao) == value;
    }

    /**
     * Tests whether the value associated with an {@link FxRecordModel} object is equal to another value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link FxRecordModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, int value) {
        return get(model) == value;
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a
     * {@link DataAccessObject} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link DataAccessObject} object is greater than {@code value}.
     */
    default int compareTo(T dao, int value) {
        return Integer.compare(get(dao), value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param model The target {@link FxRecordModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link FxRecordModel} object is equal to {@code value}. If the value associated with a
     * {@link FxRecordModel} object is less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value
     * associated with a {@link FxRecordModel} object is greater than {@code value}.
     */
    default int compareTo(U model, int value) {
        return Integer.compare(get(model), value);
    }

}
