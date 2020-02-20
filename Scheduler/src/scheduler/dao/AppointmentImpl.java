package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import scheduler.App;
import scheduler.util.DB;
import scheduler.util.Values;
import scheduler.view.ItemModel;
import scheduler.view.appointment.AppointmentModel;

public class AppointmentImpl extends DataObjectImpl implements Appointment {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    //<editor-fold defaultstate="collapsed" desc="Column names">
    /**
     * The name of the 'appointmentId' column in the 'appointment' table, which is also the primary key.
     */
    public static final String COLNAME_APPOINTMENTID = "appointmentId";

    /**
     * The name of the 'customerId' column in the 'appointment' table.
     */
    public static final String COLNAME_CUSTOMERID = "customerId";

    /**
     * The name of the 'userId' column in the 'appointment' table.
     */
    public static final String COLNAME_USERID = "userId";

    /**
     * The name of the 'title' column in the 'appointment' table.
     */
    public static final String COLNAME_TITLE = "title";

    /**
     * The name of the 'description' column in the 'appointment' table.
     */
    public static final String COLNAME_DESCRIPTION = "description";

    /**
     * The name of the 'location' column in the 'appointment' table.
     */
    public static final String COLNAME_LOCATION = "location";

    /**
     * The name of the 'contact' column in the 'appointment' table.
     */
    public static final String COLNAME_CONTACT = "contact";

    /**
     * The name of the 'type' column in the 'appointment' table.
     */
    public static final String COLNAME_TYPE = "type";

    /**
     * The name of the 'url' column in the 'appointment' table.
     */
    public static final String COLNAME_URL = "url";

    /**
     * The name of the 'start' column in the 'appointment' table.
     */
    public static final String COLNAME_START = "start";

    /**
     * The name of the 'end' column in the 'appointment' table.
     */
    public static final String COLNAME_END = "end";

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="customer property">
    private DataObjectReference<CustomerImpl, Customer> customer;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataObjectReference<CustomerImpl, Customer> getCustomer() {
        return customer;
    }

