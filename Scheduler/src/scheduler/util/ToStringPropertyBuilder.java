package scheduler.util;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.DataObject;

/**
 * Utility class to create a string representation of an object for debugging/reporting purposes.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ToStringPropertyBuilder {

    private static final Pattern STRING_ENCODE = Pattern.compile("[\"\\u0000-\\u0019\\u007f-\\uffff\\\\]");
    private static final int LINE_BREAK_COL = 128;

    public static ToStringPropertyBuilder create(Object target) {
        return new ToStringPropertyBuilder(target);
    }
//    private final StringBuilder output;
    private final ArrayList<PropertyData> properties;
    private final Object target;
    private final NumberFormat numberFormat;

    public ToStringPropertyBuilder(Object target) {
        this.target = Objects.requireNonNull(target);
        numberFormat = NumberFormat.getNumberInstance();
        properties = new ArrayList<>();
    }

    public String build() {
        ToStringWriter writer = new ToStringWriter();
        writer.append(target.getClass().getSimpleName()).append("{");
        Iterator<PropertyData> iterator = properties.iterator();
        if (iterator.hasNext()) {
            writer = writer.indent();
            PropertyData pd = iterator.next();
            if ((pd.firstBlockLength() + writer.getCurrentLineLength() + 2) > LINE_BREAK_COL) {
                writer.appendLine();
            }
            pd.writeTo(writer);
            while (iterator.hasNext()) {
                pd = iterator.next();
                if ((pd.firstBlockLength() + writer.getCurrentLineLength() + 2) > LINE_BREAK_COL) {
                    writer.append(",").appendLine();
                } else {
                    writer.append(", ");
                }
                pd.writeTo(writer);
            }
        }
        writer.append("}");
        StringBuilder result = new StringBuilder();
        Iterator<StringBuilder> sbi = writer.builderList.iterator();
        result.append(sbi.next());
        String ls = System.lineSeparator();
        while (sbi.hasNext()) {
            result.append(ls).append(sbi.next());
        }
        return result.toString();
    }

    private String encodeString(String value) {
        if (null == value) {
            return "null";
        }
        if (value.isEmpty()) {
            return "\"\"";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        Matcher matcher = STRING_ENCODE.matcher(value);
        while (matcher.find()) {
            String r = matcher.group(0);
            switch (r) {
                case "\r":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\r"));
                    break;
                case "\n":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\n"));
                    break;
                case "\t":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\t"));
                    break;
                case "\b":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\b"));
                    break;
                case "\f":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\f"));
                    break;
                case "\"":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\""));
                    break;
                case "\\":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\\"));
                    break;
                default:
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(String.format("\\u%04x", matcher.group(0).codePointAt(0))));
                    break;
            }
        }
        matcher.appendTail(sb);
        return sb.append('"').toString();
    }

    private <T> ToStringPropertyBuilder addOptional(String propertyName, Optional<T> value, Function<? super T, PropertyData> ifPresent) {
        if (null == value) {
            return addNull(propertyName);
        }

        properties.add(value.<PropertyData>map((t) -> new WrappedPropertyData("Optional[", ifPresent.apply(t), "]"))
                .orElse(new SimplePropertyData(propertyName, "Optional.empty")));
        return this;
    }

    public ToStringPropertyBuilder addNull(String propertyName) {
        properties.add(new SimplePropertyData(propertyName, "null"));
        return this;
    }

    public ToStringPropertyBuilder addToStringPropertyBuilder(String propertyName, ToStringPropertyBuilder nested) {
        properties.add(new ComplexPropertyData(propertyName, nested));
        return this;
    }

    public ToStringPropertyBuilder addNumber(String propertyName, Number value) {
        properties.add(new SimplePropertyData(propertyName, numberFormat.format(value)));
        return this;
    }

    public ToStringPropertyBuilder addNumber(String propertyName, Optional<Number> value) {
        return addOptional(propertyName, value, (t) -> new SimplePropertyData(propertyName, numberFormat.format(t)));
    }

    public ToStringPropertyBuilder addNumber(ReadOnlyProperty<Number> property) {
        if (null == property) {
            return this;
        }
        return addNumber(property.getName(), property.getValue());
    }

    public <T extends Enum> ToStringPropertyBuilder addEnum(String propertyName, T value) {
        if (null == value) {
            return addNull(propertyName);
        }
        properties.add(new SimplePropertyData(propertyName, String.format("%s#%s", value.getClass().getSimpleName(), value.name())));
        return this;
    }

    public <T extends Enum> ToStringPropertyBuilder addEnum(String propertyName, Optional<T> value) {
        return addOptional(propertyName, value, (t)
                -> new SimplePropertyData(propertyName, String.format("%s#%s", t.getClass().getSimpleName(), t.name()))
        );
    }

    public <T extends Enum> ToStringPropertyBuilder addEnum(ReadOnlyProperty<T> property) {
        if (null == property) {
            return this;
        }
        return addEnum(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addBoolean(String propertyName, boolean value) {
        properties.add(new SimplePropertyData(propertyName, (value) ? "true" : "false"));
        return this;
    }

    public ToStringPropertyBuilder addBoolean(String propertyName, Optional<Boolean> value) {
        return addOptional(propertyName, value, (t) -> new SimplePropertyData(propertyName, (t) ? "true" : "false"));
    }

    public ToStringPropertyBuilder addBoolean(ReadOnlyBooleanProperty property) {
        if (null == property) {
            return this;
        }
        return addBoolean(property.getName(), property.get());
    }

    public ToStringPropertyBuilder addString(String propertyName, String value) {
        properties.add(new SimplePropertyData(propertyName, encodeString(value)));
        return this;
    }

    public ToStringPropertyBuilder addString(String propertyName, Optional<String> value) {
        return addOptional(propertyName, value, (t) -> new SimplePropertyData(propertyName, encodeString(t)));
    }

    public ToStringPropertyBuilder addString(ReadOnlyProperty<String> property) {
        if (null == property) {
            return this;
        }
        return addString(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addTimestamp(String propertyName, Timestamp value) {
        if (null == value) {
            return addNull(propertyName);
        }
        return addLocalDateTime(propertyName, value.toLocalDateTime());
    }

    public ToStringPropertyBuilder addTimestamp(ReadOnlyProperty<Timestamp> property) {
        if (null == property) {
            return this;
        }
        if (null == property) {
            return this;
        }
        return addTimestamp(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addLocalDateTime(String propertyName, LocalDateTime value) {
        if (null == value) {
            return addNull(propertyName);
        }
        properties.add(new SimplePropertyData(propertyName, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value)));
        return this;
    }

    public ToStringPropertyBuilder addLocalDateTime(ReadOnlyProperty<LocalDateTime> property) {
        if (null == property) {
            return this;
        }
        return addLocalDateTime(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addDataObject(String propertyName, DataObject value) {
        if (null == value) {
            return addNull(propertyName);
        }
        return addToStringPropertyBuilder(propertyName, value.toStringBuilder());
    }

    public ToStringPropertyBuilder addDataObject(ReadOnlyProperty<? extends DataObject> property) {
        if (null == property) {
            return this;
        }
        return addDataObject(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addLocale(String propertyName, Locale value) {
        if (null == value) {
            return addNull(propertyName);
        }
        properties.add(new SimplePropertyData(propertyName, value.toLanguageTag()));
        return this;
    }

    public ToStringPropertyBuilder addLocale(ReadOnlyProperty<Locale> property) {
        if (null == property) {
            return this;
        }
        return addLocale(property.getName(), property.getValue());
    }

    public ToStringPropertyBuilder addTimeZone(String propertyName, TimeZone value) {
        if (null == value) {
            return addNull(propertyName);
        }
        properties.add(new SimplePropertyData(propertyName, value.toZoneId().getId()));
        return this;
    }

    public ToStringPropertyBuilder addTimeZone(ReadOnlyProperty<TimeZone> property) {
        if (null == property) {
            return this;
        }
        return addTimeZone(property.getName(), property.getValue());
    }

    private static abstract class PropertyData {

        private final String name;

        PropertyData(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        void writeTo(ToStringWriter writer) {
            writer.append(getName()).append("=");
            writeContentTo(writer);
        }

        abstract void writeContentTo(ToStringWriter writer);

        abstract int firstBlockLength();

        abstract boolean isSingleBlock();
    }

    private static class SimplePropertyData extends PropertyData {

        private final String content;

        SimplePropertyData(String name, String content) {
            super(name);
            this.content = content;
        }

        @Override
        void writeContentTo(ToStringWriter writer) {
            writer.append(content);
        }

        @Override
        int firstBlockLength() {
            return getName().length() + content.length() + 1;
        }

        @Override
        boolean isSingleBlock() {
            return true;
        }

    }

    private static class WrappedPropertyData extends PropertyData {

        private final PropertyData content;
        private final String prefix;
        private final String suffix;

        public WrappedPropertyData(String prefix, PropertyData content, String suffix) {
            super(content.name);
            this.prefix = prefix;
            this.content = content;
            this.suffix = suffix;
        }

        @Override
        void writeContentTo(ToStringWriter writer) {
            writer.append(prefix);
            content.writeContentTo(writer);
            writer.append(suffix);
        }

        @Override
        int firstBlockLength() {
            if (content.isSingleBlock()) {
                return prefix.length() + content.firstBlockLength() + suffix.length();
            }
            return prefix.length() + content.firstBlockLength();
        }

        @Override
        boolean isSingleBlock() {
            return content.isSingleBlock();
        }

    }

    private static class ComplexPropertyData extends PropertyData {

        private final String type;
        private final ComplexPropertyData parent;
        private final List<PropertyData> properties;

        ComplexPropertyData(ComplexPropertyData source, ComplexPropertyData parent) {
            super(source.getName());
            this.type = source.type;
            this.parent = parent;
            this.properties = source.properties;
        }

        ComplexPropertyData(String name, ToStringPropertyBuilder source) {
            super(name);
            this.type = source.target.getClass().getSimpleName();
            this.parent = null;
            ArrayList<PropertyData> p = new ArrayList<>();
            source.properties.forEach((t) -> {
                if (t instanceof ComplexPropertyData) {
                    p.add(new ComplexPropertyData((ComplexPropertyData) t, this));
                } else {
                    p.add(t);
                }
            });
            properties = Collections.unmodifiableList(p);
        }

        @Override
        void writeContentTo(ToStringWriter writer) {
            writer.append(type).append("{");
            Iterator<PropertyData> iterator = properties.iterator();
            if (iterator.hasNext()) {
                writer = writer.indent();
                PropertyData pd = iterator.next();
                if ((pd.firstBlockLength() + writer.getCurrentLineLength() + 2) > LINE_BREAK_COL) {
                    writer.appendLine();
                }
                pd.writeTo(writer);
                while (iterator.hasNext()) {
                    pd = iterator.next();
                    if ((pd.firstBlockLength() + writer.getCurrentLineLength() + 2) > LINE_BREAK_COL) {
                        writer.append(",").appendLine();
                    } else {
                        writer.append(", ");
                    }
                    pd.writeTo(writer);
                }
            }
            writer.append("}");
        }

        @Override
        int firstBlockLength() {
            return getName().length() + type.length() + ((properties.isEmpty()) ? 3 : 2);
        }

        @Override
        boolean isSingleBlock() {
            return properties.isEmpty();
        }

    }

    private static class ToStringWriter {

        private final LinkedList<StringBuilder> builderList;
        private final String indent;
        private ToStringWriter parent;

        ToStringWriter() {
            builderList = new LinkedList<>();
            builderList.addLast(new StringBuilder());
            indent = "";
            parent = null;
        }

        ToStringWriter(ToStringWriter parent) {
            builderList = (this.parent = parent).builderList;
            indent = "  " + parent.indent;
        }

        int getCurrentLineLength() {
            int result = builderList.getLast().length();
            if (result == 0) {
                return indent.length();
            }
            return result;
        }

        ToStringWriter append(CharSequence text) {
            StringBuilder current = builderList.getLast();
            if (current.length() == 0) {
                current.append(indent);
            }
            current.append(text);
            return this;
        }

        ToStringWriter appendLine() {
            builderList.addLast(new StringBuilder());
            return this;
        }

        ToStringWriter indent() {
            return new ToStringWriter(this);
        }
    }
}
