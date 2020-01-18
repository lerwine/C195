/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectImpl.TABLENAME_USER)
@PrimaryKeyColumn(UserImpl.COLNAME_USERID)
public class UserImpl extends DataObjectImpl implements User {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
    //<editor-fold defaultstate="collapsed" desc="static baseSelectQuery property">
    
    private static String baseSelectQuery = null;
    
    public static String getBaseSelectQuery() {
        if (null != baseSelectQuery)
            return baseSelectQuery;
        final StringBuilder sql = new StringBuilder("SELECT `");
        sql.append(COLNAME_USERID);
        Stream.of(COLNAME_CREATEDATE, COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_USERNAME, COLNAME_PASSWORD, COLNAME_ACTIVE).forEach((t) -> {
            sql.append("`, `").append(t);
        });
        baseSelectQuery = sql.append("` FROM `").append(getTableName(UserImpl.class)).toString();
        return baseSelectQuery;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="static sortOptions property">
    
    private static ObservableSet<String> sortOptions = null;
    public static ObservableSet<String> getSortOptions() {
        if (sortOptions == null)
            sortOptions = FXCollections.unmodifiableObservableSet(FXCollections.observableSet(COLNAME_USERNAME, COLNAME_ACTIVE, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, COLNAME_CREATEDATE, COLNAME_CREATEDBY));
        return sortOptions;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="userName property">
    
    private String userName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() { return userName; }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) { this.userName = (userName == null) ? "" : userName; }


    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="password property">
    
    private String password;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() { return password; }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) { this.password = (password == null) ? "" : password; }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="status property">
    
    private int status;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() { return status; }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(int status) { this.status = User.asValidStatus(status); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} user object.
     */
    public UserImpl() {
        super();
        userName = "";
        password = "";
        status = User.STATUS_USER;
    }
    
    /**
     * Initializes a user object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    private UserImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        userName = resultSet.getString(COLNAME_USERNAME);
        if (resultSet.wasNull())
            userName = "";
        password = resultSet.getString(COLNAME_PASSWORD);
        if (resultSet.wasNull())
            password = "";
        status = User.asValidStatus(resultSet.getInt(COLNAME_ACTIVE));
        if (resultSet.wasNull())
            status = User.STATUS_INACTIVE;
    }
    
    public static ArrayList<UserImpl> lookupAll(Connection connection, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = getBaseSelectQuery() + SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(), () -> SelectOrderSpec.single(COLNAME_USERNAME));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return toList(ps, (rs) -> new UserImpl(rs));
        }
    }

    public static ArrayList<UserImpl> lookupAll(Connection connection) throws SQLException {
        return lookupAll(connection, null);
    }

    public static ArrayList<UserImpl> lookupByStatus(Connection connection, int status, boolean isNegated, Iterable<SelectOrderSpec> orderBy) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE `%s` %s %% %s", getBaseSelectQuery(), COLNAME_ACTIVE, (isNegated) ? "<>" : "=",
                SelectOrderSpec.toOrderByClause(orderBy, getSortOptions(), () -> SelectOrderSpec.single(COLNAME_USERNAME)));
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, status);
            return toList(ps, (rs) -> new UserImpl(rs));
        }
    }

    public static ArrayList<UserImpl> lookupByStatus(Connection connection, int status, boolean isNegated) throws SQLException {
        return lookupByStatus(connection, status, isNegated, null);
    }

    public static Optional<UserImpl> getByUserName(Connection connection, String userName) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        Objects.requireNonNull(userName, "User name cannot be null");
        if (userName.trim().isEmpty())
            return Optional.empty();
        String sql = String.format("%s WHERE `%s` = %%", getBaseSelectQuery(), COLNAME_USERNAME);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            return toOptional(ps, (rs) -> new UserImpl(rs));
        }
    }
    
    public static Optional<UserImpl> lookupByPrimaryKey(Connection connection, int pk) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        String sql = String.format("%s WHERE `%s` = %%", getBaseSelectQuery(), COLNAME_USERID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pk);
            return toOptional(ps, (rs) -> new UserImpl(rs));
        }
    }

    @Override
    public synchronized void delete(Connection connection) throws SQLException {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert AppointmentImpl.lookupCount(connection, Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(this)) == 0 : "User is associated with one or more appointments.";
        super.delete(connection);
    }
    
}
