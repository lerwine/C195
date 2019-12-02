/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.db.Country;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCountriesController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageCountries.fxml";

    @FXML
    private TableView<Country> countriesTableView;

    @FXML
    private TableColumn<Country, Integer> countryIdTableColumn;

    @FXML
    private TableColumn<Country, String> nameTableColumn;

    @FXML
    private TableColumn<Country, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<Country, String> createdByTableColumn;

    @FXML
    private TableColumn<Country, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<Country, String> lastUpdateByTableColumn;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        countryIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_PRIMARYKEY));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<Country, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_LASTUPDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<Country, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_LASTUPDATEBY));
    }    
    
}
