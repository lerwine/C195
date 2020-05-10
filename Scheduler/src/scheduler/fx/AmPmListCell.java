package scheduler.fx;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AmPmListCell extends ListCell<Boolean> {

    private final String amText;
    private final String pmText;

    public AmPmListCell() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("a");
        amText = LocalTime.of(0, 0).format(dtf);
        pmText = LocalTime.of(12, 0).format(dtf);
    }

    public AmPmListCell(String amText, String pmText) {
        this.amText = amText;
        this.pmText = pmText;
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null) {
            setText("");
        } else {
            setText((item) ? pmText : amText);
        }
    }
    /*
    <ComboBox fx:id="amPmComboBox" maxWidth="1.7976931348623157E308">
        <cellFactory>
            <AmPmListCellFactory />
        </cellFactory>
        <buttonCell>
            <AmPmListCell />
        </buttonCell>
    </ComboBox>
     */

}
