package scheduler.view.appointment.edit;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static scheduler.model.Appointment.MAX_LENGTH_URL;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.PredefinedData;
import scheduler.model.ui.AppointmentModel;
import scheduler.model.ui.CustomerModel;
import scheduler.observables.BindingHelper;
import scheduler.util.BinarySelective;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.appointment.EditAppointment;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.view.appointment.edit.TypeContextController}
 */
public class TypeContextController {

    private static Optional<String> calculateEffectiveLocation(AppointmentType type, String location, String phone, CustomerModel customer, CorporateAddress corporateAddress) {
        switch (type) {
            case CORPORATE_LOCATION:
                if (null == corporateAddress) {
                    return Optional.empty();
                }
                return Optional.of(corporateAddress.toMultiLineAddress());
            case CUSTOMER_SITE:
                return Optional.of((null == customer) ? "" : customer.getMultiLineAddress());
            case PHONE:
                if (phone.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(phone);
            case VIRTUAL:
                return Optional.of(location);
            default:
                if (location.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(location);
        }
    }

    private static BinarySelective<String, String> calculateURL(AppointmentType type, String text) {
        if (null == text || (text = text.trim()).isEmpty()) {
            if (type == AppointmentType.VIRTUAL) {
                return BinarySelective.ofSecondary("* Required");
            }
            return BinarySelective.ofPrimaryNullable(null);
        }
        URI uri;
        try {
            uri = new URI(text);
        } catch (URISyntaxException ex) {
            Logger.getLogger(TypeContextController.class.getName()).log(Level.WARNING, String.format("Error parsing url %s", text), ex);
            text = ex.getMessage();
            return BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
        }
        URL url;
        try {
            url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException ex) {
            Logger.getLogger(TypeContextController.class.getName()).log(Level.WARNING, String.format("Error converting uri %s", text), ex);
            text = ex.getMessage();
            return BinarySelective.ofSecondary((null == text || text.trim().isEmpty()) ? "Invalid URL" : text);
        }
        if ((text = url.toString()).length() > MAX_LENGTH_URL) {
            return BinarySelective.ofSecondary("URL too long");
        }
        return BinarySelective.ofPrimary(text);
    }

    private final ResourceBundle resources;
    private final ReadOnlyObjectProperty<CustomerModel> selectedCustomer;
//    private final ReadOnlyObjectProperty<UserModel> selectedUser;
    private final StringBinding normalizedContact;
    private final BooleanProperty contactInvalid;
    private final ReadOnlyObjectProperty<AppointmentType> selectedType;
    private final StringProperty locationLabelText;
    private final BooleanProperty includeRemote;
    private final ObservableList<CorporateAddress> corporateLocationList;
    private final ObservableList<CorporateAddress> remoteLocationList;
    private final ComboBox<CorporateAddress> corporateLocationComboBox;
    private final ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
    private final TextArea locationTextArea;
    private final StringBinding normalizedLocation;
    private final TextField phoneTextField;
    private final StringBinding normalizedPhone;
    private final Label implicitLocationLabel;
    private final Label locationValidationLabel;
    private final StringProperty urlText;
    private final ObjectBinding<BinarySelective<String, String>> parsedUrl;
    private final StringProperty urlValidationMessage;
    private final BooleanProperty urlInvalid;
    private final ReadOnlyBooleanWrapper locationInvalid = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();

    public TypeContextController(EditAppointment editAppointmentControl) {
        resources = editAppointmentControl.getResources();
        selectedCustomer = editAppointmentControl.getAppointmentConflictsController().selectedCustomerProperty();
        StringProperty contactText = editAppointmentControl.getContactTextField().textProperty();
        AppointmentModel model = editAppointmentControl.getModel();
        contactText.set(model.getContact());
        normalizedContact = BindingHelper.asNonNullAndWsNormalized(contactText);
        contactInvalid = editAppointmentControl.getContactValidationLabel().visibleProperty();
        SingleSelectionModel<AppointmentType> typeSelectionModel = editAppointmentControl.getTypeComboBox().getSelectionModel();
        selectedType = typeSelectionModel.selectedItemProperty();
        typeSelectionModel.select(model.getType());
        locationLabelText = editAppointmentControl.getLocationLabel().textProperty();
        includeRemote = editAppointmentControl.getIncludeRemoteCheckBox().selectedProperty();
        corporateLocationList = FXCollections.observableArrayList();
        remoteLocationList = FXCollections.observableArrayList();
        PredefinedData.getCorporateAddressMap().values().forEach((t) -> {
            if (t.isSatelliteOffice()) {
                remoteLocationList.add(t);
            } else {
                corporateLocationList.add(t);
            }
        });
        corporateLocationComboBox = editAppointmentControl.getCorporateLocationComboBox();
        corporateLocationComboBox.setItems(corporateLocationList);
        SingleSelectionModel<CorporateAddress> corporateLocationSelectionModel = corporateLocationComboBox.getSelectionModel();
        selectedCorporateLocation = corporateLocationSelectionModel.selectedItemProperty();
        locationTextArea = editAppointmentControl.getLocationTextArea();
        StringProperty locationText = locationTextArea.textProperty();
        normalizedLocation = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationText);
        phoneTextField = editAppointmentControl.getPhoneTextField();
        StringProperty phoneText = phoneTextField.textProperty();
        normalizedPhone = BindingHelper.asNonNullAndWsNormalizedMultiLine(phoneText);
        implicitLocationLabel = editAppointmentControl.getImplicitLocationLabel();
        locationValidationLabel = editAppointmentControl.getLocationValidationLabel();
        TextField urlTextField = editAppointmentControl.getUrlTextField();
        urlTextField.setText(model.getUrl());
        urlText = urlTextField.textProperty();
        parsedUrl = Bindings.createObjectBinding(() -> calculateURL(selectedType.get(), urlText.get()), selectedType, urlText);
        Label label = editAppointmentControl.getUrlValidationLabel();
        urlValidationMessage = label.textProperty();
        urlInvalid = label.visibleProperty();
        String location = model.getLocation();
        switch (selectedType.get()) {
            case CORPORATE_LOCATION:
                restoreNode(corporateLocationComboBox);
                collapseNode(locationTextArea);
                CorporateAddress cl = corporateLocationList.stream().filter((t) -> t.getName().equals(location)).findFirst().orElseGet(() -> {
                    CorporateAddress r = remoteLocationList.stream().filter((u) -> u.getName().equals(location)).findFirst().orElse(null);
                    if (null != r) {
                        includeRemote.set(true);
                        remoteLocationList.forEach((u) -> corporateLocationList.add(u));
                    }
                    return r;
                });
                locationInvalid.set(null == cl);
                if (locationInvalid.get()) {
                    restoreLabeled(implicitLocationLabel, "(corporate location)");
                } else {
                    restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                    corporateLocationSelectionModel.select(cl);
                    collapseNode(locationValidationLabel);
                }
                break;
            case CUSTOMER_SITE:
                locationInvalid.set(false);
                collapseNode(locationValidationLabel);
                collapseNode(locationTextArea);
                CustomerModel cm = selectedCustomer.get();
                if (null == cm) {
                    restoreLabeled(implicitLocationLabel, "(customer site)");
                } else {
                    restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
                }
                break;
            case PHONE:
                collapseNode(locationTextArea);
                restoreNode(phoneTextField);
                locationLabelText.set(resources.getString(RESOURCEKEY_PHONENUMBER));
                phoneTextField.setText(location);
                locationInvalid.set(normalizedPhone.get().isEmpty());
                if (!locationInvalid.get()) {
                    collapseNode(locationValidationLabel);
                }
                break;
            case VIRTUAL:
                locationInvalid.set(false);
                collapseNode(locationValidationLabel);
                break;
            default:
                locationTextArea.setText(location);
                locationInvalid.set(normalizedLocation.get().isEmpty());
                if (!locationInvalid.get()) {
                    collapseNode(locationValidationLabel);
                }
                contactInvalid.set(normalizedContact.get().isEmpty());
                break;
        }
        parsedUrl.get().accept((t) -> {
            urlValidationMessage.set("");
            urlInvalid.set(false);
        }, (t) -> {
            urlValidationMessage.set(t);
            urlInvalid.set(true);
        });

        selectedCustomer.addListener((observable, oldValue, newValue) -> {
            onContextSensitiveChange();
            setValid(newValue, contactInvalid.get(), locationInvalid.get(), urlInvalid.get());
        });
        contactText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
        selectedCorporateLocation.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
        locationText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
        phoneText.addListener((observable, oldValue, newValue) -> onContextSensitiveChange());
        urlText.addListener((observable, oldValue, newValue) -> onUrlChanged());
        contactInvalid.addListener((observable, oldValue, newValue) -> {
            setValid(selectedCustomer.get(), newValue, locationInvalid.get(), urlInvalid.get());
        });
        locationInvalid.addListener((observable, oldValue, newValue) -> {
            setValid(selectedCustomer.get(), contactInvalid.get(), newValue, urlInvalid.get());
        });
        urlInvalid.addListener((observable, oldValue, newValue) -> {
            setValid(selectedCustomer.get(), contactInvalid.get(), locationInvalid.get(), newValue);
        });
        selectedType.addListener(this::onTypeChanged);

        includeRemote.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                remoteLocationList.forEach((t) -> {
                    if (!corporateLocationList.contains(t)) {
                        corporateLocationList.add(t);
                    }
                });
            } else {
                if (remoteLocationList.contains(corporateLocationComboBox.getValue())) {
                    corporateLocationComboBox.getSelectionModel().clearSelection();
                }
                remoteLocationList.forEach((t) -> corporateLocationList.remove(t));
            }
        });
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

    public String getNormalizedContact() {
        return normalizedContact.get();
    }

    public StringBinding normalizedContactStringBinding() {
        return normalizedContact;
    }

    public String getNormalizedLocation() {
        return normalizedLocation.get();
    }

    public StringBinding normalizedLocationStringBinding() {
        return normalizedLocation;
    }

    public String getNormalizedPhone() {
        return normalizedLocation.get();
    }

    public StringBinding normalizedPhoneStringBinding() {
        return normalizedPhone;
    }

    public BinarySelective<String, String> getParsedUrl() {
        return parsedUrl.get();
    }

    public ObjectBinding<BinarySelective<String, String>> parsedUrlStringBinding() {
        return parsedUrl;
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        if (oldValue == newValue) {
            return;
        }
        switch (oldValue) {
            case CORPORATE_LOCATION:
                if (newValue == AppointmentType.CUSTOMER_SITE) {
                    onContextSensitiveChange();
                    return;
                }
                collapseNode(corporateLocationComboBox);
                collapseNode(implicitLocationLabel);
                break;
            case CUSTOMER_SITE:
                if (newValue == AppointmentType.CORPORATE_LOCATION) {
                    onContextSensitiveChange();
                    return;
                }
                collapseNode(implicitLocationLabel);
                break;
            case PHONE:
                collapseNode(phoneTextField);
                locationLabelText.set(resources.getString(RESOURCEKEY_LOCATIONLABELTEXT));
                break;
            case VIRTUAL:
                onUrlChanged();
                if (newValue == AppointmentType.OTHER) {
                    onContextSensitiveChange();
                    return;
                }
                collapseNode(locationTextArea);
                break;
            default:
                if (newValue == AppointmentType.VIRTUAL) {
                    onContextSensitiveChange();
                    return;
                }
                collapseNode(locationTextArea);
                break;
        }
        switch (newValue) {
            case CORPORATE_LOCATION:
                contactInvalid.set(false);
                restoreNode(corporateLocationComboBox);
                break;
            case CUSTOMER_SITE:
                contactInvalid.set(false);
                locationInvalid.set(false);
                break;
            case PHONE:
                contactInvalid.set(false);
                locationLabelText.set(resources.getString(RESOURCEKEY_PHONENUMBER));
                restoreNode(phoneTextField);
                break;
            case VIRTUAL:
                locationInvalid.set(false);
                contactInvalid.set(false);
                restoreNode(locationTextArea);
                onUrlChanged();
                break;
            default:
                restoreNode(locationTextArea);
                break;
        }
        onContextSensitiveChange();
    }

    private void onUrlChanged() {
        parsedUrl.get().accept((t) -> {
            urlValidationMessage.set("");
            urlInvalid.set(false);
        }, (t) -> {
            urlValidationMessage.set(t);
            urlInvalid.set(true);
        });
    }

    private void onContextSensitiveChange() {
        boolean wasInvalid = locationInvalid.get();
        switch (selectedType.get()) {
            case CORPORATE_LOCATION:
                CorporateAddress cl = selectedCorporateLocation.get();
                locationInvalid.set(null == cl);
                if (locationInvalid.get()) {
                    restoreLabeled(implicitLocationLabel, "(corporate location)");
                } else {
                    restoreLabeled(implicitLocationLabel, cl.toMultiLineAddress());
                }
                break;
            case CUSTOMER_SITE:
                CustomerModel cm = selectedCustomer.get();
                if (null == cm) {
                    restoreLabeled(implicitLocationLabel, "(customer site)");
                } else {
                    restoreLabeled(implicitLocationLabel, cm.getMultiLineAddress());
                }
                break;
            case PHONE:
                locationLabelText.set(resources.getString(RESOURCEKEY_PHONENUMBER));
                locationInvalid.set(normalizedPhone.get().isEmpty());
                break;
            case VIRTUAL:
                break;
            default:
                locationInvalid.set(normalizedLocation.get().isEmpty());
                contactInvalid.set(normalizedContact.get().isEmpty());
                break;
        }
        if (wasInvalid != locationInvalid.get()) {
            if (wasInvalid) {
                collapseNode(locationValidationLabel);
            } else {
                restoreNode(locationValidationLabel);
            }
        }
    }

    private void setValid(CustomerModel customer, boolean c, boolean l, boolean u) {
        boolean v = null != customer && !(c || l || u);
        if (v != valid.get()) {
            valid.set(v);
        }
    }

}