    /**
     * Set the value of customer
     *
     * @param value new value of customer
     */
    public void setCustomer(DataObjectReference<CustomerImpl, Customer> value) {
        Objects.requireNonNull(value);
        customer = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="user property">
    private DataObjectReference<UserImpl, User> user;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataObjectReference<UserImpl, User> getUser() {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param value new value of user
     */
    public void setUser(DataObjectReference<UserImpl, User> value) {
        Objects.requireNonNull(value);
        user = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="title property">
    private String title;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param value new value of title
     */
    public void setTitle(String value) {
        title = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="description property">
    private String description;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param value new value of description
     */
    public void setDescription(String value) {
        description = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="location property">
    private String location;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param value new value of location
     */
    public void setLocation(String value) {
        location = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="contact property">
    private String contact;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContact() {
        return contact;
    }

    /**
     * Set the value of contact
     *
     * @param value new value of contact
     */
    public void setContact(String value) {
        contact = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="type property">
    private String type;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param value new value of type
     */
    public void setType(String value) {
        type = Values.asValidAppointmentType(value);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="url property">
    private String url;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Set the value of url
     *
     * @param value new value of url
     */
    public void setUrl(String value) {
        url = (value == null) ? "" : value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="start property">
    private Timestamp start;

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getStart() {
        return start;
    }

    /**
     * Set the value of start
     *
     * @param value new value of start
     */
    public void setStart(Timestamp value) {
        Objects.requireNonNull(value);
        start = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="end property">
    private Timestamp end;

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getEnd() {
        return end;
    }

    /**
     * Set the value of end
     *
     * @param value new value of end
     */
    public void setEnd(Timestamp value) {
        Objects.requireNonNull(value);
        end = value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    private static final String BASE_SELECT_QUERY;

    static {
        StringBuilder sql = new StringBuilder("SELECT e.`");
        sql.append(COLNAME_APPOINTMENTID).append("` AS `").append(COLNAME_APPOINTMENTID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE,
                COLNAME_LASTUPDATEBY, COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END).forEach((t) -> {
                    sql.append("`, e.`").append(t).append("` AS `").append(t);
                });
        sql.append("`, p.`").append(CustomerImpl.COLNAME_CUSTOMERNAME).append("` AS `").append(CustomerImpl.COLNAME_CUSTOMERNAME)
                .append("`, p.`").append(CustomerImpl.COLNAME_ADDRESSID).append("` AS `").append(CustomerImpl.COLNAME_ADDRESSID);
        Stream.of(AddressImpl.COLNAME_ADDRESS, AddressImpl.COLNAME_ADDRESS2, AddressImpl.COLNAME_CITYID, AddressImpl.COLNAME_POSTALCODE,
                AddressImpl.COLNAME_PHONE).forEach((t) -> {
                    sql.append("`, a.`").append(t).append("` AS `").append(t);
                });
        BASE_SELECT_QUERY = sql.append("`, u.`").append(UserImpl.COLNAME_USERID).append("` AS `").append(UserImpl.COLNAME_USERID)
                .append("`, c.`").append(CityImpl.COLNAME_CITY).append("` AS `").append(CityImpl.COLNAME_CITY)
                .append("`, c.`").append(CityImpl.COLNAME_COUNTRYID).append("` AS `").append(CityImpl.COLNAME_COUNTRYID)
                .append("`, n.`").append(CountryImpl.COLNAME_COUNTRY).append("` AS `").append(CountryImpl.COLNAME_COUNTRY)
                .append("` FROM `").append((new AppointmentImpl.FactoryImpl()).getTableName())
                .append("` e LEFT JOIN `").append(CustomerImpl.getFactory().getTableName()).append("` p ON e.`").append(COLNAME_CUSTOMERID).append("`=p.`").append(CustomerImpl.COLNAME_CUSTOMERID)
                .append("` LEFT JOIN `").append(AddressImpl.getFactory().getTableName()).append("` a ON p.`").append(CustomerImpl.COLNAME_ADDRESSID).append("`=a.`").append(AddressImpl.COLNAME_ADDRESSID)
                .append("` LEFT JOIN `").append(CityImpl.getFactory().getTableName()).append("` c ON a.`").append(AddressImpl.COLNAME_CITYID).append("`=c.`").append(CityImpl.COLNAME_CITYID)
                .append("` LEFT JOIN `").append(CountryImpl.getFactory().getTableName()).append("` n ON c.`").append(CityImpl.COLNAME_COUNTRYID).append("`=n.`").append(CountryImpl.COLNAME_COUNTRYID)
                .append("` LEFT JOIN `").append(UserImpl.getFactory().getTableName()).append("` u ON e.`").append(COLNAME_USERID).append("`=u.`").append(UserImpl.COLNAME_USERID).toString();
    }

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link Values#ROWSTATE_NEW} appointment object.
     */
    public AppointmentImpl() {
        customer = null;
        user = null;
        title = "";
        description = "";
        location = "";
        contact = "";
        type = Values.APPOINTMENTTYPE_OTHER;
        url = null;
        LocalDateTime d = LocalDateTime.now().plusHours(1).plusMinutes(30);
        d = d.minusMinutes(d.getMinute()).minusSeconds(d.getSecond()).minusNanos(d.getNano());
        start = DB.toUtcTimestamp(d);
        end = DB.toUtcTimestamp(d.plusHours(1));
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();

    public static FactoryImpl getFactory() {
        return FACTORY;
    }

    public static final class FactoryImpl extends DataObjectImpl.Factory<AppointmentImpl, AppointmentModel> {

        // This is a singleton instance
        private FactoryImpl() {
        }

        @Override
        protected AppointmentImpl fromResultSet(ResultSet resultSet) throws SQLException {
            AppointmentImpl r = new AppointmentImpl();
            onInitializeDao(r, resultSet);
            return r;
        }

        @Override
        public String getBaseSelectQuery() {
            return BASE_SELECT_QUERY;
        }

        @Override
        public Class<? extends AppointmentImpl> getDaoClass() {
            return AppointmentImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_APPOINTMENT;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_APPOINTMENTID;
        }

        @Override
        protected List<String> getExtendedColNames() {
            return Arrays.asList(COLNAME_CUSTOMERID, COLNAME_USERID, COLNAME_TITLE, COLNAME_DESCRIPTION, COLNAME_LOCATION,
                    COLNAME_CONTACT, COLNAME_TYPE, COLNAME_URL, COLNAME_START, COLNAME_END);
        }

        @Override
        protected void setSaveStatementValues(AppointmentImpl dao, PreparedStatement ps) throws SQLException {
            ps.setInt(1, dao.getCustomer().getPrimaryKey());
            ps.setInt(2, dao.getUser().getPrimaryKey());
            ps.setString(3, dao.getTitle());
            ps.setString(4, dao.getDescription());
            ps.setString(5, dao.getLocation());
            ps.setString(6, dao.getContact());
            ps.setString(7, dao.getType());
            ps.setString(8, dao.getUrl());
            ps.setTimestamp(9, dao.getStart());
            ps.setTimestamp(10, dao.getEnd());
        }

        @Override
        protected void onInitializeDao(AppointmentImpl target, ResultSet resultSet) throws SQLException {
            target.customer = DataObjectReference.of(Customer.of(resultSet, AppointmentImpl.COLNAME_CUSTOMERID));
            target.user = DataObjectReference.of(User.of(resultSet, AppointmentImpl.COLNAME_USERID));
            target.title = resultSet.getString(AppointmentImpl.COLNAME_TITLE);
            if (resultSet.wasNull()) {
                target.title = "";
            }
            target.description = resultSet.getString(AppointmentImpl.COLNAME_DESCRIPTION);
            if (resultSet.wasNull()) {
                target.description = "";
            }
            target.location = resultSet.getString(AppointmentImpl.COLNAME_LOCATION);
            if (resultSet.wasNull()) {
                target.location = "";
            }
            target.contact = resultSet.getString(AppointmentImpl.COLNAME_CONTACT);
            if (resultSet.wasNull()) {
                target.contact = "";
            }
            target.type = resultSet.getString(AppointmentImpl.COLNAME_TYPE);
            if (resultSet.wasNull()) {
                target.type = Values.APPOINTMENTTYPE_OTHER;
            } else {
                target.type = Values.asValidAppointmentType(target.type);
            }
            target.url = resultSet.getString(AppointmentImpl.COLNAME_URL);
            if (resultSet.wasNull()) {
                target.url = "";
            }
            target.start = resultSet.getTimestamp(AppointmentImpl.COLNAME_START);
            if (resultSet.wasNull()) {
                target.end = resultSet.getTimestamp(AppointmentImpl.COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = DB.toUtcTimestamp(LocalDateTime.now());
                }
                target.start = target.end;
            } else {
                target.end = resultSet.getTimestamp(AppointmentImpl.COLNAME_END);
                if (resultSet.wasNull()) {
                    target.end = target.start;
                }
            }
        }

        //<editor-fold defaultstate="collapsed" desc="get*Filter methods">
        //<editor-fold defaultstate="collapsed" desc="getAllItemsFilter overloads">
        @Override
        public ModelFilter<AppointmentImpl, AppointmentModel> getAllItemsFilter() {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                @Override
                public String getHeading() {
                    return App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTS);
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getAllItemsFilter(Customer customer) {
            if (null == customer) {
                return getAllItemsFilter();
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final int customerId = customer.getPrimaryKey();

                @Override
                public String getHeading() {
                    return App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR);
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId;
                }

            };
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getAllItemsFilter(int userId, String heading, String subHeading, String whereClause,
                String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getUser().getPrimaryKey() == userId;
                }
            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getAllItemsFilter(User user) {
            if (null == user) {
                return getAllItemsFilter();
            }
            return getAllItemsFilter(user.getPrimaryKey(), App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFOR), "",
                    String.format("`%s` = ?", COLNAME_USERID), App.getResourceString(App.RESOURCEKEY_LOADINGAPPOINTMENTS));
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getAllItemsFilter(User user, Customer customer) {
            if (null == user) {
                if (null == customer) {
                    return getAllItemsFilter();
                }
                return getAllItemsFilter(customer);
            }
            if (null == customer) {
                return getAllItemsFilter(user);
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final int userId = user.getPrimaryKey();
                private final int customerId = customer.getPrimaryKey();

                @Override
                public String getHeading() {
                    return App.getResourceString(App.RESOURCEKEY_ALLAPPOINTMENTSFORBOTH);
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId;
                }

            };
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getBeforeItemsFilter overloads">
        private ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(LocalDate end, String heading, String subHeading, String whereClause,
                String loadingMessage) {
            if (null == end) {
                return getAllItemsFilter();
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getStart().compareTo(e) < 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(LocalDate end) {
            if (null == end) {
                return getAllItemsFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSBEFORE
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getCustomerBeforeItemsFilter(int customerId, LocalDate end, String heading,
                String subHeading, String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getStart().compareTo(e) < 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(Customer customer, LocalDate end) {
            if (null == customer) {
                if (null == end) {
                    return getAllItemsFilter();
                }
                return getBeforeItemsFilter(end);
            }
            if (null == end) {
                return getAllItemsFilter(customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSBEFOREFOR
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(int userId, LocalDate end, String heading, String subHeading,
                String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0;
                }
            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(User user, LocalDate end) {
            if (null == user) {
                if (null == end) {
                    return getAllItemsFilter();
                }
                return getBeforeItemsFilter(end);
            }
            if (null == end) {
                return getAllItemsFilter(user);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSBEFOREFOR
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(int userId, int customerId, LocalDate end, String heading,
                String subHeading, String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId && t.getStart().compareTo(e) < 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getBeforeItemsFilter(User user, Customer customer, LocalDate end) {
            if (null == user) {
                return getBeforeItemsFilter(customer, end);
            }
            if (null == customer) {
                return getBeforeItemsFilter(user, end);
            }
            if (null == end) {
                return getAllItemsFilter(user, customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSBEFOREFORBOTH
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getOnOrAfterItemsFilter overloads">

        private ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(LocalDate start, String heading, String subHeading,
                String whereClause, String loadingMessage) {
            if (null == start) {
                return getAllItemsFilter();
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getEnd().compareTo(s) >= 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(LocalDate start) {
            if (null == start) {
                return getAllItemsFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSAFTER
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getCustomerOnOrAfterItemsFilter(int customerId, LocalDate start, String heading,
                String subHeading, String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getEnd().compareTo(s) >= 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(Customer customer, LocalDate start) {
            if (null == customer) {
                if (null == start) {
                    return getAllItemsFilter();
                }
                return getOnOrAfterItemsFilter(start);
            }
            if (null == start) {
                return getAllItemsFilter(customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSAFTERFOR
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(int userId, LocalDate start, String heading, String subHeading,
                String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getUser().getPrimaryKey() == userId && t.getEnd().compareTo(s) >= 0;
                }
            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(User user, LocalDate start) {
            if (null == user) {
                if (null == start) {
                    return getAllItemsFilter();
                }
                return getOnOrAfterItemsFilter(start);
            }
            if (null == start) {
                return getAllItemsFilter(user);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSAFTERFOR
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(int userId, int customerId, LocalDate start, String heading,
                String subHeading, String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId && t.getEnd().compareTo(s) >= 0;
                }

            };
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getOnOrAfterItemsFilter(User user, Customer customer, LocalDate start) {
            if (null == user) {
                return getOnOrAfterItemsFilter(customer, start);
            }
            if (null == customer) {
                return getOnOrAfterItemsFilter(user, start);
            }
            if (null == start) {
                return getAllItemsFilter(user, customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSAFTERFORBOTH
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getRangeItemsFilter overloads">
        private ModelFilter<AppointmentImpl, AppointmentModel> getRangeItemsFilter(LocalDate start, LocalDate end) {
            if (null == start) {
                if (null == end) {
                    return getAllItemsFilter();
                }
                return getBeforeItemsFilter(end);
            }
            if (null == end) {
                return getOnOrAfterItemsFilter(start);
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    // TODO: RESOURCEKEY_APPOINTMENTSBETWEEN
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

            };
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getRangeItemsFilter(Customer customer, LocalDate start, LocalDate end) {
            if (null == customer) {
                return getRangeItemsFilter(start, end);
            }
            if (null == start) {
                return getBeforeItemsFilter(customer, end);
            }
            if (null == end) {
                return getOnOrAfterItemsFilter(customer, start);
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final int customerId = customer.getPrimaryKey();
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    // TODO: RESOURCEKEY_APPOINTMENTSBETWEENFOR
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getEnd().compareTo(s) >= 0 && t.getStart().compareTo(e) < 0;
                }

            };
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getRangeItemsFilter(int userId, LocalDate start, LocalDate end, String heading,
                String subHeading, String whereClause, String loadingMessage) {
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    return heading;
                }

                @Override
                public String getSubHeading() {
                    return subHeading;
                }

                @Override
                public String getWhereClause() {
                    return whereClause;
                }

                @Override
                public String getLoadingMessage() {
                    return loadingMessage;
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getUser().getPrimaryKey() == userId && t.getEnd().compareTo(s) >= 0 && t.getStart().compareTo(e) < 0;
                }
            };
        }

        private ModelFilter<AppointmentImpl, AppointmentModel> getRangeItemsFilter(User user, LocalDate start, LocalDate end) {
            if (null == user) {
                return getRangeItemsFilter(start, end);
            }
            if (null == start) {
                return getBeforeItemsFilter(user, end);
            }
            if (null == end) {
                return getOnOrAfterItemsFilter(user, start);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_APPOINTMENTSBETWEENFOR
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getRangeItemsFilter(User user, Customer customer, LocalDate start, LocalDate end) {
            if (null == end) {
                return getOnOrAfterItemsFilter(user, customer, start);
            }
            if (null == start) {
                return getBeforeItemsFilter(user, customer, end);
            }
            if (null == customer) {
                return getRangeItemsFilter(user, start, end);
            }
            if (null == user) {
                return getRangeItemsFilter(customer, start, end);
            }
            return new ModelFilter<AppointmentImpl, AppointmentModel>() {
                private final int userId = user.getPrimaryKey();
                private final int customerId = customer.getPrimaryKey();
                private final LocalDateTime s = start.atTime(0, 0, 0, 0);
                private final LocalDateTime e = end.atTime(0, 0, 0, 0).plusDays(1L);

                @Override
                public String getHeading() {
                    // TODO: RESOURCEKEY_APPOINTMENTSBETWEENFORBOTH
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getSubHeading() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getWhereClause() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getLoadingMessage() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Factory<AppointmentImpl, ? extends ItemModel<AppointmentImpl>> getFactory() {
                    return FactoryImpl.this;
                }

                @Override
                public List<AppointmentImpl> apply(Connection t) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean test(AppointmentModel t) {
                    return t.getCustomer().getPrimaryKey() == customerId && t.getUser().getPrimaryKey() == userId && t.getEnd().compareTo(s) >= 0
                            && t.getStart().compareTo(e) < 0;
                }

            };
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="*TodayAndFutureFilter methods">

        public ModelFilter<AppointmentImpl, AppointmentModel> getTodayAndFutureFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_CURRENTANDFUTURE
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getTodayAndFutureFilter(Customer customer) {
            if (null == customer) {
                return getTodayAndFutureFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_CURRENTANDFUTUREFOR
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getTodayAndFutureFilter(User user) {
            if (null == user) {
                return getTodayAndFutureFilter();
            }
            if (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey()) {
                return getMyTodayAndFutureFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_CURRENTANDFUTUREFOR
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getTodayAndFutureFilter(User user, Customer customer) {
            if (null == customer) {
                return getTodayAndFutureFilter(user);
            }
            if (null == user) {
                return getTodayAndFutureFilter(customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: cRESOURCEKEY_CURRENTANDFUTUREFORBOTH
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getMyTodayAndFutureFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_MYCURRENTANDFUTURE
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="*YesterdayAndPastFilter methods">

        public ModelFilter<AppointmentImpl, AppointmentModel> getYesterdayAndPastFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_PASTAPPOINTMENTS
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getYesterdayAndPastFilter(Customer customer) {
            if (null == customer) {
                return getYesterdayAndPastFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_PASTAPPOINTMENTSFOR
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getYesterdayAndPastFilter(User user) {
            if (null == user) {
                return getYesterdayAndPastFilter();
            }
            if (user.getPrimaryKey() == App.getCurrentUser().getPrimaryKey()) {
                return getMyYesterdayAndPastFilter();
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_PASTAPPOINTMENTSFOR
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getYesterdayAndPastFilter(User user, Customer customer) {
            if (null == customer) {
                return getYesterdayAndPastFilter(user);
            }
            if (null == user) {
                return getYesterdayAndPastFilter(customer);
            }
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_PASTAPPOINTMENTSFORBOTH
        }

        public ModelFilter<AppointmentImpl, AppointmentModel> getMyYesterdayAndPastFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: RESOURCEKEY_MYPASTAPPOINTMENTS
        }
        //</editor-fold>

        @Override
        public ModelFilter<AppointmentImpl, AppointmentModel> getDefaultFilter() {
            return getMyTodayAndFutureFilter();
        }

        //</editor-fold>
        public int countByCustomer(Connection connection, int customerId, LocalDateTime start, LocalDateTime end) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByCustomer(Connection connection, int customerId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByUser(Connection connection, int userId, LocalDateTime start, LocalDateTime end) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        public int countByUser(Connection connection, int userId) throws Exception {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getDeleteDependencyMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSaveConflictMessage(AppointmentImpl dao, Connection connection) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static final class AddressFieldFilter extends PropertyChangeNotifiable {

        private StringFilter address1 = StringFilter.empty();

        public static final String PROP_ADDRESS1 = "address1";

        /**
         * Get the value of address1
         *
         * @return the value of address1
         */
        public StringFilter getAddress1() {
            return address1;
        }

        /**
         * Set the value of address1
         *
         * @param address1 new value of address1
         */
        public void setAddress1(StringFilter address1) {
            if (null == address1) {
                address1 = StringFilter.empty();
            }
            StringFilter oldAddress1 = this.address1;
            this.address1 = address1;
            getPropertyChangeSupport().firePropertyChange(PROP_ADDRESS1, oldAddress1, address1);
        }

        private StringFilter address2 = StringFilter.empty();

        public static final String PROP_ADDRESS2 = "address2";

        /**
         * Get the value of address2
         *
         * @return the value of address2
         */
        public StringFilter getAddress2() {
            return address1;
        }

        /**
         * Set the value of address2
         *
         * @param address2 new value of address2
         */
        public void setAddress2(StringFilter address2) {
            if (null == address2) {
                address2 = StringFilter.empty();
            }
            StringFilter oldAddress2 = this.address2;
            this.address2 = address2;
            getPropertyChangeSupport().firePropertyChange(PROP_ADDRESS2, oldAddress2, address2);
        }

        private StringFilter postalCode = StringFilter.empty();

        public static final String PROP_POSTALCODE = "postalCode";

        /**
         * Get the value of postalCode
         *
         * @return the value of postalCode
         */
        public StringFilter getPostalCode() {
            return postalCode;
        }

        /**
         * Set the value of postalCode
         *
         * @param postalCode new value of postalCode
         */
        public void setPostalCode(StringFilter postalCode) {
            if (null == postalCode) {
                postalCode = StringFilter.empty();
            }
            StringFilter oldPostalCode = this.postalCode;
            this.postalCode = postalCode;
            getPropertyChangeSupport().firePropertyChange(PROP_POSTALCODE, oldPostalCode, postalCode);
        }

        private StringFilter phone = StringFilter.empty();

        public static final String PROP_PHONE = "phone";

        /**
         * Get the value of phone
         *
         * @return the value of phone
         */
        public StringFilter getPhone() {
            return phone;
        }

        /**
         * Set the value of phone
         *
         * @param phone new value of phone
         */
        public void setPhone(StringFilter phone) {
            if (null == phone) {
                phone = StringFilter.empty();
            }
            StringFilter oldPhone = this.phone;
            this.phone = phone;
            getPropertyChangeSupport().firePropertyChange(PROP_PHONE, oldPhone, phone);
        }

    }

    public static final class CustomerFieldFilter extends PropertyChangeNotifiable {

        private StringFilter name = StringFilter.empty();

        public static final String PROP_NAME = "name";

        /**
         * Get the value of name
         *
         * @return the value of name
         */
        public StringFilter getName() {
            return name;
        }

        /**
         * Set the value of name
         *
         * @param name new value of name
         */
        public void setName(StringFilter name) {
            if (null == name) {
                name = StringFilter.empty();
            }
            StringFilter oldName = this.name;
            this.name = name;
            getPropertyChangeSupport().firePropertyChange(PROP_NAME, oldName, name);
        }

        private Optional<Boolean> active = Optional.empty();

        public static final String PROP_ACTIVE = "active";

        /**
         * Get the value of active
         *
         * @return the value of active
         */
        public Optional<Boolean> isActive() {
            return active;
        }

        /**
         * Set the value of active
         *
         * @param active new value of active
         */
        public void setActive(Optional<Boolean> active) {
            if (null == active) {
                if (!this.isActive().isPresent()) {
                    return;
                }
                active = Optional.empty();
            }
            Optional<Boolean> oldActive = this.active;
            this.active = active;
            getPropertyChangeSupport().firePropertyChange(PROP_ACTIVE, oldActive, active);
        }

    }

    public static abstract class FilterImpl extends Filter<AppointmentImpl> {

        @Override
        public FactoryImpl getFactory() {
            return FACTORY;
        }

    }

}
