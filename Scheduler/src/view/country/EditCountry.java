package view.country;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import view.EditItem;
import model.db.CountryRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/country/EditCountry")
@FXMLResource("/view/country/EditCountry.fxml")
public class EditCountry extends view.Controller implements view.ItemController<CountryRow> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_ADDNEWCOUNTRY = "addNewCountry";
    public static final String RESOURCEKEY_EDITCOUNTRY = "editCountry";
//    public static final String RESOURCEKEY_NAME = "name";
//    public static final String RESOURCEKEY_NAMECANNOTBEEMPTY = "nameCannotBeEmpty";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_UPDATED = "updated";

    //</editor-fold>
    
    private String name;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    public static CountryRow addNew() {
        EditItem.ShowAndWaitResult<CountryRow> result = EditItem.showAndWait(EditCountry.class, new CountryRow(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(CountryRow row) {
        EditItem.ShowAndWaitResult<CountryRow> result = EditItem.showAndWait(EditCountry.class, row, 640, 480);
        return result.isSuccessful();
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BooleanExpression validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void accept(EditItem<CountryRow> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWCOUNTRY : RESOURCEKEY_EDITCOUNTRY));
    }

    @Override
    public Boolean apply(EditItem<CountryRow> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
