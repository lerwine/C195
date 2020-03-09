package scheduler.view.appointment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import scheduler.App;
import scheduler.AppResources;
import scheduler.dao.AppointmentFilter;
import scheduler.dao.AppointmentImpl;
import scheduler.dao.CustomerImpl;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.UserImpl;
import scheduler.util.Alerts;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class for viewing a list of {@link AppointmentModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.AppointmentFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/appointment/ManageAppointments.fxml")
public final class ManageAppointments extends ListingController<AppointmentImpl, AppointmentModel> implements ManageAppointmentsResourceKeys {

    private static final Logger LOG = Logger.getLogger(ManageAppointments.class.getName());

    /**
     * Loads {@link AppointmentModel} listing view and controller into the {@link MainController}.
     *
     * @param mainController The {@link MainController} to contain the {@link CountryModel} listing.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param filter The {@link AppointmentFilter} to use for loading and filtering {@link CountryModel} items.
     * @throws IOException if unable to load the view.
     */
    public static void setContent(MainController mainController, Stage stage, AppointmentFilter filter) throws IOException {
        setContent(mainController, ManageAppointments.class, stage, filter);
    }
    private final BooleanProperty allItems;
    private final ObjectProperty<CustomerImpl> filterCustomer;
    private final ObjectProperty<UserImpl> filterUser;
    private final ObjectProperty<LocalDate> start;
    private final ObjectProperty<LocalDate> end;
    private RangeFilterType rangeFilter;
    private ObservableList<FilterType> filters;

    @FXML
    private Label headingLabel;

    @FXML // fx:id="filterTypeComboBox"
    private ComboBox<FilterType> filterTypeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="filterButton"
    private Button filterButton; // Value injected by FXMLLoader

    public ManageAppointments() {
        start = new SimpleObjectProperty<>();
        end = new SimpleObjectProperty<>();
        allItems = new SimpleBooleanProperty(false);
        filterCustomer = new SimpleObjectProperty<>(null);
        filterUser = new SimpleObjectProperty<>(App.getCurrentUser());
    }

    public LocalDate getStart() {
        return start.get();
    }

    public void setStart(LocalDate value) {
        start.set(value);
    }

    public ObjectProperty<LocalDate> startProperty() {
        return start;
    }

    public LocalDate getEnd() {
        return end.get();
    }

    public void setEnd(LocalDate value) {
        end.set(value);
    }

    public ObjectProperty<LocalDate> endProperty() {
        return end;
    }

    public boolean isAllItems() {
        return allItems.get();
    }

    public void setAllItems(boolean value) {
        allItems.set(value);
    }

    public BooleanProperty allItemsProperty() {
        return allItems;
    }

    public CustomerImpl getFilterCustomer() {
        return filterCustomer.get();
    }

    public void setFilterCustomer(CustomerImpl value) {
        filterCustomer.set(value);
    }

    public ObjectProperty<CustomerImpl> filterCustomerProperty() {
        return filterCustomer;
    }

    public UserImpl getFilterUser() {
        return filterUser.get();
    }

    public void setFilterUser(UserImpl value) {
        filterUser.set(value);
    }

    public ObjectProperty filterUserProperty() {
        return filterUser;
    }

    @FXML
    void filterTypeChanged(ActionEvent event) {
        Alerts.showWarningAlert("Not implemented");
    }

    @FXML
    void filterButtonClick(ActionEvent event) {
        Alerts.showWarningAlert("Not implemented");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    @Override
    protected void initialize() {
        super.initialize();
        assert headingLabel != null : String.format("fx:id=\"headingLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert filterTypeComboBox != null : String.format("fx:id=\"filterTypeComboBox\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert filterButton != null : String.format("fx:id=\"filterButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        filters = FXCollections.observableArrayList(new FilterType(getResourceString(RESOURCEKEY_CURRENT)) {
            @Override
            AppointmentFilter getMyFilter() {
                return AppointmentFilter.myCurrent();
            }

            @Override
            AppointmentFilter getFilter(CustomerImpl customer, UserImpl user) {
                if (null == customer) {
                    if (null == user) {
                        return AppointmentFilter.current();
                    }
                    return AppointmentFilter.byUserCurrent(user);
                }

                if (null == user) {
                    return AppointmentFilter.byCustomerCurrent(customer);
                }
                return AppointmentFilter.byCustomerAndUserCurrent(customer, user);
            }
        }, new FilterType(getResourceString(RESOURCEKEY_ALLCURRENTANDFUTURE)) {
            @Override
            AppointmentFilter getMyFilter() {
                return AppointmentFilter.myCurrentAndFuture();
            }

            @Override
            AppointmentFilter getFilter(CustomerImpl customer, UserImpl user) {
                if (null == customer) {
                    if (null == user) {
                        return AppointmentFilter.currentAndFuture();
                    }
                    return AppointmentFilter.byUserCurrentAndFuture(user);
                }

                if (null == user) {
                    return AppointmentFilter.byCustomerCurrentAndFuture(customer);
                }
                return AppointmentFilter.byCustomerAndUserCurrentAndFuture(customer, user);
            }
        }, new FilterType(getResourceString(RESOURCEKEY_PAST)) {
            @Override
            AppointmentFilter getMyFilter() {
                return AppointmentFilter.myPast();
            }

            @Override
            AppointmentFilter getFilter(CustomerImpl customer, UserImpl user) {
                if (null == customer) {
                    if (null == user) {
                        return AppointmentFilter.past();
                    }
                    return AppointmentFilter.byUserPast(user);
                }

                if (null == user) {
                    return AppointmentFilter.byCustomerPast(customer);
                }
                return AppointmentFilter.byCustomerAndUserPast(customer, user);
            }
        }, new FilterType(getResourceString(RESOURCEKEY_ALLAPPOINTMENTS)) {
            @Override
            AppointmentFilter getMyFilter() {
                return AppointmentFilter.allMyItems();
            }

            @Override
            AppointmentFilter getFilter(CustomerImpl customer, UserImpl user) {
                if (null == customer) {
                    if (null == user) {
                        return AppointmentFilter.all();
                    }
                    return AppointmentFilter.byUser(user);
                }

                if (null == user) {
                    return AppointmentFilter.byCustomer(customer);
                }
                return AppointmentFilter.byCustomerAndUser(customer, user);
            }
        });
        rangeFilter = new RangeFilterType();

        filterTypeComboBox.setCellFactory((ListView<FilterType> param) -> {
            return new ListCell<FilterType>() {
                @Override
                protected void updateItem(FilterType item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null) ? "" : item.getDisplayText());
                }
            };
        });
        filterTypeComboBox.setButtonCell(new ListCell<FilterType>() {
            @Override
            protected void updateItem(FilterType item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null) ? "" : item.getDisplayText());
            }
        });
        filterTypeComboBox.setItems(filters);
    }

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemAddManager() {
        return getMainController().getAppointmentAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<AppointmentModel>> getItemRemoveManager() {
        return getMainController().getAppointmentRemoveManager();
    }

    @Override
    protected void onDeleteItem(Event event, AppointmentModel item) {
        getMainController().deleteAppointment(event, item);
    }

    @Override
    protected AppointmentModel toModel(AppointmentImpl dao) {
        return new AppointmentModel(dao);
    }

    @Override
    protected DataObjectImpl.Factory<AppointmentImpl, AppointmentModel> getDaoFactory() {
        return AppointmentImpl.getFactory();
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewAppointment(event);
    }

    @Override
    protected EditItem.ShowAndWaitResult<AppointmentModel> onEditItem(Event event, AppointmentModel item) {
        return getMainController().editAppointment(event, item);
    }

    public static abstract class FilterType {

        private final ReadOnlyStringWrapper displayText;

        protected FilterType(String displayText) {
            this.displayText = new ReadOnlyStringWrapper(displayText);
        }

        public String getDisplayText() {
            return displayText.get();
        }

        public ReadOnlyStringProperty displayTextProperty() {
            return displayText.getReadOnlyProperty();
        }
        private final ReadOnlyBooleanWrapper disabled = new ReadOnlyBooleanWrapper();

        public boolean isDisabled() {
            return disabled.get();
        }

        public ReadOnlyBooleanProperty disabledProperty() {
            return disabled.getReadOnlyProperty();
        }

        abstract AppointmentFilter getMyFilter();

        abstract AppointmentFilter getFilter(CustomerImpl customer, UserImpl user);
    }

    public class RangeFilterType extends FilterType {

        public RangeFilterType() {
            super(getResourceString(RESOURCEKEY_APPOINTMENTSINRANGE));
        }

        @Override
        AppointmentFilter getMyFilter() {
            LocalDate s = start.get();
            LocalDate e = end.get();
            if (null == s) {
                if (null == e) {
                    return AppointmentFilter.myCurrent();
                }
                return AppointmentFilter.myBeforeDate(e);
            }
            if (null == e) {
                return AppointmentFilter.myOnOrAfterDate(s);
            }
            return AppointmentFilter.myWithin(s, e);
        }

        @Override
        AppointmentFilter getFilter(CustomerImpl customer, UserImpl user) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
