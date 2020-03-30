/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.PreparedStatement;
import java.util.function.Predicate;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface DaoFilter<T extends DataObjectImpl> extends Predicate<T> {
    String getLoadingTitle();
    String getLoadingMessage();
    void appendWhereClause(StringBuilder sb);
    int applyWhereParameters(PreparedStatement ps, int index);
    
    public static <T extends DataObjectImpl> DaoFilter<T> all(String loadingTitle, String loadingMessage) {
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
            public void appendWhereClause(StringBuilder sb) {
            }

            @Override
            public int applyWhereParameters(PreparedStatement ps, int index) {
                return index;
            }

            @Override
            public boolean test(T t) {
                return true;
            }
            
        };
    }
}
