/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import scheduler.dao.DaoFilter;
import scheduler.dao.DataObjectImpl;

/**
 *
 * @author lerwi
 * @param <T>
 * @param <S>
 */
public interface ModelFilter<T extends DataObjectImpl, S extends ItemModel<T>> extends DaoFilter<T> {
    String getHeadingText();
    String getSubHeadingText();
}
