package scheduler.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import scheduler.view.user.AppointmentUser;

/**
 *
 * @author erwinel
 */
@TableName(DataObjectFactory.TABLENAME_USER)
@PrimaryKeyColumn(UserFactory.COLNAME_USERID)
public class UserImpl extends DataObjectImpl implements User {
    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    
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
    public void setStatus(int status) { this.status = UserFactory.asValidStatus(status); }
    
    //</editor-fold>
    
    //</editor-fold>
    
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} user object.
     */
    public UserImpl() {
        super();
        userName = "";
        password = "";
        status = UserFactory.STATUS_USER;
    }
    
    /**
     * Initializes a user object from a {@link ResultSet}.
     * @param resultSet The data retrieved from the database.
     * @throws SQLException if not able to read data from the {@link ResultSet}.
     */
    UserImpl(ResultSet resultSet) throws SQLException {
        super(resultSet);
        userName = resultSet.getString(UserFactory.COLNAME_USERNAME);
        if (resultSet.wasNull())
            userName = "";
        password = resultSet.getString(UserFactory.COLNAME_PASSWORD);
        if (resultSet.wasNull())
            password = "";
        status = UserFactory.asValidStatus(resultSet.getInt(UserFactory.COLNAME_ACTIVE));
        if (resultSet.wasNull())
            status = UserFactory.STATUS_INACTIVE;
    }
    
    @Override
    public synchronized void delete(Connection connection) throws Exception {
        Objects.requireNonNull(connection, "Connection cannot be null");
        assert (new AppointmentFactory()).countByUser(connection, getPrimaryKey()) == 0 : "User is associated with one or more appointments.";
        super.delete(connection);
    }
    
}
