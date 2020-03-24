/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import scheduler.view.ItemModel;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 * Compares {@link DataObjectImpl} and {@link ItemModel} to string values.
 *
 * @param <T> The type of {@link DataObjectImpl} object.
 * @param <U> The type of {@link ItemModel} object.
 * @author erwinel
 */
public interface ItemStringComparer<T extends DataObjectImpl, U extends ItemModel<T>> extends ItemValueComparer<T, U, String> {

    public static final ItemStringComparer<CustomerImpl, CustomerModel> CUSTOMER_NAME = new ItemStringComparer<CustomerImpl, CustomerModel>() {
        @Override
        public String get(CustomerImpl dao) {
            return dao.getName();
        }

        @Override
        public String get(CustomerModel model) {
            return model.getName();
        }
    };
    
    public static ItemStringComparer<UserImpl, UserModel> USER_NAME = new ItemStringComparer<UserImpl, UserModel>() {
        @Override
        public String get(UserImpl dao) {
            return dao.getUserName();
        }

        @Override
        public String get(UserModel model) {
            return model.getUserName();
        }
    };
    
    /**
     * Compares the value associated with a {@link DataObjectImpl} object with another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link DataObjectImpl} object is equal to {@code value}. If the value associated with a {@link DataObjectImpl} object is
     * less than {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link DataObjectImpl} object is greater than
     * {@code value}.
     */
    default int compareTo(T dao, String value) {
        String t = get(dao);
        return (null == t) ? ((null == value) ? 0 : -1) : ((null != value) ? t.compareTo(value) : 1);
    }

    /**
     * Compares the value associated with a {@link DataObjectImpl} object with another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code 0} if the value associated with a {@link ItemModel} object is equal to {@code value}. If the value associated with a {@link ItemModel} object is less than
     * {@code value}, a negative value is returned; otherwise a positive value indicates that the value associated with a {@link ItemModel} object is greater than {@code value}.
     */
    default int compareTo(U model, String value) {
        String t = get(model);
        return (null == t) ? ((null == value) ? 0 : -1) : ((null != value) ? t.compareTo(value) : 1);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object is equal to another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object is starts with {@code value}; otherwise, {@code false}.
     */
    default boolean startsWith(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object starts with another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object starts with {@code value}; otherwise, {@code false}.
     */
    default boolean startsWith(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object ends with another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object ends with {@code value}; otherwise, {@code false}.
     */
    default boolean endsWith(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.endsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object ends with another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object ends with {@code value}; otherwise, {@code false}.
     */
    default boolean endsWith(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.startsWith(s);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object contains another value.
     *
     * @param dao The target {@link DataObjectImpl}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object contains {@code value}; otherwise, {@code false}.
     */
    default boolean contains(T dao, String value) {
        String s = get(dao);
        return (null == value) ? null == s : null != s && value.contains(s);
    }

    /**
     * Tests whether the value associated with a {@link DataObjectImpl} object contains another value.
     *
     * @param model The target {@link ItemModel}.
     * @param value The value to compare.
     * @return {@code true} if the value associated with a {@link DataObjectImpl} object contains {@code value}; otherwise, {@code false}.
     */
    default boolean contains(U model, String value) {
        String s = get(model);
        return (null == value) ? null == s : null != s && value.contains(s);
    }

}
