package scheduler.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.dao.AddressDAO;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.dao.PartialCustomerDAO;
import scheduler.dao.PartialUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.fx.AddressModel;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CityModel;
import scheduler.model.fx.CountryModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialAddressModel;
import scheduler.model.fx.PartialAddressModelImpl;
import scheduler.model.fx.PartialCityModel;
import scheduler.model.fx.PartialCityModelImpl;
import scheduler.model.fx.PartialCountryModel;
import scheduler.model.fx.PartialCountryModelImpl;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialCustomerModelImpl;
import scheduler.model.fx.PartialUserModel;
import scheduler.model.fx.PartialUserModelImpl;
import scheduler.model.fx.UserModel;
import scheduler.util.DateTimeUtil;
import scheduler.util.LogHelper;
import static scheduler.util.ResourceBundleHelper.getResourceString;
import scheduler.util.Values;
import static scheduler.util.Values.appendEnum;
import static scheduler.util.Values.appendLocalDateTime;
import static scheduler.util.Values.appendString;
import static scheduler.util.Values.appendTimestamp;
import static scheduler.util.Values.asNonNullAndWsNormalized;
import scheduler.view.address.EditAddress;
import static scheduler.view.appointment.EditAppointmentResourceKeys.RESOURCEKEY_PHONENUMBER;

