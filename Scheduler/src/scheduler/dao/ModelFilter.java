/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.util.function.Predicate;
import scheduler.view.ItemModel;

/**
 * Represents a data record filter.
 * @author lerwi
 * @param <D> The type of {@link DataObjectImpl} object that represents the data from the database.
 * @param <M>
 */
public interface ModelFilter<D extends DataObjectImpl, M extends ItemModel<D>> extends RecordReader<D>, Predicate<M> {
    String getHeading();
    String getSubHeading();
}
