package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PatientTest {

    @Test
    public void compareTo() {
        Patient a = new Patient().withID("1.xml");
        Patient b = new Patient().withID("2.xml");

        assertNotEquals(a, b);
        assertTrue(a.compareTo(b) < 0);

        Patient c = new Patient().withID("2.xml");
        assertEquals(b, c);
        assertEquals(0, b.compareTo(c));
    }

}