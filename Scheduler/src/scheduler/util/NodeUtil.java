package scheduler.util;

import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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

    private static final Logger LOG = Logger.getLogger(NodeUtil.class.getName());

    private static final String CSS_CLASS_COLLAPSED = "collapsed";
    private static final String CSS_CLASS_BORDERED = "bordered";
    private static final String CSS_CLASS_ERROR = "error";
    private static final String CSS_CLASS_WARNING = "warningMessage";
    private static final String CSS_CLASS_INFO = "info";
    private static final String CSS_CLASS_VALIDATIONMSG = "formControlValidationMessage";
    private static final String CSS_CLASS_LEFTCONTROLLABEL = "leftControlLabel";
    private static final String CSS_CLASS_LEFTLABELEDCONTROL = "leftLabeledControl";

    public static <T extends Node> T setBorderedNode(T node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains(CSS_CLASS_BORDERED)) {
            classes.add(CSS_CLASS_BORDERED);
        }
        return node;
    }

    public static <T extends Labeled> T setLeftControlLabel(T node, String text) {
        setLeftControlLabel(node).setText(text);
        return node;
    }

    public static <T extends Labeled> T setLeftControlLabel(T node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains(CSS_CLASS_LEFTCONTROLLABEL)) {
            classes.add(CSS_CLASS_LEFTCONTROLLABEL);
        }
        return node;
    }

    public static <T extends Labeled> T setLeftLabeledControl(T node, String text) {
        setLeftControlLabel(node).setText(text);
        return node;
    }

    public static <T extends Labeled> T setLeftLabeledControl(T node, boolean wrapText) {
        setLeftControlLabel(node).setWrapText(wrapText);
        return node;
    }

    public static <T extends Labeled> T setLeftLabeledControl(T node, String text, boolean wrapText) {
        setLeftLabeledControl(node, wrapText).setText(text);
        return node;
    }

    public static <T extends Node> T setLeftLabeledControl(T node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains(CSS_CLASS_LEFTLABELEDCONTROL)) {
            classes.add(CSS_CLASS_LEFTLABELEDCONTROL);
        }
        return node;
    }

    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}. This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list,
     * which sets vertical and horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible} property to {@code false}.
     *
     * @param <T> The type of {@link Node} to collapse.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     * @return The collapsed {@link Node}.
     */
    public static <T extends Node> T collapseNode(T node) {
        ObservableList<String> classes = node.getStyleClass();
        if (!classes.contains(CSS_CLASS_COLLAPSED)) {
            classes.add(CSS_CLASS_COLLAPSED);
        }
        return node;
    }

    public static StringBinding bindCollapsibleMessage(Labeled label, Callable<String> func, Observable... dependencies) {
        StringBinding result = Bindings.createStringBinding(func, dependencies);
        label.textProperty().bind(result);
        result.addListener((observable, oldValue, newValue) -> {
            if (null == newValue || newValue.isEmpty()) {
                LOG.info("Binding changed to empty - Collapsing node");
                collapseNode(label);
            } else {
                LOG.info("Binding changed to not empty - Restoring node");
                restoreNode(label);
            }
        });
        return result;
    }

    public static BooleanBinding bindCollapsible(Node node, Callable<Boolean> func, Observable... dependencies) {
        BooleanBinding result = Bindings.createBooleanBinding(func, dependencies);
        result.addListener((observable, oldValue, newValue) -> {
            if (null != newValue && newValue == true) {
                LOG.info("Binding changed to true - Collapsing node");
                collapseNode(node);
            } else {
                LOG.info("Binding changed to false - Restoring node");
                restoreNode(node);
            }
        });
        return result;
    }

    /**
     *
     * @param node
     * @param isCollapsed
     * @deprecated This doesn't work because of the "lazy" binding behavior.
     */
    public static void bindCssCollapse(Node node, BooleanExpression isCollapsed) {
        isCollapsed.addListener((observable) -> {
            if (((BooleanExpression) observable).get()) {
                collapseNode(node);
            } else {
                restoreNode(node);
            }
        });
    }

    /**
     * Restores the visibility and dimensions of a JavaFX scene graph {@link javafx.scene.Node}. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list.
     *
     * @param <T> The type of {@link Node} to restore.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     * @return The restored {@link Node}.
     */
    public static <T extends Node> T restoreNode(T node) {
        node.getStyleClass().remove(CSS_CLASS_COLLAPSED);
        if (!node.isVisible()) {
            node.setVisible(true);
        }
        return node;
    }

    /**
     * Restores the dimensions of a JavaFX scene graph {@link javafx.scene.Node} and ensures it's not visible. This removes the CSS class "collapsed"
     * from the {@link javafx.scene.Node#styleClass} list.
     *
     * @param <T> The type of {@link Node} to restore.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     * @return The restored {@link Node}.
     */
    public static <T extends Node> T restoreNodeAsNotVisible(T node) {
        node.getStyleClass().remove(CSS_CLASS_COLLAPSED);
        if (node.isVisible()) {
            node.setVisible(false);
        }
        return node;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets the
     * {@link javafx.scene.control.Labeled#text} property. This removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list
     * and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreLabeled(T control, String text) {
        control.getStyleClass().remove(CSS_CLASS_COLLAPSED);
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_ERROR}. This removes the
     * CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list, adds the {@code "error"} class and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreErrorLabel(T control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_INFO);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_WARNING);
        if (!classes.contains(CSS_CLASS_ERROR)) {
            classes.add(CSS_CLASS_ERROR);
        }
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_VALIDATIONMSG}. This
     * removes the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list, adds the {@code "formControlValidationMessage"} class and
     * sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreValidationErrorLabel(T control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_WARNING);
        classes.remove(CSS_CLASS_INFO);
        if (!classes.contains(CSS_CLASS_VALIDATIONMSG)) {
            classes.add(CSS_CLASS_VALIDATIONMSG);
        }
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_WARNING}. This removes the
     * CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list, adds the {@code "warningMessage"} class and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreWarningLabel(T control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_INFO);
        if (!classes.contains(CSS_CLASS_WARNING)) {
            classes.add(CSS_CLASS_WARNING);
        }
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link #CSS_CLASS_INFO}. This removes the
     * CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list, adds the {@code "infoMessage"} class and sets the
     * {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreInfoLabel(T control, String text) {
        ObservableList<String> classes = control.getStyleClass();
        classes.remove(CSS_CLASS_COLLAPSED);
        classes.remove(CSS_CLASS_ERROR);
        classes.remove(CSS_CLASS_VALIDATIONMSG);
        classes.remove(CSS_CLASS_WARNING);
        if (!classes.contains(CSS_CLASS_INFO)) {
            classes.add(CSS_CLASS_INFO);
        }
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        return control;
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
