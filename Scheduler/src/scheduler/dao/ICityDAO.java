package scheduler.dao;

import java.util.TimeZone;
import java.util.regex.Pattern;
import scheduler.model.City;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICityDAO extends DbObject, City {

    public static Pattern REGION_ID_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9~/._+-]+$", Pattern.CASE_INSENSITIVE);

    public static <T extends ICityDAO> T assertValidCity(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("City has already been deleted");
        }

        String name = target.getName();

        if (name.isEmpty()) {
            throw new IllegalStateException("City name not defined");
        }

        TimeZone zoneId = target.getTimeZone();

        if (null == zoneId) {
            throw new IllegalStateException("Zone Id not defined");
        }

        if ((name.length() + zoneId.toZoneId().getId().length() + 1) > MAX_LENGTH_NAME) {
            throw new IllegalStateException("Name too long");
        }

        ICountryDAO country = target.getCountry();
        if (null == country) {
            throw new IllegalStateException("Country not specified");
        }

        ICountryDAO.assertValidCountry(country);

        return target;
    }

    @Override
    public ICountryDAO getCountry();

}
