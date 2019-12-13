/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

/**
 *
 * @author Leonard T. Erwine
 */
public class PropertyModel {
    private String key;
    private String value;
    
    public String getKey() { return key; }
    
    public void setKey(String k) { key = (k == null) ? "" : k; }
    
    public String getValue() { return value; }
    
    public void setValue(String v) { value = (v == null) ? "" : v; }
    
    public PropertyModel(String key, String value) {
        this.key = (key == null) ? "" : key;
        this.value = (value == null) ? "" : value;
    }
}
