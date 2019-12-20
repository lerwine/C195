/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.factory.list;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class Integer2Cell implements Callback<ListView<Integer>, ListCell<Integer>> {

    @Override
    public ListCell<Integer> call(ListView<Integer> param) {
        return new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : ((item < 10) ? "0" + item.toString() : item.toString()));
            }
        };
    }
    
}
