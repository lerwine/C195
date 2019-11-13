/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author webmaster
 */
public class DbConnection {
    // JDBC URL Parts
    private static final String PROTOCOL = "jdbc";
    private static final String VENDOR = "mysql";
    private static final String SERVER_NAME = "3.227.166.251";
    private static final String DB_NAME = "U03vHM";
    private static final String USER_NAME = "U03vHM";
    private static final String PASSWORD = "53688096290";
    
    // JDBC URL
    private static final String JDBC_URL = PROTOCOL + ":" + VENDOR + "://" +
            SERVER_NAME + "/" + DB_NAME;
    
    private static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static Connection MYSQL_JDBC_CONNECTION = null;
    
    public static Connection CreateConnection() {
        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            MYSQL_JDBC_CONNECTION = (Connection)DriverManager
                    .getConnection(JDBC_URL, USER_NAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return MYSQL_JDBC_CONNECTION;
    }
}
