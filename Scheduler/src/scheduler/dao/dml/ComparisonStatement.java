package scheduler.dao.dml;

import scheduler.dao.DataObjectImpl;
import scheduler.view.ItemModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The data access object type.
 * @param <U> The item model type.
 */
public interface ComparisonStatement<T extends DataObjectImpl, U extends ItemModel<T>> extends WhereStatement<T, U> {
    ComparisonOperator getOperator();   
    
}
