package scheduler.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scheduler.AppResources;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.event.DaoChangeAction;
import scheduler.dao.event.DataObjectEvent;
import scheduler.util.AlertHelper;
import scheduler.util.EventHelper;
import static scheduler.util.NodeUtil.collapseNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.annotations.HandlesDataObjectEvent;
import scheduler.view.event.FxmlViewControllerEvent;
import scheduler.view.event.FxmlViewControllerEventListener;
import scheduler.view.event.FxmlViewEventType;
import scheduler.view.model.ItemModel;
import scheduler.view.task.TaskWaiter;

/**
 * Base FXML Controller class for editing {@link ItemModel} items in a new modal window.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of data access object that the model represents.
 * @param <U> The type of model being edited.
 */
@GlobalizationResource("scheduler/view/EditItem")
@FXMLResource("/scheduler/view/EditItem.fxml")
public final class EditItem<T extends DataAccessObject, U extends ItemModel<T>> extends SchedulerController {

    private static final Logger LOG = Logger.getLogger(EditItem.class.getName());

    private EditController<T, U> contentController;

    @FXML
    private StackPane contentPane;

    @FXML
    private Label createdLabel; // Value injected by FXMLLoader

    @FXML
    private Label createDateValue; // Value injected by FXMLLoader

    @FXML
    private Label createdByLabel; // Value injected by FXMLLoader

    @FXML
    private Label createdByValue; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateLabel; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateValue; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateByLabel; // Value injected by FXMLLoader

    @FXML
    private Label lastUpdateByValue; // Value injected by FXMLLoader

    @FXML
    private Button saveChangesButton; // Value injected by FXMLLoader

    @FXML
    private Button deleteButton; // Value injected by FXMLLoader

    @FXML
    private Button cancelButton; // Value injected by FXMLLoader

    private void onContentLoaded(ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = contentController.getFactory().createNew(
                contentController.getFactory().getDaoFactory().createNew());
    }

    private void onContentLoaded(U model, ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController) {
        contentController = viewAndController.getController();
        contentController.model = model;
    }

    private void onInitForNew(Parent view) {
        collapseNode(deleteButton);
        collapseNode(createdLabel);
        collapseNode(createDateValue);
        collapseNode(createdByLabel);
        collapseNode(createdByValue);
        collapseNode(lastUpdateByLabel);
        collapseNode(lastUpdateByValue);
        collapseNode(lastUpdateLabel);
        collapseNode(lastUpdateValue);
        contentPane.getChildren().add(view);
        saveChangesButton.disableProperty().bind(contentController.getValidationExpression().not());
    }

    private void onInitForEdit(Parent view, EditController<T, U> controller) {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        Locale.getDefault(Locale.Category.DISPLAY);
        createdByValue.setText(controller.model.getCreatedBy());
        createDateValue.setText(dtf.format(controller.model.getCreateDate()));
        lastUpdateByValue.setText(controller.model.getLastModifiedBy());
        lastUpdateValue.setText(dtf.format(controller.model.getLastModifiedDate()));
        contentPane.getChildren().add(view);
        saveChangesButton.disableProperty().bind(contentController.getValidationExpression().not());
    }

