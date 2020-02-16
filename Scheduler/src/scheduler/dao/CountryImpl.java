package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.observables.NonNullableStringProperty;
import scheduler.view.country.CountryReferenceModel;

public class CountryImpl extends DataObjectImpl implements Country {

    //<editor-fold defaultstate="collapsed" desc="Properties and Fields">
    //<editor-fold defaultstate="collapsed" desc="Column names">
    public static final String COLNAME_COUNTRYID = "countryId";

    public static final String COLNAME_COUNTRY = "country";

    //</editor-fold>
    private static final String BASE_SELECT_SQL = String.format("SELECT `%s`, `%s`, `%s`, `%s`, `%s`, `%s` FROM `%s`", COLNAME_COUNTRYID, COLNAME_COUNTRY, COLNAME_CREATEDATE,
            COLNAME_CREATEDBY, COLNAME_LASTUPDATE, COLNAME_LASTUPDATEBY, TABLENAME_COUNTRY);

    //<editor-fold defaultstate="collapsed" desc="name property">
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param value new value of name.
     */
    public void setName(String value) {
        name = (value == null) ? "" : value;
    }

    //</editor-fold>
    //</editor-fold>
    /**
     * Initializes a {@link DataObject.ROWSTATE_NEW} country object.
     */
    public CountryImpl() {
        super();
        name = "";
    }

    private static final FactoryImpl FACTORY = new FactoryImpl();
    
    public static FactoryImpl getFactory() { return FACTORY; }
    
    public static final class FactoryImpl extends DataObjectImpl.Factory<CountryImpl> {

        // This is a singleton instance
        private FactoryImpl() { }
        
        //    @Override
        //    protected void onApplyChanges(CountryModel model) {
        //        model.getDataObject().name = model.getName();
        //    }
        @Override
        protected CountryImpl fromResultSet(ResultSet resultSet) throws SQLException {
            CountryImpl result = new CountryImpl();
            onInitializeDao(result, resultSet);
            return result;
        }

        @Override
        public String getBaseQuery() {
            return BASE_SELECT_SQL;
        }

        @Override
        public Class<? extends CountryImpl> getDaoClass() {
            return CountryImpl.class;
        }

        @Override
        public String getTableName() {
            return TABLENAME_COUNTRY;
        }

        @Override
        public String getPrimaryKeyColName() {
            return COLNAME_COUNTRYID;
        }

        @Override
        protected Stream<String> getExtendedColNames() {
            return Stream.of(COLNAME_COUNTRY);
        }

        @Override
        protected void setStatementValues(CountryImpl dao, PreparedStatement ps) throws SQLException {
            ps.setString(1, dao.getName());
        }

        @Override
        protected void onInitializeDao(CountryImpl target, ResultSet resultSet) throws SQLException {
            target.name = resultSet.getString(COLNAME_COUNTRY);
            if (resultSet.wasNull()) {
                target.name = "";
            }
        }

    }

}
