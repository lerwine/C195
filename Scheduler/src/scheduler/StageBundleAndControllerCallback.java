/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 *
 * @author Leonard T. Erwine
 * @param <C>
 */
@FunctionalInterface
public interface StageBundleAndControllerCallback<C> {
    void accept(Stage stage, ResourceBundle rb, C controller);
}
