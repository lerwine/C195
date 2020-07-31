package scheduler.view;

/**
 * Represents {@code Segoe UI Symbol} text for displaying UI symbols.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum SymbolText {
    EDIT(""),
    COPY(""),
    DELETE(""),
    DISMISS("✔"),
    HYPHEN_POINT("‧"),
    FILTER(""),
    EXPORT(""),
    LEFT_DOUBLE_ARROW("⇐"),
    RIGHT_DOUBLE_ARROW("⇒"),
    LEFT_ARROWHEAD("⮘"),
    RIGHT_AROWHEAD("⮚"),
    LEFT_HOOK("↩"),
    RIGHT_HOOK("↪"),
    LEFT_DUPLICATE(""),
    RIGHT_DUPLICATE(""),
    HELP("❓");

    private final String value;

    private SymbolText(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
