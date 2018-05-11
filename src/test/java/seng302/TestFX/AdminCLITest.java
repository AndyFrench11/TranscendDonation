package seng302.TestFX;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Generic.DataManager;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class AdminCLITest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setupTest() {
        loginAsDefaultAdmin();
        clickOn("#cliTabButton");
        sleep(200);
    }

    @Test
    public void tabExists() {
        GridPane mainPane = lookup("#mainPane").query();
        assertTrue(!mainPane.isVisible()); //Ensure that the search tab is no longer open
    }

    @Test
    public void embeddedCliExists() {
        TextField textField = lookup("#commandInputField").query();
        assertNotNull(textField); //Ensure that the CLI is now showing
    }

    @Test
    public void cliInputIsRead() {
        clickOn("#commandInputField");
        DataManager.users.clear();
        write("adduser \"Test,User\" 01/10/1998");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        assertEquals(1, DataManager.users.size());
    }

    @Test
    public void cliOutputIsShown() {
        clickOn("#commandInputField");
        DataManager.users.clear();
        write("adduser \"Test,User\" 01/10/1998");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        sleep(200);
        assertEquals(1, lookup("#commandOutputView").queryListView().getItems().size());
    }

    @Test
    public void cliHistory() {
        TextField commandInputField = lookup("#commandInputField").query();
        clickOn("#commandInputField").write("a");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("b");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("c");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("d");
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("d", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("c", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("b", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("a", commandInputField.getText());

        // Check for a second time that the history doesn't break when overrun
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("a", commandInputField.getText());
    }
}
