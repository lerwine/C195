/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.util.function.Supplier;

/**
 *
 * @author lerwi
 */
public interface SelectStatementColumn extends Supplier<String> {
    default String getAlias() { return get(); }
    public static boolean areEqual(SelectStatementColumn a, SelectStatementColumn b) {
        if (null == a)
            return null == b;
        return null != b && a.get().equals(b.get()) && a.getAlias().equals(b.getAlias());
    }
}
