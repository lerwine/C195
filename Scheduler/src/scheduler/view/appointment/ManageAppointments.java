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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.PartialAddressDAO;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.dao.PartialUserDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.dao.schema.DbColumn;
import scheduler.events.AppointmentEvent;
import scheduler.events.AppointmentFailedEvent;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.events.ModelFailedEvent;
import scheduler.events.OperationRequestEvent;
import scheduler.model.Customer;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.EntityModel;
import scheduler.model.fx.PartialAddressModel;
import scheduler.model.fx.PartialCityModel;
import scheduler.model.fx.PartialCountryModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.MainListingControl;
import scheduler.view.ModelFilter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.*;
import scheduler.view.export.CsvDataExporter;
import scheduler.view.export.HtmlDataExporter;
import scheduler.view.export.TabularDataReader;
import scheduler.view.export.TsvDataExporter;

/**
 * FXML Controller class for viewing a list of {@link AppointmentModel} items.
 * <h3>Event Handling</h3>
 * <h4>SCHEDULER_APPOINTMENT_OP_REQUEST</h4>
 * <dl>
 * <dt>{@link #listingTableView} &#123; {@link scheduler.fx.ItemEditTableCellFactory#onItemActionRequest} &#125; (creates) {@link scheduler.events.AppointmentOpRequestEvent}
 * &#123;</dt>
 * <dd> {@link javafx.event.Event#eventType} = {@link scheduler.events.AppointmentOpRequestEvent#APPOINTMENT_OP_REQUEST "SCHEDULER_APPOINTMENT_OP_REQUEST"} &larr;
 * {@link scheduler.events.OperationRequestEvent#OP_REQUEST_EVENT "SCHEDULER_OP_REQUEST_EVENT"} &larr; {@link scheduler.events.ModelEvent#MODEL_EVENT_TYPE "SCHEDULER_MODEL_EVENT"}
 * </dd>
 * </dl>
 * &#125; (fires) {@link #onItemActionRequest(OperationRequestEvent) onItemActionRequest}({@link scheduler.events.AppointmentOpRequestEvent})
 * <dl>
 * <dt>{@link scheduler.events.AppointmentOpRequestEvent} &#123; {@link javafx.event.Event#eventType} = {@link scheduler.events.AppointmentOpRequestEvent#EDIT_REQUEST} &#125;</dt>
 * <dd>&rarr; {@link #onEditItem(AppointmentModel) onEditItem}(({@link AppointmentModel}) {@link scheduler.events.ModelEvent#getEntityModel()}</dd>
 * <dt>{@link OperationRequestEvent} &#123; {@link scheduler.events.ModelEvent#getOperation()} = {@link scheduler.events.DbOperationType#DB_DELETE}} &#125;</dt>
 * <dd>&rarr; {@link #onDeleteItem(scheduler.model.fx.AppointmentModel) onDeleteItem}({@link scheduler.events.AppointmentOpRequestEvent})</dd>
 * </dl>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends MainListingControl<AppointmentDAO, AppointmentModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ManageAppointments.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    public static ManageAppointments loadIntoMainContent(AppointmentModelFilter filter) {
        ManageAppointments newContent = initialize(new ManageAppointments());
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }

    public static ManageAppointments loadIntoMainContent() {
        return loadIntoMainContent(AppointmentModel.FACTORY.getDefaultFilter());
    }

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

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    private ManageAppointments() {

    }

    @FXML
    private void filterButtonClick(ActionEvent event) {
        LOG.entering(LOG.getName(), "filterButtonClick", event);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This method is not implemented");
        alert.initStyle(StageStyle.UTILITY);
        alert.initOwner(((Node) event.getSource()).getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Not implemented");
        alert.showAndWait();
//        FilterOptionState result = EditAppointmentFilter.waitEdit(filterState, (Stage) ((Button) event.getSource()).getScene().getWindow());
//        if (null != result && !result.equals(filterState)) {
//            filterState = result;
//            AlertHelper.showWarningAlert(((Button) event.getSource()).getScene().getWindow(), "Reload after filterButtonClick not implemented");
//        }
        // TODO: Implement scheduler.view.appointment.ManageAppointments#filterButtonClick(ActionEvent event)
        LOG.exiting(LOG.getName(), "filterButtonClick");
    }

    @FXML
    private void onExportButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onExportButtonAction", event);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
                TabularDataReader<DbColumn, AppointmentModel> dataReader;
                if (ef == csvAll || ef == tsvAll || ef == htmAll) {
                    dataReader = new AppointmentsTabularDataReader(Arrays.asList(new DbColumn[]{
                        DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS1, DbColumn.ADDRESS2,
                        DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE,
                        DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.APPOINTMENT_USER, DbColumn.USER_NAME, DbColumn.STATUS, DbColumn.TITLE,
                        DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END,
                        DbColumn.APPOINTMENT_ID, DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY,
                        DbColumn.APPOINTMENT_LAST_UPDATE, DbColumn.APPOINTMENT_LAST_UPDATE_BY
                    }));
                } else {
                    List<DbColumn> columns = new ArrayList<>();
                    getListingTableView().getColumns().forEach((c) -> {
                        if (c.isVisible()) {
                            if (c == titleTableColumn) {
                                columns.add(DbColumn.TITLE);
                            } else if (c == startTableColumn) {
                                columns.add(DbColumn.START);
                            } else if (c == endTableColumn) {
                                columns.add(DbColumn.END);
                            } else if (c == typeTableColumn) {
                                columns.add(DbColumn.TYPE);
                            } else if (c == customerTableColumn) {
                                columns.add(DbColumn.CUSTOMER_NAME);
                            } else if (c == userTableColumn) {
                                columns.add(DbColumn.USER_NAME);
                            } else if (c == locationTableColumn) {
                                columns.add(DbColumn.LOCATION);
                            } else if (c == urlTableColumn) {
                                columns.add(DbColumn.URL);
                            } else if (c == contactTableColumn) {
                                columns.add(DbColumn.CONTACT);
                            } else if (c == descriptionTableColumn) {
                                columns.add(DbColumn.DESCRIPTION);
                            } else if (c == createDateTableColumn) {
                                columns.add(DbColumn.APPOINTMENT_CREATE_DATE);
                            } else if (c == createdByTableColumn) {
                                columns.add(DbColumn.APPOINTMENT_CREATED_BY);
                            } else if (c == lastUpdateTableColumn) {
                                columns.add(DbColumn.APPOINTMENT_LAST_UPDATE);
                            } else if (c == lastUpdateByTableColumn) {
                                columns.add(DbColumn.APPOINTMENT_LAST_UPDATE_BY);
                            }
                        }
                    });
                    dataReader = new AppointmentsTabularDataReader(columns);
                }
                try {
                    if (ef == csvAll || ef == csvDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            (new CsvDataExporter<>(dataReader)).export(fileWriter, getItems());
                        }
                    } else if (ef == tsvAll || ef == tsvDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            (new TsvDataExporter<>(dataReader)).export(fileWriter, getItems());
                        }
                    } else if (ef == htmAll || ef == htmDisp) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            ModelFilter<AppointmentDAO, AppointmentModel, ? extends DaoFilter<AppointmentDAO>> filter = getFilter();
                            (new HtmlDataExporter<>(filter.getHeadingText(), dataReader)).export(fileWriter, getItems());
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, getResources().getString(RESOURCEKEY_FILETYPENOTSUPPORTED));
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initOwner(stage);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle(getResources().getString(RESOURCEKEY_UNKNOWNFILETYPE));
                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    AlertHelper.showErrorAlert(stage, "Save Error", "Error saving file. See logs for details", ButtonType.OK);
                    LOG.log(Level.SEVERE, "Error saving file", ex);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onExportButtonAction");
    }

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpButtonAction", event);
        restoreNode(helpBorderPane);
        LOG.exiting(LOG.getName(), "onHelpButtonAction");
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpOKButtonAction", event);
        collapseNode(helpBorderPane);
        LOG.exiting(LOG.getName(), "onHelpOKButtonAction");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        super.initialize();

        assert titleTableColumn != null : "fx:id=\"titleTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert startTableColumn != null : "fx:id=\"startTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert endTableColumn != null : "fx:id=\"endTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert typeTableColumn != null : "fx:id=\"typeTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert customerTableColumn != null : "fx:id=\"customerTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert userTableColumn != null : "fx:id=\"userTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert locationTableColumn != null : "fx:id=\"locationTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert urlTableColumn != null : "fx:id=\"urlTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert contactTableColumn != null : "fx:id=\"contactTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert descriptionTableColumn != null : "fx:id=\"descriptionTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert createDateTableColumn != null : "fx:id=\"createDateTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert createdByTableColumn != null : "fx:id=\"createdByTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert lastUpdateTableColumn != null : "fx:id=\"lastUpdateTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        assert lastUpdateByTableColumn != null : "fx:id=\"lastUpdateByTableColumn\" was not injected: check your FXML file 'ManageAppointments.fxml'.";
        LOG.exiting(LOG.getName(), "initialize");
    }

    @Override
    protected Comparator<? super AppointmentDAO> getComparator() {
        return AppointmentHelper::compare;
    }

    @Override
    protected EntityModel.EntityModelFactory<AppointmentDAO, AppointmentModel> getModelFactory() {
        return AppointmentModel.FACTORY;
    }

    @Override
    protected String getLoadingTitle() {
        return getResources().getString(RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    @Override
    protected String getFailMessage() {
        return getResources().getString(RESOURCEKEY_ERRORLOADINGAPPOINTMENTS);
    }

    @Override
    protected void onNewItem() {
        try {
            EditAppointment.editNew(null, null, getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onEditItem(AppointmentModel item) {
        try {
            Window w = getScene().getWindow();
            EditAppointment.edit(item, w);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(AppointmentModel item) {
        AppointmentOpRequestEvent deleteRequestEvent = new AppointmentOpRequestEvent(item, this, true);
        Event.fireEvent(item.dataObject(), deleteRequestEvent);
        Window window = getScene().getWindow();
        if (deleteRequestEvent.isCanceled()) {
            AlertHelper.showWarningAlert(window, deleteRequestEvent.getCancelMessage(), ButtonType.OK);
        } else {
            AlertHelper.showWarningAlert(window, LOG,
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((t) -> {
                if (t == ButtonType.YES) {
                    AppointmentDAO.DeleteTask task = new AppointmentDAO.DeleteTask(item, false);
                    task.setOnSucceeded(this::onDeleteTaskSucceeded);
                    MainController.startBusyTaskNow(task);
                }
            });
        }

    }

    private void onDeleteTaskSucceeded(WorkerStateEvent event) {
        LOG.entering(LOG.getName(), "onDeleteTaskSucceeded", event);
        AppointmentEvent appointmentEvent = (AppointmentEvent) event.getSource().getValue();
        if (null != appointmentEvent && appointmentEvent instanceof AppointmentFailedEvent) {
            scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                    ((ModelFailedEvent<AppointmentDAO, AppointmentModel>) appointmentEvent).getMessage(), ButtonType.OK);
        }
        LOG.exiting(LOG.getName(), "onDeleteTaskSucceeded");
    }

    @Override
    protected EventType<AppointmentSuccessEvent> getInsertedEventType() {
        return AppointmentSuccessEvent.INSERT_SUCCESS;
    }

    @Override
    protected EventType<AppointmentSuccessEvent> getUpdatedEventType() {
        return AppointmentSuccessEvent.UPDATE_SUCCESS;
    }

    @Override
    protected EventType<AppointmentSuccessEvent> getDeletedEventType() {
        return AppointmentSuccessEvent.DELETE_SUCCESS;
    }

    private class AppointmentsTabularDataReader implements TabularDataReader<DbColumn, AppointmentModel> {

        private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        private final List<DbColumn> columns;

        AppointmentsTabularDataReader(List<DbColumn> columns) {
            this.columns = columns;
        }

        @Override
        public Collection<DbColumn> getColumns() {
            return columns;
        }

        @Override
        public String getHeaderText(DbColumn column) {
            switch (column) {
                case APPOINTMENT_CUSTOMER:
                    return getResources().getString(RESOURCEKEY_CUSTOMERID);
                case CUSTOMER_NAME:
                    return getResources().getString(RESOURCEKEY_CUSTOMER);
                case CUSTOMER_ADDRESS:
                    return getResources().getString(RESOURCEKEY_ADDRESSID);
                case ADDRESS1:
                    return getResources().getString(RESOURCEKEY_ADDRESS);
                case ADDRESS2:
                    return getResources().getString(RESOURCEKEY_ADDRESS2);
                case ADDRESS_CITY:
                    return getResources().getString(RESOURCEKEY_CITYID);
                case CITY_NAME:
                    return getResources().getString(RESOURCEKEY_CITY);
                case CITY_COUNTRY:
                    return getResources().getString(RESOURCEKEY_COUNTRYID);
                case COUNTRY_NAME:
                    return getResources().getString(RESOURCEKEY_COUNTRY);
                case POSTAL_CODE:
                    return getResources().getString(RESOURCEKEY_POSTALCODE);
                case PHONE:
                    return getResources().getString(RESOURCEKEY_PHONENUMBER);
                case ACTIVE:
                    return getResources().getString(RESOURCEKEY_ACTIVE);
                case APPOINTMENT_USER:
                    return getResources().getString(RESOURCEKEY_USERID);
                case USER_NAME:
                    return getResources().getString(RESOURCEKEY_USER);
                case STATUS:
                    return getResources().getString(RESOURCEKEY_STATUS);
                case TITLE:
                    return getResources().getString(RESOURCEKEY_TITLE);
                case DESCRIPTION:
                    return getResources().getString(RESOURCEKEY_DESCRIPTION);
                case LOCATION:
                    return getResources().getString(RESOURCEKEY_LOCATION);
                case CONTACT:
                    return getResources().getString(RESOURCEKEY_POINTOFCONTACT);
                case TYPE:
                    return getResources().getString(RESOURCEKEY_TYPE);
                case URL:
                    return getResources().getString(RESOURCEKEY_MEETINGURL);
                case START:
                    return getResources().getString(RESOURCEKEY_START);
                case END:
                    return getResources().getString(RESOURCEKEY_END);
                case APPOINTMENT_ID:
                    return getResources().getString(RESOURCEKEY_APPOINTMENTID);
                case APPOINTMENT_CREATE_DATE:
                    return getResources().getString(RESOURCEKEY_CREATEDON);
                case APPOINTMENT_CREATED_BY:
                    return getResources().getString(RESOURCEKEY_CREATEDBY);
                case APPOINTMENT_LAST_UPDATE:
                    return getResources().getString(RESOURCEKEY_UPDATEDON);
                case APPOINTMENT_LAST_UPDATE_BY:
                    return getResources().getString(RESOURCEKEY_UPDATEDBY);
                default:
                    return column.getDbName().toString();
            }
        }

        @Override
        public String getColumnText(AppointmentModel item, DbColumn column) {
            PartialCustomerModel<? extends Customer> customer;
            PartialAddressModel<? extends PartialAddressDAO> address;
            PartialCityModel<? extends PartialCityDAO> city;
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
                        if (null != address) {
                            return numberFormat.format(address.getPrimaryKey());
                        }
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
                            if (null != city) {
                                return numberFormat.format(city.getPrimaryKey());
                            }
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
                                PartialCountryModel<? extends PartialCountryDAO> country = city.getCountry();
                                if (null != country) {
                                    return numberFormat.format(country.getPrimaryKey());
                                }
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
                    PartialUserModel<? extends PartialUserDAO> user = item.getUser();
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
    }

}
