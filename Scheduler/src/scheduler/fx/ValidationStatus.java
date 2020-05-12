package scheduler.fx;

import java.util.Optional;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.fx.ValidationStatus}
 */
public enum ValidationStatus {
    OK(0),
    INFO(1),
    WARNING(2),
    ERROR(3);

    private final int value;
    private final boolean valid;
    private final boolean notErrorOrWarning;
    private final Optional<CssClassName> cssClass;

    private ValidationStatus(int value) {
        this.value = value;
        valid = value < 3;
        notErrorOrWarning = value < 2;
        if (value > 0) {
            this.cssClass = Optional.of(CssClassName.VALIDATION_CSS_CLASSES.get(value - 1));
        } else {
            this.cssClass = Optional.empty();
        }
    }

    public int getValue() {
        return value;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isNotErrorOrWarning() {
        return notErrorOrWarning;
    }

    public Optional<CssClassName> getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return super.toString(); // TODO: Implement scheduler.fx.ValidationStatus#toString
    }

}
