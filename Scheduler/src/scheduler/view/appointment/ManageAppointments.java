package scheduler.view.appointment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.AddressElement;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.dao.CustomerElement;
import scheduler.dao.UserElement;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DbColumn;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.ModelFilter;
import scheduler.view.address.AddressModel;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.*;
import scheduler.view.city.CityModel;
import scheduler.view.country.CityCountryModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.model.CsvDataExporter;
import scheduler.view.model.HtmlDataExporter;
import scheduler.view.model.ItemModel;
import scheduler.view.model.TabularDataReader;
import scheduler.view.model.TsvDataExporter;
import scheduler.view.user.UserModel;

/**
 * FXML Controller class for viewing a list of {@link AppointmentModel} items.
 * <p>
 * Typically, {@link MainController} invokes {@link #loadInto(MainController, Stage, AppointmentModelFilter)} or
 * {@link #loadInto(MainController, Stage, AppointmentModelFilter, Object)}, which loads the view and instantiates the controller by calling
 * {@link #loadInto(Class, MainController, Stage, scheduler.view.ModelFilter)} or
 * {@link #loadInto(Class, MainController, Stage, scheduler.view.ModelFilter, Object)} on the base class.</p>
 * <p>
 * The associated view is {@code /resources/scheduler/view/appointment/ManageAppointments.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentDAO, AppointmentModel> {

    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    public static ManageAppointments loadInto(MainController mainController, Stage stage, AppointmentModelFilter filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageAppointments.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageAppointments loadInto(MainController mainController, Stage stage, AppointmentModelFilter filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    // PENDING: The value of the field ManageAppointments.filterState is not used
    private FilterOptionState filterState = null;

    @FXML // fx:id="titleTableColumn"
    private TableColumn<AppointmentModel, String> titleTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="startTableColumn"
    private TableColumn<AppointmentModel, LocalDateTime> startTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="endTableColumn"
    private TableColumn<AppointmentModel, LocalDateTime> endTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="typeTableColumn"
    private TableColumn<AppointmentModel, String> typeTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="customerTableColumn"
    private TableColumn<AppointmentModel, String> customerTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="userTableColumn"
    private TableColumn<AppointmentModel, String> userTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="locationTableColumn"
    private TableColumn<AppointmentModel, String> locationTableColumn; // Value injected by FXMLLoader 

    @FXML // fx:id="urlTableColumn"
    private TableColumn<AppointmentModel, String> urlTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="contactTableColumn"
    private TableColumn<AppointmentModel, String> contactTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="descriptionTableColumn"
    private TableColumn<AppointmentModel, String> descriptionTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="createDateTableColumn"
    private TableColumn<AppointmentModel, LocalDateTime> createDateTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="createdByTableColumn"
    private TableColumn<AppointmentModel, String> createdByTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateTableColumn"
    private TableColumn<AppointmentModel, LocalDateTime> lastUpdateTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="lastUpdateByTableColumn"
    private TableColumn<AppointmentModel, String> lastUpdateByTableColumn; // Value injected by FXMLLoader

    @SuppressWarnings("unchecked")
    @FXML
    void onExportButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FileChooser fc = new FileChooser();
        ObservableList<FileChooser.ExtensionFilter> filterList = fc.getExtensionFilters();
        FileChooser.ExtensionFilter csvDisp = new FileChooser.ExtensionFilter("Comma-separated values, displayed columns (*.csv)", "*.csv");
        filterList.add(csvDisp);
        FileChooser.ExtensionFilter csvAll = new FileChooser.ExtensionFilter("Comma-separated values, all columns (*.csv)", "*.csv");
        filterList.add(csvAll);
        FileChooser.ExtensionFilter tsvDisp = new FileChooser.ExtensionFilter("Tab-separated values, displayed columns (*.tsv)", "*.tsv");
        filterList.add(tsvDisp);
        FileChooser.ExtensionFilter tsvAll = new FileChooser.ExtensionFilter("Tab-separated values, all columns (*.tsv)", "*.tsv");
        filterList.add(tsvAll);
        FileChooser.ExtensionFilter htmDisp = new FileChooser.ExtensionFilter("HTML file, displayed columns (*.html)", "*.html");
        filterList.add(htmDisp);
        FileChooser.ExtensionFilter htmAll = new FileChooser.ExtensionFilter("HTML file, all columns (*.html)", "*.html");
        filterList.add(htmAll);
        File file = fc.showSaveDialog(stage);
        if (null != file) {
            FileChooser.ExtensionFilter ef = fc.getSelectedExtensionFilter();
            if (null != ef) {
                List<DbColumn> columns;
                if (ef == csvAll || ef == tsvAll || ef == htmAll) {
                    columns = Arrays.asList(new DbColumn[] {
                        DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS1, DbColumn.ADDRESS2,
                        DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE,
                        DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.APPOINTMENT_USER, DbColumn.USER_NAME, DbColumn.STATUS, DbColumn.TITLE,
                        DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END,
                        DbColumn.APPOINTMENT_ID, DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY,
                        DbColumn.APPOINTMENT_LAST_UPDATE, DbColumn.APPOINTMENT_LAST_UPDATE_BY
                    });
                } else {
                    columns = new ArrayList<>();
                    getListingTableView().getColumns().forEach((c) -> {
                        if (c.isVisible()) {
                            if (c == titleTableColumn)
                                columns.add(DbColumn.TITLE);
                            else if (c == startTableColumn)
                                columns.add(DbColumn.START);
                            else if (c == endTableColumn)
                                columns.add(DbColumn.END);
                            else if (c == typeTableColumn)
                                columns.add(DbColumn.TYPE);
                            else if (c == customerTableColumn)
                                columns.add(DbColumn.CUSTOMER_NAME);
                            else if (c == userTableColumn)
                                columns.add(DbColumn.USER_NAME);
                            else if (c == locationTableColumn)
                                columns.add(DbColumn.LOCATION);
                            else if (c == urlTableColumn)
                                columns.add(DbColumn.URL);
                            else if (c == contactTableColumn)
                                columns.add(DbColumn.CONTACT);
                            else if (c == descriptionTableColumn)
                                columns.add(DbColumn.DESCRIPTION);
                            else if (c == createDateTableColumn)
                                columns.add(DbColumn.APPOINTMENT_CREATE_DATE);
                            else if (c == createdByTableColumn)
                                columns.add(DbColumn.APPOINTMENT_CREATED_BY);
                            else if (c == lastUpdateTableColumn)
                                columns.add(DbColumn.APPOINTMENT_LAST_UPDATE);
                            else if (c == lastUpdateByTableColumn)
                                columns.add(DbColumn.APPOINTMENT_LAST_UPDATE_BY);
                        }
                    });
                }
                TabularDataReader<DbColumn, AppointmentModel> dataReader = new TabularDataReader<DbColumn, AppointmentModel>() {
                    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
                    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
                    @Override
                    public Collection<DbColumn> getColumns() {
                        return columns;
                    }

                    @Override
                    public String getHeaderText(DbColumn column) {
                        switch (column) {
                            case APPOINTMENT_CUSTOMER:
                                return getResourceString(RESOURCEKEY_CUSTOMERID);
                            case CUSTOMER_NAME:
                                return getResourceString(RESOURCEKEY_CUSTOMER);
                            case CUSTOMER_ADDRESS:
                                return getResourceString(RESOURCEKEY_ADDRESSID);
                            case ADDRESS1:
                                return getResourceString(RESOURCEKEY_ADDRESS);
                            case ADDRESS2:
                                return getResourceString(RESOURCEKEY_ADDRESS2);
                            case ADDRESS_CITY:
                                return getResourceString(RESOURCEKEY_CITYID);
                            case CITY_NAME:
                                return getResourceString(RESOURCEKEY_CITY);
                            case CITY_COUNTRY:
                                return getResourceString(RESOURCEKEY_COUNTRYID);
                            case COUNTRY_NAME:
                                return getResourceString(RESOURCEKEY_COUNTRY);
                            case POSTAL_CODE:
                                return getResourceString(RESOURCEKEY_POSTALCODE);
                            case PHONE:
                                return getResourceString(RESOURCEKEY_PHONENUMBER);
                            case ACTIVE:
                                return getResourceString(RESOURCEKEY_ACTIVE);
                            case APPOINTMENT_USER:
                                return getResourceString(RESOURCEKEY_USERID);
                            case USER_NAME:
                                return getResourceString(RESOURCEKEY_USER);
                            case STATUS:
                                return getResourceString(RESOURCEKEY_STATUS);
                            case TITLE:
                                return getResourceString(RESOURCEKEY_TITLE);
                            case DESCRIPTION:
                                return getResourceString(RESOURCEKEY_DESCRIPTION);
                            case LOCATION:
                                return getResourceString(RESOURCEKEY_LOCATION);
                            case CONTACT:
                                return getResourceString(RESOURCEKEY_POINTOFCONTACT);
                            case TYPE:
                                return getResourceString(RESOURCEKEY_TYPE);
                            case URL:
                                return getResourceString(RESOURCEKEY_MEETINGURL);
                            case START:
                                return getResourceString(RESOURCEKEY_START);
                            case END:
                                return getResourceString(RESOURCEKEY_END);
                            case APPOINTMENT_ID:
                                return getResourceString(RESOURCEKEY_APPOINTMENTID);
                            case APPOINTMENT_CREATE_DATE:
                                return getResourceString(RESOURCEKEY_CREATEDON);
                            case APPOINTMENT_CREATED_BY:
                                return getResourceString(RESOURCEKEY_CREATEDBY);
                            case APPOINTMENT_LAST_UPDATE:
                                return getResourceString(RESOURCEKEY_UPDATEDON);
                            case APPOINTMENT_LAST_UPDATE_BY:
                                return getResourceString(RESOURCEKEY_UPDATEDBY);
                            default:
                                return column.getDbName().toString();
                        }
                    }

                    @Override
                    public String getColumnText(AppointmentModel item, DbColumn column) {
                        CustomerModel<? extends CustomerElement> customer;
                        AddressModel<? extends AddressElement> address;
                        CityModel<? extends CityElement> city;
                        switch (column) {
                            case APPOINTMENT_CUSTOMER:
                                customer = item.getCustomer();
                                return (null == customer) ? "" : numberFormat.format(customer.getPrimaryKey());
                            case CUSTOMER_NAME:
                                return item.getCustomerName();
                            case CUSTOMER_ADDRESS:
                                customer = item.getCustomer();
                                if (null != customer) {
                                    address = customer.getAddress();
                                    if (null != address)
                                        return numberFormat.format(address.getPrimaryKey());
                                }
                                return "";
                            case ADDRESS1:
                                return item.getCustomerAddress1();
                            case ADDRESS2:
                                return item.getCustomerAddress2();
                            case ADDRESS_CITY:
                                customer = item.getCustomer();
                                if (null != customer) {
                                    address = customer.getAddress();
                                    if (null != address) {
                                        city = address.getCity();
                                        if (null != city)
                                            return numberFormat.format(city.getPrimaryKey());
                                    }
                                }
                                return "";
                            case CITY_NAME:
                                return item.getCustomerCityName();
                            case CITY_COUNTRY:
                                customer = item.getCustomer();
                                if (null != customer) {
                                    address = customer.getAddress();
                                    if (null != address) {
                                        city = address.getCity();
                                        if (null != city) {
                                            CityCountryModel<? extends CountryElement> country = city.getCountry();
                                            if (null != country)
                                                return numberFormat.format(country.getPrimaryKey());
                                        }
                                    }
                                }
                                return "";
                            case COUNTRY_NAME:
                                return item.getCustomerCountryName();
                            case POSTAL_CODE:
                                return item.getCustomerPostalCode();
                            case PHONE:
                                return item.getCustomerPhone();
                            case ACTIVE:
                                customer = item.getCustomer();
                                if (null != customer) {
                                    return (customer.isActive()) ? "TRUE" : "FALSE";
                                }
                                return "";
                            case APPOINTMENT_USER:
                                UserModel<? extends UserElement> user = item.getUser();
                                return (null == user) ? "" : numberFormat.format(user.getPrimaryKey());
                            case USER_NAME:
                                return item.getUserName();
                            case STATUS:
                                return item.getUserStatusDisplay();
                            case TITLE:
                                return item.getTitle();
                            case DESCRIPTION:
                                return item.getDescription();
                            case LOCATION:
                                return item.getEffectiveLocation();
                            case CONTACT:
                                return item.getContact();
                            case TYPE:
                                return item.getTypeDisplay();
                            case URL:
                                return item.getUrl();
                            case START:
                                return dateTimeFormatter.format(item.getStart());
                            case END:
                                return dateTimeFormatter.format(item.getEnd());
                            case APPOINTMENT_ID:
                                return numberFormat.format(item.getPrimaryKey());
                            case APPOINTMENT_CREATE_DATE:
                                return dateTimeFormatter.format(item.getCreateDate());
                            case APPOINTMENT_CREATED_BY:
                                return item.getCreatedBy();
                            case APPOINTMENT_LAST_UPDATE:
                                return dateTimeFormatter.format(item.getLastModifiedDate());
                            case APPOINTMENT_LAST_UPDATE_BY:
                                return item.getLastModifiedBy();
                            default:
                                return "?";
                        }
                    }
                };
                try {
                    if (ef == csvAll || ef == csvDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            (new CsvDataExporter<>(dataReader)).export(fileWriter, getItemsList());
                        }
                    } else if (ef == tsvAll || ef == tsvDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            (new TsvDataExporter<>(dataReader)).export(fileWriter, getItemsList());
                        }
                    } else if (ef == htmAll || ef == htmDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            ModelFilter<AppointmentDAO, AppointmentModel, ? extends DaoFilter<AppointmentDAO>> filter = getFilter();
                            (new HtmlDataExporter<>(filter.getHeadingText(), dataReader)).export(fileWriter, getItemsList());
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, getResourceString(RESOURCEKEY_FILETYPENOTSUPPORTED));
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(stage);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle(getResourceString(RESOURCEKEY_UNKNOWNFILETYPE));
                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error saving file", ex);
                }
            } 
        }
    }

    @FXML
    void filterButtonClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This method is not implemented");
        alert.initStyle(StageStyle.UTILITY);
        alert.initOwner((Stage) ((Button) event.getSource()).getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Not implemented");
        alert.showAndWait();
//        FilterOptionState result = EditAppointmentFilter.waitEdit(filterState, (Stage) ((Button) event.getSource()).getScene().getWindow());
//        if (null != result && !result.equals(filterState)) {
//            filterState = result;
//            AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), "Reload after filterButtonClick not implemented");
//        }
        // TODO: Implement scheduler.view.appointment.ManageAppointments#filterButtonClick(ActionEvent event)
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();

    }

    @Override
    protected void onDeleteItem(Stage stage, AppointmentModel item) {
        getMainController(stage.getScene()).deleteAppointment(stage, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentDAO dao) {
        return new AppointmentModel(dao);
    }

    @Override
    protected void onAddNewItem(Stage stage) throws IOException {
        getMainController(stage.getScene()).addNewAppointment(stage, null, null);
    }

    @Override
    protected void onEditItem(Stage stage, AppointmentModel item) throws IOException {
        getMainController(stage.getScene()).editAppointment(stage, item);
    }

    @Override
    protected ItemModel.ModelFactory<AppointmentDAO, AppointmentModel> getModelFactory() {
        return AppointmentModel.getFactory();
    }

}
