package scheduler.observables;

import java.util.ArrayList;
import java.util.Objects;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import scheduler.view.CssClassName;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.observables.CssClassSwitchBinding}
 */
public class CssClassSwitchBinding  {
    private final ObservableList<String> styleClass;
//    private final ObservableList<Observable> dependencies;
//    private final ObservableList<Observable> readOnlyDependencies;
    private final String[] addWhenValid;
    private final String[] removeWhenValid;
    private final String[] addWhenInvalid;
    private final String[] removeWhenInvalid;
    private final BooleanBinding valid;

    public static CssClassSwitchBinding collapseIfTrue(Node target, BooleanBinding test) {
        return new CssClassSwitchBinding(target.getStyleClass(), new CssClassName[] { CssClassName.COLLAPSED }, null, test);
    }
    
    
    public BooleanBinding isValid() {
        return valid;
    }
    
    private void onValidationChange(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        String[] add, remove;
        if (newValue) {
            add = addWhenValid;
            remove = removeWhenValid;
        } else {
            add = addWhenInvalid;
            remove = removeWhenInvalid;
        }
        if (add.length > 0) {
            for (String c : add) {
                if (!styleClass.contains(c))
                    styleClass.add(c);
            }
        }
        if (remove.length > 0) {
            for (String c : remove) {
                styleClass.remove(c);
            }
        }
    }
    
    public CssClassSwitchBinding(final ObservableList<String> styleClass, CssClassName[] ifValid, CssClassName[] ifInvalid,
            BooleanBinding isValid) {
        this.styleClass = Objects.requireNonNull(styleClass);
        valid = isValid;
        ArrayList<String> whenValid = new ArrayList<>();
        ArrayList<String> whenInvalid = new ArrayList<>();
        if (null != ifValid && ifValid.length > 0) {
            for (CssClassName n : ifValid) {
                if (null != n) {
                    String s = n.toString();
                    if (!whenValid.contains(s))
                        whenValid.add(s);
                }
            }
        }
        if (null != ifInvalid && ifInvalid.length > 0) {
            for (CssClassName n : ifInvalid) {
                if (null != n) {
                    String s = n.toString();
                    if (!whenInvalid.contains(s))
                        whenInvalid.add(s);
                }
            }
        }
        if (whenValid.isEmpty()) {
            addWhenValid = removeWhenInvalid = new String[0];
            if (whenInvalid.isEmpty())
                removeWhenValid = addWhenInvalid = addWhenValid;
            else {
                removeWhenValid = addWhenInvalid = whenInvalid.toArray(new String[whenInvalid.size()]);
            }
        } else {
            addWhenValid = whenValid.toArray(new String[whenValid.size()]);
            if (whenInvalid.isEmpty()) {
                removeWhenInvalid = addWhenValid;
                addWhenInvalid = removeWhenValid = new String[0];
            } else {
                addWhenInvalid = whenInvalid.toArray(new String[whenInvalid.size()]);
                removeWhenInvalid = whenValid.stream().filter((t) -> !whenInvalid.contains(t)).toArray(String[]::new);
                removeWhenValid = whenInvalid.stream().filter((t) -> !whenValid.contains(t)).toArray(String[]::new);
            }
        }
        valid.addListener(this::onValidationChange);
        boolean v = isValid.get();
        onValidationChange(isValid, null, v);
    }

}
