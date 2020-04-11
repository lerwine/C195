package scheduler.dao.filter;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import scheduler.dao.DataAccessObject;
import scheduler.dao.LoadingMessageProvider;

/**
 * Data access object filter.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of {@link DataAccessObject}.
 */
public interface DaoFilter<T extends DataAccessObject> extends DaoFilterExpression<T>, LoadingMessageProvider {

    public static <T extends DataAccessObject> DaoFilter<T> all(String loadingTitle, String loadingMessage) {
        return new DaoFilter<T>() {
            @Override
            public String getLoadingTitle() {
                return loadingTitle;
            }

            @Override
            public String getLoadingMessage() {
                return loadingMessage;
            }

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) {
                return index;
            }

            @Override
            public boolean test(T t) {
                return true;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

        };
    }

    public static <T extends DataAccessObject> DaoFilter<T> of(String loadingMessage, DaoFilterExpression<T> expr) {
        return new DaoFilter<T>() {
            private final DaoFilterExpression<T> expression = Objects.requireNonNull(expr);

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                expr.appendSimpleDmlConditional(sb);
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                expr.appendJoinedDmlConditional(sb);
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return expr.applyWhereParameters(ps, index);
            }

            @Override
            public boolean test(T t) {
                return expr.test(t);
            }

            @Override
            public String getLoadingMessage() {
                return loadingMessage;
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof DaoFilter) {
                    DaoFilter<T> other = (DaoFilter<T>) obj;
                    if (other.getLoadingMessage().equals(getLoadingMessage()) && other.getLoadingTitle().equals(getLoadingTitle())) {
                        Class<DaoFilterExpression> c = DaoFilterExpression.class;
                        Optional<Field> field = Arrays.stream(other.getClass().getDeclaredFields()).filter((Field t) -> {
                            return t.getName().equals("expression") && t.getType().equals(c);
                        }).findFirst();
                        if (field.isPresent()) {
                            try {
                                DaoFilterExpression<T> e = (DaoFilterExpression<T>) field.get().get(obj);
                                return expression.equals(e);
                            } catch (IllegalArgumentException | IllegalAccessException ex) {
                                return false;
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 29 * hash + Objects.hashCode(getLoadingMessage());
                hash = 29 * hash + Objects.hashCode(getLoadingTitle());
                hash = 29 * hash + Objects.hashCode(expr);
                return hash;
            }

        };
    }
}
