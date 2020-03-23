/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.model;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author lerwi
 */
public enum DataRowState {
    NEW(0),
    UNMODIFIED(1),
    MODIFIED(2),
    DELETED(3);
    
    private final int value;
    
    public final int getValue() { return value; }
    
    DataRowState(int value) {
        this.value = value;
    }
    
    public static boolean isExisting(DataRowState value) {
        switch (value) {
            case MODIFIED:
            case UNMODIFIED:
                return true;
        }
        return false;
    }
    
    public static boolean isExisting(IDataRow dataRow) {
        if (null != dataRow) {
            switch (dataRow.getRowState()) {
                case MODIFIED:
                case UNMODIFIED:
                    return true;
            }
        }
        return false;
    }
    
    public static Optional<DataRowState> of(int value) {
        return Arrays.stream(values()).filter(bl -> bl.value == value).findFirst();
    }
}
