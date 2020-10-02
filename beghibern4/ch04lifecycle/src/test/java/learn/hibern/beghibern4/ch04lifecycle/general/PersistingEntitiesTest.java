package learn.hibern.beghibern4.ch04lifecycle.general;

import learn.hibern.beghibern4.util.hibernate.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PersistingEntitiesTest {

    @Test
    public void testSave() {
        Long id = null;
        SimpleObject obj;

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            obj = new SimpleObject();
            obj.setKey("s1");
            obj.setValue(10L);

            session.save(obj);
            assertNotNull(obj.getId());
            id = obj.getId();

            tx.commit();
        }
    }

    @Test
    public void testSavingEntitiesTwice() {
        Long id;
        SimpleObject obj;

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            obj = new SimpleObject();

            obj.setKey("key1");
            obj.setValue(10L);

            session.save(obj);
            assertNotNull(obj.getId());

            id = obj.getId();

            tx.commit();
        }

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            obj.setValue(12L);

            session.save(obj);

            tx.commit();
        }
        assertNotEquals(id, obj.getId());
    }

    @Test
    public void testSaveOrUpdateEntity() {
        Long id;
        SimpleObject obj;

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            obj = new SimpleObject();

            obj.setKey("key2");
            obj.setValue(14L);

            session.save(obj);
            assertNotNull(obj.getId());

            id = obj.getId();

            tx.commit();
        }

        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            obj.setValue(16L);

            session.saveOrUpdate(obj);

            tx.commit();
        }
        assertEquals(id, obj.getId());
    }
}
