/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.InternalException;
import model.QuadFunction;
import model.TriFunction;

/**
 * Base class for object that ensures a database connection is open only while any extended classes are
 * invoking their respective delegate methods. The connection is closed after all delegate methods
 * have terminated.
 * @author Leonard T. Erwine
 */
public final class DbConnectionManager {
    private static final String PROTOCOL = "jdbc";
    private static final String VENDOR = "mysql";
    private static final String SERVER_NAME = "3.227.166.251";
    private static final String DB_NAME = "U03vHM";
    private static final String USER_NAME = "U03vHM";
    private static final String PASSWORD = "53688096290";
    
    // JDBC URL
    private static final String JDBC_URL = PROTOCOL + ":" + VENDOR + "://" + SERVER_NAME + "/" + DB_NAME;
    
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    
    private static Optional<DbConnectionManager> LATEST_DB_REQUIREMENT = Optional.empty();
    private Optional<DbConnectionManager> previous = Optional.empty();
    private Optional<DbConnectionManager> next = Optional.empty();
    
    /**
     * If this is not null, this will contain the currently open database connection.
     */
    private static Connection MYSQL_JDBC_CONNECTION = null;
    
    private final void start() throws SQLException {
        if (LATEST_DB_REQUIREMENT.isPresent()) {
            DbConnectionManager n = LATEST_DB_REQUIREMENT.get();
            LATEST_DB_REQUIREMENT = n.next = Optional.of(this);
            previous = Optional.of(n);
        } else {
            // Open a new database connection
            try {
                Class.forName(MYSQL_JDBC_DRIVER);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("JDBC driver not found", ex);
            }
            MYSQL_JDBC_CONNECTION = (Connection)DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            // Set this as the first database connection requirement registration.
            LATEST_DB_REQUIREMENT = Optional.of(this);
        }
    }
    
    private final void end() throws SQLException {
        if (next.isPresent()) {
            DbConnectionManager n = next.get();
            if ((n.previous = previous).isPresent()) {
                previous.get().next = next;
                previous = Optional.empty();
            }
            next = Optional.empty();
        } else if (previous.isPresent())
            previous = (LATEST_DB_REQUIREMENT = previous).get().next = Optional.empty();
        else
            try { MYSQL_JDBC_CONNECTION.close(); }
            finally { LATEST_DB_REQUIREMENT = Optional.empty(); }
    }
    
    public void apply(Consumer<Connection> consumer) throws SQLException {
        this.start();
        try { consumer.accept(MYSQL_JDBC_CONNECTION); }
        finally { this.end(); }
    }
    
    public <T> void apply(T target, BiConsumer<Connection, T> consumer) throws SQLException {
        this.start();
        try { consumer.accept(MYSQL_JDBC_CONNECTION, target); }
        finally { this.end(); }
    }
    
    public <R> R get(Function<Connection, R> func) throws SQLException {
        this.start();
        try { return func.apply(MYSQL_JDBC_CONNECTION); }
        finally { this.end(); }
    }
    
    public <T, R> R get(T target, BiFunction<Connection, T, R> func) throws SQLException {
        this.start();
        try { return func.apply(MYSQL_JDBC_CONNECTION, target); }
        finally { this.end(); }
    }
    
    public <T0, T1, R> R get(T0 arg0, T1 arg1, TriFunction<Connection, T0, T1, R> func) throws SQLException {
        this.start();
        try { return func.apply(MYSQL_JDBC_CONNECTION, arg0, arg1); }
        finally { this.end(); }
    }
    
    public <T0, T1, T2, R> R get(T0 arg0, T1 arg1, T2 arg2, QuadFunction<Connection, T0, T1, T2, R> func) throws SQLException {
        this.start();
        try { return func.apply(MYSQL_JDBC_CONNECTION, arg0, arg1, arg2); }
        finally { this.end(); }
    }
}
