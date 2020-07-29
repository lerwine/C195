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
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.model.AppointmentType;
import scheduler.util.NodeUtil;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.Values;
import scheduler.util.WeakChangeHandlingReference;
import scheduler.view.SymbolText;
import scheduler.view.appointment.CalendarCellData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CalendarListCell extends ListCell<CalendarCellData> {

    private static final float SINGLE_LINE_HEIGHT;

    static {
        Text text = new Text();
        Font font = text.getFont();
        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        SINGLE_LINE_HEIGHT = fm.getLineHeight();
    }

    private final DateTimeFormatter formatter;
    private final BooleanProperty singleLine;
    private final HBox graphic;
    private final TextFlow textFlow;
    private final Text followsText;
    private final Text startDateTimeText;
    private final Text endDateTimeText;
    private final Text withText;
    private final Text customerNameText;
    private final Text appointmentTitleText;
    private final Text precedesText;
    private final HBox secondLineHBox;
//    private final HBox locationHBox;
    private final Label locationHeadingLabel;
    private final Label locationValueLabel;
    private final Hyperlink urlHyperlink;
    private final Button openButton;
    private final Button dismissButton;
    private final ChangeListener<Boolean> singleLineChangeListener;
    private final WeakChangeHandlingReference<Boolean> continuedFromPreviousChangeListener;
    private final WeakChangeHandlingReference<LocalDateTime> startChangeListener;
    private final WeakChangeHandlingReference<LocalDateTime> endChangeListener;
    private final WeakChangeHandlingReference<String> titleChangeListener;
    private final WeakChangeHandlingReference<String> customerNameChangeListener;
    private final WeakChangeHandlingReference<String> urlChangeListener;
    private final WeakChangeHandlingReference<String> locationChangeListener;
    private final WeakChangeHandlingReference<String> effectiveLocationChangeListener;
    private final WeakChangeHandlingReference<Boolean> continuedOnNextChangeListener;
    private boolean extentsBound = false;
    private CalendarCellData boundModel;
    private AppointmentType currentType = AppointmentType.OTHER;
    private String currentTitle = "";
    private String currentUrl = "";
    private String currentLocation = "";
    private String currentEffectiveLocation = "";
    private boolean continuedFromPrevious = false;
    private boolean continuedOnNext = false;
    private boolean singleLineMode = false;

    public CalendarListCell(DateTimeFormatter formatter, BooleanProperty singleLine) {
        this.formatter = Objects.requireNonNull(formatter);
        this.singleLine = Objects.requireNonNull(singleLine);

        followsText = NodeUtil.createText(SymbolText.LEFT_DOUBLE_ARROW.toString() + " ", CssClassName.SMALL_CONTROL, CssClassName.SYMBOL);
        startDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        endDateTimeText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        customerNameText = NodeUtil.createText(CssClassName.BOLD_TEXT);
        appointmentTitleText = NodeUtil.createText();
        precedesText = NodeUtil.createText(SymbolText.RIGHT_DOUBLE_ARROW.toString() + " ", CssClassName.SMALL_CONTROL, CssClassName.SYMBOL);
        withText = NodeUtil.createText(" with ");
        textFlow = NodeUtil.createTextFlow(CssClassName.SMALL_CONTROL,
                followsText, startDateTimeText, NodeUtil.createText(" to "), endDateTimeText, withText, customerNameText,
                NodeUtil.createText(": "), appointmentTitleText, precedesText);
        locationHeadingLabel = NodeUtil.createLabel(CssClassName.BOLD_TEXT);
        locationValueLabel = NodeUtil.createLabel(CssClassName.SMALL_CONTROL);
        urlHyperlink = NodeUtil.createHyperlink(CssClassName.SMALL_CONTROL);
        secondLineHBox = NodeUtil.createFillingHBox(locationHeadingLabel, locationValueLabel, urlHyperlink);
        openButton = NodeUtil.createSymbolButton(SymbolText.EDIT, this::onOpenButtonAction);
        dismissButton = NodeUtil.createSymbolButton(SymbolText.DISMISS, this::onDismissButtonAction);

        graphic = NodeUtil.createFillingHBox(
                NodeUtil.setHBoxGrow(NodeUtil.createFillingVBox(textFlow, secondLineHBox), Priority.ALWAYS),
                NodeUtil.setHBoxGrow(openButton, Priority.NEVER),
                NodeUtil.setHBoxGrow(dismissButton, Priority.NEVER)
        );

        singleLineChangeListener = this::onSingleLineChanged;
        singleLine.addListener(new WeakChangeListener<>(singleLineChangeListener));
        continuedFromPreviousChangeListener = WeakChangeHandlingReference.<Boolean>of(this::onContinuedFromPreviousChanged);
        startChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onStartChanged);
        endChangeListener = WeakChangeHandlingReference.<LocalDateTime>of(this::onEndChanged);
        titleChangeListener = WeakChangeHandlingReference.<String>of(this::onTitleChanged);
        customerNameChangeListener = WeakChangeHandlingReference.<String>of(this::onCustomerNameChanged);
        urlChangeListener = WeakChangeHandlingReference.<String>of(this::onUrlChanged);
        locationChangeListener = WeakChangeHandlingReference.<String>of(this::onLocationChanged);
        effectiveLocationChangeListener = WeakChangeHandlingReference.<String>of(this::onEffectiveLocationChanged);
        continuedOnNextChangeListener = WeakChangeHandlingReference.<Boolean>of(this::onContinuedOnNextChanged);
    }

    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);
        Node g = getGraphic();
        if (null != g && g == graphic) {
            if (i == 0) {
                graphic.setPadding(new Insets(0, 0, 0, 16));
            } else {
                graphic.setPadding(Insets.EMPTY);
            }
        }
    }

    @Override
    protected synchronized void updateItem(CalendarCellData item, boolean empty) {
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
                boundModel.continuedFromPreviousProperty().removeListener(continuedFromPreviousChangeListener.getWeakListener());
                boundModel.startProperty().removeListener(startChangeListener.getWeakListener());
                boundModel.endProperty().removeListener(endChangeListener.getWeakListener());
                boundModel.titleProperty().removeListener(titleChangeListener.getWeakListener());
                boundModel.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
                boundModel.urlProperty().removeListener(urlChangeListener.getWeakListener());
                boundModel.locationProperty().removeListener(locationChangeListener.getWeakListener());
                boundModel.effectiveLocationProperty().removeListener(effectiveLocationChangeListener.getWeakListener());
                boundModel.continuedOnNextProperty().removeListener(continuedOnNextChangeListener.getWeakListener());
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
            if (null != boundModel) {
                if (boundModel == item) {
                    if (getIndex() == 0) {
                        graphic.setPadding(new Insets(0, 0, 0, 16));
                    } else {
                        graphic.setPadding(Insets.EMPTY);
                    }
                    return;
                }
                boundModel.continuedFromPreviousProperty().removeListener(continuedFromPreviousChangeListener.getWeakListener());
                boundModel.startProperty().removeListener(startChangeListener.getWeakListener());
                boundModel.endProperty().removeListener(endChangeListener.getWeakListener());
                boundModel.titleProperty().removeListener(titleChangeListener.getWeakListener());
                boundModel.customerNameProperty().removeListener(customerNameChangeListener.getWeakListener());
                boundModel.urlProperty().removeListener(urlChangeListener.getWeakListener());
                boundModel.locationProperty().removeListener(locationChangeListener.getWeakListener());
                boundModel.effectiveLocationProperty().removeListener(effectiveLocationChangeListener.getWeakListener());
                boundModel.continuedOnNextProperty().removeListener(continuedOnNextChangeListener.getWeakListener());
            }
            boundModel = item;
            onStartChanged(item.startProperty(), null, item.getStart());
            onEndChanged(item.endProperty(), null, item.getEnd());
            onTitleChanged(item.titleProperty(), "", item.getTitle());
            onCustomerNameChanged(item.customerNameProperty(), "", item.getCustomerName());
            onContinuedFromChanged(singleLine.get());
            updateSecondLine();
            boundModel.continuedFromPreviousProperty().addListener(continuedFromPreviousChangeListener.getWeakListener());
            boundModel.startProperty().addListener(startChangeListener.getWeakListener());
            boundModel.endProperty().addListener(endChangeListener.getWeakListener());
            boundModel.titleProperty().addListener(titleChangeListener.getWeakListener());
            boundModel.customerNameProperty().addListener(customerNameChangeListener.getWeakListener());
            boundModel.urlProperty().addListener(urlChangeListener.getWeakListener());
            boundModel.locationProperty().addListener(locationChangeListener.getWeakListener());
            boundModel.effectiveLocationProperty().addListener(effectiveLocationChangeListener.getWeakListener());
            boundModel.continuedOnNextProperty().addListener(continuedOnNextChangeListener.getWeakListener());

            if (getIndex() == 0) {
                graphic.setPadding(new Insets(0, 0, 0, 16));
            } else {
                graphic.setPadding(Insets.EMPTY);
            }
        }
    }

    private void onOpenButtonAction(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.fx.AppointmentListCell#onOpenButtonAction
    }

    private void onDismissButtonAction(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.fx.AppointmentListCell#onOpenButtonAction
    }

    public void setSingleLineMode(boolean value) {
        if (singleLineMode == value) {
            return;
        }
        singleLineMode = value;
        if (singleLineMode) {
            restoreNode(secondLineHBox);
            textFlow.setPrefHeight(USE_COMPUTED_SIZE);
            textFlow.setMaxHeight(USE_COMPUTED_SIZE);
            appointmentTitleText.setText(currentTitle);
        } else {
            collapseNode(secondLineHBox);
            textFlow.setPrefHeight(SINGLE_LINE_HEIGHT);
            textFlow.setMaxHeight(SINGLE_LINE_HEIGHT);
            appointmentTitleText.setText(Values.asNonNullAndWsNormalized(currentTitle));
        }
    }

    public void setCurrentType(AppointmentType value) {
        if (currentType == value) {
            return;
        }
        this.currentType = value;
        if (singleLineMode) {
            return;
        }
        switch (currentType) {
            case VIRTUAL:
                locationHeadingLabel.setText("URL: ");
                restoreNode(urlHyperlink);
                collapseNode(locationValueLabel);
                urlHyperlink.setText(currentUrl);
                break;
            case PHONE:
                locationHeadingLabel.setText("Phone: ");
                applyPhoneText();
                break;
            default:
                locationHeadingLabel.setText("Location: ");
                restoreNode(locationValueLabel);
                collapseNode(urlHyperlink);
                locationValueLabel.setText(currentEffectiveLocation);
                break;
        }
    }

    private void applyPhoneText() {
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

    private void updateSecondLine() {
        switch (currentType) {
            case PHONE:
                applyPhoneText();
                break;
            case VIRTUAL:
                urlHyperlink.setText(currentUrl);
                break;
            default:
                locationValueLabel.setText(currentEffectiveLocation);
                break;
        }
    }

    private synchronized void onSingleLineChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        onContinuedFromChanged(newValue);
    }

    private void onContinuedFromChanged(boolean isSingleLine) {
        if (continuedFromPrevious) {
            collapseNode(withText);
            collapseNode(customerNameText);
            restoreNode(followsText);
            if (continuedOnNext) {
                restoreNode(precedesText);
            } else {
                collapseNode(precedesText);
            }
        } else {
            collapseNode(followsText);
            if (continuedOnNext) {
                collapseNode(withText);
                collapseNode(customerNameText);
                restoreNode(precedesText);
            } else {
                restoreNode(withText);
                restoreNode(customerNameText);
                collapseNode(precedesText);
                setSingleLineMode(singleLine.get());
                return;
            }
        }
        setSingleLineMode(true);
    }

    private synchronized void onContinuedFromPreviousChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (continuedFromPrevious != newValue) {
            continuedFromPrevious = newValue;
            onContinuedFromChanged(singleLine.get());
        }
    }

    private synchronized void onContinuedOnNextChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (continuedOnNext != newValue) {
            continuedOnNext = newValue;
            onContinuedFromChanged(singleLine.get());
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
        if (null == newValue) {
            if (currentTitle.isEmpty()) {
                return;
            }
            currentTitle = "";
        } else {
            if (currentTitle.equals(newValue)) {
                return;
            }
            currentTitle = newValue;
        }
        if (singleLineMode) {
            appointmentTitleText.setText(Values.asNonNullAndWsNormalized(currentTitle));
        } else {
            appointmentTitleText.setText(currentTitle);
        }
    }

    private synchronized void onTypeChanged(ObservableValue<? extends AppointmentType> observable, AppointmentType oldValue, AppointmentType newValue) {
        setCurrentType((null == newValue) ? AppointmentType.OTHER : newValue);
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
            applyPhoneText();
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
