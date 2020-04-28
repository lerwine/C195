package scheduler.view;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum SymbolButtonValue {
    EDIT(""),
    DELETE(""),
    HYPHEN_POINT("‧"),
    FILTER(""),
    EXPORT(""),
    HELP("❓");

    private final String value;

    private SymbolButtonValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
