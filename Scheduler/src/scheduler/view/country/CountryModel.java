package scheduler.view.country;

import java.util.Objects;
import javafx.beans.property.StringProperty;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl.DaoFactory;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.ItemModel;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public final class CountryModel extends ItemModel<CountryImpl> implements CountryReferenceModel<CountryImpl> {

    private final NonNullableStringProperty name;

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    public CountryModel(CountryImpl dao) {
        super(dao);
        name = new NonNullableStringProperty(this, "name", dao.getName());
    }

    @Override
    public String toString() {
        return name.get();
    }

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }
    
    @Override
    public int hashCode() {
        if (isNewItem()) {
            return Objects.hashCode(this.name);
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CountryModel) {
            final CountryModel other = (CountryModel) obj;
            if (isNewItem()) {
                return name.isEqualTo(other.name).get();
            }
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
    public final static class Factory extends ItemModel.ModelFactory<CountryImpl, CountryModel> {

        private Factory() { }
        
        @Override
        public DaoFactory<CountryImpl> getDaoFactory() {
            return CountryImpl.getFactory();
        }

        @Override
        public CountryModel createNew(CountryImpl dao) {
            return new CountryModel(dao);
        }

        @Override
        protected void updateItem(CountryModel item, CountryImpl dao) {
            super.updateItem(item, dao);
            // TODO: Implement this
        }

        @Override
        public ModelFilter<CountryImpl, CountryModel> getAllItemsFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public ModelFilter<CountryImpl, CountryModel> getDefaultFilter() {
            throw new UnsupportedOperationException("Not supported yet.");
            // TODO: Implement this
        }

        @Override
        public CountryImpl applyChanges(CountryModel item) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
