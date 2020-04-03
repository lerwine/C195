package scheduler.dao.filter;

import java.util.function.ToIntFunction;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.view.model.ItemModel;
import scheduler.view.address.AddressModelImpl;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.city.CityModelImpl;
import scheduler.view.customer.CustomerModelImpl;

/**
 * Compares {@link DataAccessObject} and {@link ItemModel} to integer values.
 *
 * @author erwinel
 * @param <T> The type of {@link DataAccessObject} object.
 * @param <U> The type of {@link ItemModel} object.
 */
public interface ItemIntComparer<T extends DataAccessObject, U extends ItemModel<T>> {

    public static <T extends DataAccessObject, U extends ItemModel<T>> ItemIntComparer<T, U> of(ToIntFunction<T> getDaoValue, ToIntFunction<U> getModelValue) {
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
    
    public static <T extends DataAccessObject, U extends ItemModel<T>> ItemIntComparer<T, U> forPrimaryKey() {
        return new ItemIntComparer<T, U>() {
            @Override
            public int get(T dao) {
                return dao.getPrimaryKey();
            }

            @Override
            public int get(U model) {
                return model.getDataObject().getPrimaryKey();
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
            return model.getCustomer().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<AppointmentDAO, AppointmentModel> USER_APPOINTMENTS = new ItemIntComparer<AppointmentDAO, AppointmentModel>() {
        @Override
        public int get(AppointmentDAO dao) {
            return dao.getUser().getPrimaryKey();
        }

        @Override
        public int get(AppointmentModel model) {
            return model.getUser().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<CustomerDAO, CustomerModelImpl> ADDRESS_CUSTOMERS = new ItemIntComparer<CustomerDAO, CustomerModelImpl>() {
        @Override
        public int get(CustomerDAO dao) {
            return dao.getAddress().getPrimaryKey();
        }

        @Override
        public int get(CustomerModelImpl model) {
            return model.getAddress().getDataObject().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<AddressDAO, AddressModelImpl> CITY_ADDRESSES = new ItemIntComparer<AddressDAO, AddressModelImpl>() {
        @Override
        public int get(AddressDAO dao) {
            return dao.getCity().getPrimaryKey();
        }

        @Override
        public int get(AddressModelImpl model) {
            return model.getCity().getDataObject().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<CityDAO, CityModelImpl> COUNTRY_CITIES = new ItemIntComparer<CityDAO, CityModelImpl>() {
        @Override
        public int get(CityDAO dao) {
            return dao.getCountry().getPrimaryKey();
        }

        @Override
        public int get(CityModelImpl model) {
            return model.getCountry().getDataObject().getPrimaryKey();
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
     * Gets the integer value associated with the target {@link ItemModel}.
     *
     * @param model The target {@link ItemModel}.
     * @return The integer value associated with the target {@link ItemModel}.
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
     * Tests whether the value associated with an {@link ItemModel} object is equal to another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link ItemModel} object is equal to {@code value}; otherwise, {@code false}.
     */
    default boolean test(U model, int value) {
        return get(model) == value;
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param dao The target {@link DataAccessObject}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataAccessObject} object is equal to {@code value}. If the value associated with a {@link DataAccessObject} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataAccessObject} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, int value) {
        return Integer.compare(get(dao), value);
    }

    /**
     * Compares the value associated with a {@link DataAccessObject} object with another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link ItemModel} object is equal to {@code value}. If the value associated with a {@link ItemModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link ItemModel} object is greater than {@code value}.
     */
    default int compareTo(U model, int value) {
        return Integer.compare(get(model), value);
    }

}
