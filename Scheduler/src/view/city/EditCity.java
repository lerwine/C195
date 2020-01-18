package view.city;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import view.EditItem;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/city/EditCity")
@FXMLResource("/view/city/EditCity.fxml")
public class EditCity extends view.SchedulerController implements view.ItemController<CityImpl> {
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add New City"}.
     */
    public static final String RESOURCEKEY_ADDNEWCITY = "addNewCity";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit City"}.
     */
    public static final String RESOURCEKEY_EDITCITY = "editCity";

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
    private ComboBox<CountryImpl> countryComboBox;
    
    public static CityImpl addNew() {
        EditItem.ShowAndWaitResult<CityImpl> result = EditItem.showAndWait(EditCity.class, new CityImpl(), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(CityImpl row) {
        EditItem.ShowAndWaitResult<CityImpl> result = EditItem.showAndWait(EditCity.class, row, 640, 480);
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
    public void accept(EditItem<CityImpl> context) {
        context.setWindowTitle(getResources().getString((context.isNewRow().get()) ? RESOURCEKEY_ADDNEWCITY : RESOURCEKEY_EDITCITY));
    }

    @Override
    public Boolean apply(EditItem<CityImpl> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
