package scheduler.fx;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.model.fx.AppointmentModel;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCell extends ListCell<AppointmentModel> {

    private final ResourceBundle resources;
    private final DateTimeFormatter formatter;
    private final HBox graphic;
    private final Text startDateTimeText;
    private final Text endDateTimeText;
    private final Text customerNameText;
    private final Text appointmentTitleText;
    private final Label locationHeadingLabel;
    private final Label locationValueLabel;
    private final Hyperlink urlHyperlink;
    private final Button openButton;
    private final Button dismissButton;
    private boolean extentsBound = false;

    public AppointmentListCell(ResourceBundle resources, DateTimeFormatter formatter) {
        this.resources = Objects.requireNonNull(resources);
        this.formatter = Objects.requireNonNull(formatter);
        graphic = new HBox();
        graphic.setMaxWidth(Double.MAX_VALUE);
        ObservableList<Node> children;
        VBox vBox = new VBox();
        HBox.setHgrow(vBox, javafx.scene.layout.Priority.ALWAYS);
        vBox.setMaxWidth(Double.MAX_VALUE);
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add("small-control");
        children = textFlow.getChildren();
        startDateTimeText = new Text();
        startDateTimeText.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        startDateTimeText.setStrokeWidth(0.0);
        children.add(startDateTimeText);
        Text text = new Text();
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText(" to ");
        children.add(text);
        endDateTimeText = new Text();
        endDateTimeText.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        endDateTimeText.setStrokeWidth(0.0);
        children.add(endDateTimeText);
        text = new Text();
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText(" with ");
        children.add(text);
        customerNameText = new Text();
        customerNameText.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        customerNameText.setStrokeWidth(0.0);
        children.add(customerNameText);
        text = new Text();
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText(": ");
        children.add(text);
        appointmentTitleText = new Text();
        appointmentTitleText.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        appointmentTitleText.setStrokeWidth(0.0);
        children.add(appointmentTitleText);
        HBox hBox = new HBox();
        children = hBox.getChildren();
        locationHeadingLabel = new Label();
        locationHeadingLabel.getStyleClass().add("boldText");
        children.add(locationHeadingLabel);
        locationValueLabel = new Label();
        locationValueLabel.getStyleClass().add("small-control");
        locationValueLabel.setWrapText(true);
        children.add(locationValueLabel);
        urlHyperlink = new Hyperlink();
        urlHyperlink.setContentDisplay(javafx.scene.control.ContentDisplay.TEXT_ONLY);
        urlHyperlink.getStyleClass().add("small-control");
        children.add(urlHyperlink);
        children = vBox.getChildren();
        children.add(textFlow);
        children.add(hBox);
        children = graphic.getChildren();
        children.add(vBox);
        openButton = new Button();
        HBox.setHgrow(openButton, javafx.scene.layout.Priority.NEVER);
        openButton.setMnemonicParsing(false);
        openButton.getStyleClass().add("symbol-button");
        openButton.setText("Open");
        children.add(openButton);
        dismissButton = new Button();
        HBox.setHgrow(dismissButton, javafx.scene.layout.Priority.NEVER);
        dismissButton.setMnemonicParsing(false);
        dismissButton.getStyleClass().add("symbol-button");
        dismissButton.setText("Dismiss");
        children.add(dismissButton);
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
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(graphic);
            if (!extentsBound) {
                extentsBound = true;
                graphic.prefWidthProperty().bind(widthProperty());
                graphic.prefHeightProperty().bind(heightProperty());
            }
            startDateTimeText.setText(formatter.format(item.getStart()));
            endDateTimeText.setText(formatter.format(item.getEnd()));
            customerNameText.setText(item.getCustomerName());
            appointmentTitleText.setText(item.getTitle());

            switch (item.getType()) {
                case PHONE:
                    locationHeadingLabel.setText("Phone: ");
                    String location = item.getLocation();
                    try {
                        URI uri = new URI("tel:" + location);
                        restoreNode(urlHyperlink);
                        collapseNode(locationValueLabel);
                        urlHyperlink.setText(uri.toString());
                    } catch (URISyntaxException ex) {
                        restoreNode(locationValueLabel);
                        collapseNode(urlHyperlink);
                        locationValueLabel.setText(item.getEffectiveLocation());
                    }
                    break;
                case VIRTUAL:
                    locationHeadingLabel.setText("URL: ");
                    restoreNode(urlHyperlink);
                    collapseNode(locationValueLabel);
                    urlHyperlink.setText(item.getUrl());
                    break;
                default:
                    locationHeadingLabel.setText("Location: ");
                    restoreNode(locationValueLabel);
                    collapseNode(urlHyperlink);
                    locationValueLabel.setText(item.getEffectiveLocation());
                    break;
            }
            if (getIndex() == 0) {
                graphic.setPadding(new Insets(0, 0, 0, 16));
            } else {
                graphic.setPadding(Insets.EMPTY);
            }
        }
    }

}
