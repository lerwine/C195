/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.json;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import model.InternalException;
import model.Record;

/**
 *
 * @author Leonard T. Erwine
 */
public class FieldMapping {
    String propertyName;
    
    /**
     * Gets the name of the target object property, for use with @{link java.beans.PropertyChangeSupport}.
     * @return 
     */
    public String getPropertyName() { return propertyName; }
    
    String fieldName;
    
    /**
     * Gets the name of the target object field.
     * @return 
     */
    public String getFieldName() { return fieldName; }
    
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
    
    /**
     * Initializes a new {@link FieldMapping} object where the associated column is not the database primary key.
     * @param propertyName The name of the JSON object property.
     * @param fieldName The name of the field.
     * @param mapType Specifies how the field value is translated to and from a JSON value.
     * @param isOptional Indicates whether the field value derives from {@link Optional<?>}.
     */
    private FieldMapping(String propertyName, String fieldName, MapType mapType, boolean isOptional) {
        this.propertyName = propertyName;
        this.fieldName = fieldName;
        this.mapType = mapType;
        this.optional = isOptional;
    }
    
    public static Optional<FieldMapping> TryCreate(Field field) {
        Class<Property> cClass = Property.class;
        if (!field.isAnnotationPresent(cClass))
            return Optional.empty();
        Property pn = field.getAnnotation(cClass);
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
        return Optional.of(new FieldMapping((pn.value().length() == 0) ? field.getName() : pn.value(), field.getName(),
            mapType, isOptional));
    }
    
    public static Stream<FieldMapping> getFields(Class<? extends Record> c) {
        ArrayList<FieldMapping> fieldArr = new ArrayList<>();
        for (Field field : c.getFields()) {
            boolean isAccessible = field.isAccessible();
            if (!isAccessible)
                field.setAccessible(true);
            try {
                Optional<FieldMapping> fm = FieldMapping.TryCreate(field);
                if (fm.isPresent())
                    fieldArr.add(fm.get());
            } finally {
                if (!isAccessible)
                    field.setAccessible(false);
            }
        }
        return fieldArr.stream();
    }
}
