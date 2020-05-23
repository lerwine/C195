package scheduler.dao;

import scheduler.model.City;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICityDAO extends DbObject, City {

    public static <T extends ICityDAO> T assertValidCity(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("City has already been deleted");
        }
        CityDAO.PredefinedCityElement pd = target.getPredefinedElement();
        if (null == pd) {
            throw new IllegalStateException("Invalid city name");
        }
        ICountryDAO country = target.getCountry();
        if (null == country) {
            throw new IllegalStateException("Country not specified");
        }

        if (!ICountryDAO.assertValidCountry(country).getPredefinedElement().getCities().contains(pd)) {
            throw new IllegalStateException("Invalid country association");
        }
        return target;
    }

    @Override
    public ICountryDAO getCountry();

}
