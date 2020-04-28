package scheduler.view;

import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
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
import scheduler.SupportedLocale;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ResourceBundleHelper;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.FxmlViewEventHandling;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesFxmlViewEvent;
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
public final class Login {

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

    private ReadOnlyObjectWrapper<ResourceBundle> resourceBundle;

    private SingleSelectionModel<SupportedLocale> languageSelectionModel;
    private BooleanBinding loginFormInvalid;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

//    @FXML // URL location of the FXML file that was given to the FXMLLoader
//    private URL location;
    @FXML // fx:id="loginRootBorderPane"
    private BorderPane loginRootBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="languageComboBox"
    private ComboBox<SupportedLocale> languageComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="userNameLabel"
    private Label userNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="userNameTextField"
    private TextField userNameTextField; // Value injected by FXMLLoader

    @FXML // fx:id="userNameValidationLabel"
    private Label userNameValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="passwordLabel"
    private Label passwordLabel; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordValidationLabel"
    private Label passwordValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="loginButton"
    private Button loginButton; // Value injected by FXMLLoader

    @FXML // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader

    private StringBinding userNameTextBinding;
    private StringBinding passwordTextBinding;
    private StringBinding loginTextBinding;
    private StringBinding exitTextBinding;
    private StringBinding userNameValidationMessageBinding;
    private StringBinding passwordValidationMessageBinding;

    @FXML
    void onExitButtonAction(ActionEvent event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void onLanguageComboBoxAction(ActionEvent event) {
        SupportedLocale supportedLocale = languageComboBox.getValue();
        ResourceBundle rb = ResourceBundleHelper.getBundle(Login.class, supportedLocale.getLocale());
        resourceBundle.set(rb);
        Scene scene = languageComboBox.getScene();
        if (null != scene) {
            Window window = (Stage) scene.getWindow();
            if (null != window && window instanceof Stage) {
                ((Stage) window).setTitle(rb.getString(RESOURCEKEY_APPOINTMENTSCHEDULERLOGIN));
            }
        }
    }

    @FXML
    void onLoginButtonAction(ActionEvent event) {
        Stage stage = (Stage) userNameTextField.getScene().getWindow();
        Scheduler.tryLoginUser(stage, loginRootBorderPane, userNameTextField.getText(), passwordField.getText(), (ex) -> {
            ResourceBundle rb = resourceBundle.get();
            if (ex == null) {
                ErrorDetailDialog.logShowAndWait(LOG, rb.getString(RESOURCEKEY_LOGINERROR), stage, ex,
                        rb.getString(RESOURCEKEY_INVALIDCREDENTIALS));
            } else {
                ErrorDetailDialog.logShowAndWait(LOG, rb.getString(RESOURCEKEY_LOGINERROR), stage, ex,
                        rb.getString(RESOURCEKEY_VALIDATIONERROR));
            }
        });
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameLabel != null : "fx:id=\"userNameLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameTextField != null : "fx:id=\"userNameTextField\" was not injected: check your FXML file 'Login.fxml'.";
        assert userNameValidationLabel != null : "fx:id=\"userNameValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordValidationLabel != null : "fx:id=\"passwordValidationLabel\" was not injected: check your FXML file 'Login.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Login.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'Login.fxml'.";

        languageSelectionModel = languageComboBox.getSelectionModel();
        resourceBundle = new ReadOnlyObjectWrapper<>(resources);
    }

    @HandlesFxmlViewEvent(FxmlViewEventHandling.BEFORE_SHOW)
    private void onBeforeShow(FxmlViewEvent<BorderPane> event) {
        ObservableList<SupportedLocale> languages = FXCollections.observableArrayList(SupportedLocale.values());
        languageComboBox.setItems(languages);
        languageSelectionModel.select(AppResources.getCurrentLocale());
        userNameTextBinding = Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_USERNAME), resourceBundle);
        userNameLabel.textProperty().bind(userNameTextBinding);

        passwordTextBinding = Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_PASSWORD), resourceBundle);
        passwordLabel.textProperty().bind(passwordTextBinding);

        loginTextBinding = Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_LOGIN), resourceBundle);
        loginButton.textProperty().bind(loginTextBinding);

        exitTextBinding = Bindings.createStringBinding(() -> resourceBundle.get().getString(RESOURCEKEY_EXIT), resourceBundle);
        exitButton.textProperty().bind(exitTextBinding);

        userNameValidationMessageBinding = Bindings.createStringBinding(() -> {
            ResourceBundle rb = resourceBundle.get();
            String text = userNameTextField.getText();
            if (null == text || text.trim().isEmpty()) {
                return rb.getString(RESOURCEKEY_EMPTYUSERNAME);
            }
            return "";
        }, resourceBundle, userNameTextField.textProperty());
        userNameValidationLabel.textProperty().bind(userNameValidationMessageBinding);
        userNameValidationMessageBinding.addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                collapseNode(userNameValidationLabel);
            } else {
                restoreNode(userNameValidationLabel);
            }
        });

        passwordValidationMessageBinding = Bindings.createStringBinding(() -> {
            ResourceBundle rb = resourceBundle.get();
            String text = passwordField.getText();
            if (null == text || text.trim().isEmpty()) {
                return rb.getString(RESOURCEKEY_EMPTYPASSWORD);
            }
            return "";
        }, resourceBundle, passwordField.textProperty());
        passwordValidationLabel.textProperty().bind(passwordValidationMessageBinding);
        passwordValidationMessageBinding.addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                collapseNode(passwordValidationLabel);
            } else {
                restoreNode(passwordValidationLabel);
            }
        });
        loginFormInvalid = userNameValidationMessageBinding.isNotEmpty().or(passwordValidationMessageBinding.isNotEmpty())
                .or(languageSelectionModel.selectedItemProperty().isNull());
        loginButton.disableProperty().bind(loginFormInvalid);
        resourceBundle.set(ResourceBundleHelper.getBundle(Login.class));
    }

}
