/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scheduler.App;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class BusyWaiter implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "view/BusyWaiter";
    
    /**
     * The path of the View associated with this controller.
     */
    public static final String FXML_RESOURCE_NAME = "/view/BusyWaiter.fxml";
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private VBox mainVBox;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public static boolean ShowAndRunAsync(String title, String message, String errorMessage, Runnable runnable) {
        ValueAndResult<Object> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            runnable.run();
                            Platform.runLater(() -> {
                                try { result.success.set(true); }
                                finally { stage.hide(); }
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> {
                                try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                                finally { result.error.set(ex); }
                            });
                            Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            });
        });
        return result.success.get();
    }
    
    public static <R> ValueAndResult<R> ShowAndRunAsync(String title, String message, String errorMessage, Supplier<R> supplier) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            final R r = supplier.get();
                            Platform.runLater(() -> {
                                try {
                                    result.result.set(r);
                                    result.success.set(true);
                                } finally { stage.hide(); }
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> {
                                try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                                finally { result.error.set(ex); }
                            });
                            Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            });
        });
        return result;
    }
    
    public static <T, R> ValueAndResult<R> ShowAndRunAsync(String title, String message, String errorMessage, T t, Function<T, R> function) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            final R r = function.apply(t);
                            Platform.runLater(() -> {
                                try {
                                    result.result.set(r);
                                    result.success.set(true);
                                } finally { stage.hide(); }
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> {
                                try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                                finally { result.error.set(ex); }
                            });
                            Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            });
        });
        return result;
    }
    
    public static <T, U, R> ValueAndResult<R> ShowAndRunAsync(String title, String message, String errorMessage, T t, U u, BiFunction<T, U, R> function) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            final R r = function.apply(t, u);
                            Platform.runLater(() -> {
                                try {
                                    result.result.set(r);
                                    result.success.set(true);
                                } finally { stage.hide(); }
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> {
                                try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                                finally { result.error.set(ex); }
                            });
                            Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            });
        });
        return result;
    }
    
    public static boolean ShowAndRun(String title, String message, String errorMessage, Runnable runnable) {
        ValueAndResult<Object> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                try {
                    runnable.run();
                    try { result.success.set(true); }
                    finally { stage.hide(); }
                } catch (Exception ex) {
                    try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                    finally { result.error.set(ex); }
                    Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        return result.success.get();
    }
    
    public static <R> ValueAndResult<R> ShowAndRun(String title, String message, String errorMessage, Supplier<R> supplier) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                try {
                    final R r = supplier.get();
                    try {
                        result.result.set(r);
                        result.success.set(true);
                    } finally { stage.hide(); }
                } catch (Exception ex) {
                    try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                    finally { result.error.set(ex); }
                    Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        return result;
    }
    
    public static <T, R> ValueAndResult<R> ShowAndRun(String title, String message, String errorMessage, T t, Function<T, R> function) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                try {
                    final R r = function.apply(t);
                    try {
                        result.result.set(r);
                        result.success.set(true);
                    } finally { stage.hide(); }
                } catch (Exception ex) {
                    try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                    finally { result.error.set(ex); }
                    Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        return result;
    }
    
    public static <T, U, R> ValueAndResult<R> ShowAndRun(String title, String message, String errorMessage, T t, U u, BiFunction<T, U, R> function) {
        ValueAndResult<R> result = new ValueAndResult<>();
        App.showAndWait(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, 320, 240, (scheduler.App.LoaderContext<BusyWaiter> context) -> {
            BusyWaiter controller = new BusyWaiter();
            Stage stage = context.getStage();
            stage.setTitle(title);
            controller.messageLabel.setText(message);
            stage.setOnShown((event) -> {
                try {
                    final R r = function.apply(t, u);
                    try {
                        result.result.set(r);
                        result.success.set(true);
                    } finally { stage.hide(); }
                } catch (Exception ex) {
                    try { controller.setErrorState(context.getResourceBundle(), errorMessage, stage); }
                    finally { result.error.set(ex); }
                    Logger.getLogger(BusyWaiter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        return result;
    }
    
    private void setErrorState(ResourceBundle rb, String errorMessage, Stage stage) {
        Label errorLabel = new Label();
        errorLabel.setText(errorMessage);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setWrapText(true);
        errorLabel.setTextFill(Color.RED);
        mainVBox.getChildren().add(errorLabel);
        VBox.setVgrow(errorLabel, Priority.SOMETIMES);
        ButtonBar buttonBar = new ButtonBar();
        mainVBox.getChildren().add(buttonBar);
        Button okButton = new Button();
        okButton.setText(rb.getString("ok"));
        okButton.setOnAction((event) -> { stage.hide(); });
        buttonBar.getButtons().add(okButton);
    }
    
    public static class ValueAndResult<R> {
        private final ReadOnlyBooleanWrapper success = new ReadOnlyBooleanWrapper(false);
        public boolean isSuccess() { return success.get(); }
        public ReadOnlyBooleanProperty successProperty() { return success.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<R> result = new ReadOnlyObjectWrapper<>();
        public R getResult() { return result.get(); }
        public ReadOnlyObjectProperty<R> resultProperty() { return result.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectWrapper<Throwable> error = new ReadOnlyObjectWrapper<>();
        public Throwable getError() { return error.get(); }
        public ReadOnlyObjectProperty<Throwable> errorProperty() { return error.getReadOnlyProperty(); }
    }
}
