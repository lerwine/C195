package scheduler.model.ui;

import scheduler.dao.ICountryDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CountryDbItem<T extends ICountryDAO> extends CountryItem, FxDbModel<T> {

}
