package scheduler.util;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.SelectionModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class NodeUtil {

    private static final String CSS_CLASS_COLLAPSED = "collapsed";
    private static final String CSS_CLASS_ERROR = "error";

    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}. This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list,
     * which sets vertical and horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible} property to {@code false}.
     *
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     */
    public static void collapseNode(Node node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains(CSS_CLASS_COLLAPSED)) {
            classes.add(CSS_CLASS_COLLAPSED);
        }
    }

    public static void bindCssCollapse(Node node, BooleanExpression isCollapsed) {
        isCollapsed.addListener((observable) -> {
            if (((BooleanExpression)observable).get())
                collapseNode(node);
            else
                restoreNode(node);
        });
    }
    
    /**
     * Restores the visibility and dimensions of a JavaFX scene graph {@link javafx.scene.Node}. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list.
     *
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     */
    public static void restoreNode(Node node) {
        node.getStyleClass().remove(CSS_CLASS_COLLAPSED);
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets the
     * {@link javafx.scene.control.Labeled#text} property. This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list
     * and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    public static void restoreLabeled(Labeled control, String text) {
        control.getStyleClass().remove(CSS_CLASS_COLLAPSED);
        control.setText(text);
    }

    public static void restoreErrorLabel(Labeled control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        if (!classes.contains(CSS_CLASS_ERROR)) {
            classes.add(CSS_CLASS_ERROR);
        }
        control.setText(text);
    }

    public static <T> boolean selectSelection(SelectionModel<T> selectionModel, ObservableList<T> source, Predicate<T> predicate) {
        for (int i = 0; i < source.size(); i++) {
            if (predicate.test(source.get(i))) {
                selectionModel.clearAndSelect(i);
                return true;
            }
        }
        return false;
    }

    public static <T> boolean selectSelection(ComboBox<T> source, Predicate<T> predicate) {
        return selectSelection(source.getSelectionModel(), source.getItems(), predicate);
    }

    public static <T, U> boolean selectSelection(T value, SelectionModel<U> selectionModel, ObservableList<U> source, BiPredicate<T, U> predicate) {
        for (int i = 0; i < source.size(); i++) {
            if (predicate.test(value, source.get(i))) {
                selectionModel.clearAndSelect(i);
                return true;
            }
        }
        return false;
    }

    public static <T, U> boolean selectSelection(T value, ComboBox<U> source, BiPredicate<T, U> predicate) {
        return selectSelection(value, source.getSelectionModel(), source.getItems(), predicate);
    }

}
