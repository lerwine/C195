package scheduler.fx;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import scheduler.util.NodeUtil;
import scheduler.model.ui.AppointmentModel;
import static scheduler.view.appointment.ManageAppointmentsResourceKeys.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCell extends ListCell<AppointmentModel> {

    private final ResourceBundle resources;
    private final DateTimeFormatter formatter;
    private final TextFlow graphic;
    private final Text headerText;
    private final Text locationLabelText;
    private final Text locationValueText;

    public AppointmentListCell(ResourceBundle resources, DateTimeFormatter formatter) {
        this.resources = Objects.requireNonNull(resources);
        this.formatter = Objects.requireNonNull(formatter);
        graphic = new TextFlow();
        ObservableList<Node> children = graphic.getChildren();
        headerText = NodeUtil.setCssClass(new Text(), CssClassName.H1);
        children.add(headerText);
        locationLabelText = NodeUtil.setCssClass(new Text(), CssClassName.BOLD_TEXT);
        children.add(locationLabelText);
        locationValueText = new Text();
        children.add(locationValueText);
    }

    @Override
    protected void updateItem(AppointmentModel item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(graphic);
            headerText.setText(String.format(resources.getString(RESOURCEKEY_TIMESPANWITHCUSTOMER), formatter.format(item.getStart()),
                    formatter.format(item.getEnd()), item.getTitle()));
            switch (item.getType()) {
                case PHONE:
                    locationLabelText.setText(String.format("%n%s: ", resources.getString(RESOURCEKEY_PHONENUMBER)));
                    break;
                case VIRTUAL:
                    locationLabelText.setText(String.format("%n%s: ", resources.getString(RESOURCEKEY_MEETINGURL)));
                    break;
                default:
                    locationLabelText.setText(String.format("%n%s: ", resources.getString(RESOURCEKEY_ADDRESS)));
                    break;
            }
            locationValueText.setText(item.getEffectiveLocation());
        }
    }

}
