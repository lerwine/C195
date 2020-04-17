package scheduler.util;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.SelectionModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class NodeUtil {

    private static final String CSS_CLASS_COLLAPSED = "collapsed";
    private static final String CSS_CLASS_ERROR = "error";
    private static final String CSS_CLASS_WARNING = "warningMessage";
    private static final String CSS_CLASS_INFO = "info";
    private static final String CSS_CLASS_VALIDATIONMSG = "formControlValidationMessage";

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

    /**
     * 
     * @param node
     * @param isCollapsed 
     * @deprecated This doesn't work because of the "lazy" binding behavior.
     */
    public static void bindCssCollapse(Node node, BooleanExpression isCollapsed) {
        isCollapsed.addListener((observable) -> {
            if (((BooleanExpression)observable).get())
                collapseNode(node);
            else
                restoreNode(node);
        });
    }
    
    /**
     * 
     * @param node
     * @param text 
     * @deprecated This doesn't work because of the "lazy" binding behavior.
     */
    public static void bindCssCollapseLabeledText(Labeled node, StringBinding text) {
        text.addListener((observable) -> {
            String t = ((StringBinding)observable).get();
            if (null == t || t.trim().isEmpty()) {
                node.setText("");
                node.setVisible(false);
                collapseNode(node);
            } else {
                node.setVisible(true);
                restoreLabeled(node, t);
            }
        });
    }
    
    /**
     * 
     * @param node
     * @param text 
     * @deprecated This doesn't work because of the "lazy" binding behavior.
     */
    public static void bindVisibilityLabeledText(Labeled node, StringBinding text) {
        text.addListener((observable) -> {
            String t = ((StringBinding)observable).get();
            if (null == t || t.trim().isEmpty()) {
                node.setText("");
                node.setVisible(false);
            } else {
                node.setVisible(true);
                node.setText(t);
            }
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
        if (!node.isVisible())
            node.setVisible(true);
    }

    /**
     * Restores the dimensions of a JavaFX scene graph {@link javafx.scene.Node} and ensures it's not visible. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list.
     *
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     */
    public static void restoreNodeAsNotVisible(Node node) {
        node.getStyleClass().remove(CSS_CLASS_COLLAPSED);
        if (node.isVisible())
            node.setVisible(false);
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
        if (!control.isVisible())
            control.setVisible(true);
        control.setText(text);
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_ERROR}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list,
     * adds the {@code "error"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    public static void restoreErrorLabel(Labeled control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_INFO);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_WARNING);
        if (!classes.contains(CSS_CLASS_ERROR)) {
            classes.add(CSS_CLASS_ERROR);
        }
        if (!control.isVisible())
            control.setVisible(true);
        control.setText(text);
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_VALIDATIONMSG}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list,
     * adds the {@code "formControlValidationMessage"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    public static void restoreValidationErrorLabel(Labeled control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_WARNING);
        classes.remove(CSS_CLASS_INFO);
        if (!classes.contains(CSS_CLASS_VALIDATIONMSG)) {
            classes.add(CSS_CLASS_VALIDATIONMSG);
        }
        if (!control.isVisible())
            control.setVisible(true);
        control.setText(text);
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_WARNING}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list,
     * adds the {@code "warningMessage"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    public static void restoreWarningLabel(Labeled control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_INFO);
        if (!classes.contains(CSS_CLASS_WARNING)) {
            classes.add(CSS_CLASS_WARNING);
        }
        if (!control.isVisible())
            control.setVisible(true);
        control.setText(text);
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_INFO}.
     * This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list,
     * adds the {@code "infoMessage"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     */
    public static void restoreInfoLabel(Labeled control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_WARNING);
        if (!classes.contains(CSS_CLASS_INFO)) {
            classes.add(CSS_CLASS_INFO);
        }
        if (!control.isVisible())
            control.setVisible(true);
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
