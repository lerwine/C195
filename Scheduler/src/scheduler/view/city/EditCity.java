package scheduler.view.city;

import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduler.dao.CityImpl;
import scheduler.dao.CountryImpl;
import scheduler.view.EditItem;
import scheduler.view.ItemController;
import scheduler.view.ViewManager;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/city/EditCity")
@FXMLResource("/scheduler/view/city/EditCity.fxml")
public class EditCity extends ItemController<CityModel> {
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
    
    public static CityModel addNew(ViewManager parentViewManager) {
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.showAndWait(parentViewManager, EditCity.class, new CityModel(new CityImpl()), 640, 480);
        return (result.isSuccessful()) ? result.getTarget() : null;
    }

    public static boolean edit(ViewManager parentViewManager, CityModel item) {
        EditItem.ShowAndWaitResult<CityModel> result = EditItem.showAndWait(parentViewManager, EditCity.class, item, 640, 480);
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
    public void accept(EditItem<CityModel> context) {
        context.getChildViewManager().setWindowTitle(getResources().getString((context.getTarget().isNewItem()) ? RESOURCEKEY_ADDNEWCITY : RESOURCEKEY_EDITCITY));
    }

    @Override
    public Boolean apply(EditItem<CityModel> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
