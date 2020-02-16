/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.controls;

import javafx.collections.ObservableMap;
import javafx.scene.control.TableCell;
import scheduler.dao.UserImpl;

/**
 *
 * @author lerwi
 * @param <S> The item type.
 */
public class UserStatusTableCell<S> extends TableCell<S, Integer> {
    private final ObservableMap<Integer, String> userStatusMap;
    UserStatusTableCell(ObservableMap<Integer, String> userStatusMap) {
        this.userStatusMap = (null == userStatusMap) ? UserImpl.getUserStatusMap() : userStatusMap;
    }
    public UserStatusTableCell() { this(null); }
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText((item == null) ? "" : ((userStatusMap.containsKey(item)) ? userStatusMap.get(item) : item.toString()));
    }
}
