package scheduler.dao;

/**
 * Represents a data row from the "user" database table.
 * This object contains the login credentials for users of the current application.
 * 
 * @author erwinel
 */
public interface User extends DataObject {
    //<editor-fold defaultstate="collapsed" desc="Status values">
    
    /**
     * Value of {@link #getStatus()} when the current user is inactive.
     */
    public static final short STATUS_INACTIVE = 0;
    
    /**
     * Value of {@link #getStatus()} when the current user is active and not an administrative account.
     */
    public static final short STATUS_USER = 1;
    
    /**
     * Value of {@link #getStatus()} when the current user is an active administrative account.
     */
    public static final short STATUS_ADMIN = 2;
    
    //</editor-fold>
    
    /**
     * Gets the current user's login name.
     * This corresponds to the "userName" database column.
     * 
     * @return The current user's login name.
     */
    String getUserName();

    /**
     * Gets the encoded hash that is used to validate the current user's password.
     * This corresponds to the "password" database column.
     * 
     * @return The encoded hash that is used to validate the current user's password.
     */
    String getPassword();
    
    /**
     * Gets a value that indicates the status of the current user.
     * This corresponds to the "active" database column.
     * 
     * @return {@link #STATUS_USER}, {@link #STATUS_ADMIN} or {@link #STATUS_INACTIVE}.
     */
    int getStatus();
}
