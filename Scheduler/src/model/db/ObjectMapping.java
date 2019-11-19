/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import model.InternalException;
import model.Record;

/**
 *
 * @author Leonard T. Erwine
 */
public class ObjectMapping {
    String tableName;
    
    public String getTableName() { return tableName; }

    boolean readOnly;
    
    public boolean isReadOnly() { return readOnly; }
    
    Stream<FieldMapping> fields;
    
    public Stream<FieldMapping> getFields() { return fields; }
    
    public ObjectMapping(Class<? extends Record> c) {
        Class<Table> tmClass = Table.class;
        if (!c.isAnnotationPresent(tmClass))
            // If this occurs, then it is a software bug.
            throw new InternalException("Class is not annotated by @TableMap");
        Table tableMap = c.getAnnotation(tmClass);
        ArrayList<FieldMapping> fieldArr = new ArrayList<>();
        fieldArr.add(new FieldMapping(tableMap.pk()));
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
        tableName = tableMap.name();
        readOnly  = tableMap.readOnly() == true;
        fields = fieldArr.stream();
    }
}
