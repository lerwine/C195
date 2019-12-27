/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controls;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class ZeroPadDigitListCellFactory  implements Callback<ListView<Integer>, ListCell<Integer>> {
    @Override
    public ListCell<Integer> call(ListView<Integer> param) { return new ZeroPadDigitListCell(); }
}
