package scheduler.dao.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import static scheduler.AppResourceBundleConstants.RESOURCEKEY_LOADINGAPPOINTMENTS;
import scheduler.AppResources;
import scheduler.dao.AppointmentDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface AppointmentFilter extends DaoFilter<AppointmentDAO> {

    DaoFilterExpression<AppointmentDAO> getExpression();
    
    @Override
    public default String getLoadingMessage() {
        return AppResources.getResourceString(RESOURCEKEY_LOADINGAPPOINTMENTS);
    }

    public static AppointmentFilter of(DaoFilterExpression<AppointmentDAO> expr) {
        return new AppointmentFilter() {

            @Override
            public void appendSimpleDmlConditional(StringBuffer sb) {
                expr.appendSimpleDmlConditional(sb);
            }

            @Override
            public void appendJoinedDmlConditional(StringBuffer sb) {
                expr.appendJoinedDmlConditional(sb);
            }
            
            @Override
            public DaoFilterExpression<AppointmentDAO> getExpression() {
                return expr;
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) throws SQLException {
                return expr.applyWhereParameters(ps, index);
            }

            @Override
            public boolean test(AppointmentDAO t) {
                return expr.test(t);
            }

            @Override
            public boolean equals(Object obj) {
                if (null != obj && obj instanceof AppointmentFilter) {
                    AppointmentFilter other = (AppointmentFilter)obj;
                    return other.getLoadingMessage().equals(getLoadingMessage()) && other.getLoadingTitle().equals(getLoadingTitle()) &&
                            other.getExpression().equals(expr);
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
