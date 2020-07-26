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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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
 * @todo Implement scheduler.view.appointment.edit.TypeContextController
 */
public final class TypeContextController {

    private static final Logger LOG = Logger.getLogger(TypeContextController.class.getName());
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
    private final ReadOnlyBooleanWrapper valid;
    private final AppointmentConflictsController appointmentConflicts;
    private final ObservableList<CorporateAddress> corporateLocationList;
    private final ObservableList<CorporateAddress> remoteLocationList;
    private StringBinding normalizedContact;
    private ReadOnlyObjectProperty<AppointmentType> selectedType;
    private ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
    private StringBinding normalizedLocation;
    private StringBinding normalizedPhone;
    private ObjectBinding<BinarySelective<String, String>> parsedUrl;
    private AppointmentModel model;
    private TextField contactTextField;
    private SingleSelectionModel<AppointmentType> typeSelectionModel;
    private SingleSelectionModel<CorporateAddress> corporateLocationSelectionModel;
    private StringProperty locationText;
    private StringProperty phoneText;
    private Label locationValidationLabel;
    private Label contactValidationLabel;
    private TextArea locationTextArea;
    private ComboBox<CorporateAddress> corporateLocationComboBox;
    private TextField phoneTextField;
    private Label urlValidationLabel;
    private Label implicitLocationLabel;
    private Label locationLabel;

