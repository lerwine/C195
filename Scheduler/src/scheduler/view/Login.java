package scheduler.view;

import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.util.AlertHelper;
import scheduler.util.ResourceBundleHelper;
import scheduler.observables.BindingHelper;
import static scheduler.util.NodeUtil.bindCssCollapse;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
import scheduler.view.city.SupportedLocale;
import scheduler.view.event.FxmlViewEvent;

/**
 * FXML Controller class for the application login screen.
 * <p>
 * The associated view is {@code /resources/scheduler/view/Login.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/Login")
@FXMLResource("/scheduler/view/Login.fxml")
public final class Login extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(Login.class.getName());

    public static final String RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN = "appointmentSchedulerLogin";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_EXIT = "exit";
    public static final String RESOURCEKEY_INVALIDCREDENTIALS = "invalidCredentials";
    public static final String RESOURCEKEY_LOGIN = "login";
    public static final String RESOURCEKEY_LOGINERROR = "loginError";
    public static final String RESOURCEKEY_PASSWORD = "password";
    public static final String RESOURCEKEY_USERNAME = "userName";
    public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";
    public static final String RESOURCEKEY_EMPTYUSERNAME = "emptyUserName";
    public static final String RESOURCEKEY_EMPTYPASSWORD = "emptyPassword";

    /**
     * The {@link ComboBox} that lets the user select their preferred language.
     */
    @FXML
    private ComboBox<SupportedLocale> languageComboBox;

    /**
     * The {@link Label} for the User Name {@link TextField}.
     */
    @FXML
    private Label userNameLabel;

    /**
     * The {@link TextField} where the user provides the User Name.
     */
    @FXML
    private TextField userNameTextField;

    /**
     * The {@link Label} for the User Name validation message.
     */
    @FXML
    private Label userNameValidationLabel;

    /**
     * The {@link Label} for the {@link PasswordField}.
     */
    @FXML
    private Label passwordLabel;

    /**
     * The {@link PasswordField} where the user provides the password.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * The {@link Label} for the Password validation message.
     */
    @FXML
    private Label passwordValidationLabel;

    /**
     * The {@link Button} which begins the login attempt.
     */
    @FXML
    private Button loginButton;

    /**
     * The {@link Button} which cancels login and closes the application.
     */
    @FXML
    private Button exitButton;

    private ResourceBundle currentResourceBundlex;

    private SingleSelectionModel<SupportedLocale> languageSelectionModel;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert userNameValidationLabel != null : "fx:id=\"userNameValidationLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert passwordValidationLabel != null : "fx:id=\"passwordValidationLabel\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'LoginScene.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'LoginScene.fxml'.";
    }

    private ReadOnlyObjectWrapper<ResourceBundle> resourceBundle;

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<BorderPane> event) {
        resourceBundle = new ReadOnlyObjectWrapper<>(getResources());
        languageSelectionModel = languageComboBox.getSelectionModel();
        languageSelectionModel.selectedItemProperty().addListener(this::onLanguageChanged);
        
        ObservableList<SupportedLocale> languages = FXCollections.observableArrayList(SupportedLocale.values());
        languageComboBox.setItems(languages);
        languageSelectionModel.select(AppResources.getCurrentLocale());

        userNameLabel.textProperty().bind(Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_USERNAME), resourceBundle));
        userNameValidationLabel.visibleProperty().bind(isUserNameValid().not());
        bindCssCollapse(userNameValidationLabel, isUserNameValid());

        passwordLabel.textProperty().bind(Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_PASSWORD), resourceBundle));
        passwordValidationLabel.visibleProperty().bind(isPasswordValid().not());
        bindCssCollapse(passwordValidationLabel, isPasswordValid());

        loginButton.textProperty().bind(Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_LOGIN), resourceBundle));
        exitButton.textProperty().bind(Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_EXIT), resourceBundle));
        loginButton.disableProperty().bind(isUserNameValid().and(isPasswordValid()).not().or(languageSelectionModel.selectedItemProperty().isNull()));
        resourceBundle.set(ResourceBundleHelper.getBundle(Login.class));
    }
    
    private void onLanguageChanged(Observable observable) {
        SupportedLocale supportedLocale = ((ReadOnlyObjectProperty<SupportedLocale>)observable).get();
        languageComboBox.getButtonCell().setText(SupportedLocale.toDisplayLanguage(supportedLocale));
        ResourceBundle rb = ResourceBundleHelper.getBundle(Login.class);
        resourceBundle.set(rb);
        Scene scene = languageComboBox.getScene();
        if (null != scene) {
            Window window = (Stage) scene.getWindow();
            if (null != window && window instanceof Stage) {
                ((Stage) window).setTitle(rb.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            }
        }
    }
    
    private BooleanBinding isUserNameValid() {
        return BindingHelper.notNullOrWhiteSpace(userNameTextField.textProperty());
    }
    
    private BooleanBinding isPasswordValid() {
        return BindingHelper.notNullOrWhiteSpace(passwordField.textProperty());
    }
    
    @FXML
    void loginButtonClick(ActionEvent event) {
        Stage stage = (Stage) userNameTextField.getScene().getWindow();
        Scheduler.tryLoginUser(stage, userNameTextField.getText(), passwordField.getText(), (ex) -> {
            if (ex == null) {
                AlertHelper.showErrorAlert(stage, LOG, currentResourceBundlex.getString(RESOURCEKEY_LOGINERROR),
                        currentResourceBundlex.getString(RESOURCEKEY_INVALIDCREDENTIALS), ex);
            } else {
                AlertHelper.showErrorAlert(stage, LOG, currentResourceBundlex.getString(RESOURCEKEY_LOGINERROR),
                        currentResourceBundlex.getString(RESOURCEKEY_VALIDATIONERROR), ex);
            }
        });
    }

    @FXML
    void exitButtonClick(ActionEvent event) {
        languageComboBox.getScene().getWindow().hide();
    }

}
