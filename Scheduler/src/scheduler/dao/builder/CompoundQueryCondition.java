/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.builder;

import java.util.SortedSet;

/**
 *
 * @author erwinel
 */
public interface CompoundQueryCondition extends QueryCondition<CompoundOperator>, SortedSet<QueryCondition<?>> {
}
