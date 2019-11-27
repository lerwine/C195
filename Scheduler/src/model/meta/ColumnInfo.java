/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.meta;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import model.annotations.DbColumn;
import model.annotations.FieldName;
import model.annotations.PrimaryKey;
import model.db.DataRow;
import utils.InternalException;

/**
 *
 * @author Leonard T. Erwine
 */
public class ColumnInfo {

    private final Field field;

    //<editor-fold defaultstate="collapsed" desc="fieldName">
    private final String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="colName">
    private final String colName;

    public String getColName() {
        return colName;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="propertyName">
    private final String propertyName;

    public String getPropertyName() {
        return propertyName;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="optional">
    private final boolean optional;

    public boolean isOptional() {
        return optional;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="baseField">
    private final boolean baseField;

    public boolean isBaseField() {
        return baseField;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="primaryKey">
    private final boolean primaryKey;

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="importType">
    private final SqlImportTypes importType;

    public SqlImportTypes getImportType() {
        return importType;
    }

    //</editor-fold>
    static boolean isBaseFieldName(String name) {
        return name != null && (name.equals(model.db.DataRow.PROP_PRIMARYKEY) || name.equals(model.db.DataRow.PROP_CREATEDATE)
                || name.equals(model.db.DataRow.PROP_CREATEDBY) || name.equals(model.db.DataRow.PROP_LASTUPDATE)
                || name.equals(model.db.DataRow.PROP_LASTUPDATEBY));
    }

    public static Stream<ColumnInfo> getColumnInfo(Class<? extends DataRow> rowClass) {
        if (!rowClass.isAnnotationPresent(PrimaryKey.class)) {
            throw new InternalException(String.format("Class {0} does not have the {1} annotation",
                    rowClass.getName(), PrimaryKey.class.getName()));
        }
        String primaryKeyColName = rowClass.getAnnotation(PrimaryKey.class).value();
        if (primaryKeyColName == null || primaryKeyColName.trim().isEmpty()) {
            throw new InternalException(String.format("Class {0} has an empty {1} annotation",
                    rowClass.getName(), PrimaryKey.class.getName()));
        }

        Class<DbColumn> dbColumnAnnotation = DbColumn.class;
        return Arrays.stream(rowClass.getFields()).filter((Field f) -> f.isAnnotationPresent(dbColumnAnnotation)).map((Field f) -> {
            boolean resetAccessible = !f.isAccessible();
            String n;
            if (resetAccessible) {
                f.setAccessible(true);
            }
            try {
                    n = f.getName();
                    if (n.equals(model.db.DataRow.PROP_PRIMARYKEY)) {
                        n = primaryKeyColName;
                    } else {
                        String v = f.getAnnotation(DbColumn.class).value();
                        if (v != null && !v.trim().isEmpty()) {
                            n = v;
                        }
                    }
                
            } finally {
                if (resetAccessible) {
                    f.setAccessible(false);
                }
            }
                    return new ColumnInfo(f, n);
        });
    }

    private ColumnInfo(Field f, String dbColName) {
        field = f;
        fieldName = field.getName();
        if (field.isAnnotationPresent(FieldName.class)) {
            String fn = field.getAnnotation(FieldName.class).value();
            propertyName = (fn == null || fn.trim().isEmpty()) ? fieldName : fn;
        } else {
            propertyName = fieldName;
        }
        primaryKey = fieldName.equals(model.db.DataRow.PROP_PRIMARYKEY);
        baseField = primaryKey || fieldName.equals(model.db.DataRow.PROP_CREATEDATE) || fieldName.equals(model.db.DataRow.PROP_CREATEDBY)
                || fieldName.equals(model.db.DataRow.PROP_LASTUPDATE) || fieldName.equals(model.db.DataRow.PROP_LASTUPDATEBY);
        if (baseField) {
            if (!(primaryKey || propertyName.equals(field.getName()))) {
                throw new InternalException("Incorrect assignment of base column field name");
            }
            colName = propertyName;
        } else {
            if (isBaseFieldName(propertyName)) {
                throw new InternalException("Incorrect assignment of base property name");
            }
            colName = (dbColName == null || dbColName.isEmpty()) ? field.getName() : dbColName;
            if (isBaseFieldName(colName)) {
                throw new InternalException("Incorrect assignment of base column name");
            }
        }
        Class<?> t = field.getType();
        optional = (Optional.class.isAssignableFrom(t));
        if (optional) {
            t = t.getTypeParameters()[0].getGenericDeclaration();
        }

        if (String.class.isAssignableFrom(t)) {
            importType = SqlImportTypes.STRING;
        } else if (Integer.class.isAssignableFrom(t)) {
            importType = SqlImportTypes.INTEGER;
        } else if (Boolean.class.isAssignableFrom(t)) {
            importType = SqlImportTypes.BOOLEAN;
        } else if (LocalDateTime.class.isAssignableFrom(t)) {
            importType = SqlImportTypes.DATETIME;
        } else // If this occurs, then it is a software bug.
        {
            throw new InternalException("Unsupported field type: " + field.getType().getTypeName());
        }
    }

    public void apply(ResultSet resultSet, DataRow row, PropertyChangeSupport propertyChangeSupport) throws SQLException, IllegalAccessException {
        Object newValue;

        switch (importType) {
            case INTEGER:
                Integer i = resultSet.getInt(colName);
                if (optional) {
                    if (resultSet.wasNull()) {
                        newValue = Optional.empty();
                    } else {
                        newValue = Optional.of(i);
                    }
                } else {
                    newValue = i;
                }
                break;
            case BOOLEAN:
                Boolean b = resultSet.getBoolean(colName);
                if (optional) {
                    if (resultSet.wasNull()) {
                        newValue = Optional.empty();
                    } else {
                        newValue = Optional.of(b);
                    }
                } else {
                    newValue = b;
                }
                break;
            case DATETIME:
                LocalDateTime t = resultSet.getTimestamp(colName).toLocalDateTime();
                if (optional) {
                    if (resultSet.wasNull()) {
                        newValue = Optional.empty();
                    } else {
                        newValue = Optional.of(t);
                    }
                } else {
                    newValue = t;
                }
                break;
            case SHORT:
                short n = resultSet.getShort(colName);
                if (optional) {
                    if (resultSet.wasNull()) {
                        newValue = Optional.empty();
                    } else {
                        newValue = Optional.of(n);
                    }
                } else {
                    newValue = n;
                }
                break;
            default:
                String s = resultSet.getString(colName);
                if (optional) {
                    if (resultSet.wasNull()) {
                        newValue = Optional.empty();
                    } else {
                        newValue = Optional.of(s);
                    }
                } else {
                    newValue = s;
                }
                break;
        }
        boolean resetAccessible = !field.isAccessible();
        if (resetAccessible) {
            field.setAccessible(true);
        }
        try {
            Object currentValue = field.get(row);
            field.set(row, newValue);
            propertyChangeSupport.firePropertyChange(propertyName, currentValue, newValue);
        } finally {
            if (resetAccessible) {
                field.setAccessible(false);
            }
        }
    }

    public class ValueChangeInfo {

        private final Object oldValue;
        private final Object newValue;

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }

        public ValueChangeInfo(Object o, Object n) {
            oldValue = o;
            newValue = n;
        }
    }
}
