package view.city;

import view.EditItemController;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.db.CityRow;
import model.db.CountryRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/city/EditCity")
@FXMLResource("/view/city/EditCity.fxml")
public class EditCity extends EditItemController<CityRow> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_ADDNEWCITY = "addNewCity";
//    public static final String RESOURCEKEY_COUNTRY = "country";
    public static final String RESOURCEKEY_EDITCITY = "editCity";
//    public static final String RESOURCEKEY_NAME = "name";
//    public static final String RESOURCEKEY_NAMECANNOTBEEMPTY = "nameCannotBeEmpty";
//    public static final String RESOURCEKEY_BY = "by";
//    public static final String RESOURCEKEY_CANCEL = "cancel";
//    public static final String RESOURCEKEY_CREATED = "created";
//    public static final String RESOURCEKEY_SAVE = "save";
//    public static final String RESOURCEKEY_UPDATED = "updated";

    //</editor-fold>
    private int countryId;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private Label nameError;
    
    @FXML
    private Label countryLabel;
    
    @FXML
    private ComboBox<CountryRow> countryComboBox;
    
    public static CityRow addNew() {
        return showAndWait(EditCity.class, 640, 480, (ContentChangeContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(new CityRow());
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_ADDNEWCITY));
        }, (ContentChangeContext<EditCity> context) -> {
            EditCity controller = context.getController();
            return (controller.isCanceled()) ? null : controller.getModel();
        });
    }

    public static boolean edit(CityRow row) {
        return showAndWait(EditCity.class, 640, 480, (ContentChangeContext<EditCity> context) -> {
            EditCity controller = context.getController();
            controller.setModel(row);
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_EDITCITY));
        }, (ContentChangeContext<EditCity> context) -> {
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
