package scheduler.dao;

import java.util.Locale;
import java.util.Map;
import javafx.collections.ObservableMap;
import scheduler.model.CustomerCountry;
import scheduler.model.PredefinedData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICountryDAO extends DbObject, CustomerCountry {

    public static <T extends ICountryDAO> T assertValidCountry(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Country has already been deleted");
        }

        String name = target.getName();

        if (name.isEmpty()) {
            throw new IllegalStateException("Country name not defined");
        }

        Locale locale = target.getLocale();

        if (null == locale) {
            throw new IllegalStateException("Locale not defined");
        }

        String lt = locale.toLanguageTag();
        if ((name.length() + lt.length() + 1) > CountryDAO.MAX_LENGTH_NAME) {
            Map<String, String> localeDisplayMap = PredefinedData.getLocaleDisplayMap();
            if (!localeDisplayMap.containsKey(lt) || !name.equals(localeDisplayMap.get(lt))) {
                throw new IllegalStateException("Name too long");
            }
        }

        return target;
    }

}
