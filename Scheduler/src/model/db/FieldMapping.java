/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import model.InternalException;
import model.Record;

/**
 * Represent a mapping between an object field and a database column.
 * @author Leonard T. Erwine
 */
public class FieldMapping {
    String columnName;
    
    /**
     * Gets the name of the associated database column.
     * @return 
     */
    public String getColumnName() { return columnName; }
    
    String fieldName;
    
    /**
     * Gets the name of the target object field.
     * @return 
     */
    public String getFieldName() { return fieldName; }
    
    String propertyName;
    
    /**
     * Gets the name of the target object property, for use with @{link java.beans.PropertyChangeSupport}.
     * @return 
     */
    public String getPropertyName() { return propertyName; }
    
    QueryMode mode;
    
    /**
     * Gets a {@link QueryMode} value which indicates the field usage in INSERT and UPDATE queries.
     * @return A {@link QueryMode} value which indicates the field usage in INSERT and UPDATE queries.
     */
    public QueryMode getMode() { return mode; }
    
    MapType mapType;
    
    /**
     * Gets a {@link MapType} value that defines how the field value is translated to and from a database column value.
     * @return A {@link MapType} value that defines how the field value is translated to and from a database column value.
     */
    public MapType getMapType() { return mapType; }
    
    boolean optional;
    
    /**
     * Indicates whether the field value derives from {@link Optional<?>}.
     * @return {@code true} if the field value derives from {@link Optional<?>}; otherwise, false.
     */
    public boolean isOptional() { return optional; }
    
    boolean primaryKey;
    
    /**
     * Indicates whether the field is mapped to the primary key.
     * @return {@code true} if the field is mapped to the primary key; otherwise, false.
     */
    public boolean isPrimaryKey() { return primaryKey; }
    
    /**
     * Initializes a new {@link FieldMapping} object for a field that is mapped to the database primary key.
     * @param pk The database column name of the primary key. 
     */
    public FieldMapping(String pk) {
        columnName = pk;
        fieldName = propertyName = Record.PROP_ID;
        mode = QueryMode.ReadOnly;
        mapType = MapType.INTEGER;
        optional = primaryKey = true;
    }

    /**
     * Initializes a new {@link FieldMapping} object where the associated column is not the database primary key.
     * @param columnName The name of the database column.
     * @param fieldName The name of the field.
     * @param propertyName The property name, for use with @{link java.beans.PropertyChangeSupport}.
     * @param mode Indicates the field usage in INSERT and UPDATE queries.
     * @param mapType Specifies how the field value is translated to and from a database column value.
     * @param isOptional Indicates whether the field value derives from {@link Optional<?>}.
     */
    private FieldMapping(String columnName, String fieldName, String propertyName, QueryMode mode, MapType mapType, boolean isOptional) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.propertyName = propertyName;
        this.mode = mode;
        this.mapType = mapType;
        this.optional = isOptional;
        primaryKey = false;
    }
    
    /**
     * Attempts to get a database column mapping for a field from field annotations including {@link mode.db.Column}.
     * @param field The target field of a class derived from {@link model.Record}.
     * @return An Optional that has a value if the annotations for a field specified a database column mapping;
     * otherwise an empty Optional.
     */
    public static Optional<FieldMapping> TryCreate(Field field) {
        Class<Column> cClass = Column.class;
        if (!field.isAnnotationPresent(cClass))
            return Optional.empty();
        Column column = field.getAnnotation(cClass);
        Class<ValueMap> vmClass = ValueMap.class;
        MapType mapType = (field.isAnnotationPresent(vmClass)) ? field.getAnnotation(vmClass).value() : MapType.INFERRED;
        Class<?> type = field.getType();
        boolean isOptional = (Optional.class.isAssignableFrom(type));
        if (mapType == MapType.INFERRED) {
            if (isOptional)
                type = type.getTypeParameters()[0].getGenericDeclaration();
            if (String.class.isAssignableFrom(type))
                mapType = MapType.STRING;
            else if (Integer.class.isAssignableFrom(type))
                mapType = MapType.INTEGER;
            else if (Boolean.class.isAssignableFrom(type))
                mapType = MapType.BOOLEAN;
            else if (LocalDateTime.class.isAssignableFrom(type))
                mapType = MapType.DATETIME;
            else
                // If this occurs, then it is a software bug.
                throw new InternalException("Unsupported field type: " + field.getType().getTypeName());
        }
        Class<PropertyName> pnClass = PropertyName.class;
        Class<Mode> mClass = Mode.class;
        return Optional.of(new FieldMapping((column.value().length() == 0) ? field.getName() : column.value(),
                field.getName(),
                (field.isAnnotationPresent(pnClass)) ? field.getAnnotation(pnClass).value() : field.getName(),
                (field.isAnnotationPresent(mClass)) ? field.getAnnotation(mClass).value() : QueryMode.All,
                mapType, isOptional));
    }
}
