package scheduler.dao;

import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICountryDAO extends DbObject, Country {

    public static <T extends ICountryDAO> T assertValidCountry(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Country has already been deleted");
        }
        CountryDAO.PredefinedCountryElement predefinedElement = target.getPredefinedElement();
        if (null == predefinedElement) {
            throw new IllegalStateException("Invalid country name");
        }
        return target;
    }

}
