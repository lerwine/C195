package scheduler.view.appointment;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scheduler.dao.AppointmentDAO;
import static scheduler.model.Appointment.MAX_LENGTH_URL;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.PredefinedData;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.CustomerModel;
import scheduler.model.fx.UserModel;
import scheduler.observables.BindingHelper;
import scheduler.util.BinarySelective;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class TypeContextController {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(TypeContextController.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(TypeContextController.class.getName());
    private static final String INVALID_URL = "Invalid URL";

    private static BinarySelective<String, String> calculateURL(AppointmentType type, String text) {
        LOG.entering(LOG.getName(), "calculateURL", new Object[]{type, text});
        BinarySelective<String, String> result;
        if (null == text || (text = text.trim()).isEmpty()) {
            if (type == AppointmentType.VIRTUAL) {
                result = BinarySelective.ofSecondary("* Required");
            } else {
                result = BinarySelective.ofPrimaryNullable(null);
            }
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        URI uri;
        try {
            uri = new URI(text);
        } catch (URISyntaxException ex) {
            LOG.log(Level.WARNING, String.format("Error parsing url %s", text), ex);
            text = ex.getMessage();
            result = BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? INVALID_URL : text);
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException ex) {
            LOG.log(Level.WARNING, String.format("Error converting uri %s", text), ex);
            text = ex.getMessage();
            result = BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? INVALID_URL : text);
            LOG.exiting(LOG.getName(), "calculateURL", result);
            return result;
        }
        if ((text = url.toString()).length() > MAX_LENGTH_URL) {
            result = BinarySelective.ofSecondary("URL too long");
        } else {
            result = BinarySelective.ofPrimary(text);
        }
        LOG.exiting(LOG.getName(), "calculateURL", result);
        return result;
    }

    private final EditAppointment editAppointmentControl;
    private final AppointmentConflictsController appointmentConflicts;
    private final ObservableList<CorporateAddress> corporateLocationList;
    private final ObservableList<CorporateAddress> remoteLocationList;
    private StringBinding normalizedContact;
    private ReadOnlyObjectProperty<AppointmentType> selectedType;
    private ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
    private StringBinding normalizedLocation;
    private StringBinding normalizedPhone;
    private ObjectBinding<BinarySelective<String, String>> parsedUrl;
    private BooleanBinding valid;
    private BooleanBinding modified;
    private StringBinding daoLocation;
    private StringBinding urlString;

    TypeContextController(EditAppointment editAppointmentControl) {
        this.editAppointmentControl = editAppointmentControl;
        corporateLocationList = FXCollections.observableArrayList();
        remoteLocationList = FXCollections.observableArrayList();
        PredefinedData.getCorporateAddressMap().values().forEach((t) -> {
            if (t.isSatelliteOffice()) {
                remoteLocationList.add(t);
            } else {
                corporateLocationList.add(t);
            }
        });
        appointmentConflicts = new AppointmentConflictsController(editAppointmentControl);
    }

    public StringBinding normalizedContactBinding() {
        return normalizedContact;
    }

    public StringBinding normalizedPhoneBinding() {
        return normalizedPhone;
    }

    public StringBinding normalizedLocationBinding() {
        return normalizedLocation;
    }

    public ObjectBinding<BinarySelective<String, String>> parsedUrlBinding() {
        return parsedUrl;
    }

    public ReadOnlyObjectProperty<AppointmentType> selectedTypeProperty() {
        return selectedType;
    }

    public ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocationProperty() {
        return selectedCorporateLocation;
    }

    public ReadOnlyObjectProperty<CustomerModel> selectedCustomerProperty() {
        return appointmentConflicts.selectedCustomerProperty();
    }

    public ReadOnlyObjectProperty<UserModel> selectedUserProperty() {
        return appointmentConflicts.selectedUserProperty();
    }

    public LocalDateTime getStartDateTimeValue() {
        return appointmentConflicts.getStartDateTimeValue();
    }

    public String getStartValidationMessage() {
        return appointmentConflicts.getStartValidationMessage();
    }

    public LocalDateTime getEndDateTimeValue() {
        return appointmentConflicts.getEndDateTimeValue();
    }

    public boolean isWithinBusinessHours() {
        return appointmentConflicts.isWithinBusinessHours();
    }

    public String getConflictMessage() {
        return appointmentConflicts.getConflictMessage();
    }

    ConflictCheckStatus getConflictCheckStatus() {
        return appointmentConflicts.getConflictCheckStatus();
    }

    public boolean isValid() {
        return valid.get();
    }

    public BooleanBinding validBinding() {
        return valid;
    }

    void initialize() {
        LOG.entering(LOG.getName(), "initialize");

        appointmentConflicts.initialize();
        AppointmentModel model = editAppointmentControl.getModel();

        //<editor-fold defaultstate="collapsed" desc="contact">
        TextField contactTextField = editAppointmentControl.getContactTextField();
        contactTextField.setText(model.getContact());
        StringProperty contactText = contactTextField.textProperty();
        normalizedContact = BindingHelper.asNonNullAndWsNormalized(contactText);
        contactText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactTextField#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactTextField#text"), "changed");
        });
        Label contactValidationLabel = editAppointmentControl.getContactValidationLabel();

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="type">
        ComboBox<AppointmentType> typeComboBox = editAppointmentControl.getTypeComboBox();
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        SingleSelectionModel<AppointmentType> typeSelectionModel = typeComboBox.getSelectionModel();
        typeSelectionModel.select(model.getType());
        selectedType = typeSelectionModel.selectedItemProperty();

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="corporateLocatio">
        ComboBox<CorporateAddress> corporateLocationComboBox = editAppointmentControl.getCorporateLocationComboBox();
        corporateLocationComboBox.setItems(corporateLocationList);
        SingleSelectionModel<CorporateAddress> corporateLocationSelectionModel = corporateLocationComboBox.getSelectionModel();
        selectedCorporateLocation = corporateLocationSelectionModel.selectedItemProperty();
        selectedCorporateLocation.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "corporateLocationComboBox#value"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "corporateLocationComboBox#value"), "changed");
        });

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="phone">
        TextField phoneTextField = editAppointmentControl.getPhoneTextField();
        StringProperty phoneText = phoneTextField.textProperty();
        normalizedPhone = BindingHelper.asNonNullAndWsNormalizedMultiLine(phoneText);
        phoneText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "phoneTextField#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "phoneTextField#text"), "changed");
        });

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="parsedUrl">
        TextField urlTextField = editAppointmentControl.getUrlTextField();
        urlTextField.setText(model.getUrl());
        parsedUrl = Bindings.createObjectBinding(() -> calculateURL(selectedType.get(), urlTextField.textProperty().get()), selectedType, urlTextField.textProperty());
        Label urlValidationLabel = editAppointmentControl.getUrlValidationLabel();
        parsedUrl.get().accept((t) -> {
            urlValidationLabel.setText("");
            urlValidationLabel.setVisible(false);
        }, (t) -> {
            urlValidationLabel.setText(t);
            urlValidationLabel.setVisible(true);
        });
        urlTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlTextField#text"), "changed", new Object[]{oldValue, newValue});
            onUrlChanged();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlTextField#text"), "changed");
        });
        urlString = Bindings.createStringBinding(() -> {
            String u = parsedUrl.get().toPrimary("");
            return (null == u) ? "" : u;
        }, parsedUrl);

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="location">
        TextArea locationTextArea = editAppointmentControl.getLocationTextArea();
        StringProperty locationText = locationTextArea.textProperty();
        normalizedLocation = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationText);
        String location = model.getLocation();
        Label locationValidationLabel = editAppointmentControl.getLocationValidationLabel();
        locationText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationTextArea#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationTextArea#text"), "changed");
        });
        daoLocation = AppointmentHelper.daoLocationBinding(selectedType, normalizedLocation, normalizedPhone, selectedCorporateLocation, appointmentConflicts.selectedCustomerProperty(), true);

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init by type">
        switch (selectedType.get()) {
            case CORPORATE_LOCATION:
                restoreNode(corporateLocationComboBox);
                collapseNode(locationTextArea);
                CorporateAddress cl = corporateLocationList.stream().filter((t) -> t.getName().equals(location)).findFirst().orElseGet(() -> {
                    CorporateAddress r = remoteLocationList.stream().filter((u) -> u.getName().equals(location)).findFirst().orElse(null);
                    if (null != r) {
                        editAppointmentControl.getIncludeRemoteCheckBox().setSelected(true);
                        remoteLocationList.forEach((u) -> corporateLocationList.add(u));
                    }
                    return r;
                });
                locationValidationLabel.setVisible(null == cl);
                if (locationValidationLabel.isVisible()) {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), "(corporate location)");
                } else {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), cl.toMultiLineAddress());
                    corporateLocationSelectionModel.select(cl);
                    collapseNode(locationValidationLabel);
                }
                contactValidationLabel.setVisible(false);
                break;
            case CUSTOMER_SITE:
                locationValidationLabel.setVisible(false);
                collapseNode(locationValidationLabel);
                collapseNode(locationTextArea);
                CustomerModel cm = appointmentConflicts.selectedCustomerProperty().get();
                if (null == cm) {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), "(customer site)");
                } else {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), cm.getMultiLineAddress());
                }
                contactValidationLabel.setVisible(false);
                break;
            case PHONE:
                editAppointmentControl.getLocationLabel().setText("Phone:");
                collapseNode(locationTextArea);
                restoreNode(phoneTextField);
                editAppointmentControl.getLocationLabel().setText(editAppointmentControl.getResources().getString(RESOURCEKEY_PHONENUMBER));
                phoneTextField.setText(location);
                locationValidationLabel.setVisible(normalizedPhone.get().isEmpty());
                if (!locationValidationLabel.isVisible()) {
                    collapseNode(locationValidationLabel);
                }
                contactValidationLabel.setVisible(false);
                break;
            case VIRTUAL:
                locationValidationLabel.setVisible(false);
                collapseNode(locationValidationLabel);
                contactValidationLabel.setVisible(false);
                break;
            default:
                locationTextArea.setText(location);
                locationValidationLabel.setVisible(normalizedLocation.get().isEmpty());
                if (!locationValidationLabel.isVisible()) {
                    collapseNode(locationValidationLabel);
                }
                contactValidationLabel.setVisible(normalizedContact.get().isEmpty());
                break;
        }

        //</editor-fold>
        appointmentConflicts.selectedCustomerProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#selectedCustomer"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#selectedCustomer"), "changed");
        });

        valid = appointmentConflicts.validBinding()
                .and(contactValidationLabel.visibleProperty().or(locationValidationLabel.visibleProperty()).or(urlValidationLabel.visibleProperty()).not());
        modified = appointmentConflicts.modifiedBinding().or(normalizedContact.isNotEqualTo(model.contactProperty()))
                .or(daoLocation.isNotEqualTo(model.locationProperty()))
                .or(urlString.isNotEqualTo(model.urlProperty()));
        valid.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("valid changed from %s to %s", oldValue, newValue));
        });
        modified.addListener((observable, oldValue, newValue) -> {
            LOG.info(String.format("modified changed from %s to %s", oldValue, newValue));
        });
        LOG.info(String.format("modified initial value is %s", modified.get()));
        selectedType.addListener(this::onTypeChanged);
        LOG.exiting(LOG.getName(), "initialize");
    }

    public StringBinding daoLocationBinding() {
        return daoLocation;
    }

    public BooleanBinding modifiedBinding() {
        return modified;
    }

    void initialize(Task<List<AppointmentDAO>> task) {
        appointmentConflicts.initialize(task);
    }

    boolean canSave() {
        return appointmentConflicts.canSave();
    }

    private synchronized void onContextSensitiveChange() {
        Label locationValidationLabel = editAppointmentControl.getLocationValidationLabel();
        boolean wasInvalid = locationValidationLabel.isVisible();
        switch (selectedType.get()) {
            case CORPORATE_LOCATION:
                CorporateAddress cl = selectedCorporateLocation.get();
                locationValidationLabel.setVisible(null == cl);
                if (locationValidationLabel.isVisible()) {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), "(corporate location)");
                } else {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), cl.toMultiLineAddress());
                }
                break;
            case CUSTOMER_SITE:
                CustomerModel cm = appointmentConflicts.selectedCustomerProperty().get();
                if (null == cm) {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), "(customer site)");
                } else {
                    restoreLabeled(editAppointmentControl.getImplicitLocationLabel(), cm.getMultiLineAddress());
                }
                break;
            case PHONE:
                locationValidationLabel.setVisible(normalizedPhone.get().isEmpty());
                break;
            case VIRTUAL:
                onUrlChanged();
                break;
            default:
                locationValidationLabel.setVisible(normalizedLocation.get().isEmpty());
                editAppointmentControl.getContactValidationLabel().setVisible(normalizedContact.get().isEmpty());
                break;
        }
        if (wasInvalid != locationValidationLabel.isVisible()) {
            if (wasInvalid) {
                collapseNode(locationValidationLabel);
            } else {
                restoreNode(locationValidationLabel);
            }
        }
    }

    private synchronized void onUrlChanged() {
        Label urlValidationLabel = editAppointmentControl.getUrlValidationLabel();
        parsedUrl.get().accept((t) -> {
            urlValidationLabel.setText("");
            urlValidationLabel.setVisible(false);
        }, (t) -> {
            urlValidationLabel.setText(t);
            urlValidationLabel.setVisible(true);
        });
    }

    private synchronized void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        LOG.entering(LOG.getName(), "onTypeChanged", new Object[]{oldValue, newValue});
        if (oldValue == newValue) {
            return;
        }
        switch (oldValue) {
            case CORPORATE_LOCATION:
                if (newValue == AppointmentType.CUSTOMER_SITE) {
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                collapseNode(editAppointmentControl.getCorporateLocationComboBox());
                collapseNode(editAppointmentControl.getImplicitLocationLabel());
                break;
            case CUSTOMER_SITE:
                if (newValue == AppointmentType.CORPORATE_LOCATION) {
                    restoreNode(editAppointmentControl.getCorporateLocationComboBox());
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                collapseNode(editAppointmentControl.getImplicitLocationLabel());
                break;
            case PHONE:
                collapseNode(editAppointmentControl.getPhoneTextField());
                editAppointmentControl.getLocationLabel().setText(editAppointmentControl.getResources().getString(RESOURCEKEY_LOCATIONLABELTEXT));
                break;
            case VIRTUAL:
                onUrlChanged();
                if (newValue == AppointmentType.OTHER) {
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                collapseNode(editAppointmentControl.getLocationTextArea());
                break;
            default:
                if (newValue == AppointmentType.VIRTUAL) {
                    editAppointmentControl.getLocationValidationLabel().setVisible(false);
                    editAppointmentControl.getContactValidationLabel().setVisible(false);
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                editAppointmentControl.getContactValidationLabel().setVisible(false);
                collapseNode(editAppointmentControl.getLocationTextArea());
                break;
        }
        switch (newValue) {
            case CORPORATE_LOCATION:
                editAppointmentControl.getContactValidationLabel().setVisible(false);
                restoreNode(editAppointmentControl.getCorporateLocationComboBox());
                break;
            case CUSTOMER_SITE:
                editAppointmentControl.getContactValidationLabel().setVisible(false);
                editAppointmentControl.getLocationValidationLabel().setVisible(false);
                break;
            case PHONE:
                editAppointmentControl.getContactValidationLabel().setVisible(false);
                editAppointmentControl.getLocationLabel().setText(editAppointmentControl.getResources().getString(RESOURCEKEY_PHONENUMBER));
                restoreNode(editAppointmentControl.getPhoneTextField());
                break;
            case VIRTUAL:
                editAppointmentControl.getLocationValidationLabel().setVisible(false);
                editAppointmentControl.getContactValidationLabel().setVisible(false);
                restoreNode(editAppointmentControl.getLocationTextArea());
                onUrlChanged();
                break;
            default:
                restoreNode(editAppointmentControl.getLocationTextArea());
                break;
        }
        onContextSensitiveChange();
        LOG.exiting(LOG.getName(), "onTypeChanged");
    }

}
