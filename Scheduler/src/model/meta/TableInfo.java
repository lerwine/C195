/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import model.db.DataRow;
import utils.InternalException;
import model.annotations.TableName;
import utils.InvalidArgumentException;

/**
 *
 * @author Leonard T. Erwine
 * @param <C>
 */
public class TableInfo<C extends DataRow> {
    private Class<C> rowClass;
    
    public Class<C> getRowClass() { return rowClass; }
    
    //<editor-fold defaultstate="collapsed" desc="tableName">
    
    private final String _tableName;
    
    public String tableName() { return _tableName; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="primaryKeyColName">
    
    private final String _primaryKeyColName;
    
    public String primaryKeyColName() { return _primaryKeyColName; }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="HashMap-like members">
    
    private final HashMap<String, ColumnInfo> columnInfo;
    
    public int size() { return columnInfo.size(); }
    
    public boolean containsColumn(String name) { return columnInfo.containsKey(name); }
    
    public boolean containsValue(ColumnInfo value) { return columnInfo.containsValue(value); }
    
    public ColumnInfo get(String name) { return columnInfo.get(name); }
    
    public ColumnInfo getByFieldName(String name) {
        for (ColumnInfo c: columnInfo.values()) {
            if (c.getFieldName().equals(name))
                return c;
        }
        return  null;
    }
    
    public Set<String> colNameSet() { return columnInfo.keySet(); }
    
    public Stream<ColumnInfo> stream() { return columnInfo.values().stream(); }
    
    //</editor-fold>
    
    public TableInfo(Class<C> rowClass) {
        if (!rowClass.isAnnotationPresent(TableName.class))
            throw new InternalException(String.format("Class {0} does not have the {1} annotation",
                    rowClass.getName(), TableName.class.getName()));
        _tableName = rowClass.getAnnotation(TableName.class).value();
        if (_tableName == null || _tableName.trim().isEmpty())
            throw new InternalException(String.format("Class {0} has an empty {1} annotation",
                    rowClass.getName(), TableName.class.getName()));
        columnInfo = new HashMap<>();
        ColumnInfo.getColumnInfo(rowClass).forEach((f) -> columnInfo.put(f.getColName(), f));
        _primaryKeyColName = columnInfo.values().stream().filter((c) -> c.isPrimaryKey()).findFirst().get().getColName();
    }
    
    /**
     * Pattern that matches a column value place holder.
     */
    static Pattern placeHolderPattern = Pattern.compile(":(:|[a-zA-Z][a-zA-Z\\d]+)");

    public String getInsertQuery(ArrayList<ColumnInfo> sourceColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `").append(_tableName).append("` (`").append(DataRow.PROP_CREATEDATE).append("`, `")
                .append(DataRow.PROP_LASTUPDATE).append("`, `")
                .append(DataRow.PROP_CREATEDBY).append("`, `")
                .append(DataRow.PROP_LASTUPDATEBY).append("`");
        ColumnInfo createdBy = columnInfo.get(DataRow.PROP_CREATEDBY);
        sourceColumns.add(createdBy);
        sourceColumns.add(createdBy);
        stream().filter((ColumnInfo col) -> !col.isBaseField()).forEach((ColumnInfo col) -> {
            sb.append(", `").append(col.getColName()).append("`");
            sourceColumns.add(col);
        });
        sb.append(") VALUES(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP");
        sourceColumns.forEach((i) -> { sb.append(", ?"); });
        sb.append(")");
        return sb.toString();
    }
    
    public String getUpdateByIdQuery(ArrayList<ColumnInfo> sourceColumns) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE `").append(_tableName).append("` SET `").append(DataRow.PROP_LASTUPDATE).append("` = CURRENT_TIMESTAMP, `")
                .append(DataRow.PROP_LASTUPDATEBY).append("` = ?");
        sourceColumns.add(columnInfo.get(DataRow.PROP_LASTUPDATEBY));
        stream().filter((ColumnInfo col) -> !col.isBaseField()).forEach((ColumnInfo col) -> {
            sb.append(", `").append(col.getColName()).append("` = ?");
            sourceColumns.add(col);
        });
        sb.append(" WHERE `").append(_primaryKeyColName).append("` = ?");
        sourceColumns.add(columnInfo.get(_primaryKeyColName));
        return sb.toString();
    }
}
