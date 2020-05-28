package scheduler.model.ui;

import java.time.ZoneId;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedCountry.class.getName());

    private final JavaBeanStringProperty rawName;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        try {
            rawName = JavaBeanStringPropertyBuilder.create().bean(rowData).name("rawName").name(CountryDAO.PROP_NAME).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#nameProperty
    }

    @Override
    public ZoneId getZoneId() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#getZoneId
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#zoneIdProperty
    }

    @Override
    public String getDefaultTimeZoneDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#getDefaultTimeZoneDisplay
    }

    @Override
    public ReadOnlyStringProperty defaultTimeZoneDisplayProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#defaultTimeZoneDisplayProperty
    }

    @Override
    public String getLanguage() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#getLanguage
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#languageProperty
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#getName
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCountry#getLocale
    }
}
