package seng302.GUI.Controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.User;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class UserSettingsController implements Initializable {

    @FXML
    private TextField usernameField, emailField, passwordField;
    @FXML
    private Button updateButton, cancelButton;
    @FXML
    private Label userNameLabel, errorLabel;
    @FXML
    private AnchorPane accountBackground;

    private User currentUser;

    private UserController userController;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        userNameLabel.setText("user: " + currentUser.getName());
    }

    /**
     * Set the parent user controller that spawned this window.
     *
     * @param userController The parent user controller
     */
    public void setParent(UserController userController) {
        this.userController = userController;
    }

    /**
     * Populates the account details inputs based on the current user's attributes.
     */
    public void populateAccountDetails() {
        userNameLabel.setText("user: " + currentUser.getName());
        usernameField.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "");
        emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        passwordField.setText(currentUser.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {
        try {
            if (!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(usernameField.getText(), currentUser.getId())) {
                errorLabel.setText("That username is already taken.");
                errorLabel.setVisible(true);
                return;
            } else if(!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(emailField.getText(), currentUser.getId())) {
                errorLabel.setText("There is already a user account with that email.");
                errorLabel.setVisible(true);
                return;
            }
        } catch (HttpResponseException e){
            Debugger.error("Failed to uniqueness of user with id: " + currentUser.getId());
        }
        errorLabel.setVisible(false);
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ",
                "The changes made will not be saved to the server until you save.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentUser.setUsername(usernameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPassword(passwordField.getText());

            userController.addHistoryEntry("Account settings updated", "Profile account settings were updated.");
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
        } else {
            alert.close();
        }
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        accountBackground.requestFocus();
    }

    /**
     * Function which closes the current stage upon the user pressing the 'cancel' button.
     */
    public void exit() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }

    private void updateUpdateButtonState() {
        updateButton.setDisable((usernameField.getText().isEmpty() && emailField.getText().isEmpty()) || passwordField.getText().isEmpty());
    }

    public void setEnterEvent() {
        updateButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !updateButton.isDisable()) {
                updateAccountDetails();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        emailField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
    }
}
