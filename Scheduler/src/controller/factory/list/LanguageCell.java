/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.factory.list;

import java.util.Locale;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class LanguageCell implements Callback<ListView<Locale>, ListCell<Locale>> {

    @Override
    public ListCell<Locale> call(ListView<Locale> param) {
        return new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                String s;
                setText((item == null || (s = item.getDisplayLanguage(item)) == null) ? "" : s);
            }
        };
    }
    
}
