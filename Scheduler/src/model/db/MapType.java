package model.db;

/**
 * Specifies how a field value is translated to and from a database column value.
 * @author Leonard T. Erwine
 */
public enum MapType {
    /**
     * Mapping type is to be inferred from the field type.
     */
    INFERRED,
    
    /**
     * Field type is {@link String} or {@link Optional<String>}.
     */
    STRING,
    
    /**
     * Field type is {@link LocalDateTime} or {@link Optional<LocalDateTime>}.
     */
    DATETIME,
    
    /**
     * Field type is {@link Integer} or {@link Optional<Integer>}.
     */
    INTEGER,
    
    /**
     * Field type is {@link Boolean} or {@link Optional<Boolean>}.
     */
    BOOLEAN
}
