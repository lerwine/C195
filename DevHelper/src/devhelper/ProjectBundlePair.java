package devhelper;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
