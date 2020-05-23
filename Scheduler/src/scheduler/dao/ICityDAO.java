package scheduler.dao;

import java.util.Objects;
import scheduler.model.City;
import scheduler.model.predefined.PredefinedCity;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICityDAO extends DbObject, City {

    public static <T extends ICityDAO> T assertValidCity(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Data access object already deleted");
        }
        PredefinedCity pd = target.getPredefinedData();
        if (null == pd) {
            throw new IllegalStateException("Invalid city name");
        }
        ICountryDAO country = target.getCountry();
        if (null == country) {
            throw new IllegalStateException("Country not specified");
        }
        if (!Objects.equals(pd.getCountry(), ICountryDAO.assertValidCountry(country).getPredefinedData())) {
            throw new IllegalStateException("Invalid country association");
        }
        switch (country.getRowState()) {
            case DELETED:
                throw new IllegalStateException("Country has been deleted");
            case NEW:
        }
        return target;
    }

    @Override
    public ICountryDAO getCountry();

}
