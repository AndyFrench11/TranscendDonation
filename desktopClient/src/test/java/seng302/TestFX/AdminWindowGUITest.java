package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;

public class AdminWindowGUITest extends TestFXTest {

    private User currentSelectedUser;
    private Clinician currentSelectedClinician;
    private Admin currentSelectedAdmin;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    /**
     * Refreshes the currently selected profile from the three profile tabs
     */
    private void refreshTableSelections() {
        TabPane tableTabPane = lookup("#tableTabPane").query();

        switch (tableTabPane.getSelectionModel().getSelectedItem().getId()) {
            case "usersTab":
                TableView<User> userTableView = lookup("#userTableView").query();
                currentSelectedUser = userTableView.getSelectionModel().getSelectedItem();
                currentSelectedClinician = null;
                currentSelectedAdmin = null;
                break;
            case "administratorsTab":
                TableView<Admin> adminTableView = lookup("#adminTableView").query();
                currentSelectedAdmin = adminTableView.getSelectionModel().getSelectedItem();
                currentSelectedClinician = null;
                currentSelectedUser = null;
                break;
            case "cliniciansTab":
                TableView<Clinician> clinicianTableView = lookup("#clinicianTableView").query();
                currentSelectedClinician = clinicianTableView.getSelectionModel().getSelectedItem();
                currentSelectedUser = null;
                currentSelectedAdmin = null;
                break;
        }
    }


    /**
     * Add a simple user and verify it appears with appropriate details in the TabPane
     */
    @Ignore
    @Test
    public void addUser() {
        loginAsDefaultAdmin();
        clickOn("#fileMenu");
        moveTo("#createMenu");
        // To align the movement properly:
        moveTo("#adminMenuItem");
        clickOn("#userMenuItem");

        // Create a user
        clickOn("#usernameInput");
        write("buzz");
        clickOn("#emailInput");
        write("mkn29@uclive.ac.nz");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password123");
        clickOn("#dateOfBirthInput");
        write("12/06/1997");
        clickOn("#firstNameInput");
        write("Matthew");
        clickOn("#middleNamesInput");
        write("Pieter");
        clickOn("#lastNameInput");
        write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());

        clickOn("Matthew Pieter Knight");
        refreshTableSelections();
        // Check this is the user:
        assertEquals(LocalDate.of(1997, 6, 12), currentSelectedUser.getDateOfBirth());

        rightClickOn("Matthew Pieter Knight");
        clickOn("Delete Matthew Pieter Knight");
        clickOn("OK");
    }

    /**
     * Add a simple admin and verify it appears with appropriate details in the TabPane
     */
    @Ignore
    @Test
    public void addAdmin() {
        loginAsDefaultAdmin();;
        clickOn("#fileMenu");
        moveTo("#createMenu");
        clickOn("#adminMenuItem");

        // Create an admin
        clickOn("#usernameInput");
        write("buzz");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password123");
        clickOn("#firstNameInput");
        write("Matthew");
        clickOn("#middleNamesInput");
        write("Pieter");
        clickOn("#lastNameInput");
        write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());

        clickOn("#administratorsTab");
        clickOn("Matthew Pieter Knight");
        refreshTableSelections();
        // Check this is the user:
        assertEquals("buzz", currentSelectedAdmin.getUsername());

        rightClickOn("buzz");
        clickOn("Delete Matthew Pieter Knight");
        clickOn("OK");
    }

    /**
     * Add a simple user and verify it appears with appropriate details in the TabPane
     */
    @Ignore
    @Test
    public void addThenDeleteClinician() {
        loginAsDefaultAdmin();
        clickOn("#fileMenu");
        sleep(100);
        moveTo("#createMenu");
        // To align the movement properly:
        moveTo("#adminMenuItem");
        clickOn("#clinicianMenuItem");

        // Create a clinician
        clickOn("#usernameInput");
        write("buzz");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password123");
        clickOn("#firstNameInput");
        write("Matthew");
        clickOn("#middleNamesInput");
        write("Pieter");
        clickOn("#lastNameInput");
        write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());

        clickOn("#cliniciansTab");
        clickOn("Matthew Pieter Knight");
        refreshTableSelections();
        // Check this is the user:
        assertEquals("buzz", currentSelectedClinician.getUsername());

        rightClickOn("Matthew Pieter Knight");
        clickOn("Delete Matthew Pieter Knight");
        clickOn("OK");
    }

    @Test
    public void deleteProfiles() {

    }


    @Test
    public void checkExistenceDefaultClinician() {
        loginAsDefaultAdmin();
        clickOn("#cliniciansTab");
        clickOn("default");
        refreshTableSelections();
        assertEquals("default", currentSelectedClinician.getName());
    }

    @Test
    public void checkExistenceDefaultAdmin() {
        loginAsDefaultAdmin();
        clickOn("#administratorsTab");
        clickOn("default");
        refreshTableSelections();
        assertEquals("default", currentSelectedAdmin.getName());
    }
}