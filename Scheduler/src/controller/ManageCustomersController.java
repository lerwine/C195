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
import model.db.Customer;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCustomersController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageCustomers.fxml";

    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIdTableColumn;

    @FXML
    private TableColumn<Customer, String> customerNameTableColumn;

    @FXML
    private TableColumn<Customer, Integer> addressTableColumn;

    @FXML
    private TableColumn<Customer, Boolean> activeTableColumn;

    @FXML
    private TableColumn<Customer, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<Customer, String> createdByTableColumn;

    @FXML
    private TableColumn<Customer, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<Customer, String> lastUpdateByTableColumn;

    /**
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_PRIMARYKEY));
        customerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_CUSTOMERNAME));
        activeTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_ACTIVE));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_ADDRESSID));
        // TODO: Format address
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<Customer, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.Context.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_LASTUPDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<Customer, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.Context.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(Customer.PROP_LASTUPDATEBY));
    }    
    
}
