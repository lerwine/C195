package scheduler.dao;

import scheduler.model.AppointmentType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentCountByType extends ItemCountResult<AppointmentType> {

    private final String displayText;

    public AppointmentCountByType(AppointmentType value, int count) {
        super(value, count);
        displayText = AppointmentType.toDisplayText(value);
    }

    public String getDisplayText() {
        return displayText;
    }

}
