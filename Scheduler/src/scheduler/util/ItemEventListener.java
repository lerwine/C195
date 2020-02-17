/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

/**
 *
 * @author lerwi
 * @param <T>
 */
@FunctionalInterface
public interface ItemEventListener<T extends ItemEvent<?>> extends java.util.EventListener {
    void handle(T event);
}
