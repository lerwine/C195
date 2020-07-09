package scheduler.dao;

import java.util.regex.Pattern;
import scheduler.model.City;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface PartialCityDAO extends PartialDataAccessObject, City {

    public static Pattern REGION_ID_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9~/._+-]+$", Pattern.CASE_INSENSITIVE);

    @Override
    public PartialCountryDAO getCountry();

}
