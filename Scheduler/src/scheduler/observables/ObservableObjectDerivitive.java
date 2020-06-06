package scheduler.observables;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import scheduler.util.QuadFunction;
import scheduler.util.TriFunction;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <R> The result (derived) value type.
 * @deprecated
 */
@Deprecated
public interface ObservableObjectDerivitive<R> extends ObservableDerivitive<R>, ObservableObjectValue<R> {

    public static <T, U, S, V, R> ObservableObjectDerivitive<R> of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, ObservableValue<V> s4, QuadFunction<T, U, S, V, R> calculate) {
        return new DerivedObservableObject<>(new ObservableQuadruplet<>(s1, s2, s3, s4), (t) -> calculate.apply(t.getValue1(), t.getValue2(), t.getValue3(), t.getValue4()));
    }

    public static <T, U, S, R> ObservableObjectDerivitive<R> of(ObservableValue<T> s1, ObservableValue<U> s2, ObservableValue<S> s3, TriFunction<T, U, S, R> calculate) {
        return new DerivedObservableObject<>(new ObservableTriplet<>(s1, s2, s3), (t) -> calculate.apply(t.getValue1(), t.getValue2(), t.getValue3()));
    }

    public static <T, U, R> ObservableObjectDerivitive<R> of(ObservableValue<T> s1, ObservableValue<U> s2, BiFunction<T, U, R> calculate) {
        return new DerivedObservableObject<>(new ObservableTuple<>(s1, s2), (t) -> calculate.apply(t.getValue1(), t.getValue2()));
    }

    public static <T, R> ObservableObjectDerivitive<R> of(ObservableValue<T> source, Function<T, R> calculate) {
        return new DerivedObservableObject<>(source, calculate);
    }

    public static <T, R> ObservableObjectDerivitive<R> ofNested(ObservableValue<T> source, Function<T, ObservableValue<R>> selector) {
        return new NestedObjectDerivation<>(source, selector);
    }

    public static <T> ObservableObjectDerivitive<T> ofSelection(ObservableValue<? extends SelectionModel<T>> selectionModel) {
        return new NestedObjectDerivation<>(selectionModel, (t) -> t.selectedItemProperty());
    }

    public static <T> ObservableObjectDerivitive<T> ofSelection(ComboBox<T> comboBox) {
        return ofSelection(comboBox.selectionModelProperty());
    }

    public static <T> ObservableObjectDerivitive<T> ofSelection(TableView<T> tableView) {
        return ofSelection(tableView.selectionModelProperty());
    }

    default DerivedObservableBoolean isNull() {
        return new DerivedObservableBoolean<>(this, (t) -> null == t);
    }

    default DerivedObservableBoolean isNotNull() {
        return new DerivedObservableBoolean<>(this, (t) -> null != t);
    }

    default ObjectExpression<R> asObjectExpression() {
        return new ObjectExpression<R>() {

            private ExpressionHelper<R> helper = null;

            {
                ObservableObjectDerivitive.this.addListener((observable) -> ExpressionHelper.fireValueChangedEvent(helper));
            }

            @Override
            public R get() {
                return ObservableObjectDerivitive.this.get();
            }

            @Override
            public void addListener(ChangeListener<? super R> listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(ChangeListener<? super R> listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

            @Override
            public void addListener(InvalidationListener listener) {
                helper = ExpressionHelper.addListener(helper, this, listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                helper = ExpressionHelper.removeListener(helper, listener);
            }

        };
    }

}
