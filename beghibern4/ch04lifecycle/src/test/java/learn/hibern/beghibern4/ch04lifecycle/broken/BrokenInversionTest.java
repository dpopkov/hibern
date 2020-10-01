package learn.hibern.beghibern4.ch04lifecycle.broken;

import learn.hibern.beghibern4.util.hibernate.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BrokenInversionTest {

    @Test
    public void testBrokenInversionCode() {
        Long emailId;
        Long messageId;
        Email email;
        Message message;

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            email = new Email("Broken Email");
            message = new Message("Broken Message");

            email.setMessage(message);
//            message.setEmail(email);

            session.save(email);
            session.save(message);

            emailId = email.getId();
            messageId = message.getId();

            tx.commit();
        }
        assertNotNull(email.getMessage());
        assertNull(message.getEmail());

        try (Session session = SessionUtil.openSession()) {
            email = session.get(Email.class, emailId);
            System.out.println(email);
            message = session.get(Message.class, messageId);
            System.out.println(message);
        }
        assertNotNull(email.getMessage());
        assertNull(message.getEmail());
    }

    @Test
    public void testProperInversionCode() {
        Long emailId;
        Long messageId;
        Email email;
        Message message;

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            email = new Email("Proper Email");
            message = new Message("Proper Message");

            email.setMessage(message);
            message.setEmail(email);

            session.save(email);
            session.save(message);

            emailId = email.getId();
            messageId = message.getId();

            tx.commit();
        }
        assertNotNull(email.getMessage());
        assertNotNull(message.getEmail());

        try (Session session = SessionUtil.openSession()) {
            email = session.get(Email.class, emailId);
            System.out.println(email);
            message = session.get(Message.class, messageId);
            System.out.println(message);
        }
        assertNotNull(email.getMessage());
        assertNotNull(message.getEmail());
    }
}
