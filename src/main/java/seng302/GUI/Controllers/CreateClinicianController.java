package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.GUI.TFScene;
import seng302.Generic.Main;
import seng302.User.Admin;
import seng302.User.Clinician;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller class for the create admin screen.
 */
public class CreateClinicianController implements Initializable {
    @FXML
    private TextField usernameInput, passwordConfirmInput, firstNameInput, middleNamesInput, lastNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label errorText;
    @FXML
    private AnchorPane background;

    private Clinician clinician;

    private Stage stage;

    public Clinician showAndWait(Stage stage){
        this.stage = stage;
        stage.showAndWait();
        return clinician;
    }

    /**
     * Closes the popup
     */
    public void cancel() {
        stage.close();
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Attempts to create a new clinician based on the information currently provided by the user. Provides appropriate feedback if this fails.
     */
    public void createAccount() {

        if (!passwordInput.getText().equals(passwordConfirmInput.getText())) {
            errorText.setText("Passwords do not match");
            errorText.setVisible(true);
        }
        else {
            errorText.setVisible(false);
            String username = usernameInput.getText();
            String name = firstNameInput.getText() + " " + middleNamesInput.getText() + " " + lastNameInput.getText();
            String password = passwordInput.getText();
            clinician = new Clinician(username, password, name);
            stage.close();
        }
    }

    /**
     * Enable/disable the create account button based on whether the required information is present or not.
     */
    private void checkRequiredFields() {
        createAccountButton.setDisable(usernameInput.getText().isEmpty() || firstNameInput.getText().isEmpty() ||
            passwordInput.getText().isEmpty() || passwordConfirmInput.getText().isEmpty());
    }

    /**
     * Add listeners to enable/disable the create account button based on information supplied
     * @param location Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordConfirmInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        firstNameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
    }
}