package learn.hibern.beghibern4.util.hibernate;

import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SessionUtilTest {

    @Test
    public void testOpenSession() {
        try (Session session = SessionUtil.openSession()) {
            assertNotNull(session);
        }
    }
}
