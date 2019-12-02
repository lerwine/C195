/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import com.mysql.jdbc.Connection;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.db.User;

/**
 *
 * @author Leonard T. Erwine
 */
public final class SqlConnectionDependency {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    private static Connection connection;
    private static Optional<SqlConnectionDependency> latest = Optional.empty();
    private Optional<SqlConnectionDependency> previous;
    private Optional<SqlConnectionDependency> next;

    private boolean open;

    public static final String PROP_CONNECTION = "connection";

    /**
     * Gets the current {@link Connection}.
     * @return The current {@link Connection} or null if no SQL connection dependency is open.
     */
    public final Connection getconnection() {
        if (open)
            return connection;
        return null;
    }

    public static final String PROP_OPEN = "open";

    /**
     * Gets a value that indicates whether an SQL connection dependency is open.
     * @return {@code true} if an SQL connection dependency is open; otherwise, {@code false}.
     */
    public final boolean isOpen() { return open; }

    public SqlConnectionDependency(boolean open) throws ClassNotFoundException, SQLException {
        this.open = open;
        next = Optional.empty();
        if (open)
            open();
        else
            previous = Optional.empty();
    }
    
    public SqlConnectionDependency() {
        next = Optional.empty();
        previous = Optional.empty();
    }
    
    /**
     * Opens an SQL connection dependency.
     * If this is the first dependency, then a new SQL database {@link Connection} will be established.
     * @return An opened SQL database {@link Connection}.
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public final Connection open() throws ClassNotFoundException, SQLException {
        if (open)
            throw new InternalException("SQL connetion dependency is already open");
        if ((previous = latest).isPresent())
            latest = latest.get().next = Optional.of(this);
        else {
            Class.forName(DB_DRIVER);
            String url = AppConfig.getConnectionUrl();
            connection = (Connection)DriverManager.getConnection(url, AppConfig.getDbLoginName(), AppConfig.getDbLoginPassword());
            Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Connected to {0}", url));
            latest = Optional.of(this);
        }
        open = true;
        try { propertyChangeSupport.firePropertyChange(PROP_CONNECTION, null, connection); }
        finally { propertyChangeSupport.firePropertyChange(PROP_OPEN, false, true); }
        return connection;
    }
    
    public final void close() throws SQLException {
        if (!open)
            throw new InternalException("SQL connetion dependency is already closed");
        Connection oldConnection = connection;
        if (next.isPresent()) {
            if ((next.get().previous = previous).isPresent()) {
                previous.get().next = next;
                previous = Optional.empty();
            }
            next = Optional.empty();
        } else {
            if ((latest = previous).isPresent())
                previous = previous.get().next = Optional.empty();
            else {
                connection.close();
                previous = latest = Optional.empty();
            }
        }
        open = false;
        try { propertyChangeSupport.firePropertyChange(PROP_CONNECTION, oldConnection, null); }
        finally { propertyChangeSupport.firePropertyChange(PROP_OPEN, true, false); }
    }
    
    public static final <T> T get(Function<Connection, T> func) throws ClassNotFoundException, SQLException {
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        T result;
        try { result = func.apply(dep.getconnection()); } finally { dep.close(); }
        return result;
    }
    
    public static void apply(Consumer<Connection> consumer) throws ClassNotFoundException, SQLException {
        SqlConnectionDependency dep = new SqlConnectionDependency(true);
        try { consumer.accept(connection); } finally { dep.close(); }
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
