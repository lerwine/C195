package scheduler.dao;

import java.util.function.ToIntFunction;
import scheduler.view.ItemModel;
import scheduler.view.address.AddressModel;
import scheduler.view.appointment.AppointmentModel;
import scheduler.view.city.CityModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 * Compares {@link DataObjectImpl} and {@link ItemModel} to integer values.
 *
 * @author erwinel
 * @param <T> The type of {@link DataObjectImpl} object.
 * @param <U> The type of {@link ItemModel} object.
 */
public interface ItemIntComparer<T extends DataObjectImpl, U extends ItemModel<T>> {

    public static <T extends DataObjectImpl, U extends ItemModel<T>> ItemIntComparer<T, U> of(ToIntFunction<T> getDaoValue, ToIntFunction<U> getModelValue) {
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
    
    public static <T extends DataObjectImpl, U extends ItemModel<T>> ItemIntComparer<T, U> forPrimaryKey() {
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
    
    public static final ItemIntComparer<AppointmentImpl, AppointmentModel> CUSTOMER_APPOINTMENTS = new ItemIntComparer<AppointmentImpl, AppointmentModel>() {
        @Override
        public int get(AppointmentImpl dao) {
            return dao.getCustomer().getPrimaryKey();
        }

        @Override
        public int get(AppointmentModel model) {
            return model.getCustomer().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<AppointmentImpl, AppointmentModel> USER_APPOINTMENTS = new ItemIntComparer<AppointmentImpl, AppointmentModel>() {
        @Override
        public int get(AppointmentImpl dao) {
            return dao.getUser().getPrimaryKey();
        }

        @Override
        public int get(AppointmentModel model) {
            return model.getUser().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<CustomerImpl, CustomerModel> ADDRESS_CUSTOMERS = new ItemIntComparer<CustomerImpl, CustomerModel>() {
        @Override
        public int get(CustomerImpl dao) {
            return dao.getAddress().getPrimaryKey();
        }

        @Override
        public int get(CustomerModel model) {
            return model.getAddress().getDataObject().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<AddressImpl, AddressModel> CITY_ADDRESSES = new ItemIntComparer<AddressImpl, AddressModel>() {
        @Override
        public int get(AddressImpl dao) {
            return dao.getCity().getPrimaryKey();
        }

        @Override
        public int get(AddressModel model) {
            return model.getCity().getDataObject().getPrimaryKey();
        }
    };
    
    public static ItemIntComparer<CityImpl, CityModel> COUNTRY_CITIES = new ItemIntComparer<CityImpl, CityModel>() {
        @Override
        public int get(CityImpl dao) {
            return dao.getCountry().getPrimaryKey();
        }

        @Override
        public int get(CityModel model) {
            return model.getCountry().getDataObject().getPrimaryKey();
        }
    };
    
    /**
     * Gets the integer value associated with the target {@link DataObjectImpl}.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @return The integer value associated with the target {@link DataObjectImpl}.
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
     * Tests whether the value associated with a {@link DataObjectImpl} object is equal to another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}; otherwise, {@code false}.
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
     * Compares the value associated with a {@link DataObjectImpl} object with another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}. If the value associated with a {@link DataObjectImpl} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataObjectImpl} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, int value) {
        return Integer.compare(get(dao), value);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object with another value.
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
