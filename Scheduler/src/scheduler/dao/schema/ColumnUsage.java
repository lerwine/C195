/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

/**
 *
 * @author lerwi
 */
public enum ColumnUsage {
    PRIMARY_KEY,
    FOREIGN_KEY,
    UNIQUE_KEY,
    AUDIT,
    CRYPTO_HASH,
    OTHER
}
