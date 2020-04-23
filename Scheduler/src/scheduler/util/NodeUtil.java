package scheduler.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import scheduler.controls.ItemEditTableCellFactory;
import scheduler.view.event.ItemActionRequestEventListener;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class NodeUtil {

    private static final Logger LOG = Logger.getLogger(NodeUtil.class.getName());
    
    public enum ExclusiveCssClassGroup {
        VALIDATION,
        NONE
    }
    
    public enum CssClassName {
        SYMBOL_BUTTON("symbol-button"),
        COLLAPSED("collapsed", ExclusiveCssClassGroup.VALIDATION),
        BORDERED("bordered"),
        ERROR("error", ExclusiveCssClassGroup.VALIDATION),
        WARNING("warningMessage", ExclusiveCssClassGroup.VALIDATION),
        INFO("info", ExclusiveCssClassGroup.VALIDATION),
        VALIDATIONMSG("formControlValidationMessage", ExclusiveCssClassGroup.VALIDATION),
        LEFTCONTROLLABEL("leftControlLabel"),
        LEFTLABELEDCONTROL("leftLabeledControl");
        
        private static final Map<ExclusiveCssClassGroup, List<CssClassName>> byGroup;
        private final String value;
        private final List<ExclusiveCssClassGroup> exclusiveGroups;

        public List<ExclusiveCssClassGroup> getExclusiveGroups() {
            return exclusiveGroups;
        }
        
        @Override
        public String toString() {
            return value;
        }
        
        static {
             byGroup = Collections.unmodifiableMap(MapHelper.remap(MapHelper.groupMapFlat(CssClassName.values(), (t) -> (t.exclusiveGroups.isEmpty()) ? 
                    Collections.singleton(ExclusiveCssClassGroup.NONE).iterator() : t.exclusiveGroups.iterator()), (t) -> Collections.unmodifiableList(t)));
                    
        }
        
        public static List<CssClassName> ofGroup(ExclusiveCssClassGroup group) {
            return byGroup.get(group);
        }
        
        private CssClassName(String value, ExclusiveCssClassGroup ...exclusiveGroups) {
            this.value = value;
            this.exclusiveGroups = (null == exclusiveGroups || exclusiveGroups.length == 0) ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(exclusiveGroups));
        }

        public static String[] toStringArray(CssClassName ...classNames) {
            if (null == classNames || classNames.length == 0)
                return new String[0];
            String[] result = new String[classNames.length];
            Arrays.setAll(classNames, (t) -> classNames[t].value);
            return result;
        }
    
        public static <T> void forEachStringValue(Supplier<T> targetSupplier, BiConsumer<T, String> consumer, CssClassName ...classNames) {
            if (null != classNames && classNames.length > 0) {
                T target = targetSupplier.get();
                for (CssClassName e : classNames) {
                    consumer.accept(target, e.value);
                }
            }
        }
    
        public static <T> void forEachStringValue(Supplier<T> targetSupplier, BiConsumer<T, String> consumer, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                T target = targetSupplier.get();
                classNames.forEach((t) -> consumer.accept(target, t.value));
            }
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate, BiConsumer<U, String> consumer, CssClassName ...classNames) {
            if (null != classNames && classNames.length > 0) {
                U target = targetSupplier.apply(source);
                for (CssClassName e : classNames) {
                    if (predicate.test(target, e))
                        consumer.accept(target, e.value);
                }
            }
            return source;
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                U target = targetSupplier.apply(source);
                classNames.stream().filter((e) -> (predicate.test(target, e))).forEachOrdered((e) -> {
                    consumer.accept(target, e.value);
                });
            }
            return source;
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate, BiConsumer<U, String> consumer, CssClassName ...classNames) {
            int z;
            if (null != classNames && (z = classNames.length) > 0) {
                for (int i = 0; i < z; i++) {
                    CssClassName e = classNames[i];
                    if (predicate.test(e)) {
                        U target = targetSupplier.apply(source);
                        consumer.accept(target, e.value);
                        while (++i < z) {
                            e = classNames[i];
                            if (predicate.test(e)) {
                                consumer.accept(target, e.value);
                            }
                        }
                        break;
                    }
                }
            }
            return source;
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                Iterator<CssClassName> iterator = classNames.iterator();
                do {
                    CssClassName e = iterator.next();
                    if (predicate.test(e)) {
                        U target = targetSupplier.apply(source);
                        consumer.accept(target, e.value);
                        while (iterator.hasNext()) {
                            e = iterator.next();
                            if (predicate.test(e)) {
                                consumer.accept(target, e.value);
                            }
                        }
                        break;
                    }
                } while (iterator.hasNext());
            }
            return source;
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiConsumer<U, String> consumer, CssClassName ...classNames) {
            if (null != classNames && classNames.length > 0) {
                U target = targetSupplier.apply(source);
                for (CssClassName e : classNames) {
                    consumer.accept(target, e.value);
                }
            }
            return source;
        }
    
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                U target = targetSupplier.apply(source);
                classNames.forEach((t) -> consumer.accept(target, t.value));
            }
            return source;
        }
    
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func, CssClassName ...classNames) {
            int z;
            if (null != classNames && (z = classNames.length) > 0) {
                T target = targetSupplier.get();
                for (int i = 0; i < z; i++) {
                    CssClassName e = classNames[i];
                    if (predicate.test(target, e)) {
                        Stream.Builder<U> builder = Stream.builder();
                        builder.accept(func.apply(target, e.value));
                        while (++i < z) {
                            e = classNames[i];
                            if (predicate.test(target, e)) {
                                builder.accept(func.apply(target, e.value));
                            }
                        }
                        return builder.build();
                    }
                }
            }
            return Stream.empty();
        }

        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                T target = targetSupplier.get();
                return classNames.stream().filter((t) -> predicate.test(target, t)).map((t) -> func.apply(target, t.value));
            }
            return Stream.empty();
        }

        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func, CssClassName ...classNames) {
            int z;
            if (null != classNames && (z = classNames.length) > 0) {
                for (int i = 0; i < z; i++) {
                    CssClassName e = classNames[i];
                    if (predicate.test(e)) {
                        Stream.Builder<U> builder = Stream.builder();
                        T target = targetSupplier.get();
                        builder.accept(func.apply(target, e.value));
                        while (++i < z) {
                            e = classNames[i];
                            if (predicate.test(e)) {
                                builder.accept(func.apply(target, e.value));
                            }
                        }
                        return builder.build();
                    }
                }
            }
            return Stream.empty();
        }
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                Iterator<CssClassName> iterator = classNames.iterator();
                do {
                    CssClassName e = iterator.next();
                    if (predicate.test(e)) {
                        Stream.Builder<U> builder = Stream.builder();
                        T target = targetSupplier.get();
                        builder.accept(func.apply(target, e.value));
                        while (iterator.hasNext()) {
                            e = iterator.next();
                            if (predicate.test(e)) {
                                builder.accept(func.apply(target, e.value));
                            }
                        }
                        return builder.build();
                    }
                } while (iterator.hasNext());
            }
            return Stream.empty();
        }
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiFunction<T, String, U> func, CssClassName ...classNames) {
            if (null != classNames && classNames.length > 0) {
                T target = targetSupplier.get();
                return Arrays.stream(classNames).map((e) -> func.apply(target, e.value));
            }
            return Stream.empty();
        }
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                T target = targetSupplier.get();
                return classNames.stream().map((e) -> func.apply(target, e.value));
            }
            return Stream.empty();
        }
        
        public static <T> Stream<T> mapStringValues(Function<String, T> func, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                return classNames.stream().map((e) -> func.apply(e.value));
            }
            return Stream.empty();
        }
        
    }

    public enum SymbolButtonValue {
        EDIT(""),
        DELETE(""),
        HYPHEN_POINT("‧"),
        FILTER(""),
        EXPORT(""),
        HELP("❓");
        
        private final String value;
        
        private SymbolButtonValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
        
    }
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
        Button button = setCssClass(new Button(value.value), CssClassName.SYMBOL_BUTTON);
        if (null != onAction)
            button.setOnAction(onAction);
        return button;
    }
    
    public static Button createSymbolButton(SymbolButtonValue value) {
        return createSymbolButton(value, null);
    }
    
    public static <T> TableColumn<T, T> appendEditColumn(TableView<T> tableView, ItemActionRequestEventListener<T> onItemActionRequest) {
        TableColumn<T, T> column = new TableColumn<>(SymbolButtonValue.HYPHEN_POINT.value);
        tableView.getColumns().add(column);
        ItemEditTableCellFactory<T> factory = new ItemEditTableCellFactory<>();
        column.setCellFactory(factory);
        factory.setOnItemActionRequest(onItemActionRequest);
        return column;
    }
    
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
        classes.removeAll(g.stream().filter((t) -> t != className).toArray(String[]::new));
        String s = className.value;
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

}
