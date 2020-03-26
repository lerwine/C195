package scheduler.view.country;

import java.util.Objects;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.AppResources;
import scheduler.dao.CountryImpl;
import scheduler.dao.DataObjectImpl.Factory_obsolete;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreLabeled;
import scheduler.util.ValueBindings;
import scheduler.view.EditItem;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/EditCountry")
@FXMLResource("/scheduler/view/country/EditCountry.fxml")
public final class EditCountry extends EditItem.EditController<CountryImpl, CountryModel> {

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New Country"}.
     */
    public static final String RESOURCEKEY_ADDNEWCOUNTRY = "addNewCountry";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cities"}.
     */
    public static final String RESOURCEKEY_CITIES = "cities";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "There are %d addresses that reference this city."}.
     */
    public static final String RESOURCEKEY_DELETEMSGMULTIPLE = "deleteMsgMultiple";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This is one address that references this city."}.
     */
    public static final String RESOURCEKEY_DELETEMSGSINGLE = "deleteMsgSingle";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit Country"}.
     */
    public static final String RESOURCEKEY_EDITCOUNTRY = "editCountry";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading cities. Please wait...."}.
     */
    public static final String RESOURCEKEY_LOADINGCITIES = "loadingCities";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name:"}.
     */
    public static final String RESOURCEKEY_NAME = "name";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name cannot be empty."}.
     */
    public static final String RESOURCEKEY_NAMECANNOTBEEMPTY = "nameCannotBeEmpty";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That country is referenced by one or more cities and cann
     * ot be deleted."}.
     */
    public static final String RESOURCEKEY_COUNTRYHASCITIES = "countryHasCities";

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "A city with that name has already been added."}.
     */
    public static final String RESOURCEKEY_SAVECONFLICTMESSAGE = "saveConflictMessage";

    @FXML
    private TextField nameTextField;

    @FXML
    private Label nameError;

    private StringBinding normalizedName;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    protected void initialize() {
        Objects.requireNonNull(nameTextField, String.format("fx:id=\"nameTextField\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setText(getModel().getName());
        normalizedName = ValueBindings.asNormalized(nameTextField.textProperty());
        normalizedName.isNotEmpty().addListener((observable) -> {
            if (normalizedName.isNotEmpty().get()) {
                collapseNode(nameError);
            } else {
                restoreLabeled(nameError, getResources().getString(RESOURCEKEY_NAMECANNOTBEEMPTY));
            }
        });
    }

    @Override
    protected BooleanExpression getValidationExpression() {
        return normalizedName.isNotEmpty();
    }

    @Override
    protected Factory_obsolete<CountryImpl, CountryModel> getDaoFactory() {
        return CountryImpl.getFactory();
    }
}
