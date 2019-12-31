package view.country;

import view.EditItemController;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
public class EditCountry extends EditItemController<CountryRow> {
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
        return showAndWait(EditCountry.class, 640, 480, (ContentChangeContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(new CountryRow());
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_ADDNEWCOUNTRY));
        }, (ContentChangeContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CountryRow row) {
        return showAndWait(EditCountry.class, 640, 480, (ContentChangeContext<EditCountry> context) -> {
            EditCountry controller = context.getController();
            controller.setModel(row);
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_EDITCOUNTRY));
        }, (ContentChangeContext<EditCountry> context) -> {
            return !context.getController().isCanceled();
        });
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
    protected boolean saveChanges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}