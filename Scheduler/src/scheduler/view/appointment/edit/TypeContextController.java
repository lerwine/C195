package scheduler.view.appointment.edit;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static scheduler.model.Appointment.MAX_LENGTH_URL;
import scheduler.model.AppointmentType;
import scheduler.model.CorporateAddress;
import scheduler.model.ui.CustomerModel;
import scheduler.observables.BindingHelper;
import scheduler.util.BinarySelective;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;

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
                return Optional.of(corporateAddress.toString());
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

    private static Optional<String> calculateContact(AppointmentType type, String contact) {
        if (contact.isEmpty() && type == AppointmentType.OTHER) {
            return Optional.empty();
        }
        return Optional.of(contact);
    }

    private static BinarySelective<String, String> calculateURL(AppointmentType type, String text) {
        if (text.isEmpty()) {
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

    private final ReadOnlyObjectProperty<AppointmentType> selectedType;
    private final TextArea locationTextArea;
    private final TextField phoneTextField;
    private final Label effectiveLocationLabel;
    private final ComboBox<CorporateAddress> corporateLocationComboBox;
    private final ReadOnlyObjectProperty<CorporateAddress> selectedCorporateLocation;
    private final ReadOnlyStringWrapper effectiveLocation;
    private final ReadOnlyBooleanWrapper locationInvalid;
    private final ReadOnlyBooleanWrapper customerInvalid;
    private final ReadOnlyStringWrapper url;
    private final ReadOnlyStringWrapper urlValidationMessage;
    private final ReadOnlyBooleanWrapper contactInvalid;
    private final ReadOnlyBooleanWrapper valid;
    private final StringBinding normalizedPhoneBinding;
    private final StringBinding normalizedLocationBinding;
    private final StringBinding normalizedContactBinding;
    private final StringBinding normalizedUrlBinding;
    private final ObjectBinding<Optional<String>> effectiveLocationBinding;
    private final ObjectBinding<Optional<String>> contactBinding;
    private final ObjectBinding<BinarySelective<String, String>> urlBinding;

    public TypeContextController(ReadOnlyObjectProperty<AppointmentType> selectedType, TextArea locationTextArea, TextField phoneTextField,
            Label effectiveLocationLabel, StringProperty contactProperty, StringProperty urlProperty, ComboBox<CorporateAddress> corporateLocationComboBox,
            ReadOnlyObjectProperty<CustomerModel> selectedCustomer) {
        this.selectedType = selectedType;
        this.locationTextArea = locationTextArea;
        this.phoneTextField = phoneTextField;
        this.effectiveLocationLabel = effectiveLocationLabel;
        this.corporateLocationComboBox = corporateLocationComboBox;
        selectedCorporateLocation = corporateLocationComboBox.getSelectionModel().selectedItemProperty();

        normalizedPhoneBinding = BindingHelper.asNonNullAndWsNormalized(phoneTextField.textProperty());
        normalizedLocationBinding = BindingHelper.asNonNullAndWsNormalizedMultiLine(locationTextArea.textProperty());
        effectiveLocationBinding = Bindings.createObjectBinding(() -> calculateEffectiveLocation(selectedType.get(), normalizedLocationBinding.get(),
                normalizedPhoneBinding.get(), selectedCustomer.get(), selectedCorporateLocation.get()), selectedType, normalizedLocationBinding,
                normalizedPhoneBinding, selectedCustomer, selectedCorporateLocation);
        effectiveLocation = new ReadOnlyStringWrapper(effectiveLocationBinding.get().orElse(""));
        locationInvalid = new ReadOnlyBooleanWrapper(!effectiveLocationBinding.get().isPresent());
        customerInvalid = new ReadOnlyBooleanWrapper(null == selectedCustomer.get());

        normalizedContactBinding = BindingHelper.asNonNullAndWsNormalized(contactProperty);
        contactBinding = Bindings.createObjectBinding(() -> calculateContact(selectedType.get(), normalizedContactBinding.get()), selectedType,
                normalizedContactBinding);
        contactInvalid = new ReadOnlyBooleanWrapper(!contactBinding.get().isPresent());
        normalizedUrlBinding = BindingHelper.asTrimmedAndNotNull(urlProperty);
        urlBinding = Bindings.createObjectBinding(() -> calculateURL(selectedType.get(), normalizedUrlBinding.get()), selectedType, normalizedUrlBinding);
        url = new ReadOnlyStringWrapper(urlBinding.get().toPrimary(""));
        urlValidationMessage = new ReadOnlyStringWrapper(urlBinding.get().toSecondary(""));
        valid = new ReadOnlyBooleanWrapper(!(locationInvalid.get() || customerInvalid.get() || contactInvalid.get() || urlValidationMessage.get().isEmpty()));

        selectedType.addListener(this::onTypeChanged);
        locationTextArea.textProperty().addListener((observable, oldValue, newValue) -> onEffectiveLocationComponentChanged());
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> onEffectiveLocationComponentChanged());
        selectedCustomer.addListener((observable, oldValue, newValue) -> {
            customerInvalid.set(null == selectedCustomer.get());
            onEffectiveLocationComponentChanged();
        });
        urlProperty.addListener((observable, oldValue, newValue) -> {
            BinarySelective<String, String> u = urlBinding.get();
            url.set(u.toPrimary(""));
            urlValidationMessage.set(u.toSecondary(""));
        });
        contactProperty.addListener((observable, oldValue, newValue) -> contactInvalid.set(!contactBinding.get().isPresent()));
        selectedCorporateLocation.addListener((observable, oldValue, newValue) -> onEffectiveLocationComponentChanged());
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

    public String getEffectiveLocation() {
        return effectiveLocation.get();
    }

    public ReadOnlyStringProperty effectiveLocationProperty() {
        return effectiveLocation.getReadOnlyProperty();
    }

    public boolean isLocationInvalid() {
        return locationInvalid.get();
    }

    public ReadOnlyBooleanProperty locationInvalidProperty() {
        return locationInvalid.getReadOnlyProperty();
    }

    public boolean isCustomerInvalid() {
        return customerInvalid.get();
    }

    public ReadOnlyBooleanProperty customerInvalidProperty() {
        return customerInvalid.getReadOnlyProperty();
    }

    public String getUrl() {
        return url.get();
    }

    public ReadOnlyStringProperty urlProperty() {
        return url.getReadOnlyProperty();
    }

    public String getUrlValidationMessage() {
        return urlValidationMessage.get();
    }

    public ReadOnlyStringProperty urlValidationMessageProperty() {
        return urlValidationMessage.getReadOnlyProperty();
    }

    public String getNormalizedContact() {
        return normalizedContactBinding.get();
    }

    public StringBinding getNormalizedContactBinding() {
        return normalizedContactBinding;
    }

    public boolean isContactInvalid() {
        return contactInvalid.get();
    }

    public ReadOnlyBooleanProperty contactInvalidProperty() {
        return contactInvalid.getReadOnlyProperty();
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private void onEffectiveLocationComponentChanged() {
        String value = effectiveLocationBinding.get().orElse("");
        effectiveLocation.set(value);
        locationInvalid.set(!effectiveLocationBinding.get().isPresent());
        if (selectedType.get() == AppointmentType.CUSTOMER_SITE) {
            effectiveLocationLabel.setText(value);
        } else {
            effectiveLocationLabel.setText("");
        }
    }

    private void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        if (null != oldValue) {
            if (oldValue == newValue) {
                return;
            }
            switch (oldValue) {
                case CORPORATE_LOCATION:
                    collapseNode(effectiveLocationLabel);
                    break;
                case CUSTOMER_SITE:
                    collapseNode(effectiveLocationLabel);
                    break;
                case PHONE:
                    collapseNode(phoneTextField);
                    break;
                case VIRTUAL:
                    if (newValue == AppointmentType.OTHER) {
                        return;
                    }
                    collapseNode(locationTextArea);
                    break;
                default:
                    if (newValue == AppointmentType.VIRTUAL) {
                        return;
                    }
                    collapseNode(locationTextArea);
                    break;
            }
            switch (newValue) {
                case CORPORATE_LOCATION:
                    restoreNode(corporateLocationComboBox);
                    break;
                case CUSTOMER_SITE:
                    restoreNode(effectiveLocationLabel);
                    break;
                case PHONE:
                    restoreNode(phoneTextField);
                    break;
                default:
                    restoreNode(locationTextArea);
                    break;
            }
        } else {
            switch (newValue) {
                case CORPORATE_LOCATION:
                    collapseNode(locationTextArea);
                    restoreNode(corporateLocationComboBox);
                    break;
                case CUSTOMER_SITE:
                    collapseNode(locationTextArea);
                    restoreNode(effectiveLocationLabel);
                    break;
                case PHONE:
                    collapseNode(locationTextArea);
                    restoreNode(phoneTextField);
                    break;
            }
        }
        onEffectiveLocationComponentChanged();
    }

}
