/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.scene.control.Control;
import javafx.scene.control.Labeled;

/**
 *
 * @author Leonard T. Erwine
 */
class util {
    static void collapseLabeledVertical(Labeled labeled) {
        labeled.setText("");
        collapseControlVertical(labeled);
    }
    
    static void collapseControlVertical(Control control) {
        control.setMinHeight(0);
        control.setPrefHeight(0);
        control.setMaxHeight(0);
        control.setVisible(false);
    }
    
    static void restoreLabeledVertical(Labeled labeled, String text) {
        labeled.setText(text);
        restoreControlVertical(labeled);
    }
    
    static void restoreControlVertical(Control control) {
        restoreControlVertical(control, Control.USE_COMPUTED_SIZE);
    }
    
    static void restoreLabeledVertical(Labeled labeled, String text, double prefHeight) {
        labeled.setText(text);
        restoreControlVertical(labeled, prefHeight);
    }
    
    static void restoreControlVertical(Control control, double prefHeight) {
        restoreControlVertical(control, Control.USE_COMPUTED_SIZE, prefHeight, Control.USE_COMPUTED_SIZE);
    }
    
    static void restoreLabeledVertical(Labeled labeled, String text, double minHeight, double prefHeight, double maxHeight) {
        labeled.setText(text);
        restoreControlVertical(labeled, minHeight, prefHeight, maxHeight);
    }
    
    static void restoreControlVertical(Control control, double minHeight, double prefHeight, double maxHeight) {
        control.setMaxHeight(maxHeight);
        control.setPrefHeight(prefHeight);
        control.setMinHeight(minHeight);
        control.setVisible(true);
    }
}