/**
 * Helper class for getting information about {@link PartialDataEntity} objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ModelHelper {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ModelHelper.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ModelHelper.class.getName());

    public static <T extends PartialDataEntity> BooleanBinding sameRecordBinding(ObservableObjectValue<? extends T> a, ObservableObjectValue<? extends T> b) {
        return Bindings.createBooleanBinding(() -> areSameRecord(a.get(), b.get()), a, b);
    }

    /**
     * Tests whether two {@link PartialDataEntity} objects represent the same database entity.
     *
     * @param <T> The {@link PartialDataEntity} type.
     * @param a The first {@link PartialDataEntity} compare.
     * @param b The second {@link PartialDataEntity} to compare.
     * @return {@code true} if both {@link PartialDataEntity}s represent the same database entity; otherwise, {@code false}.
     */
    public static <T extends PartialDataEntity> boolean areSameRecord(T a, T b) {
        LOG.entering(LOG.getName(), "areSameRecord", new Object[] { a, b });
        boolean result;
        if (null == a) {
            result = null == b;
        } else if (null == b) {
            result = false;
        } else if (a == b) {
            result = true;
        } else if (existsInDatabase(a)) {
            result = existsInDatabase(b) && a.getPrimaryKey() == b.getPrimaryKey();
        } else if (!existsInDatabase(b)) {
            result = arePropertiesEqual(a, b);
        } else {
            LOG.exiting(LOG.getName(), "areSameRecord", false);
            return false;
        }
        LOG.exiting(LOG.getName(), "areSameRecord", result);
        return result;
    }

    /**
     * Tests whether two {@link PartialDataEntity} objects represent the same database entity.
     *
     * @param <T> The {@link PartialDataEntity} type.
     * @param a The first {@link PartialDataEntity} compare.
     * @param b The second {@link PartialDataEntity} to compare.
     * @return {@code true} if both {@link PartialDataEntity}s represent the same database entity; otherwise, {@code false}.
     */
    public static <T extends PartialDataEntity> boolean arePropertiesEqual(T a, T b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        if (a == b) {
            return true;
        }
        if (a instanceof Appointment) {
            return b instanceof Appointment && AppointmentHelper.arePropertiesEqual((Appointment<?>) a, (Appointment<?>) b);
        }

        if (a instanceof Customer) {
            return b instanceof Customer && CustomerHelper.arePropertiesEqual((Customer) a, (Customer) b);
        }

        if (a instanceof AddressProperties) {
            return b instanceof AddressProperties && AddressHelper.arePropertiesEqual((AddressProperties) a, (AddressProperties) b);
        }

        if (a instanceof CityProperties) {
            return b instanceof CityProperties && CityHelper.arePropertiesEqual((CityProperties) a, (CityProperties) b);
        }

        if (a instanceof CountryProperties) {
            return b instanceof CountryProperties && CountryHelper.arePropertiesEqual((CountryProperties) a, (CountryProperties) b);
        }

        if (a instanceof User) {
            return b instanceof User && UserHelper.arePropertiesEqual((User) a, (User) b);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the primary key value for a {@link PartialDataEntity} object. If the target object has never been saved to the database or if it has been deleted, then this will return
     * {@link Integer#MIN_VALUE}.
     *
     * @param obj The target {@link PartialDataEntity} object.
     * @return The value from {@link PartialDataEntity#getPrimaryKey()} or {@link Integer#MIN_VALUE} if the object does not have a valid primary key value.
     */
    public static int getPrimaryKey(PartialDataEntity obj) {
        if (existsInDatabase(obj)) {
            return obj.getPrimaryKey();
        }
        return Integer.MIN_VALUE;
    }

    public static boolean existsInDatabase(PartialDataEntity obj) {
        if (null != obj) {
            switch (obj.getRowState()) {
                case MODIFIED:
                case UNMODIFIED:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * Asserts that the properties of a {@link DataAccessObject} can be applied to a {@link EntityModel}.
     *
     * @param <T> The {@link DataAccessObject} type.
     * @param <U> The {@link EntityModel} type.
     * @param source The source {@link DataAccessObject}.
     * @param targetFxModel The target {@link EntityModel}.
     * @return The target {@link EntityModel}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends DataAccessObject, U extends EntityModel<T>> U requiresAssignable(T source, U targetFxModel) {
        Objects.requireNonNull(source);
        if (null != targetFxModel && targetFxModel.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW
                || targetFxModel.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetFxModel;
    }

    /**
     * Asserts that the properties of a {@link EntityModel} can be applied to a {@link DataAccessObject}.
     *
     * @param <T> The {@link EntityModel} type.
     * @param <U> The {@link DataAccessObject} type.
     * @param source The source {@link EntityModel}.
     * @param targetDataAccessObject The target {@link DataAccessObject}.
     * @return The target {@link DataAccessObject}.
     * @throws IllegalArgumentException if the objects do not represent the same entity.
     */
    public static <T extends EntityModel<U>, U extends DataAccessObject> U requiresAssignable(T source, U targetDataAccessObject) {
        Objects.requireNonNull(source);
        if (null != targetDataAccessObject && targetDataAccessObject.getRowState() != DataRowState.NEW && (source.getRowState() == DataRowState.NEW
                || targetDataAccessObject.getPrimaryKey() != source.getPrimaryKey())) {
            throw new IllegalArgumentException("Objects do not represent the same database entity");
        }
        return targetDataAccessObject;
    }

    public static <T extends PartialDataEntity, U extends T> Optional<U> findMatching(T source, Stream<U> target) {
        if (null == source) {
            return Optional.empty();
        }
        if (source.getRowState() != DataRowState.NEW) {
            return findByPrimaryKey(source.getPrimaryKey(), target);
        }
        if (source instanceof Appointment) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && AppointmentHelper.arePropertiesEqual((Appointment<?>) source, (Appointment<?>) t)).findAny();
        }
        if (source instanceof Customer) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && CustomerHelper.arePropertiesEqual((Customer) source, (Customer) t)).findAny();
        }
        if (source instanceof Address) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && AddressHelper.arePropertiesEqual((AddressProperties) source, (AddressProperties) t)).findAny();
        }
        if (source instanceof City) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && CityHelper.arePropertiesEqual((CityProperties) source, (CityProperties) t)).findAny();
        }
        if (source instanceof Country) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && CountryHelper.arePropertiesEqual((CountryProperties) source, (CountryProperties) t)).findAny();
        }
        if (source instanceof User) {
            return target.filter((t) -> t.getRowState() == DataRowState.NEW && UserHelper.arePropertiesEqual((User) source, (User) t)).findAny();
        }
        throw new UnsupportedOperationException();
    }

    public static <T extends PartialDataEntity, U extends T> Optional<U> findMatching(T source, Collection<U> target) {
        if (null == source || null == target || target.isEmpty()) {
            return Optional.empty();
        }
        return findMatching(source, target.stream());
    }

    public static <T extends PartialDataEntity> Optional<T> findByPrimaryKey(int primaryKey, Stream<T> source) {
        if (null == source) {
            return Optional.empty();
        }
        return source.filter((t) -> ModelHelper.matchesPrimaryKey(t, primaryKey)).findAny();
    }

    public static <T extends PartialDataEntity> Optional<T> findByPrimaryKey(int primaryKey, Collection<T> source) {
        if (null == source || source.isEmpty()) {
            return Optional.empty();
        }
        return findByPrimaryKey(primaryKey, source.stream());
    }

    public static boolean matchesPrimaryKey(PartialDataEntity source, int pk) {
        return null != source && source.getRowState() != DataRowState.NEW && source.getPrimaryKey() == pk;
    }

    private ModelHelper() {
    }

    private static class StringBuilderHelper {

        @SuppressWarnings("unchecked")
        private static StringBuilder appendCountryPropertiesX(CountryProperties entity, StringBuilder builder) {
            if (entity instanceof SupportedCountryDefinition) {
                return CountryHelper.appendCountryProperties(entity, builder);
            }
            if (entity instanceof CountryDAO) {
                return CountryHelper.appendCountryProperties(entity, appendDataEntityPropertiesT((DataEntity<Timestamp>) entity, builder));
            }
            if (entity instanceof CountryModel) {
                return CountryHelper.appendCountryProperties(entity, appendDataEntityPropertiesL((DataEntity<LocalDateTime>) entity, builder));
            }
            if (entity instanceof Country) {
                return CountryHelper.appendCountryProperties(entity, appendPartialDataEntityProperties((PartialDataEntity) entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        @SuppressWarnings("unchecked")
        private static StringBuilder appendCityPropertiesX(CityProperties entity, StringBuilder builder) {
            if (entity instanceof SupportedCityDefinition) {
                return CityHelper.appendCityDefinitionProperties((SupportedCityDefinition) entity, builder);
            }
            if (entity instanceof CityDAO) {
                return CityHelper.appendCityProperties(entity, appendDataEntityPropertiesT((DataEntity<Timestamp>) entity, builder));
            }
            if (entity instanceof CityModel) {
                return CityHelper.appendCityProperties(entity, appendDataEntityPropertiesL((DataEntity<LocalDateTime>) entity, builder));
            }
            if (entity instanceof City) {
                return CityHelper.appendCityProperties(entity, appendPartialDataEntityProperties((PartialDataEntity) entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        @SuppressWarnings("unchecked")
        private static StringBuilder appendAddressPropertiesX(AddressProperties entity, StringBuilder builder) {
            if (entity instanceof CorporateAddress) {
                return AddressHelper.appendCorporateAddressProperties((CorporateAddress) entity, builder);
            }
            if (entity instanceof AddressDAO) {
                return AddressHelper.appendAddressProperties(entity, appendDataEntityPropertiesT((DataEntity<Timestamp>) entity, builder));
            }
            if (entity instanceof AddressModel) {
                return AddressHelper.appendAddressProperties(entity, appendDataEntityPropertiesL((DataEntity<LocalDateTime>) entity, builder));
            }
            if (entity instanceof Address) {
                return AddressHelper.appendAddressProperties(entity, appendPartialDataEntityProperties((PartialDataEntity) entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        private static StringBuilder appendPartialDataEntityProperties(PartialDataEntity entity, StringBuilder builder) {
            builder.append("primaryKey=").append(entity.getPrimaryKey()).append("; rowState=");
            return appendEnum(entity.getRowState(), builder);
        }

        private static StringBuilder appendPartialDataEntityPropertiesX(PartialDataEntity entity, StringBuilder builder) {
            if (entity instanceof DataEntity) {
                return appendDataEntityPropertiesX((DataEntity<?>) entity, builder);
            }

            if (entity instanceof User) {
                return UserHelper.appendUserProperties((User) entity, appendPartialDataEntityProperties(entity, builder));
            }

            if (entity instanceof Customer) {
                return CustomerHelper.appendCustomerProperties((Customer) entity, appendPartialDataEntityProperties(entity, builder));
            }

            if (entity instanceof Country) {
                return CountryHelper.appendCountryProperties((CountryProperties) entity, appendPartialDataEntityProperties(entity, builder));
            }

            if (entity instanceof Address) {
                return AddressHelper.appendAddressProperties((AddressProperties) entity, appendPartialDataEntityProperties(entity, builder));
            }

            if (entity instanceof SupportedCityDefinition) {
                return CityHelper.appendCityDefinitionProperties((SupportedCityDefinition) entity, appendPartialDataEntityProperties(entity, builder));
            }

            if (entity instanceof City) {
                return CityHelper.appendCityProperties((CityProperties) entity, appendPartialDataEntityProperties(entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        private static StringBuilder appendDataEntityPropertiesT(DataEntity<Timestamp> entity, StringBuilder builder) {
            appendPartialDataEntityProperties(entity, builder);
            appendTimestamp(entity.getCreateDate(), builder.append("; createDate="));
            appendString(entity.getCreatedBy(), builder.append("; createdBy="));
            appendTimestamp(entity.getLastModifiedDate(), builder.append("; lastModifiedDate="));
            return appendString(entity.getLastModifiedBy(), builder.append("; lastModifiedBy="));
        }

        private static StringBuilder appendDataEntityPropertiesTX(DataEntity<Timestamp> entity, StringBuilder builder) {
            if (entity instanceof Appointment) {
                return AppointmentHelper.appendAppointmentPropertiesT((Appointment<Timestamp>) entity, appendDataEntityPropertiesT(entity, builder));
            }
            if (entity instanceof Customer) {
                return CustomerHelper.appendCustomerProperties((Customer) entity, appendDataEntityPropertiesT(entity, builder));
            }
            if (entity instanceof UserEntity) {
                return UserHelper.appendUserEntityProperties((UserEntity<?>) entity, appendDataEntityPropertiesT(entity, builder));
            }
            if (entity instanceof Address) {
                return AddressHelper.appendAddressProperties((AddressProperties) entity, appendDataEntityPropertiesT(entity, builder));
            }
            if (entity instanceof City) {
                return CityHelper.appendCityProperties((CityProperties) entity, appendDataEntityPropertiesT(entity, builder));
            }
            if (entity instanceof Country) {
                return CountryHelper.appendCountryProperties((CountryProperties) entity, appendDataEntityPropertiesT(entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        private static StringBuilder appendDataEntityPropertiesL(DataEntity<LocalDateTime> entity, StringBuilder builder) {
            appendPartialDataEntityProperties(entity, builder);
            appendLocalDateTime(entity.getCreateDate(), builder.append("; createDate="));
            appendString(entity.getCreatedBy(), builder.append("; createdBy="));
            return appendLocalDateTime(entity.getLastModifiedDate(), builder.append("; lastModifiedDate="));
        }

        private static StringBuilder appendDataEntityPropertiesLX(DataEntity<LocalDateTime> entity, StringBuilder builder) {
            if (entity instanceof AppointmentModel) {
                return AppointmentHelper.appendAppointmentPropertiesL((Appointment<LocalDateTime>) entity, appendDataEntityPropertiesL(entity, builder));
            }
            if (entity instanceof Customer) {
                return CustomerHelper.appendCustomerProperties((Customer) entity, appendDataEntityPropertiesL(entity, builder));
            }
            if (entity instanceof UserEntity) {
                return UserHelper.appendUserEntityProperties((UserEntity<?>) entity, appendDataEntityPropertiesL(entity, builder));
            }
            if (entity instanceof Address) {
                return AddressHelper.appendAddressProperties((AddressProperties) entity, appendDataEntityPropertiesL(entity, builder));
            }
            if (entity instanceof City) {
                return CityHelper.appendCityProperties((CityProperties) entity, appendDataEntityPropertiesL(entity, builder));
            }
            if (entity instanceof Country) {
                return CountryHelper.appendCountryProperties((CountryProperties) entity, appendDataEntityPropertiesL(entity, builder));
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        @SuppressWarnings("unchecked")
        private static StringBuilder appendDataEntityPropertiesX(DataEntity<?> entity, StringBuilder builder) {
            if (entity instanceof DataAccessObject) {
                appendDataEntityPropertiesTX((DataEntity<Timestamp>) entity, builder);
            }

            if (entity instanceof EntityModel) {
                appendDataEntityPropertiesLX((DataEntity<LocalDateTime>) entity, builder);
            }

            throw new UnsupportedOperationException(String.format("Type %s  not supported", entity.getClass().getName()));
        }

        private StringBuilderHelper() {
        }

    }

    public static final class AppointmentHelper {

        private static final Logger LOG = Logger.getLogger(AppointmentHelper.class.getName());

        private static StringBuilder appendAppointmentPropertiesT(Appointment<Timestamp> appointment, StringBuilder builder) {
            appendTimestamp(appointment.getStart(), builder.append("; start="));
            appendTimestamp(appointment.getEnd(), builder.append("; end="));
            return appendAppointmentProperties(appointment, builder);
        }

        private static StringBuilder appendAppointmentPropertiesL(Appointment<LocalDateTime> appointment, StringBuilder builder) {
            appendLocalDateTime(appointment.getStart(), builder.append("; start="));
            appendLocalDateTime(appointment.getEnd(), builder.append("; end="));
            return appendAppointmentProperties(appointment, builder);
        }

        private static StringBuilder appendAppointmentProperties(Appointment<?> appointment, StringBuilder builder) {
            appendEnum(appointment.getType(), builder.append("; type="));
            appendString(appointment.getTitle(), builder.append("; title="));
            appendString(appointment.getContact(), builder.append("; contact="));
            Customer customer = appointment.getCustomer();
            User user = appointment.getUser();
            if (null == customer) {
                if (null == user) {
                    builder.append("; customer=null; user=null; ");
                } else {
                    builder.append("; customer=null;").append(Values.LINEBREAK_STRING)
                            .append("\tuser=").append(Values.indentText(user.toString(), 2, false));
                }
            } else {
                builder.append(";").append(Values.LINEBREAK_STRING).append("\tcustomer=").append(Values.indentText(customer.toString(), 1, false)).append(";").append(Values.LINEBREAK_STRING);
                if (null == user) {
                    builder.append("\tuser=null; description=");
                } else {
                    builder.append("\tuser=").append(Values.indentText(user.toString(), 2, false)).append(";").append(Values.LINEBREAK_STRING)
                            .append("\tdescription=");
                }
            }
            appendString(appointment.getDescription(), builder);
            appendString(appointment.getLocation(), builder.append(";").append(Values.LINEBREAK_STRING).append("\tlocation="));
            return appendString(appointment.getUrl(), builder.append(";").append(Values.LINEBREAK_STRING).append("\turl="));
        }

        public static StringBuilder appendModelProperties(AppointmentModel appointment, StringBuilder builder) {
            return appendAppointmentPropertiesL(appointment, StringBuilderHelper.appendDataEntityPropertiesL(appointment, builder));
        }

        public static StringBuilder appendDaoProperties(AppointmentDAO user, StringBuilder builder) {
            return appendAppointmentPropertiesT(user, StringBuilderHelper.appendDataEntityPropertiesT(user, builder));
        }

        public static int compare(Appointment<?> a, Appointment<?> b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            int result = DateTimeUtil.compareDates(a.getStart(), b.getStart());
            if (result == 0 && (result = DateTimeUtil.compareDates(a.getEnd(), b.getEnd())) == 0 && (result = CustomerHelper.compare(a.getCustomer(), b.getCustomer())) == 0
                    && (result = UserHelper.compare(a.getUser(), b.getUser())) == 0) {
                return a.getPrimaryKey() - b.getPrimaryKey();
            }
            return result;
        }

        public static boolean arePropertiesEqual(Appointment<?> a, Appointment<?> b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || (areSameRecord(a.getCustomer(), b.getCustomer())
                    && areSameRecord(a.getUser(), b.getUser())
                    && a.getContact().equalsIgnoreCase(b.getContact())
                    && a.getDescription().equalsIgnoreCase(b.getDescription())
                    && a.getLocation().equalsIgnoreCase(b.getLocation())
                    && a.getType() == b.getType()
                    && a.getTitle().equalsIgnoreCase(b.getTitle())
                    && a.getUrl().equalsIgnoreCase(b.getUrl())
                    && DateTimeUtil.areDatesEqual(a.getStart(), b.getStart())
                    && DateTimeUtil.areDatesEqual(a.getStart(), b.getEnd())));
        }

        public static <T extends Appointment<?>> Stream<T> matchesCustomerOrUser(int customerPrimaryKey, int userPrimaryKey, Stream<T> source) {
            if (null == source) {
                return Stream.empty();
            }

            return source.filter((t) -> ModelHelper.matchesPrimaryKey(t.getCustomer(), customerPrimaryKey) || ModelHelper.matchesPrimaryKey(t.getUser(), userPrimaryKey));
        }

        public static String calculateEffectiveLocation(final AppointmentType type, final String customerAddress,
                final String url, final String location) {
            LOG.entering(LOG.getName(), "calculateEffectiveLocation", new Object[] {type, customerAddress, url, location});
            String result;
            switch (type) {
                case CUSTOMER_SITE:
                    result = (customerAddress.isEmpty())
                            ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER)
                            : customerAddress;
                    break;
                case VIRTUAL:
                    result = (url.isEmpty())
                            ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL)
                            : url;
                    break;
                case CORPORATE_LOCATION:
                    final CorporateAddress corporateAddress = PredefinedData.getCorporateAddress(location);
                    result = AddressHelper.calculateSingleLineAddress(corporateAddress.getAddress1(),
                            corporateAddress.getAddress2(),
                            AddressHelper.calculateCityZipCountry(corporateAddress.getCity().getName(),
                                    corporateAddress.getCity().getCountry().getName(), corporateAddress.getPostalCode()),
                            corporateAddress.getPhone());
                    break;
                case PHONE:
                    result = (location.isEmpty())
                            ? AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_APPOINTMENTTYPE_PHONE)
                            : String.format("tel: %s", location);
                    break;
                default:
                    result = location;
                    break;
            }
            LOG.exiting(LOG.getName(), "calculateEffectiveLocation", result);
            return result;
        }

        public static <T extends Serializable & Comparable<? super T>> int compareByDates(final Appointment<T> a, final Appointment<T> b) {
            if (null == a) {
                return (null == b) ? 0 : 1;
            }
            if (null == b) {
                return -1;
            }
            T x = a.getStart();
            T y = b.getStart();
            if (null == x) {
                return (null == x) ? 0 : 1;
            }
            if (null == y) {
                return -1;
            }
            final int c = x.compareTo(y);
            if (c != 0) {
                return c;
            }
            x = a.getEnd();
            y = b.getEnd();
            if (null == x) {
                return (null == x) ? 0 : 1;
            }
            if (null == y) {
                return -1;
            }
            return x.compareTo(y);
        }

        private static String calculateDaoLocationImpl(AppointmentType type, String explicitLocation, String phone, CorporateAddress corporateLocation, CustomerModel customer) {
            LOG.entering(LOG.getName(), "calculateDaoLocationImpl", new Object[] {type, explicitLocation, phone, corporateLocation, customer});
            String result;
            switch (type) {
                case CORPORATE_LOCATION:
                    result = (null == corporateLocation) ? "" : corporateLocation.getName();
                    break;
                case CUSTOMER_SITE:
                    result = (null == customer) ? "" : customer.getMultiLineAddress();
                    break;
                case PHONE:
                    result = phone;
                    break;
                default:
                    result = explicitLocation;
                    break;
            }
            LOG.exiting(LOG.getName(), "calculateDaoLocationImpl", result);
            return result;
        }

        private static String calculateDaoLocation(AppointmentType type, String explicitLocation, String phone, CorporateAddress corporateLocation, CustomerModel customer) {
            return calculateDaoLocationImpl(type, Values.asNonNullAndWsNormalizedMultiLine(explicitLocation), Values.asNonNullAndWsNormalized(phone), corporateLocation, customer);
        }

        public static StringBinding daoLocationBinding(ReadOnlyObjectProperty<AppointmentType> type, StringBinding explicitLocation, StringBinding phone,
                ReadOnlyObjectProperty<CorporateAddress> corporateLocation, ReadOnlyObjectProperty<CustomerModel> customer, boolean stringBindingsAlreadyNormalized) {
            if (stringBindingsAlreadyNormalized) {
                return Bindings.createStringBinding(() -> calculateDaoLocationImpl(type.get(), explicitLocation.get(), phone.get(), corporateLocation.get(), customer.get()),
                        type, corporateLocation, customer, phone, explicitLocation);
            }
            return Bindings.createStringBinding(() -> calculateDaoLocation(type.get(), explicitLocation.get(), phone.get(), corporateLocation.get(), customer.get()),
                    type, corporateLocation, customer, phone, explicitLocation);
        }

        private AppointmentHelper() {
        }

    }

    public static class CustomerHelper {

        public static int compare(Customer a, Customer b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            String x = a.getName();
            String y = b.getName();
            int result = x.compareToIgnoreCase(y);
            if (result == 0) {
                return x.compareTo(y);
            }
            return result;
        }

        public static boolean arePropertiesEqual(Customer a, Customer b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName()) && a.isActive() == b.isActive()
                    && ModelHelper.areSameRecord(a.getAddress(), b.getAddress())));
        }

        public static <T extends Customer> Stream<T> matchesAddress(int addressPrimaryKey, Stream<T> source) {
            if (null == source) {
                return Stream.empty();
            }

            return source.filter((t) -> ModelHelper.matchesPrimaryKey(t.getAddress(), addressPrimaryKey));
        }

        public static <T extends Customer> Stream<T> matchesName(String name, Stream<T> source, boolean caseSensitive) {
            if (null == source || null == name) {
                return Stream.empty();
            }

            if (caseSensitive) {
                return source.filter((t) -> name.equals(t.getName()));
            }

            return source.filter((t) -> name.equalsIgnoreCase(t.getName()));
        }

        public static <T extends Customer> Stream<T> matchesName(String name, Collection<T> source, boolean caseSensitive) {
            if (null == source || source.isEmpty()) {
                return Stream.empty();
            }

            return matchesName(name, source.stream(), caseSensitive);
        }

        public static <T extends Customer> Stream<T> matchesName(String name, Stream<T> source) {
            return matchesName(name, source, false);
        }

        public static <T extends Customer> Stream<T> matchesName(String name, Collection<T> source) {
            return matchesName(name, source, false);
        }

        @SuppressWarnings("unchecked")
        public static PartialCustomerModel<? extends PartialCustomerDAO> createModel(PartialCustomerDAO t) {
            if (null == t) {
                return null;
            }
            if (t instanceof CustomerDAO) {
                return ((CustomerDAO) t).cachedModel(true);
            }

            return new PartialCustomerModelImpl((CustomerDAO.Partial) t);
        }

        private static StringBuilder appendCustomerProperties(Customer customer, StringBuilder builder) {
            appendString(customer.getName(), builder.append("; name=")).append("; active=").append(customer.isActive());
            Address address = customer.getAddress();
            if (null == address) {
                return builder.append("; address=null");
            }
            return builder.append(";").append(Values.LINEBREAK_STRING).append("\taddress=").append(Values.indentText(address.toString(), 2, false));
        }

        public static StringBuilder appendModelProperties(CustomerModel customer, StringBuilder builder) {
            return appendCustomerProperties(customer, StringBuilderHelper.appendDataEntityPropertiesL(customer, builder));
        }

        public static StringBuilder appendDaoProperties(CustomerDAO customer, StringBuilder builder) {
            return appendCustomerProperties(customer, StringBuilderHelper.appendDataEntityPropertiesT(customer, builder));
        }

        public static StringBuilder appendPartialDaoProperties(CustomerDAO.Partial customer, StringBuilder builder) {
            return appendCustomerProperties(customer, StringBuilderHelper.appendPartialDataEntityProperties(customer, builder));
        }

        public static StringBuilder appendPartialModelProperties(PartialCustomerModelImpl customer, StringBuilder builder) {
            return appendCustomerProperties(customer, StringBuilderHelper.appendPartialDataEntityProperties(customer, builder));
        }

        private CustomerHelper() {
        }

    }

    public static class AddressHelper {

        public static String toString(AddressProperties address) {
            if (null == address) {
                return "";
            }

            String cityZipCountry = address.getPostalCode();
            CityProperties city;
            if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
                if (null == (city = address.getCity())) {
                    cityZipCountry = "";
                } else if ((cityZipCountry = CityHelper.toString(city).trim()).isEmpty()) {
                    cityZipCountry = CountryHelper.toString(city.getCountry()).trim();
                } else {
                    String country = CountryHelper.toString(city.getCountry()).trim();
                    if (!country.isEmpty()) {
                        cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                    }
                }
            } else if (null != (city = address.getCity())) {
                String cityName = city.getName();
                String country = CountryHelper.toString(city.getCountry()).trim();
                if (null == cityName || (cityName = cityName.trim()).isEmpty()) {
                    if (!country.isEmpty()) {
                        cityZipCountry = String.format("%s, %s", cityZipCountry, cityName);
                    }
                } else {
                    if (country.isEmpty()) {
                        cityZipCountry = String.format("%s %s", cityName, cityZipCountry);
                    } else {
                        cityZipCountry = String.format("%s %s, %s", cityName, cityZipCountry, country);
                    }
                }
            } else {
                cityZipCountry = "";
            }
            StringBuilder sb = new StringBuilder();
            String s = address.getAddress1();
            if (null != s && !(s = s.trim()).isEmpty()) {
                sb.append(s);
            }
            s = address.getAddress2();
            if (null != s && !(s = s.trim()).isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("\n").append(s);
                } else {
                    sb.append(s);
                }
            }
            if (!cityZipCountry.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("\n").append(cityZipCountry);
                } else {
                    sb.append(cityZipCountry);
                }
            }
            s = address.getPhone();
            if (null != s && !(s = s.trim()).isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("\n").append(s);
                } else {
                    sb.append(s);
                }
            }
            return sb.toString();
        }

        public static int compare(AddressProperties a, AddressProperties b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            int result = CityHelper.compare(a.getCity(), b.getCity());
            if (result == 0) {
                String x = a.getAddress1();
                String y = b.getAddress1();
                if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                    x = a.getAddress2();
                    y = b.getAddress2();
                    if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                        x = a.getPostalCode();
                        y = b.getPostalCode();
                        if ((result = x.compareToIgnoreCase(y)) == 0 && (result = x.compareTo(y)) == 0) {
                            x = a.getPhone();
                            y = b.getPhone();
                            if ((result = x.compareToIgnoreCase(y)) == 0) {
                                return x.compareTo(y);
                            }
                        }
                    }
                }
            }
            return result;
        }

        public static boolean arePropertiesEqual(AddressProperties a, AddressProperties b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || (a.getAddress1().equalsIgnoreCase(b.getAddress1()) && a.getAddress2().equalsIgnoreCase(b.getAddress2())
                    && CityHelper.arePropertiesEqual(a.getCity(), b.getCity()) && a.getPostalCode().equalsIgnoreCase(b.getPostalCode())
                    && a.getPhone().equalsIgnoreCase(b.getPhone())));
        }

        public static <T extends Address> Stream<T> matchesCity(int cityPrimaryKey, Stream<T> source) {
            if (null == source) {
                return Stream.empty();
            }

            return source.filter((t) -> ModelHelper.matchesPrimaryKey(t.getCity(), cityPrimaryKey));
        }

        @SuppressWarnings("unchecked")
        public static PartialAddressModel<? extends PartialAddressDAO> createModel(PartialAddressDAO t) {
            if (null == t) {
                return null;
            }
            if (t instanceof AddressDAO) {
                return ((AddressDAO) t).cachedModel(true);
            }

            return new PartialAddressModelImpl((AddressDAO.Partial) t);
        }

        public static StringBinding createMultiLineAddressBinding(ObservableValue<String> address1, ObservableValue<String> address2,
                ObservableValue<String> cityZipCountry, ObservableValue<String> phone) {
            return Bindings.createStringBinding(() -> {
                String a1 = address1.getValue().trim();
                String a2 = address2.getValue().trim();
                String c = cityZipCountry.getValue().trim();
                String p = phone.getValue().trim();
                if (a1.isEmpty()) {
                    if (a2.isEmpty()) {
                        if (c.isEmpty()) {
                            return (p.isEmpty()) ? "" : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                        }
                        return (p.isEmpty()) ? c : String.format("%s\n%s %s", c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                    }
                    if (c.isEmpty()) {
                        return (p.isEmpty()) ? a2 : String.format("%s\n%s %s", a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                    }
                    return (p.isEmpty()) ? String.format("%s\n%s", a2, c)
                            : String.format("%s\n%s\n%s %s", a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                if (a2.isEmpty()) {
                    if (c.isEmpty()) {
                        return (p.isEmpty()) ? a1 : String.format("%s\n%s %s", a1, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                    }
                    return (p.isEmpty()) ? String.format("%s\n%s", a1, c)
                            : String.format("%s\n%s\n%s %s", a1, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                if (c.isEmpty()) {
                    return (p.isEmpty()) ? String.format("%s\n%s", a1, a2)
                            : String.format("%s\n%s\n%s %s", a1, a2, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
                }
                return (p.isEmpty()) ? String.format("%s\n%s\n%s", a1, a2, c)
                        : String.format("%s\n%s\n%s\n%s %s", a1, a2, c, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), p);
            }, address1, address2, cityZipCountry, phone);
        }

        /**
         * Formats both address lines into a one or two line string.
         *
         * @param line1 The first address line.
         * @param line2 The second address line.
         * @return A First and second address lines formatted as a 1 or 2 line white-space-normalized string.
         */
        public static String calculateAddressLines(String line1, String line2) {
            if ((line1 = asNonNullAndWsNormalized(line1)).isEmpty()) {
                return asNonNullAndWsNormalized(line2);
            }
            return ((line2 = asNonNullAndWsNormalized(line2)).isEmpty()) ? line1 : String.format("%s\n%s", line1, line2);
        }

        /**
         * Formats city, postal code and country into a single line.
         *
         * @param city The name of the city.
         * @param country The name of the country.
         * @param postalCode The postal code.
         * @return The city, postal code and country formated as a single white-space-normalized string.
         */
        public static String calculateCityZipCountry(String city, String country, String postalCode) {
            if ((city = asNonNullAndWsNormalized(city)).isEmpty()) {
                return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                        ? asNonNullAndWsNormalized(country)
                        : (((asNonNullAndWsNormalized(country)).isEmpty())
                        ? postalCode : String.format("%s, %s", postalCode, country));
            }
            if ((country = asNonNullAndWsNormalized(country)).isEmpty()) {
                return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                        ? city : String.format("%s %s", city, postalCode);
            }
            return ((postalCode = asNonNullAndWsNormalized(postalCode)).isEmpty())
                    ? String.format("%s, %s", city, country)
                    : String.format("%s %s, %s", city, postalCode, country);
        }

        public static String calculateCityZipCountry(City city, String postalCode) {
            if (null == city) {
                return calculateCityZipCountry("", "", postalCode);
            }
            Country country = city.getCountry();
            return calculateCityZipCountry(city.getName(), (null == country) ? "" : country.getName(), postalCode);
        }

        /**
         * Formats address as a multi-line string.
         *
         * @param address A 1 or 2 line white-space-normalized string, usually formatted using {@link #calculateAddressLines(String, String)}.
         * @param cityZipCountry A single line white-space-normalized string, usually formatted using {@link #calculateCityZipCountry(String, String, String)}.
         * @param phone The phone number string which will be normalized in this method.
         * @return A multi-line white-space-normalized address string.
         */
        public static String calculateMultiLineAddress(String address, String cityZipCountry, String phone) {
            if (address.isEmpty()) {
                if (cityZipCountry.isEmpty()) {
                    return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? ""
                            : String.format("%s %s", getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
                }
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? cityZipCountry : String.format("%s\n%s %s", cityZipCountry,
                        getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            if (cityZipCountry.isEmpty()) {
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address
                        : String.format("%s\n%s %s", address, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
            }
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? String.format("%s\n%s", address, cityZipCountry)
                    : String.format("%s\n%s\n%s %s", address, cityZipCountry, getResourceString(EditAddress.class, RESOURCEKEY_PHONENUMBER), phone);
        }

        /**
         * Formats an address as a single line string.
         *
         * @param address1 The first line of the street address which will be normalized by this method.
         * @param address2 The second line of the street address which will be normalized by this method.
         * @param cityZipCountry A single line white-space-normalized string, usually formatted using {@link #calculateCityZipCountry(String, String, String)}.
         * @param phone The phone number string which will be normalized in this method.
         * @return The address formatted as a single line white-space-normalized string.
         */
        public static String calculateSingleLineAddress(String address1, String address2, String cityZipCountry, String phone) {
            if ((address1 = asNonNullAndWsNormalized(address1)).isEmpty()) {
                if ((address2 = asNonNullAndWsNormalized(address2)).isEmpty()) {
                    return (cityZipCountry.isEmpty()) ? asNonNullAndWsNormalized(phone)
                            : (((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                            ? cityZipCountry : String.format("%s, %s", cityZipCountry, phone));
                }
                if (cityZipCountry.isEmpty()) {
                    return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address2 : String.format("%s, %s", address2, phone);
                }
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                        ? String.format("%s, %s", address2, cityZipCountry)
                        : String.format("%s, %s, %s", address2, cityZipCountry, phone);
            }
            if ((address2 = asNonNullAndWsNormalized(address2)).isEmpty()) {
                if (cityZipCountry.isEmpty()) {
                    return ((phone = asNonNullAndWsNormalized(phone)).isEmpty()) ? address1 : String.format("%s, %s", address1, phone);
                }
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                        ? String.format("%s, %s", address1, cityZipCountry)
                        : String.format("%s, %s, %s", address1, cityZipCountry, phone);
            }
            if (cityZipCountry.isEmpty()) {
                return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                        ? String.format("%s, %s", address1, address2)
                        : String.format("%s, %s, %s", address1, address2, phone);
            }
            return ((phone = asNonNullAndWsNormalized(phone)).isEmpty())
                    ? String.format("%s, %s, %s", address1, address2, cityZipCountry)
                    : String.format("%s, %s, %s, %s", address1, address2, cityZipCountry, phone);
        }

        private static StringBuilder appendAddressProperties(AddressProperties address, StringBuilder builder) {
            appendString(address.getAddress1(), builder.append("; address1="));
            appendString(address.getAddress2(), builder.append("; address2="));
            CityProperties city = address.getCity();
            if (null == city) {
                builder.append("; city=null; postalCode=");
            } else {
                builder.append(";").append(Values.LINEBREAK_STRING).append("\tcity=").append(Values.indentText(city.toString(), 2, false)).append(";")
                        .append(Values.LINEBREAK_STRING).append("\tpostalCode=");
            }
            return appendString(address.getPhone(), appendString(address.getPostalCode(), builder).append("; phone="));
        }

        public static StringBuilder appendCorporateAddressProperties(CorporateAddress address, StringBuilder builder) {
            appendString(address.getName(), builder.append("; name=")).append("; satelliteOffice=").append(address.isSatelliteOffice());
            return appendAddressProperties(address, builder);
        }

        public static StringBuilder appendModelProperties(AddressModel address, StringBuilder builder) {
            return appendAddressProperties(address, StringBuilderHelper.appendDataEntityPropertiesL(address, builder));
        }

        public static StringBuilder appendDaoProperties(AddressDAO address, StringBuilder builder) {
            return appendAddressProperties(address, StringBuilderHelper.appendDataEntityPropertiesT(address, builder));
        }

        public static StringBuilder appendPartialDaoProperties(AddressDAO.Partial address, StringBuilder builder) {
            return appendAddressProperties(address, StringBuilderHelper.appendPartialDataEntityProperties(address, builder));
        }

        public static StringBuilder appendPartialModelProperties(PartialAddressModelImpl address, StringBuilder builder) {
            return appendAddressProperties(address, StringBuilderHelper.appendPartialDataEntityProperties(address, builder));
        }

        private AddressHelper() {
        }

    }

    public static class CityHelper {

        public static String toString(CityProperties city) {
            if (null != city) {
                String n = city.getName();
                String country = CountryHelper.toString(city.getCountry()).trim();
                if (null == n || (n = n.trim()).isEmpty()) {
                    return country;
                }
                return (country.isEmpty()) ? n : String.format("%s, %s", n, country);
            }
            return "";
        }

        public static int compare(CityProperties a, CityProperties b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            int result = CountryHelper.compare(a.getCountry(), b.getCountry());
            if (result == 0) {
                String x = a.getName();
                String y = b.getName();
                if ((result = x.compareToIgnoreCase(y)) == 0) {
                    return x.compareTo(y);
                }
            }
            return result;
        }

        public static boolean arePropertiesEqual(CityProperties a, CityProperties b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || (a.getName().equalsIgnoreCase(b.getName())
                    && CountryHelper.arePropertiesEqual(a.getCountry(), b.getCountry())));
        }

        public static <T extends City> Stream<T> matchesCountry(int countryPrimaryKey, Stream<T> source) {
            if (null == source) {
                return Stream.empty();
            }

            return source.filter((t) -> ModelHelper.matchesPrimaryKey(t.getCountry(), countryPrimaryKey));
        }

        public static <T extends City> Stream<T> matchesCountry(int countryPrimaryKey, Collection<T> source) {
            if (null == source || source.isEmpty()) {
                return Stream.empty();
            }

            return matchesCountry(countryPrimaryKey, source.stream());
        }

        public static <T extends City> Stream<T> matchesName(String name, Stream<T> source, boolean caseSensitive) {
            if (null == source || null == name) {
                return Stream.empty();
            }

            if (caseSensitive) {
                return source.filter((t) -> name.equals(t.getName()));
            }

            return source.filter((t) -> name.equalsIgnoreCase(t.getName()));
        }

        public static <T extends City> Stream<T> matchesName(String name, Collection<T> source, boolean caseSensitive) {
            if (null == source || source.isEmpty()) {
                return Stream.empty();
            }

            return matchesName(name, source.stream(), caseSensitive);
        }

        public static <T extends City> Stream<T> matchesName(String name, Stream<T> source) {
            return matchesName(name, source, false);
        }

        public static <T extends City> Stream<T> matchesName(String name, Collection<T> source) {
            return matchesName(name, source, false);
        }

        @SuppressWarnings("unchecked")
        public static PartialCityModel<? extends PartialCityDAO> createModel(PartialCityDAO t) {
            if (null == t) {
                return null;
            }
            if (t instanceof CityDAO) {
                return ((CityDAO) t).cachedModel(true);
            }

            return new PartialCityModelImpl((CityDAO.Partial) t);
        }

        private static StringBuilder appendCityProperties(CityProperties city, StringBuilder builder) {
            appendString(city.getName(), builder.append("; name="));
            CountryProperties country = city.getCountry();
            if (null == country) {
                return builder.append("; country=null");
            }
            return builder.append(";").append(Values.LINEBREAK_STRING).append("\tcountry=").append(Values.indentText(country.toString(), 2, false));
        }

        public static StringBuilder appendCityDefinitionProperties(SupportedCityDefinition city, StringBuilder builder) {
            return appendCityProperties(city, builder).append(";").append(Values.LINEBREAK_STRING).append("\ttimeZone=").append(city.getTimeZone().getID());
        }

        public static StringBuilder appendModelProperties(CityModel city, StringBuilder builder) {
            return appendCityProperties(city, StringBuilderHelper.appendDataEntityPropertiesL(city, builder));
        }

        public static StringBuilder appendDaoProperties(CityDAO city, StringBuilder builder) {
            return appendCityProperties(city, StringBuilderHelper.appendDataEntityPropertiesT(city, builder));
        }

        public static StringBuilder appendPartialDaoProperties(CityDAO.Partial city, StringBuilder builder) {
            return appendCityProperties(city, StringBuilderHelper.appendPartialDataEntityProperties(city, builder));
        }

        public static StringBuilder appendPartialModelProperties(PartialCityModelImpl city, StringBuilder builder) {
            return appendCityProperties(city, StringBuilderHelper.appendPartialDataEntityProperties(city, builder));
        }

        private CityHelper() {
        }

    }

    public static class CountryHelper {

        public static String toString(CountryProperties country) {
            if (null != country) {
                String n = country.getName();
                return (null == n) ? "" : n;
            }
            return "";
        }

        public static String getCountryDisplayText(Locale locale) {
            if (null != locale) {
                return locale.getDisplayCountry();
            }
            return "";
        }

        public static String getCountryAndLanguageDisplayText(Locale locale) {
            if (null != locale) {
                String c = locale.getDisplayCountry();
                if (!c.isEmpty()) {
                    String d = locale.getDisplayLanguage();
                    if (d.isEmpty()) {
                        return c;
                    }
                    String v = locale.getDisplayVariant();
                    if (!(v.isEmpty() && (v = locale.getDisplayScript()).isEmpty())) {
                        return String.format("%s (%s, %s)", c, d, v);
                    }
                    return String.format("%s (%s)", c, d);
                }
            }
            return "";
        }

        public static String getLanguageDisplayText(Locale locale) {
            if (null != locale) {
                String d = locale.getDisplayLanguage();
                if (!d.isEmpty()) {
                    String v = locale.getDisplayVariant();
                    String s = locale.getDisplayScript();
                    if (v.isEmpty()) {
                        return (s.isEmpty()) ? d : String.format("%s (%s)", d, s);
                    }
                    return (s.isEmpty()) ? String.format("%s (%s)", d, v) : String.format("%s (%s, %s)", d, v, s);
                }
            }
            return "";
        }

        public static boolean arePropertiesEqual(CountryProperties a, CountryProperties b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || Objects.equals(a.getLocale(), b.getLocale()));
        }

        public static int compare(CountryProperties a, CountryProperties b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            String x = a.getName();
            String y = b.getName();
            int result = x.compareToIgnoreCase(y);
            if (result == 0) {
                return x.compareTo(y);
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        public static PartialCountryModel<? extends PartialCountryDAO> createModel(PartialCountryDAO t) {
            if (null == t) {
                return null;
            }
            if (t instanceof CountryDAO) {
                return ((CountryDAO) t).cachedModel(true);
            }

            return new PartialCountryModelImpl((CountryDAO.Partial) t);
        }

        private static StringBuilder appendCountryProperties(CountryProperties country, StringBuilder builder) {
            Locale locale = country.getLocale();
            builder.append("; locale=");
            if (null != locale) {
                builder.append("; locale=").append(locale.toLanguageTag()).append("; name=");
            } else {
                builder.append("; locale=; name=");
            }
            return appendString(country.getName(), builder);
        }

        public static StringBuilder appendCountryDefinitionProperties(SupportedCountryDefinition country, StringBuilder builder) {
            return appendCountryProperties(country, builder);
        }

        public static StringBuilder appendModelProperties(CountryModel country, StringBuilder builder) {
            return appendCountryProperties(country, StringBuilderHelper.appendDataEntityPropertiesL(country, builder));
        }

        public static StringBuilder appendDaoProperties(CountryDAO country, StringBuilder builder) {
            return appendCountryProperties(country, StringBuilderHelper.appendDataEntityPropertiesT(country, builder));
        }

        public static StringBuilder appendPartialDaoProperties(CountryDAO.Partial country, StringBuilder builder) {
            return appendCountryProperties(country, StringBuilderHelper.appendPartialDataEntityProperties(country, builder));
        }

        public static StringBuilder appendPartialModelProperties(PartialCountryModelImpl country, StringBuilder builder) {
            return appendCountryProperties(country, StringBuilderHelper.appendPartialDataEntityProperties(country, builder));
        }

        private CountryHelper() {
        }

    }

    public static class UserHelper {

        private static StringBuilder appendUserProperties(User user, StringBuilder builder) {
            return appendEnum(user.getStatus(), appendString(user.getUserName(), builder.append("; userName=")).append("; state="));
        }

        private static StringBuilder appendUserEntityProperties(UserEntity<?> user, StringBuilder builder) {
            return appendString(user.getPassword(), appendUserProperties(user, builder).append("; password="));
        }

        public static StringBuilder appendModelProperties(UserModel user, StringBuilder builder) {
            return appendUserEntityProperties(user, StringBuilderHelper.appendDataEntityPropertiesL(user, builder));
        }

        public static StringBuilder appendDaoProperties(UserDAO user, StringBuilder builder) {
            return appendUserEntityProperties(user, StringBuilderHelper.appendDataEntityPropertiesT(user, builder));
        }

        public static StringBuilder appendPartialDaoProperties(UserDAO.Partial user, StringBuilder builder) {
            return appendUserProperties(user, StringBuilderHelper.appendPartialDataEntityProperties(user, builder));
        }

        public static StringBuilder appendPartialModelProperties(PartialUserModelImpl user, StringBuilder builder) {
            return appendUserProperties(user, StringBuilderHelper.appendPartialDataEntityProperties(user, builder));
        }

        public static int compare(User a, User b) {
            if (Objects.equals(a, b)) {
                return 0;
            }
            if (null == a) {
                return 1;
            }
            if (null == b) {
                return -1;
            }

            String x = a.getUserName();
            String y = b.getUserName();
            int result = x.compareToIgnoreCase(y);
            if (result == 0) {
                return x.compareTo(y);
            }
            return result;
        }

        public static boolean arePropertiesEqual(User a, User b) {
            if (null == a) {
                return null == b;
            }

            return null != b && (a == b || (a.getUserName().equalsIgnoreCase(b.getUserName()) && a.getStatus().equals(b.getStatus())));
        }

        public static <T extends User> Stream<T> matchesUserName(String name, Stream<T> source, boolean caseSensitive) {
            if (null == source || null == name) {
                return Stream.empty();
            }

            if (caseSensitive) {
                return source.filter((t) -> name.equals(t.getUserName()));
            }

            return source.filter((t) -> name.equalsIgnoreCase(t.getUserName()));
        }

        public static <T extends User> Stream<T> matchesUserName(String name, Collection<T> source, boolean caseSensitive) {
            if (null == source || source.isEmpty()) {
                return Stream.empty();
            }

            return matchesUserName(name, source.stream(), caseSensitive);
        }

        public static <T extends User> Stream<T> matchesUserName(String name, Stream<T> source) {
            return matchesUserName(name, source, false);
        }

        public static <T extends User> Stream<T> matchesUserName(String name, Collection<T> source) {
            return matchesUserName(name, source, false);
        }

        @SuppressWarnings("unchecked")
        public static PartialUserModel<? extends PartialUserDAO> createModel(PartialUserDAO t) {
            if (null == t) {
                return null;
            }
            if (t instanceof UserDAO) {
                return ((UserDAO) t).cachedModel(true);
            }

            return new PartialUserModelImpl((UserDAO.Partial) t);
        }

        private UserHelper() {
        }

    }

}
