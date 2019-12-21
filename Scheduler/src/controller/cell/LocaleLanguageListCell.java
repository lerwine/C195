/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;

/**
 *
 * @author Leonard T. Erwine
 */
public class LocaleLanguageListCell extends ListCell<Locale> {
    @Override
    protected void updateItem(Locale item, boolean empty) {
        super.updateItem(item, empty);
        String s;
        setText((item == null || (s = item.getDisplayLanguage(item)) == null) ? "" : s);
    }
}
