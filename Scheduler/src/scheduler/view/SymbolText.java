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
    HYPHEN_POINT("‧"),
    FILTER(""),
    EXPORT(""),
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
