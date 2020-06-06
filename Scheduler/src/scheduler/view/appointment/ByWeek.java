/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import scheduler.model.ui.AppointmentModel;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import scheduler.Scheduler;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/Calendar")
@FXMLResource("/scheduler/view/appointment/ByWeek.fxml")
public class ByWeek extends StackPane {

    private static final Logger LOG = Logger.getLogger(ByWeek.class.getName());

    public static ByWeek loadIntoMainContent(LocalDate week) {
        ByWeek newContent = new ByWeek();
        LocalDate d = (null == week) ? LocalDate.now() : week;
        while (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
            d = d.minusDays(1);
        }
        newContent.weekStart = d;
        try {
            ViewControllerLoader.initializeCustomControl(newContent);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
            throw new InternalError("Error loading view", ex);
        }
        Scheduler.getMainController().replaceContent(newContent);
        return newContent;
    }

    private LocalDate weekStart;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="sundayListView"
    private ListView<AppointmentModel> sundayListView; // Value injected by FXMLLoader

    @FXML // fx:id="mondayListView"
    private ListView<AppointmentModel> mondayListView; // Value injected by FXMLLoader

    @FXML // fx:id="tuesdayListView"
    private ListView<AppointmentModel> tuesdayListView; // Value injected by FXMLLoader

    @FXML // fx:id="wednesdayListView"
    private ListView<AppointmentModel> wednesdayListView; // Value injected by FXMLLoader

    @FXML // fx:id="thursdayListView"
    private ListView<AppointmentModel> thursdayListView; // Value injected by FXMLLoader

    @FXML // fx:id="fridayListView"
    private ListView<AppointmentModel> fridayListView; // Value injected by FXMLLoader

    @FXML // fx:id="saturdayListView"
    private ListView<AppointmentModel> saturdayListView; // Value injected by FXMLLoader

    @FXML // fx:id="detailsBorderPane"
    private BorderPane detailsBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="titleLabel"
    private Label titleLabel; // Value injected by FXMLLoader

    @FXML // fx:id="timeLabel"
    private Label timeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="customerLabel"
    private Label customerLabel; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert sundayListView != null : "fx:id=\"sundayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert mondayListView != null : "fx:id=\"mondayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert tuesdayListView != null : "fx:id=\"tuesdayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert wednesdayListView != null : "fx:id=\"wednesdayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert thursdayListView != null : "fx:id=\"thursdayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert fridayListView != null : "fx:id=\"fridayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert saturdayListView != null : "fx:id=\"saturdayListView\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert detailsBorderPane != null : "fx:id=\"detailsBorderPane\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'ByWeek.fxml'.";
        assert customerLabel != null : "fx:id=\"customerLabel\" was not injected: check your FXML file 'ByWeek.fxml'.";

    }

}
