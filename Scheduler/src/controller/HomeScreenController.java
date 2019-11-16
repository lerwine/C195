/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author webmaster
 */
public class HomeScreenController implements Initializable {
    /**
     * The path of the View for Modify Part and Add Part.
     */
    public static final String VIEW_PATH = "/view/HomeScreen.fxml";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    /**
     * Utility method to initialize the controller and switch scenes.
     * 
     * @param <T> The type of controller to initialize.
     * @param eventSource The source Node for the event.
     * @param path The path of the FXML file to load.
     * @param initializeController Function for initializing the controller.
     */
    public static <T> void changeScene(Node eventSource, String path, java.util.function.Consumer<T> initializeController) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(HomeScreenController.class.getResource(path));
        Stage stage = (Stage)eventSource.getScene().getWindow();
        try {
            stage.setScene(new Scene(loader.load()));
            T controller = loader.getController();
            if (initializeController != null)
                initializeController.accept(controller);
        } catch (IOException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        stage.show();
    }
    
    /**
     * Utility method to change switch to another scene.
     * 
     * @param eventSource The source node for the event.
     * @param path The path of the FXML file to load.
     */
    public static void changeScene(Node eventSource, String path) {
        changeScene(eventSource, path, null);
    }
    
}
