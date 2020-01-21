package scheduler.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import view.ItemModel;

/**
 *
 * @author erwinel
 * @param <M>
 */
public class FilterOr<M extends ItemModel<?>> extends TreeSet<ModelFilter<M>> implements ModelFilter<M> {

    /**
     * SQL operator for compound "OR" statements
     */
    public static final String LOGICAL_OPERATOR_OR = "OR";

    public static <T extends ItemModel<?>> ModelFilter<T> combine(ModelFilter<T> x, ModelFilter<T> y) {
        if (null == x)
            return (y == null) ? ModelFilter.empty() : y;
        if (null == y || (y.isEmpty() && y.getOperator().isEmpty()))
            return x;
        if (x.isEmpty() && x.getOperator().isEmpty())
            return y;
        if (x instanceof FilterOr)
            return ((FilterOr<T>)x).or(y);
        return new FilterOr<>(x, y);
    }

    public static <T extends ItemModel<?>> ModelFilter<T> join(ModelFilter<T> ...items) {
        if (null == items || items.length == 0)
            return ModelFilter.empty();
            
        Iterator<ModelFilter<T>> it = Arrays.asList(items).stream().filter((i) -> null != i && !(i.isEmpty() && i.getOperator().isEmpty())).iterator();
        if (!it.hasNext())
            return ModelFilter.empty();
        ModelFilter<T> f = it.next();
        while (it.hasNext())
            f = f.or(it.next());
        return f;
    }
    
    private FilterOr() { }
    
    public FilterOr(ModelFilter<M> x, ModelFilter<M> y) {
        if (Objects.requireNonNull(x, "Logically joined filter cannot be null") instanceof FilterOr)
            ((FilterOr<M>)x).stream().forEach((t) -> super.add(t.makeClone()));
        else if (!(x.isEmpty() && x.getOperator().isEmpty()))
            super.add(x);
        if (Objects.requireNonNull(y, "Logically joined filter cannot be null") instanceof FilterOr)
            ((FilterOr<M>)y).stream().forEach((t) -> super.add(t.makeClone()));
        else if (!(y.isEmpty() && y.getOperator().isEmpty()))
            super.add(y);
    }

    public FilterOr(Collection<? extends ModelFilter<M>> c) {
        if (null == c || c.isEmpty())
            return;
        c.stream().filter((t) -> null != t).forEach((t) -> {
            if (t instanceof FilterOr)
                ((FilterOr<M>)t).stream().forEach((n) -> super.add(n.makeClone()));
            else
                super.add(t);
        });
    }

    public FilterOr(SortedSet<ModelFilter<M>> s) {
        if (null == s || s.isEmpty())
            return;
        s.stream().filter((t) -> null != t).forEach((t) -> {
            if (t instanceof FilterOr)
                ((FilterOr<M>)t).stream().forEach((n) -> super.add(n.makeClone()));
            else
                super.add(t);
        });
    }

    @Override
    public boolean add(ModelFilter<M> e) {
        if (Objects.requireNonNull(e, "Logically joined filter cannot be null") instanceof FilterOr) {
            if (e == this)
                return false;
            ((FilterOr<M>)e).stream().forEach((n) -> super.add(n.makeClone()));
            return true;
        }
        if (e.isEmpty() && e.getOperator().isEmpty())
            return false;
        return super.add(e);
    }

    @Override
    public ModelFilter<M> or(ModelFilter<M> other) {
        if (null != other && !(other.isEmpty() && other.getOperator().isEmpty()))
            add(other);
        return this;
    }

    @Override
    public String getOperator() { return LOGICAL_OPERATOR_OR; }

    @Override
    public void setParameterValues(ParameterConsumer consumer) throws SQLException {
        Iterator<ModelFilter<M>> it = iterator();
        while (it.hasNext())
            it.next().setParameterValues(consumer);
    }

    @Override
    public boolean isCompound() { return true; }

    @Override
    public ModelFilter<M> makeClone() {
        FilterOr result = new FilterOr();
        Iterator<ModelFilter<M>> it = iterator();
        while (it.hasNext())
            result.add(it.next().makeClone());
        return result;
    }

    @Override
    public SqlConditional toConditional() {
        Iterator<SqlConditional> it = stream().map((t) -> t.toConditional()).filter((t) -> !t.isEmpty()).iterator();
        if (!it.hasNext())
            return ModelFilter.empty();
        SqlConditional f = it.next();
        if (!it.hasNext())
            return f;
        StringBuilder result = new StringBuilder();
        if (f.isCompound())
            result.append("(").append(f.toString()).append(")");
        do {
            f = it.next();
            if (f.isCompound())
                result.append(" ").append(LOGICAL_OPERATOR_OR).append(" (").append(f.toString()).append(")");
            else
                result.append(" ").append(LOGICAL_OPERATOR_OR).append(" ").append(f.toString());
        } while (it.hasNext());
        return new SqlConditional() {
            private final String string = result.toString();
            @Override
            public boolean isCompound() { return true; }
            @Override
            public boolean isEmpty() { return false; }
            @Override
            public String get() { return string; }
        };
    }

    @Override
    public boolean test(M t) {
        if (t == null)
            return false;
        
        Iterator<ModelFilter<M>> it = iterator();
        while (it.hasNext()) {
            if (it.next().test(t))
                return true;
        }
        return false;
    }

    @Override
    public String get() { return toConditional().get(); }
    
    @Override
    public String getColName() { return ""; }
    
}
