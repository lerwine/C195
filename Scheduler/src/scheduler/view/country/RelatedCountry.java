package scheduler.view.country;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataRowState;
import scheduler.model.ModelHelper;
import scheduler.model.db.CountryRowData;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CountryDbItem;
import scheduler.observables.RowStateProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry implements CountryDbItem<CountryRowData> {

    private final ReadOnlyObjectWrapper<CountryRowData> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyStringWrapper name;
    private final PredefinedCountry predefinedData;
    private final RowStateProperty rowState;

    public RelatedCountry(CountryRowData rowData) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", rowData.getPrimaryKey());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", rowData);
        rowState = new RowStateProperty(this, "rowState", ModelHelper.getRowState(rowData));
        name = new ReadOnlyStringWrapper(this, "name", rowData.getName());
        predefinedData = rowData.asPredefinedData();
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CountryRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyProperty<? extends DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    @Override
    public PredefinedCountry asPredefinedData() {
        return predefinedData;
    }

}
