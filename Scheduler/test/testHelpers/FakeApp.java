/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testHelpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.util.DbConnector;

/**
 *
 * @author erwinel
 */
public class FakeApp extends Application {
    private static Stage fakeAppStage = null;
    private static CyclicBarrier appStarted;
    public static void setUp() {
        if (null != fakeAppStage)
            return;
        assert null == appStarted : "App already starting. Test cannot continue";
        appStarted = new CyclicBarrier(2);
        try {
            Thread t = new Thread("JavaFX Init Thread") {
                @Override
                public void run() {
                    Application.launch(FakeApp.class, new String[0]);
                }
            };
            t.setDaemon(true);
            t.start();
            try {
                appStarted.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(FakeApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.printf("Fake FX App thread started\n");
        } finally { appStarted = null; }
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        fakeAppStage = primaryStage;
        appStarted.await();
    }
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DbConnector.DB_DRIVER);
        String url = AppResources.getConnectionUrl();
        System.out.println(String.format("Connecting to %s", url));
        Connection result = (Connection)DriverManager.getConnection(url, AppResources.getDbLoginName(), AppResources.getDbLoginPassword());
        System.out.println(String.format("Connected to %s", url));
        return result;
    }
}
