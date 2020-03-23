/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

/**
 *
 * @author lerwi
 */
@FunctionalInterface
public interface BiIntPredicate {
    boolean test(int t, int u);
}
