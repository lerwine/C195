package scheduler.dao.dml;

import scheduler.dao.DataObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface ComparisonStatement<T extends DataObject> extends WhereStatement<T> {
    ComparisonOperator getOperator();   
    
}
