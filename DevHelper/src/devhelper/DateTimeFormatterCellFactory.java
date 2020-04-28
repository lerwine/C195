package devhelper;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code devhelper.DateTimeFormatterCellFactory}
 */
public class DateTimeFormatterCellFactory implements Callback<ListView<DateTimeFormatterCellFactory.Item>, ListCell<DateTimeFormatterCellFactory.Item>> {
    private final ObservableList<Item> items;

    public ObservableList<Item> getItems() {
        return items;
    }

    public DateTimeFormatterCellFactory() {
        items = FXCollections.observableArrayList();
        Arrays.stream(FormatStyle.values()).forEach((t) -> {
            String n = t.name();
            items.add(new Item(String.format("Localized Date, %s%s", n.substring(0, 1),  n.substring(1).toLowerCase()),
                    DateTimeFormatter.ofLocalizedDate(t)));
            items.add(new Item(String.format("Localized DateTime, %s%s", n.substring(0, 1),  n.substring(1).toLowerCase()),
                    DateTimeFormatter.ofLocalizedDateTime(t)));
            items.add(new Item(String.format("Localized Time, %s%s", n.substring(0, 1),  n.substring(1).toLowerCase()),
                    DateTimeFormatter.ofLocalizedTime(t)));
        });
        
        items.add(new Item("Of Pattern", (t, u) -> DateTimeFormatter.ofPattern(t.get())));
        items.add(new Item("Of Pattern and Locale", (t, u) -> DateTimeFormatter.ofPattern(t.get(), u.get())));
        
        items.add(new Item("BASIC_ISO_DATE", DateTimeFormatter.BASIC_ISO_DATE));
        items.add(new Item("ISO_DATE", DateTimeFormatter.ISO_DATE));
        items.add(new Item("ISO_DATE_TIME", DateTimeFormatter.ISO_DATE_TIME));
        items.add(new Item("ISO_INSTANT", DateTimeFormatter.ISO_INSTANT));
        items.add(new Item("ISO_LOCAL_DATE", DateTimeFormatter.ISO_LOCAL_DATE));
        items.add(new Item("ISO_LOCAL_DATE_TIME", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        items.add(new Item("ISO_LOCAL_TIME", DateTimeFormatter.ISO_LOCAL_TIME));
        items.add(new Item("ISO_OFFSET_DATE", DateTimeFormatter.ISO_OFFSET_DATE));
        items.add(new Item("ISO_OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        items.add(new Item("ISO_OFFSET_TIME", DateTimeFormatter.ISO_OFFSET_TIME));
        items.add(new Item("ISO_ORDINAL_DATE", DateTimeFormatter.ISO_ORDINAL_DATE));
        items.add(new Item("ISO_TIME", DateTimeFormatter.ISO_TIME));
        items.add(new Item("ISO_WEEK_DATE", DateTimeFormatter.ISO_WEEK_DATE));
        items.add(new Item("ISO_ZONED_DATE_TIME", DateTimeFormatter.ISO_ZONED_DATE_TIME));
        items.add(new Item("RFC_1123_DATE_TIME", DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    @Override
    public ListCell<Item> call(ListView<Item> param) {
        return new Cell();
    }

    public static class Cell extends ListCell<Item> {

        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (null == item)
                setText("");
            else
                setText(item.getTitle());
        }
        
    }
    
    public class Item {

        private final String title;
        private final BiFunction<StringProperty, ReadOnlyObjectProperty<Locale>, DateTimeFormatter> getFormatterFunc;
        private final boolean formatTextAreaDisabled;
        
        public String getTitle() {
            return title;
        }

        public boolean isFormatTextAreaDisabled() {
            return formatTextAreaDisabled;
        }

        DateTimeFormatter getFormatter(StringProperty textProperty, ReadOnlyObjectProperty<Locale> localeProperty) {
            return getFormatterFunc.apply(textProperty, localeProperty);
        }
        
        @Override
        public boolean equals(Object obj) {
            return null != obj && obj instanceof Item && title.equals(((Item)obj).title);
        }

        @Override
        public int hashCode() {
            return title.hashCode();
        }

        @Override
        public String toString() {
            return title;
        }
        
        Item(String title, DateTimeFormatter formatter) {
            this.title = title;
            this.getFormatterFunc = (t, u) -> formatter;
            formatTextAreaDisabled = true;
        }
        
        Item(String title, BiFunction<StringProperty, ReadOnlyObjectProperty<Locale>, DateTimeFormatter> getFormatterFunc) {
            this.title = title;
            this.getFormatterFunc = getFormatterFunc;
            formatTextAreaDisabled = false;
        }
        
    }
}
