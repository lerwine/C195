package scheduler.view.city;

import com.sun.javafx.binding.ExpressionHelper;
import java.time.ZoneId;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import scheduler.dao.DataRowState;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CityItem;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.ModelValidationResult;
import scheduler.observables.CalculatedBooleanProperty;
import scheduler.observables.CalculatedObjectProperty;
import scheduler.observables.CalculatedObjectValueExpression;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.ObservableQuadruplet;
import scheduler.observables.ObservableTriplet;
import scheduler.observables.ObservableTuple;
import scheduler.util.Quadruplet;
import scheduler.util.Triplet;
import scheduler.util.Tuple;
import scheduler.view.country.CountryProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.view.city.CityProperty}
 */
public class CityProperty extends ReadOnlyStringProperty {
    private final Object bean;
    private final String name;
    private ExpressionHelper<String> helper = null;
    private final SimpleObjectProperty<CityItem> model;
    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final CalculatedObjectProperty<CityItem, CountryItem> country;
    private final CalculatedObjectProperty<CityItem, PredefinedCity> predefinedData;
    private final CalculatedStringProperty<PredefinedCity> language;
    private final CalculatedObjectProperty<PredefinedCity, ZoneId> zoneId;
    private final CalculatedObjectProperty<Triplet<CityItem, PredefinedCity, Tuple<CountryItem, PredefinedCountry>>,
            ModelValidationResult> validationResult;
    private final CalculatedBooleanProperty<ModelValidationResult> valid;
    private String value;

    public CityProperty(Object bean, String name, CityItem initialValue) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        model = new SimpleObjectProperty<>(this, "model", initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        model.addListener((observable, oldValue, newValue) -> {
            String s = (null == newValue) ? "" : newValue.getName();
            if (!s.equals(value)) {
                value = s;
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        });
        predefinedData = new CalculatedObjectProperty<>(this, "predefinedData", model, (t) -> (null == t) ? null : t.getPredefinedData());
        country = new CalculatedObjectProperty<>(this, "country", model, (t) -> (null == t) ? null : t.getCountry());
        language = new CalculatedStringProperty<>(this, "language", predefinedData, (t) -> (null == t) ? "" : t.getLanguage());
        zoneId = new CalculatedObjectProperty<>(this, "zoneId", predefinedData, (t) -> {
            if (null != t) {
                ZoneId z = t.getZoneId();
                if (null != z)
                    return z;
            }
            return ZoneId.systemDefault();
        });
        validationResult = new CalculatedObjectProperty<>(this, "validationResult",
                new ObservableTriplet<>(
                        model,
                        predefinedData,
                        new ObservableTuple<>(
                                country,
                                new CalculatedObjectValueExpression<>(country, (t) -> (null == t) ? null : t.getPredefinedData())
                        )
                ), (t) -> {
                    if (null == t.getValue1()) {
                        return ModelValidationResult.NOT_PRESENT;
                    }
                    PredefinedCity p = t.getValue2();
                    if (null != p) {
                        Tuple<CountryItem, PredefinedCountry> v = t.getValue3();
                        CountryItem c = v.getValue1();
                        if (null != c && c.getRowState() != DataRowState.DELETED) {
                            PredefinedCountry d = v.getValue2();
                            if (null != d && p.getCountry().equals(d))
                                return ModelValidationResult.VALID;
                        }
                    }
                    return ModelValidationResult.INVALID;
                });
        valid = new CalculatedBooleanProperty<>(this, "valid", validationResult, (t) -> t == ModelValidationResult.VALID);
    }
    
    public CityItem getModel() {
        return model.get();
    }
    
    public void setModel(CityItem type) {
        model.set(type);
    }

    public SimpleObjectProperty<CityItem> modelProperty() {
        return model;
    }

    public ReadOnlyObjectProperty<CityItem> getReadOnlyProperty() {
        return readOnlyProperty;
    }
    
    public CountryItem getCountry() {
        return country.get();
    }
    
    public ReadOnlyObjectProperty<CountryItem> countryProperty() {
        return country.getReadOnlyObjectProperty();
    }

    public PredefinedCity getPredefinedData() {
        return predefinedData.get();
    }
    
    public ReadOnlyObjectProperty<PredefinedCity> predefinedDataProperty() {
        return predefinedData.getReadOnlyObjectProperty();
    }
    
    public String getLanuage() {
        return language.get();
    }
    
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
    }
    
    public ZoneId getZoneId() {
        return zoneId.get();
    }
    
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyObjectProperty();
    }
    
    public ModelValidationResult getValidationResult() {
        return validationResult.get();
    }
    
    public ReadOnlyObjectProperty<ModelValidationResult> validationResultProperty() {
        return validationResult.getReadOnlyObjectProperty();
    }
    
    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void addListener(ChangeListener<? super String> listener) {
        ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
        ExpressionHelper.removeListener(helper, listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        ExpressionHelper.removeListener(helper, listener);
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<CityItem> {

        @Override
        public CityItem get() {
            return CityProperty.this.model.get();
        }

        @Override
        public Object getBean() {
            return CityProperty.this;
        }

        @Override
        public String getName() {
            return "model";
        }

        private ReadOnlyPropertyImpl() {
            CityProperty.this.model.addListener((observable, oldValue, newValue) -> {
                ReadOnlyPropertyImpl.this.fireValueChangedEvent();
            });
        }

    }

}
