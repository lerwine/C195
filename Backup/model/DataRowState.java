package scheduler.model;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
