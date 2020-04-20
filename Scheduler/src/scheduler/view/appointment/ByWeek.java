/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.util.EventHelper;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEvent;
import scheduler.view.event.FxmlViewEventType;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/Calendar")
@FXMLResource("/scheduler/view/appointment/ByWeek.fxml")
public class ByWeek {

    public static ByWeek loadInto(MainController mainController, Stage stage, LocalDate week,
            Object loadEventListener) throws IOException {
        return mainController.loadContent(ByWeek.class, (FxmlViewControllerEventListener<Parent, ByWeek>) (event) -> {
            if (event.getType() == FxmlViewEventType.LOADED) {
                LocalDate d = (null == week) ? LocalDate.now() : week;
                while (d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    d = d.minusDays(1);
                }
                event.getController().weekStart = d;
            }

            EventHelper.fireFxmlViewEvent(loadEventListener, event);
        });
    }

    public static ByWeek loadInto(MainController mainController, Stage stage, LocalDate month) throws IOException {
        return loadInto(mainController, stage, month, null);
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
    void initialize() {
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

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<? extends Parent> event) {

    }

}
