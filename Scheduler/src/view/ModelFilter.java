/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.function.Predicate;
import scheduler.dao.DataObjectFilter;
import scheduler.dao.DataObjectImpl;

/**
 *
 * @author erwinel
 */
public interface ModelFilter<R extends DataObjectImpl, M extends ItemModel<R>> extends DataObjectFilter<R>, Predicate<M> {
    
}
