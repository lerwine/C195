package scheduler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import scheduler.observables.BindingHelper;
import scheduler.observables.MutationBindableObservableList;
import scheduler.view.CssClassName;
import scheduler.view.ExclusiveCssClassGroup;
import scheduler.view.SymbolButtonValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class NodeUtil {

    private static final Logger LOG = Logger.getLogger(NodeUtil.class.getName());
    
    private static void addCssClass(ObservableList<String> styleClass, String name) {
        if (!styleClass.contains(name))
            styleClass.add(name);
    }
    
    public static <T extends Styleable> T addCssClass(T stylable, CssClassName ...classNames) {
        return CssClassName.applyEachStringValue(stylable, (t) -> t.getStyleClass(), NodeUtil::addCssClass, classNames);
    }

    public static <T extends Styleable> T addCssClass(T stylable, Collection<CssClassName> classNames) {
        return CssClassName.applyEachStringValue(stylable, (t) -> t.getStyleClass(), NodeUtil::addCssClass, classNames);
    }

    public static <T extends Styleable> T removeCssClass(T stylable, CssClassName ...classNames) {
        if (null != classNames && classNames.length > 0)
            stylable.getStyleClass().removeAll(CssClassName.toStringArray(classNames));
        return stylable;
    }

    public static <T extends Styleable> T setCssClass(T stylable, CssClassName ...classNames) {
        if (null != classNames && classNames.length > 0) {
            stylable.getStyleClass().setAll(CssClassName.toStringArray(classNames));
        }
        javafx.collections.FXCollections.observableArrayList();
        return stylable;
    }

    public static <T extends Region> T setCompactXY(T node) {
        node.setMinWidth(USE_PREF_SIZE);
        node.setMinHeight(USE_PREF_SIZE);
        node.setPrefWidth(USE_COMPUTED_SIZE);
        node.setPrefHeight(USE_COMPUTED_SIZE);
        node.setMaxWidth(USE_PREF_SIZE);
        node.setMaxHeight(USE_PREF_SIZE);
        return node;
    }
    
    public static <T extends Pane> T appendChildNodes(T parent, Node ...elements) {
        if (null != elements && elements.length > 0)
            parent.getChildren().addAll(elements);
        return parent;
    }
    
    public static HBox createCompactHBox(double spacing, Node ...elements) {
        HBox hBox = setCompactXY(new HBox());
        hBox.setSpacing(spacing);
        return appendChildNodes(hBox, elements);
    }
    
    public static HBox createCompactHBox(Node ...elements) {
        return appendChildNodes(setCompactXY(new HBox()), elements);
    }
    
    public static Button createSymbolButton(SymbolButtonValue value, EventHandler<ActionEvent> onAction) {
        Button button = setCssClass(new Button(value.toString()), CssClassName.SYMBOL_BUTTON);
        if (null != onAction)
            button.setOnAction(onAction);
        return button;
    }
    
    public static Button createSymbolButton(SymbolButtonValue value) {
        return createSymbolButton(value, null);
    }
    
//    public static <T> TableColumn<T, T> appendEditColumn(TableView<T> tableView, ItemActionRequestEventListener<T> onItemActionRequest) {
//        TableColumn<T, T> column = new TableColumn<>(SymbolButtonValue.HYPHEN_POINT.value);
//        column.setCellValueFactory((TableColumn.CellDataFeatures<T, T> param) -> {
//            return new ReadOnlyObjectWrapper<>(param.getValue());
//        });
//        ItemEditTableCellFactory<T> factory = new ItemEditTableCellFactory<>();
//        column.setCellFactory(factory);
//        factory.setOnItemActionRequest(onItemActionRequest);
//        tableView.getColumns().add(column);
//        return column;
//    }
    
    public static <T extends Node> T setBorderedNode(T node) {
        return setCssClass(node, CssClassName.BORDERED);
    }

    public static <T extends Labeled> T setLeftControlLabel(T node, String text) {
        setLeftControlLabel(node).setText(text);
        return node;
    }

    public static <T extends Labeled> T setLeftControlLabel(T node) {
        return setCssClass(node, CssClassName.LEFTCONTROLLABEL);
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
        return setCssClass(node, CssClassName.LEFTLABELEDCONTROL);
    }

    private static <T extends Styleable> T setGroup(T node, CssClassName className, ExclusiveCssClassGroup group) {
        List<CssClassName> g = CssClassName.ofGroup(group);
        ObservableList<String> classes = node.getStyleClass();
        classes.removeAll(g.stream().filter((t) -> t != className).map((t) -> t.toString()).toArray(String[]::new));
        String s = className.toString();
        if (!classes.contains(s))
            classes.add(s);
        return node;
    }
    
    private static <T extends Styleable> T clearGroup(T node, CssClassName className, ExclusiveCssClassGroup group) {
        node.getStyleClass().removeAll(CssClassName.ofGroup(group));
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
        return setGroup(node, CssClassName.COLLAPSED, ExclusiveCssClassGroup.VALIDATION);
    }

    /**
     * @deprecated Doesn't work
     */
    public static void bindCssClassSwitch(Node node, BooleanBinding observable, Collection<CssClassName> ifTrue, Collection<CssClassName> ifFalse) {
        ArrayList<String> addIfTrue = new ArrayList<>();
        ArrayList<String> removeIfTrue = new ArrayList<>();
        ArrayList<String> addIfFalse = new ArrayList<>();
        ArrayList<String> removeIfFalse = new ArrayList<>();
        if (ifFalse.isEmpty()) {
            ifTrue.stream().forEach((t) -> {
                String n = t.toString();
                if (!addIfTrue.contains(n)) {
                    removeIfFalse.add(n);
                    addIfTrue.add(n);
                }
            });
        } else if (ifTrue.isEmpty()) {
            ifFalse.stream().forEach((t) -> {
                String n = t.toString();
                if (!addIfFalse.contains(n)) {
                    removeIfTrue.add(n);
                    addIfFalse.add(n);
                }
            });
        } else {
            ifTrue.stream().forEach((t) -> {
                String n = t.toString();
                if (!addIfTrue.contains(n)) {
                    if (!ifFalse.contains(t))
                        removeIfFalse.add(n);
                    addIfTrue.add(n);
                }
            });
            ifFalse.stream().forEach((t) -> {
                String n = t.toString();
                if (!addIfFalse.contains(n)) {
                    if (!ifTrue.contains(t))
                        removeIfTrue.add(n);
                    addIfFalse.add(n);
                }
            });
        }
        MutationBindableObservableList<String> boundClassNames = new MutationBindableObservableList();
        boundClassNames.addAll(node.getStyleClass());
        Bindings.bindContentBidirectional(boundClassNames, node.getStyleClass());
        boundClassNames.mutationProperty().bind(
                Bindings.when(observable)
                .then(MutationBindableObservableList.createRemoveAddOperation(removeIfTrue, addIfTrue))
                .otherwise(MutationBindableObservableList.createRemoveAddOperation(removeIfFalse, addIfFalse))
        );
    }
    
    public static void bindCssClassSwitch(Node node, BooleanBinding observable, CssClassName[] ifTrue, CssClassName ...ifFalse) {
        bindCssClassSwitch(node, observable, Arrays.asList(ifTrue), (null == ifFalse || ifFalse.length == 0) ? Collections.emptyList() : Arrays.asList(ifFalse));
    }
    
    /**
     * @deprecated Doesn't work
     */
    public static void bindCssClassSwitch(Node node, BooleanBinding observable, CssClassName ...ifTrue) {
        bindCssClassSwitch(node, observable, Arrays.asList(ifTrue), Collections.emptyList());
    }
    
    /**
     * @deprecated Doesn't work
     */
     public static BooleanBinding bindCssClassSwitch(Node node, Collection<CssClassName> ifTrue, Collection<CssClassName> ifFalse, Callable<Boolean> func, Observable... dependencies) {
        BooleanBinding result = Bindings.createBooleanBinding(func, dependencies);
        bindCssClassSwitch(node, result, ifTrue, ifFalse);
        return result;
     }
    
    /**
     * @deprecated Doesn't work
     */
     public static BooleanBinding bindCssClassSwitch(Node node, CssClassName[] ifTrue, CssClassName[] ifFalse, Callable<Boolean> func, Observable... dependencies) {
        BooleanBinding result = Bindings.createBooleanBinding(func, dependencies);
        bindCssClassSwitch(node, result, Arrays.asList(ifTrue), Arrays.asList(ifFalse));
        return result;
     }
     
    /**
     * @deprecated Doesn't work
     */
    public static BooleanBinding bindCollapsibleMessage(Labeled label, ObservableValue<String> observable) {
        label.textProperty().bind(observable);
        BooleanBinding result = BindingHelper.isNullOrWhiteSpace(Objects.requireNonNull(observable));
        bindCssCollapse(label, result);
        return result;
    }

    /**
     * @deprecated Doesn't work
     */
    public static StringBinding bindCollapsibleMessage(Labeled label, Callable<String> func, Observable... dependencies) {
        StringBinding result = Bindings.createStringBinding(func, dependencies);
        bindCollapsibleMessage(label, result);
        return result;
    }

    /**
     * @deprecated Doesn't work
     */
    public static BooleanBinding bindCollapsible(Node node, Callable<Boolean> func, Observable... dependencies) {
        BooleanBinding result = Bindings.createBooleanBinding(func, dependencies);
        bindCssCollapse(node, result);
        return result;
    }

    /**
     *
     * @param node
     * @param isCollapsed
     * @deprecated Doesn't work
     */
    public static void bindCssCollapse(Node node, BooleanExpression isCollapsed) {
        MutationBindableObservableList<String> boundClassNames = new MutationBindableObservableList();
        boundClassNames.addAll(node.getStyleClass());
        Bindings.bindContentBidirectional(boundClassNames, node.getStyleClass());
        boundClassNames.mutationProperty().bind(Bindings.when(isCollapsed).then(MutationBindableObservableList.createAddOperationBinding(CssClassName.COLLAPSED.toString()))
                .otherwise(MutationBindableObservableList.createRemoveOperationBinding(CssClassName.COLLAPSED.toString())));
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
        removeCssClass(node, CssClassName.COLLAPSED);
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
        removeCssClass(node, CssClassName.COLLAPSED);
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
        removeCssClass(control, CssClassName.COLLAPSED);
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
        setGroup(control, CssClassName.ERROR, ExclusiveCssClassGroup.VALIDATION);
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
        setGroup(control, CssClassName.VALIDATIONMSG, ExclusiveCssClassGroup.VALIDATION);
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
        setGroup(control, CssClassName.WARNING, ExclusiveCssClassGroup.VALIDATION);
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
        setGroup(control, CssClassName.INFO, ExclusiveCssClassGroup.VALIDATION);
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

    public static void collapseWhenTrue(Node node, ObservableValue<Boolean> observable) {
        observable.addListener((o) -> {
            if (((ObservableValue<Boolean>)o).getValue())
                collapseNode(node);
            else
                restoreNode(node);
        });
    }

}