    TypeContextController(EditAppointment editAppointmentControl) {
        this.editAppointmentControl = editAppointmentControl;
        valid = new ReadOnlyBooleanWrapper(this, "valid", false);
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

    public String getNormalizedContact() {
        return normalizedContact.get();
    }

    public StringBinding normalizedContactBinding() {
        return normalizedContact;
    }

    public String getNormalizedPhone() {
        return normalizedPhone.get();
    }

    public StringBinding normalizedPhoneBinding() {
        return normalizedPhone;
    }

    public String getNormalizedLocation() {
        return normalizedLocation.get();
    }

    public StringBinding normalizedLocationBinding() {
        return normalizedLocation;
    }

    public BinarySelective<String, String> getParsedUrl() {
        return parsedUrl.get();
    }

    public ObjectBinding<BinarySelective<String, String>> parsedUrlBinding() {
        return parsedUrl;
    }

    public AppointmentType getSelectedType() {
        return selectedType.get();
    }

    public ReadOnlyObjectProperty<AppointmentType> selectedTypeProperty() {
        return selectedType;
    }

    public CorporateAddress getSelectedCorporateLocation() {
        return selectedCorporateLocation.get();
    }

    public ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocationProperty() {
        return selectedCorporateLocation;
    }

    public CustomerModel getSelectedCustomer() {
        return appointmentConflicts.getSelectedCustomer();
    }

//    public ReadOnlyObjectProperty<CustomerModel> selectedCustomerProperty() {
//        return appointmentConflicts.selectedCustomerProperty();
//    }
    public UserModel getSelectedUser() {
        return appointmentConflicts.getSelectedUser();
    }

//    public ReadOnlyObjectProperty<UserModel> selectedUserProperty() {
//        return appointmentConflicts.selectedUserProperty();
//    }
    public LocalDateTime getStartDateTimeValue() {
        return appointmentConflicts.getStartDateTimeValue();
    }

//    public ReadOnlyObjectProperty<LocalDateTime> startDateTimeValueProperty() {
//        return startDateTimeValue.getReadOnlyProperty();
//    }
    public String getStartValidationMessage() {
        return appointmentConflicts.getStartValidationMessage();
    }

//    public ReadOnlyStringProperty startValidationMessageProperty() {
//        return startValidationMessage.getReadOnlyProperty();
//    }
    public LocalDateTime getEndDateTimeValue() {
        return appointmentConflicts.getEndDateTimeValue();
    }

//    public ReadOnlyObjectProperty<LocalDateTime> endDateTimeValueProperty() {
//        return endDateTimeValue.getReadOnlyProperty();
//    }
    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    void initialize() {
        LOG.entering(LOG.getName(), "initialize");
        appointmentConflicts.initialize();
        model = editAppointmentControl.getModel();
        contactTextField = editAppointmentControl.getContactTextField();
        contactTextField.setText(model.getContact());
        StringProperty contactText = contactTextField.textProperty();
        normalizedContact = BindingHelper.asNonNullAndWsNormalized(contactText);
        ComboBox<AppointmentType> typeComboBox = editAppointmentControl.getTypeComboBox();
        typeComboBox.setItems(FXCollections.observableArrayList(AppointmentType.values()));
        typeSelectionModel = typeComboBox.getSelectionModel();
        typeSelectionModel.select(model.getType());
        selectedType = typeSelectionModel.selectedItemProperty();
        corporateLocationComboBox = editAppointmentControl.getCorporateLocationComboBox();
        corporateLocationComboBox.setItems(corporateLocationList);
        corporateLocationSelectionModel = corporateLocationComboBox.getSelectionModel();
        selectedCorporateLocation = corporateLocationSelectionModel.selectedItemProperty();
        locationLabel = editAppointmentControl.getLocationLabel();
        locationTextArea = editAppointmentControl.getLocationTextArea();
        locationText = locationTextArea.textProperty();
        normalizedLocation = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationText);

        phoneText = editAppointmentControl.getPhoneTextField().textProperty();
        normalizedPhone = BindingHelper.asNonNullAndWsNormalizedMultiLine(phoneText);
        TextField urlTextField = editAppointmentControl.getUrlTextField();
        urlTextField.setText(model.getUrl());
        parsedUrl = Bindings.createObjectBinding(() -> calculateURL(selectedType.get(), urlTextField.textProperty().get()), selectedType, urlTextField.textProperty());
        phoneTextField = editAppointmentControl.getPhoneTextField();

        String location = model.getLocation();
        locationValidationLabel = editAppointmentControl.getLocationValidationLabel();
        contactValidationLabel = editAppointmentControl.getContactValidationLabel();
        implicitLocationLabel = editAppointmentControl.getImplicitLocationLabel();
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
                    restoreLabeled(implicitLocationLabel, "(corporate location)");
                } else {
                    restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                    corporateLocationSelectionModel.select(cl);
                    collapseNode(locationValidationLabel);
                }
                break;
            case CUSTOMER_SITE:
                locationValidationLabel.setVisible(false);
                collapseNode(locationValidationLabel);
                collapseNode(locationTextArea);
                CustomerModel cm = appointmentConflicts.getSelectedCustomer();
                if (null == cm) {
                    restoreLabeled(implicitLocationLabel, "(customer site)");
                } else {
                    restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
                }
                break;
            case PHONE:
                collapseNode(locationTextArea);
                restoreNode(phoneTextField);
                editAppointmentControl.getLocationLabel().setText(editAppointmentControl.getResources().getString(RESOURCEKEY_PHONENUMBER));
                phoneTextField.setText(location);
                locationValidationLabel.setVisible(normalizedPhone.get().isEmpty());
                if (!locationValidationLabel.isVisible()) {
                    collapseNode(locationValidationLabel);
                }
                break;
            case VIRTUAL:
                locationValidationLabel.setVisible(false);
                collapseNode(locationValidationLabel);
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
        urlValidationLabel = editAppointmentControl.getUrlValidationLabel();
        parsedUrl.get().accept((t) -> {
            urlValidationLabel.setText("");
            urlValidationLabel.setVisible(false);
        }, (t) -> {
            urlValidationLabel.setText(t);
            urlValidationLabel.setVisible(true);
        });
        appointmentConflicts.selectedCustomerProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#selectedCustomer"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#selectedCustomer"), "changed");
        });
        contactText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactTextField#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactTextField#text"), "changed");
        });
        selectedCorporateLocation.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "corporateLocationComboBox#value"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "corporateLocationComboBox#value"), "changed");
        });
        locationText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationTextArea#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationTextArea#text"), "changed");
        });
        phoneText.addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "phoneTextField#text"), "changed", new Object[]{oldValue, newValue});
            onContextSensitiveChange();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "phoneTextField#text"), "changed");
        });
        urlTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlTextField#text"), "changed", new Object[]{oldValue, newValue});
            onUrlChanged();
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlTextField#text"), "changed");
        });
        appointmentConflicts.validProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#valid"), "changed", new Object[]{oldValue, newValue});
            onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), newValue);
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "appointmentConflicts#valid"), "changed");
        });
        contactValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactValidationLabel#visible"), "changed", new Object[]{oldValue, newValue});
            onValidityChanged(newValue, locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), appointmentConflicts.isValid());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "contactValidationLabel#visible"), "changed");
        });
        locationValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationValidationLabel#visible"), "changed", new Object[]{oldValue, newValue});
            onValidityChanged(contactValidationLabel.isVisible(), newValue, urlValidationLabel.isVisible(), appointmentConflicts.isValid());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "locationValidationLabel#visible"), "changed");
        });
        urlValidationLabel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlValidationLabel#visible"), "changed", new Object[]{oldValue, newValue});
            onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), newValue, appointmentConflicts.isValid());
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "initialize", "urlValidationLabel#visible"), "changed");
        });
        onValidityChanged(contactValidationLabel.isVisible(), locationValidationLabel.isVisible(), urlValidationLabel.isVisible(), appointmentConflicts.isValid());
        selectedType.addListener(this::onTypeChanged);
        LOG.exiting(LOG.getName(), "initialize");
    }

    void initialize(Task<List<AppointmentDAO>> task) {
        appointmentConflicts.initialize(task);
    }

    private synchronized void onContextSensitiveChange() {
        boolean wasInvalid = locationValidationLabel.isVisible();
        switch (selectedType.get()) {
            case CORPORATE_LOCATION:
                CorporateAddress cl = selectedCorporateLocation.get();
                locationValidationLabel.setVisible(null == cl);
                if (locationValidationLabel.isVisible()) {
                    restoreLabeled(implicitLocationLabel, "(corporate location)");
                } else {
                    restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                }
                break;
            case CUSTOMER_SITE:
                CustomerModel cm = appointmentConflicts.getSelectedCustomer();
                if (null == cm) {
                    restoreLabeled(implicitLocationLabel, "(customer site)");
                } else {
                    restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
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
                contactValidationLabel.setVisible(normalizedContact.get().isEmpty());
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

    private synchronized void onValidityChanged(boolean contactIsInvalid, boolean locationIsInvalid, boolean urlIsInvalid, boolean rangesAreValid) {
        LOG.entering(LOG.getName(), "onValidityChanged", new Object[]{contactIsInvalid, locationIsInvalid, urlIsInvalid, rangesAreValid});
        boolean v = rangesAreValid && !(contactIsInvalid || locationIsInvalid || urlIsInvalid);
        if (v != valid.get()) {
            valid.set(v);
        }
    }

    private synchronized void onUrlChanged() {
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
                collapseNode(corporateLocationComboBox);
                collapseNode(implicitLocationLabel);
                break;
            case CUSTOMER_SITE:
                if (newValue == AppointmentType.CORPORATE_LOCATION) {
                    restoreNode(corporateLocationComboBox);
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                collapseNode(implicitLocationLabel);
                break;
            case PHONE:
                collapseNode(phoneTextField);
                locationLabel.setText(editAppointmentControl.getResources().getString(RESOURCEKEY_LOCATIONLABELTEXT));
                break;
            case VIRTUAL:
                onUrlChanged();
                if (newValue == AppointmentType.OTHER) {
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                collapseNode(locationTextArea);
                break;
            default:
                if (newValue == AppointmentType.VIRTUAL) {
                    locationValidationLabel.setVisible(false);
                    contactValidationLabel.setVisible(false);
                    onContextSensitiveChange();
                    LOG.exiting(LOG.getName(), "onTypeChanged");
                    return;
                }
                contactValidationLabel.setVisible(false);
                collapseNode(locationTextArea);
                break;
        }
        switch (newValue) {
            case CORPORATE_LOCATION:
                contactValidationLabel.setVisible(false);
                restoreNode(corporateLocationComboBox);
                break;
            case CUSTOMER_SITE:
                contactValidationLabel.setVisible(false);
                locationValidationLabel.setVisible(false);
                break;
            case PHONE:
                contactValidationLabel.setVisible(false);
                locationLabel.setText(editAppointmentControl.getResources().getString(RESOURCEKEY_PHONENUMBER));
                restoreNode(phoneTextField);
                break;
            case VIRTUAL:
                locationValidationLabel.setVisible(false);
                contactValidationLabel.setVisible(false);
                restoreNode(locationTextArea);
                onUrlChanged();
                break;
            default:
                restoreNode(locationTextArea);
                break;
        }
        onContextSensitiveChange();
        LOG.exiting(LOG.getName(), "onTypeChanged");
    }

}
