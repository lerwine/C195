/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine
 */
public class AppointmentTypeListCellFactory implements Callback<ListView<model.AppointmentType>, ListCell<model.AppointmentType>> {
    @Override
    public ListCell<model.AppointmentType> call(ListView<model.AppointmentType> param) { return new AppointmentTypeListCell(); }
}