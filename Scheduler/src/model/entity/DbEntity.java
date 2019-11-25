/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Leonard T. Erwine
 */
public interface DbEntity extends Serializable {
    public Integer getPrimaryKey();
    public void setPrimaryKey(Integer value);
    public Timestamp getCreateDate();
    public void setCreateDate(Timestamp createDate);
    public String getCreatedBy();
    public void setCreatedBy(String createdBy);
    public Timestamp getLastUpdate();
    public void setLastUpdate(Timestamp lastUpdate);
    public String getLastUpdateBy();
    public void setLastUpdateBy(String lastUpdateBy);
}
