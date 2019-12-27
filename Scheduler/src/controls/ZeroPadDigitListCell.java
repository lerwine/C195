/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine
 */
public class ZeroPadDigitListCell extends ListCell<Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        setText((empty || item == null) ? "" : ((item < 10) ? "0" + item.toString() : item.toString()));
    }
}
