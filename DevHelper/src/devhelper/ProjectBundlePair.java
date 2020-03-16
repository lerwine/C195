/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.util.Locale;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.observables.OptionalValueProperty;

/**
 *
 * @author lerwi
 */
public class ProjectBundlePair {

    private final ReadOnlyObjectWrapper<BundleSet> leftBundleSet = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<BundleSet> rightBundleSet = new ReadOnlyObjectWrapper<>();
    private ObservableList<LocaleItem> allLanguages = FXCollections.observableArrayList();

    public BundleSet getLeftBundleSet() {
        return leftBundleSet.get();
    }

    public ReadOnlyObjectProperty<BundleSet> leftBundleSetProperty() {
        return leftBundleSet.getReadOnlyProperty();
    }
    
    public BundleSet getRightBundleSet() {
        return rightBundleSet.get();
    }

    public ReadOnlyObjectProperty<BundleSet> rightBundleSetProperty() {
        return rightBundleSet.getReadOnlyProperty();
    }
    
}
