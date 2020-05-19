package scheduler.view.country;

import com.sun.javafx.binding.ExpressionHelper;
import java.time.ZoneId;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.model.ui.CountryItem;
import scheduler.model.ui.ModelValidationResult;
import scheduler.observables.CalculatedBooleanProperty;
import scheduler.observables.CalculatedObjectProperty;
import scheduler.observables.CalculatedStringProperty;
import scheduler.observables.ObservableTuple;
import scheduler.util.Tuple;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CountryProperty extends ReadOnlyStringProperty {

    private final Object bean;
    private final String name;
    private ExpressionHelper<String> helper = null;
    private final SimpleObjectProperty<CountryItem> model;
    private final ReadOnlyPropertyImpl readOnlyProperty;
    private final CalculatedObjectProperty<CountryItem, PredefinedCountry> predefinedData;
    private final CalculatedStringProperty<PredefinedCountry> language;
    private final CalculatedObjectProperty<PredefinedCountry, ZoneId> zoneId;
    private final CalculatedObjectProperty<Tuple<CountryItem, PredefinedCountry>, ModelValidationResult> validationResult;
    private final CalculatedBooleanProperty<ModelValidationResult> valid;
    private String value;

    public CountryProperty(Object bean, String name, CountryItem initialValue) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        model = new SimpleObjectProperty<>(this, "", initialValue);
        readOnlyProperty = new ReadOnlyPropertyImpl();
        model.addListener((observable, oldValue, newValue) -> {
            String s = (null == newValue) ? "" : newValue.getName();
            if (!s.equals(value)) {
                value = s;
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        });
        value = (null == initialValue) ? "" : initialValue.getName();
        predefinedData = new CalculatedObjectProperty<>(this, "predefinedData", model, (t) -> (null == t) ? null : t.getPredefinedData());
        language = new CalculatedStringProperty<>(this, "language", predefinedData, (t) -> (null == t) ? "" : t.getLanguage());
        zoneId = new CalculatedObjectProperty<>(this, "zoneId", predefinedData, (t) -> {
            if (null != t) {
                ZoneId z = t.getZoneId();
                if (null != z) {
                    return z;
                }
            }
            return ZoneId.systemDefault();
        });
        validationResult = new CalculatedObjectProperty<>(this, "validationResult", new ObservableTuple<>(model, predefinedData), (t) -> {
            if (null == t.getValue1()) {
                return ModelValidationResult.NOT_PRESENT;
            }
            if (null == t.getValue2()) {
                return ModelValidationResult.INVALID;
            }
            return ModelValidationResult.VALID;
        });
        valid = new CalculatedBooleanProperty<>(this, "valid", validationResult, (t) -> t == ModelValidationResult.VALID);
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

    public CountryItem getModel() {
        return model.get();
    }

    public void setModel(CountryItem type) {
        model.set(type);
    }

    public SimpleObjectProperty<CountryItem> modelProperty() {
        return model;
    }

    public ReadOnlyObjectProperty<CountryItem> getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public PredefinedCountry getPredefinedData() {
        return predefinedData.get();
    }

    public ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty() {
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

    private class ReadOnlyPropertyImpl extends ReadOnlyObjectPropertyBase<CountryItem> {

        @Override
        public CountryItem get() {
            return CountryProperty.this.model.get();
        }

        @Override
        public Object getBean() {
            return CountryProperty.this;
        }

        @Override
        public String getName() {
            return "model";
        }

        private ReadOnlyPropertyImpl() {
            CountryProperty.this.model.addListener((observable, oldValue, newValue) -> {
                ReadOnlyPropertyImpl.this.fireValueChangedEvent();
            });
        }

    }

//    private class PredefinedDataProperty extends ReadOnlyObjectProperty<PredefinedCountry> {
//        private PredefinedCountry value;
//        private ExpressionHelper<PredefinedCountry> helper = null;
//        private PredefinedDataProperty() {
//            CountryItem m = CountryProperty.this.model.get();
//            value = (null == m) ? null : m.getPredefinedData();
//            CountryProperty.this.model.addListener((observable, oldValue, newValue) -> {
//                PredefinedCountry p = (null == newValue) ? null : newValue.getPredefinedData();
//                if (!Objects.equals(value, p)) {
//                    value = p;
//                }
//            });
//        }
//
//        @Override
//        public PredefinedCountry get() {
//            return value;
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super PredefinedCountry> listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super PredefinedCountry> listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public void addListener(InvalidationListener listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(InvalidationListener listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public Object getBean() {
//            return CountryProperty.this;
//        }
//
//        @Override
//        public String getName() {
//            return "predefinedData";
//        }
//
//    }
//
//    private class LanguageProperty extends ReadOnlyStringProperty {
//        private String value;
//        private ExpressionHelper<String> helper = null;
//        private LanguageProperty() {
//            value = calculate(CountryProperty.this.predefinedData.get());
//            CountryProperty.this.predefinedData.addListener((observable, oldValue, newValue) -> {
//                String s = calculate(newValue);
//                if (!Objects.equals(value, s)) {
//                    value = s;
//                }
//            });
//        }
//
//        private String calculate(PredefinedCountry country) {
//            if (null != country) {
//                String s = country.getLanguage();
//                if (null != s)
//                    return s;
//            }
//            return "";
//        }
//        
//        @Override
//        public String get() {
//            return value;
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super String> listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super String> listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public void addListener(InvalidationListener listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(InvalidationListener listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public Object getBean() {
//            return CountryProperty.this;
//        }
//
//        @Override
//        public String getName() {
//            return "language";
//        }
//
//    }
//    
//    private class ZoneIdProperty extends ReadOnlyObjectProperty<ZoneId> {
//        private ZoneId value;
//        private ExpressionHelper<ZoneId> helper = null;
//        private ZoneIdProperty() {
//            PredefinedCountry c = CountryProperty.this.predefinedData.get();
//            value = (null == c) ? ZoneId.systemDefault() : c.getZoneId();
//            CountryProperty.this.predefinedData.addListener((observable, oldValue, newValue) -> {
//                ZoneId z = (null == newValue) ? ZoneId.systemDefault() : newValue.getZoneId();
//                if (!Objects.equals(value, z)) {
//                    value = z;
//                }
//            });
//        }
//
//        @Override
//        public ZoneId get() {
//            return value;
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super ZoneId> listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super ZoneId> listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public void addListener(InvalidationListener listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(InvalidationListener listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public Object getBean() {
//            return CountryProperty.this;
//        }
//
//        @Override
//        public String getName() {
//            return "language";
//        }
//
//    }
//
//    private class ValidationMessageProperty extends ReadOnlyStringProperty {
//        PredefinedCountry predefinedCountry;
//        CountryItem countryItem;
//        private String value;
//        private ExpressionHelper<String> helper = null;
//        private ValidationMessageProperty() {
//            predefinedCountry = CountryProperty.this.predefinedData.get();
//            countryItem = CountryProperty.this.model.get();
//            value = calculate();
//            CountryProperty.this.predefinedData.addListener((observable, oldValue, newValue) -> {
//                if (!Objects.equals(predefinedCountry, newValue)) {
//                    predefinedCountry = newValue;
//                    checkChange();
//                }
//            });
//            CountryProperty.this.model.addListener((observable, oldValue, newValue) -> {
//                if (!Objects.equals(countryItem, newValue)) {
//                    countryItem = newValue;
//                    checkChange();
//                }
//            });
//        }
//        
//        private String calculate() {
//            return (null == countryItem) ? "* Required" : ((null == predefinedCountry) ? "Invalid country selection" : "");
//        }
//
//        @Override
//        public String get() {
//            return value;
//        }
//        
//        private void checkChange() {
//            String s = calculate();
//            if (!s.equals(value)) {
//                value = s;
//                ExpressionHelper.fireValueChangedEvent(helper);
//            }
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super String> listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super String> listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public void addListener(InvalidationListener listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(InvalidationListener listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public Object getBean() {
//            return CountryProperty.this;
//        }
//
//        @Override
//        public String getName() {
//            return "validationMessage";
//        }
//
//    }
//    
//    private class ValidProperty extends ReadOnlyBooleanProperty {
//
//        private boolean value = CountryProperty.this.validationMessage.get().isEmpty();
//        private ExpressionHelper<Boolean> helper = null;
//
//        private ValidProperty() {
//            CountryProperty.this.validationMessage.addListener((observable, oldValue, newValue) -> {
//                boolean v = newValue.isEmpty();
//                if (v != value) {
//                    value = v;
//                    ExpressionHelper.fireValueChangedEvent(helper);
//                }
//            });
//        }
//
//        @Override
//        public boolean get() {
//            return value;
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super Boolean> listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super Boolean> listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public void addListener(InvalidationListener listener) {
//            helper = ExpressionHelper.addListener(helper, this, listener);
//        }
//
//        @Override
//        public void removeListener(InvalidationListener listener) {
//            helper = ExpressionHelper.removeListener(helper, listener);
//        }
//
//        @Override
//        public Object getBean() {
//            return CountryProperty.this;
//        }
//
//        @Override
//        public String getName() {
//            return "valid";
//        }
//
//    }
}
