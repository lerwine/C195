package scheduler.fx;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.model.AppointmentType;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Values;
import scheduler.util.WeakChangeHandlingReference;
import scheduler.view.SymbolText;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCell extends ListCell<AppointmentModel> {

    private final DateTimeFormatter formatter;
    private final VBox graphic;
    private final Text startDateTimeText;
    private final Text endDateTimeText;
    private final Text customerNameText;
    private final Text appointmentTitleText;
    private final Label locationHeadingLabel;
    private final Label locationValueLabel;
    private final Hyperlink urlHyperlink;
    private final WeakChangeHandlingReference<LocalDateTime> startChangeListener;
    private final WeakChangeHandlingReference<LocalDateTime> endChangeListener;
    private final WeakChangeHandlingReference<String> titleChangeListener;
    private final WeakChangeHandlingReference<String> customerNameChangeListener;
    private final WeakChangeHandlingReference<String> urlChangeListener;
    private final WeakChangeHandlingReference<String> locationChangeListener;
    private final WeakChangeHandlingReference<String> effectiveLocationChangeListener;
    private boolean extentsBound = false;
    private AppointmentModel boundModel;
    private AppointmentType currentType = AppointmentType.OTHER;
    private String currentUrl = "";
    private String currentLocation = "";
    private String currentEffectiveLocation = "";

    public AppointmentListCell(DateTimeFormatter formatter) {
        this.formatter = Objects.requireNonNull(formatter);
        
        startDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        endDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        customerNameText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        appointmentTitleText = NodeUtil.createText();
        locationHeadingLabel = NodeUtil.createLabel(CssClassName.BOLD_TEXT);
        locationValueLabel = NodeUtil.createLabel(CssClassName.SMALL_CONTROL);
        urlHyperlink = NodeUtil.createHyperlink(CssClassName.SMALL_CONTROL);
        
        graphic = NodeUtil.createFillingVBox(
                NodeUtil.createTextFlow(CssClassName.SMALL_CONTROL,
                        startDateTimeText, NodeUtil.createText(" to "), endDateTimeText, NodeUtil.createText(" with "), customerNameText, NodeUtil.createText(": ")),
                NodeUtil.createFillingHBox(locationHeadingLabel, locationValueLabel, urlHyperlink)
        );
        
        startChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onStartChanged);
        endChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onEndChanged);
        titleChangeListener = WeakChangeHandlingReference.<String>of(this::onTitleChanged);
        customerNameChangeListener = WeakChangeHandlingReference.<String>of(this::onCustomerNameChanged);
        urlChangeListener = WeakChangeHandlingReference.<String>of(this::onUrlChanged);
        locationChangeListener = WeakChangeHandlingReference.<String>of(this::onLocationChanged);
        effectiveLocationChangeListener = WeakChangeHandlingReference.<String>of(this::onEffectiveLocationChanged);
    }

    @Override
    protected synchronized void updateItem(AppointmentModel item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            if (extentsBound) {
                extentsBound = false;
                graphic.prefWidthProperty().unbind();
                graphic.prefHeightProperty().unbind();
            }
            setGraphic(null);

            if (null != boundModel) {
                startDateTimeText.setText("");
                endDateTimeText.setText("");
                appointmentTitleText.setText("");
                currentLocation = currentEffectiveLocation = currentUrl = "";
                customerNameText.setText("");
                locationValueLabel.setText("");
                urlHyperlink.setText("");
                boundModel.startProperty().removeListener(startChangeListener.getWeakListener());
                boundModel.endProperty().removeListener(endChangeListener.getWeakListener());
                boundModel.titleProperty().removeListener(titleChangeListener.getWeakListener());
                boundModel.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
                boundModel.urlProperty().removeListener(urlChangeListener.getWeakListener());
                boundModel.locationProperty().removeListener(locationChangeListener.getWeakListener());
                boundModel.effectiveLocationProperty().removeListener(effectiveLocationChangeListener.getWeakListener());
                boundModel = null;
            }
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(graphic);
            if (!extentsBound) {
                extentsBound = true;
                graphic.prefWidthProperty().bind(widthProperty());
                graphic.prefHeightProperty().bind(heightProperty());
            }
            if (null == boundModel || boundModel != item) {
                boundModel = item;
                onStartChanged(item.startProperty(), null, item.getStart());
                onEndChanged(item.endProperty(), null, item.getEnd());
                onTitleChanged(item.titleProperty(), "", item.getTitle());
                onCustomerNameChanged(item.customerNameProperty(), "", item.getCustomerName());
                appointmentTitleText.setText(item.getTitle());
                applyType();
                boundModel.startProperty().addListener(startChangeListener.getWeakListener());
                boundModel.endProperty().addListener(endChangeListener.getWeakListener());
                boundModel.titleProperty().addListener(titleChangeListener.getWeakListener());
                boundModel.customerNameProperty().addListener(customerNameChangeListener.getWeakListener());
                boundModel.urlProperty().addListener(urlChangeListener.getWeakListener());
                boundModel.locationProperty().addListener(locationChangeListener.getWeakListener());
                boundModel.effectiveLocationProperty().addListener(effectiveLocationChangeListener.getWeakListener());
            }
        }
    }

    private void toOtherType() {
        locationHeadingLabel.setText("Location: ");
        restoreNode(locationValueLabel);
        collapseNode(urlHyperlink);
        locationValueLabel.setText(currentEffectiveLocation);
    }

    private void toVirtualType() {
        locationHeadingLabel.setText("URL: ");
        restoreNode(urlHyperlink);
        collapseNode(locationValueLabel);
        urlHyperlink.setText(currentUrl);
    }

    private void toPhoneType() {
        locationHeadingLabel.setText("Phone: ");
        onPhoneChanged();
    }

    private void onPhoneChanged() {
        try {
            restoreNode(urlHyperlink);
            collapseNode(locationValueLabel);
            URI uri = new URI("tel:" + currentLocation);
            urlHyperlink.setText(uri.toString());
        } catch (URISyntaxException ex) {
            restoreNode(locationValueLabel);
            collapseNode(urlHyperlink);
            locationValueLabel.setText(currentEffectiveLocation);
        }
    }

    private synchronized void onStartChanged(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
        startDateTimeText.setText((null == newValue) ? "(unspecified)" : formatter.format(newValue));
    }

    private synchronized void onEndChanged(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
        endDateTimeText.setText((null == newValue) ? "(unspecified)" : formatter.format(newValue));
    }

    private synchronized void onCustomerNameChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        customerNameText.setText((null == newValue) ? "" : newValue);
    }

    private synchronized void onTitleChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        appointmentTitleText.setText((null == newValue) ? "" : newValue);
    }

    private synchronized void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        if (null == newValue) {
            if (currentType == AppointmentType.OTHER) {
                return;
            }
            currentType = AppointmentType.OTHER;
        } else if (currentType != newValue) {
            currentType = newValue;
        } else {
            return;
        }
        applyType();
    }

    private void applyType() {
        switch (currentType) {
            case PHONE:
                toPhoneType();
                break;
            case VIRTUAL:
                toVirtualType();
                break;
            default:
                toOtherType();
                break;
        }
    }
    
    private synchronized void onUrlChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue) {
            if (currentUrl.isEmpty()) {
                return;
            }
            currentUrl = "";
        } else {
            if (currentUrl.equals(newValue)) {
                return;
            }
            currentUrl = newValue;
        }
        if (currentType == AppointmentType.VIRTUAL) {
            urlHyperlink.setText(currentUrl);
        }
    }

    private synchronized void onLocationChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue) {
            if (currentLocation.isEmpty()) {
                return;
            }
            currentLocation = "";
        } else {
            if (currentLocation.equals(newValue)) {
                return;
            }
            currentLocation = newValue;
        }
        if (currentType == AppointmentType.PHONE) {
            onPhoneChanged();
        }
    }

    private synchronized void onEffectiveLocationChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue) {
            if (currentEffectiveLocation.isEmpty()) {
                return;
            }
            currentEffectiveLocation = "";
        } else {
            if (currentEffectiveLocation.equals(newValue)) {
                return;
            }
            currentEffectiveLocation = newValue;
        }
        switch (currentType) {
            case PHONE:
            case VIRTUAL:
                break;
            default:
                locationValueLabel.setText(currentEffectiveLocation);
                break;
        }
    }

}
