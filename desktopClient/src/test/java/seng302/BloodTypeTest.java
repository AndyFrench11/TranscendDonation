package seng302;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import seng302.User.Attribute.BloodType;

public class BloodTypeTest {

    @Test
    public void testValidParse() {
        assertEquals(BloodType.parse("a+"), BloodType.A_POS);
        assertEquals(BloodType.parse("o-"), BloodType.O_NEG);
        assertEquals(BloodType.parse("O-"), BloodType.O_NEG);
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            BloodType.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}