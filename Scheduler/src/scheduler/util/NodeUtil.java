package scheduler.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;
import javafx.util.Callback;
import scheduler.dao.DataRowState;
import scheduler.fx.CssClassName;
import scheduler.fx.ValidationStatus;
import scheduler.model.PartialDataEntity;
import scheduler.view.SymbolText;

/**
 * Utility class for working with FXML UI objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class NodeUtil {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(NodeUtil.class.getName()), Level.FINE);
//    private static final Logger LOG = Logger.getLogger(NodeUtil.class.getName());

    public static boolean isVisibleInWindow(Node node) {
        if (node != null && node.isVisible()) {
            Scene scene = node.getScene();
            if (null != scene) {
                Window window = scene.getWindow();
                return null != window && window.isShowing();
            }
        }
        return false;
    }

    public static boolean isInShownWindow(Node node) {
        if (node != null) {
            Scene scene = node.getScene();
            if (null != scene) {
                Window window = scene.getWindow();
                return null != window && window.isShowing();
            }
        }
        return false;
    }

    public static boolean isInWindow(Node node) {
        if (node != null) {
            Scene scene = node.getScene();
            return null != scene && null != scene.getWindow();
        }
        return false;
    }

    private static void addCssClass(ObservableList<String> styleClass, String name) {
        LOG.entering(LOG.getName(), "addCssClass", new Object[]{styleClass, name});
        if (!styleClass.contains(name)) {
            LOG.finer(() -> String.format("Adding class %s", name));
            styleClass.add(name);
        }
        LOG.exiting(LOG.getName(), "addCssClass");
    }

    /**
     * Adds CSS class names.
     *
     * @param <T> The {@link Styleable} type.
     * @param stylable The target {@link Styleable} node.
     * @param classNames The {@link CssClassName} values representing CSS class names to be added.
     * @return The target {@link Styleable} with the specified CSS {@code classNames} added.
     */
    public static <T extends Styleable> T addCssClass(T stylable, CssClassName... classNames) {
        LOG.entering(LOG.getName(), "addCssClass", new Object[]{stylable, classNames});
        if (null != classNames && classNames.length > 0) {
            CssClassName.applyEachStringValue(stylable, (t) -> t.getStyleClass(), NodeUtil::addCssClass, classNames);
        }
        LOG.exiting(LOG.getName(), "addCssClass", stylable);
        return stylable;
    }

    /**
     * Adds CSS class names.
     *
     * @param <T> The {@link Styleable} type.
     * @param stylable The target {@link Styleable} node.
     * @param classNames The {@link CssClassName} values representing CSS class names to be added.
     * @return The target {@link Styleable} with the specified CSS {@code classNames} added.
     */
    public static <T extends Styleable> T addCssClass(T stylable, Collection<CssClassName> classNames) {
        LOG.entering(LOG.getName(), "addCssClass", new Object[]{stylable, classNames});
        if (null != classNames && !classNames.isEmpty()) {
            CssClassName.applyEachStringValue(stylable, (t) -> t.getStyleClass(), NodeUtil::addCssClass, classNames);
        }
        LOG.exiting(LOG.getName(), "addCssClass", stylable);
        return stylable;
    }

    /**
     * Removes specific CSS class names.
     *
     * @param <T> The {@link Styleable} type.
     * @param stylable The target {@link Styleable} node.
     * @param classNames The {@link CssClassName} values representing CSS class names to be removed.
     * @return The target {@link Styleable} with the specified CSS {@code classNames} removed.
     */
    public static <T extends Styleable> T removeCssClass(T stylable, CssClassName... classNames) {
        LOG.entering(LOG.getName(), "removeCssClass", new Object[]{stylable, classNames});
        if (null != classNames && classNames.length > 0) {
            stylable.getStyleClass().removeAll(CssClassName.toStringArray(classNames));
        }
        LOG.exiting(LOG.getName(), "removeCssClass", stylable);
        return stylable;
    }

    /**
     * Removes existing CSS class names, replacing them with the specified {@link CssClassName}s.
     *
     * @param <T> The {@link Styleable} type.
     * @param stylable The target {@link Styleable} node.
     * @param classNames The {@link CssClassName} values representing the new CSS class names.
     * @return The target {@link Styleable} with the new CSS {@code classNames} applied.
     */
    public static <T extends Styleable> T setCssClass(T stylable, CssClassName... classNames) {
        LOG.entering(LOG.getName(), "setCssClass", new Object[]{stylable, classNames});
        if (null != classNames && classNames.length > 0) {
            stylable.getStyleClass().setAll(CssClassName.toStringArray(classNames));
        }
        LOG.exiting(LOG.getName(), "setCssClass", stylable);
        return stylable;
    }

    /**
     * Sets the computed height and width properties so the {@link Region} takes up only what space is needed. This sets the minimum and maximum computed width properties
     * {@link Region#USE_PREF_SIZE}, and the preferred height and width properties to {@link Region#USE_COMPUTED_SIZE}.
     *
     * @param <T> The {@link Region} type.
     * @param region The target {@link Region}.
     * @return The {@link Region} with computed height and width properties applied.
     */
    public static <T extends Region> T setCompactXY(T region) {
        region.setMinWidth(USE_PREF_SIZE);
        region.setMinHeight(USE_PREF_SIZE);
        region.setPrefWidth(USE_COMPUTED_SIZE);
        region.setPrefHeight(USE_COMPUTED_SIZE);
        region.setMaxWidth(USE_PREF_SIZE);
        region.setMaxHeight(USE_PREF_SIZE);
        return region;
    }

    public static <T extends Region> T setMaxXY(T region) {
        region.setMinWidth(USE_COMPUTED_SIZE);
        region.setMinHeight(USE_COMPUTED_SIZE);
        region.setPrefWidth(USE_COMPUTED_SIZE);
        region.setPrefHeight(USE_COMPUTED_SIZE);
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
        return region;
    }

    public static <T extends Pane> T appendChildNodes(T parent, Node... elements) {
        if (null != elements && elements.length > 0) {
            parent.getChildren().addAll(elements);
        }
        return parent;
    }

    public static void bindExtents(Region inner, Region outer) {
        inner.prefWidthProperty().bind(outer.widthProperty());
        inner.minWidthProperty().bind(outer.widthProperty());
        inner.prefHeightProperty().bind(outer.heightProperty());
        inner.minHeightProperty().bind(outer.heightProperty());
    }

    public static void unbindExtents(Region region) {
        region.prefWidthProperty().unbind();
        region.minWidthProperty().unbind();
        region.prefHeightProperty().unbind();
        region.minHeightProperty().unbind();
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param columnspan The number of columns to span.
     * @param rowspan The number of rows to span.
     * @param halignment The horizontal alignment or {@code null} for no horizontal alignment constraint.
     * @param valignment The vertical alignment or {@code null} for no vertical alignment constraint.
     * @param hgrow The horizontal grow priority or {@code null} for no horizontal grow priority constraint.
     * @param vgrow The vertical grow priority or {@code null} for no vertical grow priority constraint.
     * @param margin The margin of space around the child node or {@code null} for no margin constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, int columnspan, int rowspan,
            HPos halignment, VPos valignment, Priority hgrow, Priority vgrow, Insets margin) {
        if (rowIndex < 0) {
            throw new IllegalArgumentException("Invalid row index");
        }
        if (columnIndex < 0) {
            throw new IllegalArgumentException("Invalid column index");
        }
        if (columnspan < 1) {
            throw new IllegalArgumentException("Invalid column span");
        }
        if (rowspan < 1) {
            throw new IllegalArgumentException("Invalid row span");
        }
        ObservableList<ColumnConstraints> columnConstraints = gridPane.getColumnConstraints();
        int count = columnIndex + columnspan;
        if (columnConstraints.size() < count) {
            Priority p = hgrow;
            HPos h = halignment;
            if (!columnConstraints.isEmpty()) {
                ColumnConstraints cc = columnConstraints.get(columnConstraints.size() - 1);
                p = cc.getHgrow();
                h = cc.getHalignment();
            }
            do {
                columnConstraints.add(new ColumnConstraints(USE_PREF_SIZE, USE_PREF_SIZE, USE_PREF_SIZE, p, h, true));
            } while (columnConstraints.size() < count);
        }
        count = rowIndex + rowspan;
        ObservableList<RowConstraints> rowConstraints = gridPane.getRowConstraints();
        if (rowConstraints.size() < count) {
            Priority p = vgrow;
            VPos v = valignment;
            if (!rowConstraints.isEmpty()) {
                RowConstraints rc = rowConstraints.get(rowConstraints.size() - 1);
                p = rc.getVgrow();
                v = rc.getValignment();
            }
            do {
                rowConstraints.add(new RowConstraints(USE_PREF_SIZE, USE_PREF_SIZE, USE_PREF_SIZE, p, v, true));
            } while (rowConstraints.size() < count);
        }
        ObservableList<Node> children = gridPane.getChildren();
        if (!children.contains(child)) {
            children.add(child);
        }
        GridPane.setConstraints(child, columnIndex, rowIndex, columnspan, rowspan, halignment, valignment, hgrow, vgrow, margin);
        return child;
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param columnspan The number of columns to span.
     * @param rowspan The number of rows to span.
     * @param halignment The horizontal alignment or {@code null} for no horizontal alignment constraint.
     * @param valignment The vertical alignment or {@code null} for no vertical alignment constraint.
     * @param hgrow The horizontal grow priority or {@code null} for no horizontal grow priority constraint.
     * @param vgrow The vertical grow priority or {@code null} for no vertical grow priority constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, int columnspan, int rowspan,
            HPos halignment, VPos valignment, Priority hgrow, Priority vgrow) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, columnspan, rowspan, halignment, valignment, hgrow, vgrow, null);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param columnspan The number of columns to span.
     * @param rowspan The number of rows to span.
     * @param halignment The horizontal alignment or {@code null} for no horizontal alignment constraint.
     * @param valignment The vertical alignment or {@code null} for no vertical alignment constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, int columnspan, int rowspan,
            HPos halignment, VPos valignment) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, columnspan, rowspan, halignment, valignment, null, null);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param columnspan The number of columns to span.
     * @param rowspan The number of rows to span.
     * @param hgrow The horizontal grow priority or {@code null} for no horizontal grow priority constraint.
     * @param vgrow The vertical grow priority or {@code null} for no vertical grow priority constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, int columnspan, int rowspan,
            Priority hgrow, Priority vgrow) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, columnspan, rowspan, null, null, hgrow, vgrow);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param columnspan The number of columns to span.
     * @param rowspan The number of rows to span.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, int columnspan, int rowspan) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, columnspan, rowspan, null, (VPos) null);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param halignment The horizontal alignment or {@code null} for no horizontal alignment constraint.
     * @param valignment The vertical alignment or {@code null} for no vertical alignment constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, HPos halignment, VPos valignment) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, 1, 1, halignment, valignment);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @param hgrow The horizontal grow priority or {@code null} for no horizontal grow priority constraint.
     * @param vgrow The vertical grow priority or {@code null} for no vertical grow priority constraint.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex, Priority hgrow, Priority vgrow) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, 1, 1, hgrow, vgrow);
    }

    /**
     * Set {@link GridPane} constraints, adding the {@link Node} to the {@link GridPane} if it is not already a child node.
     *
     * @param <T> The child node type.
     * @param child The child node.
     * @param gridPane The target {@link GridPane}.
     * @param columnIndex The zero-based column index.
     * @param rowIndex The zero-based row index.
     * @return The {@code child} {@link Node} constrained within the {@link GridPane}.
     */
    public static <T extends Node> T setGridPanePosition(T child, GridPane gridPane, int columnIndex, int rowIndex) {
        return setGridPanePosition(child, gridPane, columnIndex, rowIndex, 1, 1);
    }

    public static HBox createCompactHBox(double spacing, CssClassName className, Node... elements) {
        HBox hBox = createCompactHBox(spacing, elements);
        return addCssClass(hBox, className);
    }

    public static HBox createCompactHBox(double spacing, CssClassName[] className, Node... elements) {
        HBox hBox = createCompactHBox(spacing, elements);
        return addCssClass(hBox, className);
    }

    public static HBox createCompactHBox(double spacing, Node... elements) {
        HBox hBox = createCompactHBox(elements);
        hBox.setSpacing(spacing);
        return hBox;
    }

    public static HBox createCompactHBox(Node... elements) {
        HBox hBox = setCompactXY(new HBox());
        hBox.setFillHeight(false);
        return appendChildNodes(hBox, elements);
    }

    public static HBox createFillingHBox(double spacing, CssClassName className, Node... elements) {
        HBox hBox = createFillingHBox(spacing, elements);
        return addCssClass(hBox, className);
    }

    public static HBox createFillingHBox(double spacing, CssClassName[] className, Node... elements) {
        HBox hBox = createFillingHBox(spacing, elements);
        return addCssClass(hBox, className);
    }

    public static HBox createFillingHBox(double spacing, Node... elements) {
        HBox hBox = createFillingHBox(elements);
        hBox.setSpacing(spacing);
        return hBox;
    }

    public static HBox createFillingHBox(Node... elements) {
        HBox hBox = setMaxXY(new HBox());
        hBox.setFillHeight(true);
        return appendChildNodes(hBox, elements);
    }

    public static <T extends Node> T setHBoxGrow(T node, Priority priority) {
        HBox.setHgrow(node, priority);
        return node;
    }
    
    public static VBox createCompactVBox(double spacing, CssClassName className, Node... elements) {
        VBox vBox = createCompactVBox(spacing, elements);
        return addCssClass(vBox, className);
    }

    public static VBox createCompactVBox(double spacing, CssClassName[] className, Node... elements) {
        VBox vBox = createCompactVBox(spacing, elements);
        return addCssClass(vBox, className);
    }

    public static VBox createCompactVBox(double spacing, Node... elements) {
        VBox vBox = createCompactVBox(elements);
        vBox.setSpacing(spacing);
        return vBox;
    }

    public static VBox createCompactVBox(Node... elements) {
        VBox vBox = setCompactXY(new VBox());
        vBox.setFillWidth(false);
        return appendChildNodes(vBox, elements);
    }

    public static VBox createFillingVBox(double spacing, CssClassName className, Node... elements) {
        VBox vBox = createFillingVBox(spacing, elements);
        return addCssClass(vBox, className);
    }

    public static VBox createFillingVBox(double spacing, CssClassName[] className, Node... elements) {
        VBox vBox = createFillingVBox(spacing, elements);
        return addCssClass(vBox, className);
    }

    public static VBox createFillingVBox(double spacing, Node... elements) {
        VBox vBox = createFillingVBox(elements);
        vBox.setSpacing(spacing);
        return vBox;
    }

    public static VBox createFillingVBox(Node... elements) {
        VBox vBox = setMaxXY(new VBox());
        vBox.setFillWidth(true);
        return appendChildNodes(vBox, elements);
    }

    public static <T extends Node> T setVBoxGrow(T node, Priority priority) {
        VBox.setVgrow(node, priority);
        return node;
    }
    
    public static <T extends Node> T setGridVgrow(T node, Priority priority) {
        GridPane.setVgrow(node, priority);
        return node;
    }
    
    public static <T extends Node> T setGridHgrow(T node, Priority priority) {
        GridPane.setVgrow(node, priority);
        return node;
    }
    
    public static BorderPane createCompactBorderPane(Node top, Node center, Node bottom, CssClassName... className) {
        BorderPane result = setCompactXY(new BorderPane());
        if (null != top) {
            result.setTop(top);
        }
        if (null != center) {
            result.setCenter(center);
        }
        if (null != bottom) {
            result.setBottom(bottom);
        }
        if (null != className && className.length > 0) {
            addCssClass(result, className);
        }
        return result;
    }

    public static BorderPane createCompactBorderPane(Node top, Node center, CssClassName... className) {
        return createCompactBorderPane(top, center, null, className);
    }

    public static BorderPane createCompactBorderPane(Node center, CssClassName... className) {
        return createCompactBorderPane(null, center, null, className);
    }

    public static BorderPane createCompactBorderPane(CssClassName... className) {
        return createCompactBorderPane(null, null, null, className);
    }

    public static TextFlow createTextFlow(TextAlignment textAlignment, CssClassName[] className, Node... children) {
        TextFlow result = createTextFlow(className, children);
        if (null != textAlignment) {
            result.setTextAlignment(textAlignment);
        }
        return result;
    }

    public static TextFlow createTextFlow(TextAlignment textAlignment, CssClassName className, Node... children) {
        TextFlow result = createTextFlow(className, children);
        if (null != textAlignment) {
            result.setTextAlignment(textAlignment);
        }
        return result;
    }

    public static TextFlow createTextFlow(TextAlignment textAlignment, Node... children) {
        TextFlow result = createTextFlow(children);
        if (null != textAlignment) {
            result.setTextAlignment(textAlignment);
        }
        return result;
    }

    public static TextFlow createTextFlow(CssClassName className, Node... children) {
        TextFlow result = createTextFlow(children);
        return addCssClass(result, className);
    }

    public static TextFlow createTextFlow(CssClassName[] className, Node... children) {
        TextFlow result = createTextFlow(children);
        return addCssClass(result, className);
    }

    public static TextFlow createTextFlow(Node... children) {
        return (null == children) ? new TextFlow() : new TextFlow(children);
    }

    public static TextFlow createTextFlow(CssClassName... className) {
        TextFlow result = new TextFlow();
        return addCssClass(result, className);
    }

    public static Text createText(String content, double strokeWidth, StrokeType strokeType, CssClassName... className) {
        Text result = new Text(content);
        result.setStrokeType(strokeType);
        result.setStrokeWidth(strokeWidth);
        return addCssClass(result, className);
    }

    public static Text createText(String content, double strokeWidth, CssClassName... className) {
        return createText(content, strokeWidth, StrokeType.OUTSIDE, className);
    }

    public static Text createText(double strokeWidth, StrokeType strokeType, CssClassName... className) {
        Text result = new Text();
        result.setStrokeType(strokeType);
        result.setStrokeWidth(strokeWidth);
        return addCssClass(result, className);
    }

    public static Text createText(String content, CssClassName... className) {
        return createText(content, 0.0, StrokeType.OUTSIDE, className);
    }

    public static Text createText(double strokeWidth, CssClassName... className) {
        return createText(strokeWidth, StrokeType.OUTSIDE, className);
    }

    public static Text createText(CssClassName... className) {
        return createText(0.0, StrokeType.OUTSIDE, className);
    }

    public static Label createLabel(String text, boolean wrapText, ContentDisplay contentDisplay, CssClassName... className) {
        Label result = addCssClass(new Label((null == text) ? "" : text), className);
        result.setWrapText(wrapText);
        result.setContentDisplay(contentDisplay);
        return result;
    }

    public static Label createLabel(String text, boolean wrapText, CssClassName... className) {
        return createLabel(text, wrapText, ContentDisplay.TEXT_ONLY, className);
    }

    public static Label createLabel(boolean wrapText, ContentDisplay contentDisplay, CssClassName... className) {
        Label result = addCssClass(new Label(), className);
        result.setWrapText(wrapText);
        result.setContentDisplay(contentDisplay);
        return result;
    }

    public static Label createLabel(String text, CssClassName... className) {
        return createLabel(text, true, ContentDisplay.TEXT_ONLY, className);
    }

    public static Label createLabel(boolean wrapText, CssClassName... className) {
        return createLabel(wrapText, ContentDisplay.TEXT_ONLY, className);
    }

    public static Label createLabel(CssClassName... className) {
        return createLabel(true, ContentDisplay.TEXT_ONLY, className);
    }

    public static Hyperlink createHyperlink(String text, boolean wrapText, ContentDisplay contentDisplay, CssClassName... className) {
        Hyperlink result = addCssClass(new Hyperlink((null == text) ? "" : text), className);
        result.setWrapText(wrapText);
        result.setContentDisplay(contentDisplay);
        return result;
    }

    public static Hyperlink createHyperlink(String text, boolean wrapText, CssClassName... className) {
        return createHyperlink(text, wrapText, ContentDisplay.TEXT_ONLY, className);
    }

    public static Hyperlink createHyperlink(boolean wrapText, ContentDisplay contentDisplay, CssClassName... className) {
        Hyperlink result = addCssClass(new Hyperlink(), className);
        result.setWrapText(wrapText);
        result.setContentDisplay(contentDisplay);
        return result;
    }

    public static Hyperlink createHyperlink(String text, CssClassName... className) {
        return createHyperlink(text, true, ContentDisplay.TEXT_ONLY, className);
    }

    public static Hyperlink createHyperlink(boolean wrapText, CssClassName... className) {
        return createHyperlink(wrapText, ContentDisplay.TEXT_ONLY, className);
    }

    public static Hyperlink createHyperlink(CssClassName... className) {
        return createHyperlink(true, ContentDisplay.TEXT_ONLY, className);
    }

    public static <E> ListView<E> createListView(ObservableList<E> items, Callback<ListView<E>, ListCell<E>> cellFactory, CssClassName... className) {
        ListView<E> result = (null == items) ? new ListView<>() : new ListView<>(items);
        addCssClass(result, className);
        if (null != cellFactory) {
            result.setCellFactory(cellFactory);
        }
        return result;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <S> TableView<S> createTableView(ObservableList<S> items, String placeHolderText, TableColumn<S, ?>... column) {
        TableView<S> result = (null == items) ? new TableView<>() : new TableView<>(items);
        result.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        result.setEditable(false);
        if (null != placeHolderText) {
            result.setPlaceholder(createLabel(placeHolderText, CssClassName.INFO));
        }
        if (null != column && column.length > 0) {
            result.getColumns().addAll(column);
        }
        return result;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <S> TableView<S> createTableView(String placeHolderText, TableColumn<S, ?>... column) {
        return createTableView(null, placeHolderText, column);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <S> TableView<S> createTableView(TableColumn<S, ?>... column) {
        return createTableView(null, null, column);
    }

    public static <S, T> TableColumn<S, T> createTableColumn(String propertyName, String heading, Callback<TableColumn<S, T>, TableCell<S, T>> cellFactory) {
        TableColumn<S, T> result = new TableColumn<>(heading);
        result.setEditable(false);
        result.setMinWidth(USE_COMPUTED_SIZE);
        result.setPrefWidth(USE_COMPUTED_SIZE);
        result.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if (null != cellFactory) {
            result.setCellFactory(cellFactory);
        }
        return result;
    }

    public static <S, T> TableColumn<S, T> createTableColumn(String propertyName, String heading) {
        return createTableColumn(heading, propertyName, null);
    }

    public static Button createSymbolButton(SymbolText value, EventHandler<ActionEvent> onAction) {
        return createButton(value.toString(), onAction, CssClassName.SYMBOL_BUTTON);
    }

    public static Button createSymbolButton(SymbolText value) {
        return createButton(value.toString(), CssClassName.SYMBOL_BUTTON);
    }

    public static Button createButton(String text, boolean mnemonicParsing, EventHandler<ActionEvent> onAction, CssClassName... className) {
        Button button = createButton(text, mnemonicParsing, className);
        if (null != onAction) {
            button.setOnAction(onAction);
        }
        return button;
    }

    public static Button createButton(String text, EventHandler<ActionEvent> onAction, CssClassName... className) {
        return createButton(text, false, onAction, className);
    }

    public static Button createButton(String text, boolean mnemonicParsing, CssClassName... className) {
        Button button = addCssClass(new Button((null == text) ? "" : text), className);
        button.setMnemonicParsing(mnemonicParsing);
        return button;
    }

    public static Button createButton(boolean mnemonicParsing, EventHandler<ActionEvent> onAction, CssClassName... className) {
        Button button = createButton(mnemonicParsing, className);
        if (null != onAction) {
            button.setOnAction(onAction);
        }
        return button;
    }

    public static Button createButton(String text, CssClassName... className) {
        return createButton(text, false, className);
    }

    public static Button createButton(EventHandler<ActionEvent> onAction, CssClassName... className) {
        return createButton(false, onAction, className);
    }

    public static Button createButton(boolean mnemonicParsing, CssClassName... className) {
        Button button = addCssClass(new Button(), className);
        button.setMnemonicParsing(mnemonicParsing);
        return button;
    }

    public static Button createButton(CssClassName... className) {
        return createButton(false, className);
    }

    public static ButtonBar createButtonBar(Button... buttons) {
        ButtonBar result = new ButtonBar();
        if (null != buttons && buttons.length > 0) {
            ObservableList<Node> btnList = result.getButtons();
            btnList.addAll(Arrays.asList(buttons));
        }
        return result;
    }

    public static <T extends Node> T setBorderedNode(T node) {
        return addCssClass(node, CssClassName.BORDERED);
    }

    public static <T extends Labeled> T setLeftControlLabel(T node, String text) {
        setLeftControlLabel(node).setText(text);
        return node;
    }

    public static <T extends Labeled> T setLeftControlLabel(T node) {
        return addCssClass(node, CssClassName.LEFTCONTROLLABEL);
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
        return addCssClass(node, CssClassName.LEFTLABELEDCONTROL);
    }

    @SuppressWarnings("unused")
    private static <T extends Styleable> T setCollapsingValidationCss(T node, ValidationStatus status) {
        Optional<CssClassName> cssClass = status.getCssClass();
        if (cssClass.isPresent()) {
            CssClassName c = cssClass.get();
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> {
                    if (t != c) {
                        removeCssClass(node, t);
                    }
                });
            }
            removeCssClass(node, CssClassName.COLLAPSED);
            addCssClass(node, c);
            if (node instanceof Node) {
                Node n = (Node) node;
                if (!n.isVisible()) {
                    n.setVisible(true);
                }
            }
        } else {
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> removeCssClass(node, t));
            }
            addCssClass(node, CssClassName.COLLAPSED);
            if (node instanceof Node) {
                Node n = (Node) node;
                if (n.isVisible()) {
                    n.setVisible(false);
                }
            }
        }
        return node;
    }

    @SuppressWarnings("unused")
    private static <T extends Node> T setValidationCss(T node, ValidationStatus status, boolean hideIfOk) {
        Optional<CssClassName> cssClass = status.getCssClass();
        if (cssClass.isPresent()) {
            CssClassName c = cssClass.get();
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> {
                    if (t != c) {
                        removeCssClass(node, t);
                    }
                });
            }
            addCssClass(node, c);
            if (hideIfOk && !node.isVisible()) {
                node.setVisible(true);
            }
        } else {
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> removeCssClass(node, t));
            }
            if (hideIfOk && node.isVisible()) {
                node.setVisible(false);
            }
        }
        return node;
    }

    @SuppressWarnings("unused")
    private static <T extends Styleable> T setValidationCss(T node, ValidationStatus status) {
        Optional<CssClassName> cssClass = status.getCssClass();
        if (cssClass.isPresent()) {
            CssClassName c = cssClass.get();
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> {
                    if (t != c) {
                        removeCssClass(node, t);
                    }
                });
            }
            addCssClass(node, c);
        } else {
            for (ValidationStatus s : ValidationStatus.values()) {
                s.getCssClass().ifPresent((t) -> removeCssClass(node, t));
            }
        }
        return node;
    }

    /**
     * Collapses a JavaFX scene graph {@link javafx.scene.Node}. This adds the CSS class "collapsed" to the {@link javafx.scene.Node#styleClass} list, which sets vertical and
     * horizontal dimensions to zero and sets the {@link javafx.scene.Node#visible} property to {@code false}.
     *
     * @param <T> The type of {@link Node} to collapse.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be collapsed and hidden.
     * @return The collapsed {@link Node}.
     */
    public static <T extends Node> T collapseNode(T node) {
        LOG.entering(LOG.getName(), "collapseNode", node);
        T result = addCssClass(node, CssClassName.COLLAPSED);
        LOG.exiting(LOG.getName(), "collapseNode", result);
        return result;
    }

    public static void bindCssCollapse(Styleable target, BooleanBinding predicate) {
        final ObservableList<String> css = target.getStyleClass();
        final ObservableList<String> whenFalse = FXCollections.observableArrayList(css);
        final String n = CssClassName.COLLAPSED.toString();
        whenFalse.remove(n);
        if (whenFalse.isEmpty()) {
            predicate.addListener((observable, oldValue, newValue) -> {
                css.clear();
                if (newValue) {
                    css.add(n);
                }
            });
        } else {
            predicate.addListener((observable, oldValue, newValue) -> {
                css.clear();
                if (newValue) {
                    css.add(n);
                } else {
                    css.addAll(whenFalse);
                }
            });
        }
    }

    public static void bindCssCollapse(Styleable target, BooleanBinding predicate, CssClassName... whenFalse) {
        final ObservableList<String> targetCss = target.getStyleClass();
        final ObservableList<String> removed = FXCollections.observableArrayList(targetCss);
        final String n = CssClassName.COLLAPSED.toString();
        ChangeListener<Boolean> listener;
        if (null == whenFalse || whenFalse.length == 0) {
            listener = (observable, oldValue, newValue) -> {
                if (newValue) {
                    targetCss.forEach((t) -> {
                        if (!removed.contains(t)) {
                            removed.add(t);
                        }
                    });
                    targetCss.clear();
                    targetCss.add(n);
                } else {
                    targetCss.remove(n);
                    removed.forEach((t) -> {
                        if (!targetCss.contains(t)) {
                            targetCss.add(t);
                        }
                    });
                    removed.clear();
                }
            };
        } else {
            final ObservableList<String> css = FXCollections.observableArrayList();
            for (CssClassName c : whenFalse) {
                String s = c.toString();
                if (!css.contains(s)) {
                    css.add(s);
                }
            }
            listener = (observable, oldValue, newValue) -> {
                if (newValue) {
                    targetCss.forEach((t) -> {
                        if (!removed.contains(t)) {
                            removed.add(t);
                        }
                    });
                    targetCss.clear();
                    targetCss.add(n);
                } else {
                    targetCss.remove(n);
                    removed.forEach((t) -> {
                        if (!targetCss.contains(t)) {
                            targetCss.add(t);
                        }
                    });
                    removed.clear();
                    css.forEach((t) -> {
                        if (!targetCss.contains(t)) {
                            targetCss.add(t);
                        }
                    });
                }
            };
        }
        predicate.addListener(listener);
        boolean b = predicate.get();
        listener.changed(predicate, !b, b);
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
        LOG.entering(LOG.getName(), "collapseNode", node);
        removeCssClass(node, CssClassName.COLLAPSED);
        if (!node.isVisible()) {
            node.setVisible(true);
        }
        LOG.exiting(LOG.getName(), "collapseNode", node);
        return node;
    }

    /**
     * Restores the dimensions of a JavaFX scene graph {@link javafx.scene.Node} and ensures it's not visible. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list.
     *
     * @param <T> The type of {@link Node} to restore.
     * @param node The JavaFX scene graph {@link javafx.scene.Node} to be un-collapsed.
     * @return The restored {@link Node}.
     */
    public static <T extends Node> T restoreNodeAsNotVisible(T node) {
        LOG.entering(LOG.getName(), "restoreNodeAsNotVisible", node);
        removeCssClass(node, CssClassName.COLLAPSED);
        if (node.isVisible()) {
            node.setVisible(false);
        }
        LOG.exiting(LOG.getName(), "restoreNodeAsNotVisible", node);
        return node;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control and sets the {@link javafx.scene.control.Labeled#text} property. This removes
     * the CSS class "collapsed" from the {@link javafx.scene.Node#styleClass} list and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreLabeled(T control, String text) {
        LOG.entering(LOG.getName(), "restoreLabeled", new Object[]{control, text});
        removeCssClass(control, CssClassName.COLLAPSED);
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        LOG.exiting(LOG.getName(), "restoreLabeled", control);
        return control;
    }

    public static <T extends Labeled> T setErrorMessage(T control, String text) {
        LOG.entering(LOG.getName(), "setErrorMessage", new Object[]{control, text});
        CssClassName n = CssClassName.ERROR;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        addCssClass(control, n);
        control.setText(text);
        LOG.exiting(LOG.getName(), "setErrorMessage", control);
        return control;
    }

    public static <T extends Labeled> T setWarningMessage(T control, String text) {
        LOG.entering(LOG.getName(), "setWarningMessage", new Object[]{control, text});
        CssClassName n = CssClassName.WARNING;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        addCssClass(control, n);
        control.setText(text);
        LOG.exiting(LOG.getName(), "setWarningMessage", control);
        return control;
    }

    public static <T extends Labeled> T setInfoMessage(T control, String text) {
        LOG.entering(LOG.getName(), "setInfoMessage", new Object[]{control, text});
        CssClassName n = CssClassName.INFO;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        addCssClass(control, n);
        control.setText(text);
        LOG.exiting(LOG.getName(), "setInfoMessage", control);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link CssClassName#ERROR}. This removes the CSS class "collapsed" from
     * the {@link javafx.scene.Node#styleClass} list, adds the {@code "error"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreErrorLabeled(T control, String text) {
        LOG.entering(LOG.getName(), "restoreErrorLabeled", new Object[]{control, text});
        CssClassName n = CssClassName.ERROR;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        removeCssClass(control, CssClassName.COLLAPSED);
        addCssClass(control, n);
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        LOG.exiting(LOG.getName(), "restoreErrorLabeled", control);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link CssClassName#WARNING}. This removes the CSS class "collapsed" from
     * the {@link javafx.scene.Node#styleClass} list, adds the {@code "warning"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreWarningLabeled(T control, String text) {
        LOG.entering(LOG.getName(), "restoreWarningLabeled", new Object[]{control, text});
        CssClassName n = CssClassName.WARNING;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        removeCssClass(control, CssClassName.COLLAPSED);
        addCssClass(control, n);
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        LOG.exiting(LOG.getName(), "restoreWarningLabeled", control);
        return control;
    }

    /**
     * Restores the visibility and dimensions of a JavaFX {@link javafx.scene.control.Labeled} control as {@link CssClassName#INFO}. This removes the CSS class "collapsed" from the
     * {@link javafx.scene.Node#styleClass} list, adds the {@code "information"} class and sets the {@link javafx.scene.control.Labeled#text} property.
     *
     * @param <T> The type of {@link Labeled} control to restore.
     * @param control The JavaFX scene graph {@link javafx.scene.control.Labeled} control to be un-collapsed.
     * @param text The text to apply to the {@link javafx.scene.control.Labeled} control.
     * @return The restored {@link Labeled} control.
     */
    public static <T extends Labeled> T restoreInfoLabeled(T control, String text) {
        LOG.entering(LOG.getName(), "restoreInfoLabeled", new Object[]{control, text});
        CssClassName n = CssClassName.INFO;
        for (ValidationStatus s : ValidationStatus.values()) {
            s.getCssClass().ifPresent((t) -> {
                if (t != n) {
                    removeCssClass(control, t);
                }
            });
        }
        removeCssClass(control, CssClassName.COLLAPSED);
        addCssClass(control, n);
        if (!control.isVisible()) {
            control.setVisible(true);
        }
        control.setText(text);
        LOG.exiting(LOG.getName(), "restoreInfoLabeled", control);
        return control;
    }

    public static <T> boolean clearAndSelect(ListView<T> listView, Predicate<T> predicate) {
        ObservableList<T> items = listView.getItems();
        if (!(null == items || items.isEmpty())) {
            MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
            for (int i = 0; i < items.size(); i++) {
                if (predicate.test(items.get(i))) {
                    selectionModel.clearAndSelect(i);
                    for (int n = i + i; n < items.size(); i++) {
                        if (predicate.test(items.get(n))) {
                            selectionModel.select(n);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean clearAndSelect(ComboBox<T> comboBox, Predicate<T> predicate) {
        ObservableList<T> items = comboBox.getItems();
        if (!(null == items || items.isEmpty())) {
            SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
            for (int i = 0; i < items.size(); i++) {
                if (predicate.test(items.get(i))) {
                    selectionModel.clearAndSelect(i);
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean clearAndSelectFirst(ListView<T> listView, Predicate<T> predicate) {
        ObservableList<T> items = listView.getItems();
        if (!(null == items || items.isEmpty())) {
            MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
            for (int i = 0; i < items.size(); i++) {
                if (predicate.test(items.get(i))) {
                    selectionModel.clearAndSelect(i);
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean clearAndSelect(TableView<? extends T> tableView, T item) {
        MultipleSelectionModel<? extends T> selectionModel = tableView.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else {
            ObservableList<? extends T> items = tableView.getItems();
            if (!(null == items || items.isEmpty())) {
                for (int i = 0; i < items.size(); i++) {
                    if (item.equals(items.get(i))) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T> boolean clearAndSelect(ListView<? extends T> listView, T item) {
        MultipleSelectionModel<? extends T> selectionModel = listView.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else {
            ObservableList<? extends T> items = listView.getItems();
            if (!(null == items || items.isEmpty())) {
                for (int i = 0; i < items.size(); i++) {
                    if (item.equals(items.get(i))) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T> boolean clearAndSelect(ComboBox<? extends T> comboBox, T item) {
        SelectionModel<? extends T> selectionModel = comboBox.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else {
            ObservableList<? extends T> items = comboBox.getItems();
            if (!(null == items || items.isEmpty())) {
                for (int i = 0; i < items.size(); i++) {
                    if (item.equals(items.get(i))) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T extends PartialDataEntity> boolean clearAndSelectEntity(TableView<? extends T> tableView, T item) {
        MultipleSelectionModel<? extends T> selectionModel = tableView.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else if (item.getRowState() != DataRowState.NEW) {
            ObservableList<? extends T> items = tableView.getItems();
            if (!(null == items || items.isEmpty())) {
                int pk = item.getPrimaryKey();
                for (int i = 0; i < items.size(); i++) {
                    T t = items.get(i);
                    if (null != t && t.getPrimaryKey() == pk) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        } else {
            return clearAndSelect(tableView, item);
        }
        return false;
    }

    public static <T extends PartialDataEntity> boolean clearAndSelectEntity(ListView<? extends T> listView, T item) {
        MultipleSelectionModel<? extends T> selectionModel = listView.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else if (item.getRowState() != DataRowState.NEW) {
            ObservableList<? extends T> items = listView.getItems();
            if (!(null == items || items.isEmpty())) {
                int pk = item.getPrimaryKey();
                for (int i = 0; i < items.size(); i++) {
                    T t = items.get(i);
                    if (null != t && t.getPrimaryKey() == pk) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        } else {
            return clearAndSelect(listView, item);
        }
        return false;
    }

    public static <T extends PartialDataEntity> boolean clearAndSelectEntity(ComboBox<? extends T> comboBox, T item) {
        SelectionModel<? extends T> selectionModel = comboBox.getSelectionModel();
        if (null == item) {
            if (selectionModel.getSelectedIndex() >= 0) {
                selectionModel.clearSelection();
                return true;
            }
        } else if (item.getRowState() != DataRowState.NEW) {
            ObservableList<? extends T> items = comboBox.getItems();
            if (!(null == items || items.isEmpty())) {
                int pk = item.getPrimaryKey();
                for (int i = 0; i < items.size(); i++) {
                    T t = items.get(i);
                    if (null != t && t.getPrimaryKey() == pk) {
                        selectionModel.clearAndSelect(i);
                        return true;
                    }
                }
            }
        } else {
            return clearAndSelect(comboBox, item);
        }
        return false;
    }

    private NodeUtil() {
    }

}
