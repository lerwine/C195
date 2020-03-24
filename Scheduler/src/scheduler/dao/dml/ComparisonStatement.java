package scheduler.dao.dml;

import scheduler.dao.DataObjectImpl;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface ComparisonStatement<T extends DataObjectImpl> extends WhereStatement<T> {
    ComparisonOperator getOperator();   
    
}
