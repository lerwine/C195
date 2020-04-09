package scheduler.view.appointment;

import java.time.LocalDateTime;
import scheduler.dao.CityDAO;
import scheduler.dao.CountryDAO;
import scheduler.dao.CustomerDAO;
import scheduler.dao.filter.DateFilterType;
import scheduler.dao.filter.TextFilterType;
import scheduler.dao.UserDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class FilterOptionState implements Cloneable {

    public static LocalDateTime asNormalized(LocalDateTime value) {
        if (null == value) {
            return null;
        }
        int f = value.getMinute() % 12;
        if (f != 0 || value.getNano() != 0 || value.getSecond() != 0) {
            return value.toLocalDate().atTime(value.getHour(), f * 5, 0, 0);
        }
        return value;
    }

    private LocalDateTime startDateTime = null;
    private DateFilterType startOption = DateFilterType.NONE;
    private LocalDateTime endDateTime = null;
    private DateFilterType endOption = DateFilterType.NONE;
    private CustomerDAO customer = null;
    private CityDAO city = null;
    private CountryDAO country = null;
    private UserDAO user = null;
    private String titleText = "";
    private TextFilterType titleOption = TextFilterType.NONE;
    private String locationText = "";
    private TextFilterType locationOption = TextFilterType.NONE;

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public synchronized void setStartDateTime(LocalDateTime start) {
        this.startDateTime = start;
    }

    public DateFilterType getStartOption() {
        return startOption;
    }

    public synchronized void setStartOption(DateFilterType startOption) {
        this.startOption = startOption;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public synchronized void setEndDateTime(LocalDateTime end) {
        this.endDateTime = end;
    }

    public DateFilterType getEndOption() {
        return endOption;
    }

    public synchronized void setEndOption(DateFilterType endOption) {
        this.endOption = endOption;
    }

    public CustomerDAO getCustomer() {
        return customer;
    }

    public synchronized void setCustomer(CustomerDAO customer) {
        this.customer = customer;
    }

    public CityDAO getCity() {
        return city;
    }

    public synchronized void setCity(CityDAO city) {
        this.city = city;
    }

    public CountryDAO getCountry() {
        return country;
    }

    public synchronized void setCountry(CountryDAO country) {
        this.country = country;
    }

    public UserDAO getUser() {
        return user;
    }

    public synchronized void setUser(UserDAO user) {
        this.user = user;
    }

    public String getTitleText() {
        return titleText;
    }

    public synchronized void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public TextFilterType getTitleOption() {
        return titleOption;
    }

    public synchronized void setTitleOption(TextFilterType titleOption) {
        this.titleOption = titleOption;
    }

    public String getLocationText() {
        return locationText;
    }

    public synchronized void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public TextFilterType getLocationOption() {
        return locationOption;
    }

    public synchronized void setLocationOption(TextFilterType locationOption) {
        this.locationOption = locationOption;
    }

    public FilterOptionState asNormalized() {
        return asNormalized(false);
    }

    public synchronized FilterOptionState asNormalized(boolean forceClone) {
        FilterOptionState copied = new FilterOptionState();
        copied.startDateTime = startDateTime;
        copied.startOption = startOption;
        copied.endDateTime = endDateTime;
        copied.endOption = endOption;
        copied.customer = customer;
        copied.user = user;
        copied.titleText = titleText;
        copied.titleOption = titleOption;
        copied.locationText = locationText;
        copied.locationOption = locationOption;
        FilterOptionState result = (forceClone) ? copied : this;
        if (null == copied.startDateTime) {
            if (null == copied.startOption || !copied.startOption.equals(DateFilterType.NONE)) {
                (result = copied).startOption = DateFilterType.NONE;
            }
        } else if (copied.startOption.equals(DateFilterType.NONE)) {
            (result = copied).startDateTime = null;
        }
        if (null == copied.endDateTime) {
            if (null == copied.endOption || !copied.endOption.equals(DateFilterType.NONE)) {
                (result = copied).endOption = DateFilterType.NONE;
            }
        } else if (copied.endOption.equals(DateFilterType.NONE)) {
            (result = copied).endDateTime = null;
        }
        if (copied.startOption.equals(DateFilterType.ON)) {
            if (null != copied.endDateTime) {
                (result = copied).startOption = DateFilterType.INCLUSIVE;
                if (copied.endOption.equals(DateFilterType.ON)) {
                    copied.endOption = DateFilterType.INCLUSIVE;
                }
            }
        } else if (null != copied.endDateTime && copied.endOption.equals(DateFilterType.ON)) {
            if (null == copied.startDateTime) {
                (result = copied).startOption = DateFilterType.ON;
                copied.startDateTime = copied.endDateTime;
                copied.endDateTime = null;
                copied.endOption = DateFilterType.NONE;
            } else {
                (result = copied).endOption = DateFilterType.INCLUSIVE;
            }
        }
        if (null == copied.titleText || null == copied.titleOption || copied.titleText.isEmpty() != copied.titleOption.equals(TextFilterType.NONE)) {
            (result = copied).titleText = "";
            copied.titleOption = TextFilterType.NONE;
        }
        if (null == copied.locationText || null == copied.locationOption || copied.locationText.isEmpty() != copied.locationOption.equals(TextFilterType.NONE)) {
            (result = copied).locationText = "";
            copied.locationOption = TextFilterType.NONE;
        }

        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FilterOptionState result = (FilterOptionState) super.clone();
        result.startDateTime = startDateTime;
        if (null == (result.startOption = startOption)) {
            result.startOption = DateFilterType.NONE;
        }
        result.endDateTime = endDateTime;
        if (null == (result.endOption = endOption)) {
            result.endOption = DateFilterType.NONE;
        }
        result.customer = customer;
        if (null != customer) {
            result.city = null;
            result.country = null;
        } else {
            result.city = city;
            result.country = (null == result.city) ? country : null;
        }
        result.user = user;
        if (null == (result.titleText = titleText)) {
            result.titleText = "";
        }
        if (null == (result.titleOption = titleOption)) {
            result.titleOption = TextFilterType.NONE;
        }
        if (null == (result.locationText = locationText)) {
            result.locationText = "";
        }
        if (null == (result.locationOption = locationOption)) {
            result.locationOption = TextFilterType.NONE;
        }
        return result;
    }

}