    @HandlesDataObjectEvent
    protected void onDataObjectEvent(DataObjectEvent<? extends DataAccessObject> event) {
        EventHelper.fireDataObjectEvent(contentController, event);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdLabel != null : String.format("fx:id=\"createdLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateLabel != null : String.format("fx:id=\"lastUpdateLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createDateValue != null : String.format("fx:id=\"createDateValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdByLabel != null : String.format("fx:id=\"createdByLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert createdByValue != null : String.format("fx:id=\"createdByValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateValue != null : String.format("fx:id=\"lastUpdateValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateByLabel != null : String.format("fx:id=\"lastUpdateByLabel\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        assert lastUpdateByValue != null : String.format("fx:id=\"lastUpdateByValue\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()));
        Objects.requireNonNull(saveChangesButton, String.format("fx:id=\"saveChangesButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            contentController.updateModel(contentController.model);
            ItemModel.ModelFactory<T, U> factory = contentController.getFactory();
            T dao = factory.updateDAO(contentController.model);
            TaskWaiter.startNow(new SaveTask(dao, (Stage) saveChangesButton.getScene().getWindow()));
        });
        Objects.requireNonNull(deleteButton, String.format("fx:id=\"deleteButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) saveChangesButton.getScene().getWindow(), LOG,
                    AppResources.getResourceString(AppResources.RESOURCEKEY_CONFIRMDELETE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
            if (response.isPresent() && response.get() == ButtonType.YES) {
                TaskWaiter.startNow(new DeleteTask((Stage) saveChangesButton.getScene().getWindow()));
            }
        });
        Objects.requireNonNull(cancelButton, String.format("fx:id=\"cancelButton\" was not injected: check your FXML file '%s'.",
                AppResources.getFXMLResourceName(getClass()))).setOnAction((event) -> {
            cancelButton.getScene().getWindow().hide();
        });
    }

    /**
     * Base class for item edit content controllers.
     *
     * @param <T> The type of data access object that the model represents.
     * @param <U> The type of model being edited.
     */
    public static abstract class EditController<T extends DataAccessObject, U extends ItemModel<T>> extends SchedulerController {

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Add"}.
         */
        public static final String RESOURCEKEY_ADD = "add";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "* Required"}.
         */
        public static final String RESOURCEKEY_REQUIRED = "required";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created"}.
         */
        public static final String RESOURCEKEY_CREATED = "created";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "by"}.
         */
        public static final String RESOURCEKEY_BY = "by";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated"}.
         */
        public static final String RESOURCEKEY_UPDATED = "updated";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Save"}.
         */
        public static final String RESOURCEKEY_SAVE = "save";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Cancel"}.
         */
        public static final String RESOURCEKEY_CANCEL = "cancel";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Would you like to save, anyway?"}.
         */
        public static final String RESOURCEKEY_SAVEANYWAY = "saveAnyway";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
         */
        public static final String RESOURCEKEY_DELETE = "delete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Confirm Delete"}.
         */
        public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "This action cannot be undone!..."}.
         */
        public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Validation Error"}.
         */
        public static final String RESOURCEKEY_VALIDATIONERROR = "validationError";

        protected static <T extends DataAccessObject, U extends ItemModel<T>> U editNew(Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new FxmlViewControllerEventListener<Parent, EditItem<T, U>>() {

                private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

                @Override
                public void onFxmlViewControllerEvent(FxmlViewControllerEvent<Parent, EditItem<T, U>> event) {
                    LOG.log(Level.INFO, String.format("Handling FxmlViewControllerEvent %s for %s", event.getType(),
                            event.getController().getClass().getName()));
                    switch (event.getType()) {
                        case LOADED:
                            try {
                                viewAndController = ViewControllerLoader.loadViewAndController(controllerClass, EditItem.class);
                                event.getController().onContentLoaded(viewAndController);
                                EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                        viewAndController.toEvent(this, FxmlViewEventType.LOADED, stage));
                            } catch (IOException ex) {
                                // PENDING: Internationalize message
                                AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading edit window content", ex);
                            }
                            break;
                        case BEFORE_SHOW:
                            event.getController().onInitForNew(viewAndController.getView());
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.BEFORE_SHOW, event.getStage()));
                            break;
                        case SHOWN:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.SHOWN, event.getStage()));
                            break;
                        case UNLOADED:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.UNLOADED, event.getStage()));
                            break;
                    }
                }

            }, stage, EditItem.class);

            U model = fc.contentController.model;
            T dataAccessObject = (null == model) ? null : model.getDataObject();
            if (null != dataAccessObject) {
                EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.CREATED,
                        dataAccessObject));
            }
            return model;
        }

        protected static <T extends DataAccessObject, U extends ItemModel<T>> U edit(U model, Class<? extends EditController<T, U>> controllerClass,
                MainController mainController, Stage stage) throws IOException {
            EditItem<T, U> fc = ViewControllerLoader.showAndWait(new FxmlViewControllerEventListener<Parent, EditItem<T, U>>() {

                private ViewAndController<? extends Parent, ? extends EditController<T, U>> viewAndController;

                @Override
                public void onFxmlViewControllerEvent(FxmlViewControllerEvent<Parent, EditItem<T, U>> event) {
                    switch (event.getType()) {
                        case LOADED:
                            try {
                                event.getController().onContentLoaded(model, ViewControllerLoader.loadViewAndController(controllerClass));
                                EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                        viewAndController.toEvent(this, FxmlViewEventType.LOADED, stage));
                            } catch (IOException ex) {
                                AlertHelper.showErrorAlert(event.getStage(), LOG, "Error loading edit window content", ex);
                            }
                            break;
                        case BEFORE_SHOW:
                            event.getController().onInitForEdit(viewAndController.getView(), viewAndController.getController());
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.BEFORE_SHOW, event.getStage()));
                            break;
                        case SHOWN:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.SHOWN, event.getStage()));
                            break;
                        case UNLOADED:
                            EventHelper.fireFxmlViewEvent(viewAndController.getController(),
                                    viewAndController.toEvent(event.getController(), FxmlViewEventType.UNLOADED, event.getStage()));
                            break;
                    }
                }

            }, stage, EditItem.class);

            U r = fc.contentController.model;
            T dataAccessObject = (null == r) ? null : r.getDataObject();
            if (null != dataAccessObject) {
                if (dataAccessObject.getRowState() == DataRowState.DELETED) {
                    EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.DELETED,
                            dataAccessObject));
                } else {
                    EventHelper.fireDataObjectEvent(mainController, new DataObjectEvent<>(fc.contentController, DaoChangeAction.UPDATED,
                            dataAccessObject));
                }
            }
            return r;
        }

        private U model;

        /**
         * Gets the current {@link ItemModel} being edited.
         *
         * @return the current {@link ItemModel} being edited.
         */
        protected U getModel() {
            return model;
        }

        protected abstract ItemModel.ModelFactory<T, U> getFactory();

        /**
         * Gets the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         *
         * @return the {@link BooleanExpression} that indicates whether the property values for the current item are valid.
         */
        protected abstract BooleanExpression getValidationExpression();

        protected abstract void updateModel(U model);

    }

    private class SaveTask extends TaskWaiter<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;

        SaveTask(T dataAccessobject, Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVINGCHANGES));
            this.dataAccessobject = dataAccessobject;
            daoFactory = contentController.getFactory().getDaoFactory();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                contentController.getFactory().updateItem(contentController.model, dataAccessobject);
                owner.hide();
            } else {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_SAVEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORSAVINGCHANGES), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = daoFactory.getSaveDbConflictMessage(contentController.model.getDataObject(), connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            
            daoFactory.save(contentController.model.getDataObject(), connection);

            return "";
        }
    }

    private class DeleteTask extends TaskWaiter<String> {

        private final T dataAccessobject;
        private final DaoFactory<T> daoFactory;

        DeleteTask(Stage stage) {
            super(stage, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETINGRECORD));
            daoFactory = contentController.getFactory().getDaoFactory();
            dataAccessobject = contentController.model.getDataObject();
        }

        @Override
        protected void processResult(String message, Stage owner) {
            if (null == message || message.trim().isEmpty()) {
                owner.hide();
            } else {
                AlertHelper.showWarningAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE), message);
            }
        }

        @Override
        protected void processException(Throwable ex, Stage owner) {
            AlertHelper.showErrorAlert(owner, LOG, AppResources.getResourceString(AppResources.RESOURCEKEY_DELETEFAILURE),
                    AppResources.getResourceString(AppResources.RESOURCEKEY_ERRORDELETINGFROMDB), ex);
        }

        @Override
        protected String getResult(Connection connection) throws SQLException {
            String message = daoFactory.getDeleteDependencyMessage(dataAccessobject, connection);
            if (null != message && !message.trim().isEmpty()) {
                return message;
            }
            daoFactory.delete(dataAccessobject, connection);
            return null;
        }
    }

}
