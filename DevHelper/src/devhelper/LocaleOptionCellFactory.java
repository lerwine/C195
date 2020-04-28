package devhelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code devhelper.LocaleOptionCellFactory}
 */
public class LocaleOptionCellFactory implements Callback<ListView<Locale>, ListCell<Locale>> {
    private final ObservableList<Locale> items;
    
    public LocaleOptionCellFactory() {
        items = FXCollections.observableArrayList();
        Arrays.stream(Locale.getAvailableLocales()).sorted(LocaleOptionCellFactory::compareLocales).forEach((t) -> items.add(t));
    }

    public ObservableList<Locale> getItems() {
        return items;
    }

    public Optional<Locale> find(String languageTag) {
        return items.stream().filter((t) -> t.toLanguageTag().equals(languageTag)).findAny();
    }
    
    @Override
    public ListCell<Locale> call(ListView<Locale> param) {
        return new Cell();
    }

    public static int compareLocales(Locale o1, Locale o2) {
        if (null == o1)
            return (null == o2) ? 0 : 1;
        if (null == o2)
            return -1;
        int result = o1.getDisplayLanguage().compareTo(o2.getDisplayLanguage());
        if (result == 0 && (result = o1.getDisplayCountry().compareTo(o2.getDisplayCountry())) == 0)
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        return result;
    }

    public static class Cell extends ListCell<Locale> {

        @Override
        protected void updateItem(Locale item, boolean empty) {
            super.updateItem(item, empty);
            if (null == item)
                setText("");
            else
                setText(item.getDisplayName());
        }
    }
}
