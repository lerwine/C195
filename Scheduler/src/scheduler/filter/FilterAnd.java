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
public class FilterAnd<M extends ItemModel<?>> extends TreeSet<ModelFilter<M>> implements ModelFilter<M> {
    /**
     * SQL operator for compound "AND" statements
     */
    public static final String LOGICAL_OPERATOR_AND = "AND";

    public static <T extends ItemModel<?>> ModelFilter<T> combine(ModelFilter<T> x, ModelFilter<T> y) {
        if (null == x)
            return (y == null) ? ModelFilter.empty() : y;
        if (null == y || (y.isEmpty() && y.getOperator().isEmpty()))
            return x;
        if (x.isEmpty() && x.getOperator().isEmpty())
            return y;
        if (x instanceof FilterAnd)
            return ((FilterAnd<T>)x).and(y);
        return new FilterAnd<>(x, y);
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

    private FilterAnd() { }
    
    public FilterAnd(ModelFilter<M> x, ModelFilter<M> y) {
        if (Objects.requireNonNull(x, "Logically joined filter cannot be null") instanceof FilterAnd)
            ((FilterAnd<M>)x).stream().forEach((t) -> super.add(t.makeClone()));
        else if (!(x.isEmpty() && x.getOperator().isEmpty()))
            super.add(x);
        if (Objects.requireNonNull(y, "Logically joined filter cannot be null") instanceof FilterAnd)
            ((FilterAnd<M>)y).stream().forEach((t) -> super.add(t.makeClone()));
        else if (!(y.isEmpty() && y.getOperator().isEmpty()))
            super.add(y);
    }

    public FilterAnd(Collection<? extends ModelFilter<M>> c) {
        if (null == c || c.isEmpty())
            return;
        c.stream().filter((t) -> null != t).forEach((t) -> {
            if (t instanceof FilterAnd)
                ((FilterAnd<M>)t).stream().forEach((n) -> super.add(n.makeClone()));
            else
                super.add(t);
        });
    }

    public FilterAnd(SortedSet<ModelFilter<M>> s) {
        if (null == s || s.isEmpty())
            return;
        s.stream().filter((t) -> null != t).forEach((t) -> {
            if (t instanceof FilterAnd)
                ((FilterAnd<M>)t).stream().forEach((n) -> super.add(n.makeClone()));
            else
                super.add(t);
        });
    }

    @Override
    public boolean add(ModelFilter<M> e) {
        if (Objects.requireNonNull(e, "Logically joined filter cannot be null") instanceof FilterAnd) {
            if (e == this)
                return false;
            ((FilterAnd<M>)e).stream().forEach((n) -> super.add(n.makeClone()));
            return true;
        }
        if (e.isEmpty() && e.getOperator().isEmpty())
            return false;
        return super.add(e);
    }

    @Override
    public ModelFilter<M> and(ModelFilter<M> other) {
        if (null != other && !(other.isEmpty() && other.getOperator().isEmpty()))
            add(other);
        return this;
    }

    @Override
    public String getOperator() { return LOGICAL_OPERATOR_AND; }

    @Override
    public boolean isCompound() { return true; }

    @Override
    public ModelFilter<M> makeClone() {
        FilterAnd result = new FilterAnd();
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
                result.append(" ").append(LOGICAL_OPERATOR_AND).append(" (").append(f.toString()).append(")");
            else
                result.append(" ").append(LOGICAL_OPERATOR_AND).append(" ").append(f.toString());
        } while (it.hasNext());
        return new SqlConditional() {
            private final String string = result.toString();
            @Override
            public boolean isCompound() { return true; }
            @Override
            public boolean isEmpty() { return false; }
            @Override
            public String get(){ return string; }
        };
    }
    
    @Override
    public boolean test(M t) {
        if (t == null)
            return false;
        
        Iterator<ModelFilter<M>> it = iterator();
        if (!it.hasNext())
            return false;
        do {
            if (!it.next().test(t))
                return false;
        } while (it.hasNext());
        return true;
    }

    @Override
    public void setParameterValues(ParameterConsumer consumer) throws SQLException {
        Iterator<ModelFilter<M>> it = iterator();
        while (it.hasNext())
            it.next().setParameterValues(consumer);
    }

    @Override
    public String get() { return toConditional().get(); }

    @Override
    public String getColName() { return ""; }
    
}
