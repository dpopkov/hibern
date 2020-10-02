package learn.hibern.beghibern4.ch04lifecycle.general;

import learn.hibern.beghibern4.util.hibernate.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MergeRefreshTest {

    @Test
    public void testMerge() {
        Long id;
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            SimpleObject obj = new SimpleObject();

            obj.setKey("testMerge");
            obj.setValue(1L);

            session.save(obj);
            id = obj.getId();

            tx.commit();
        }
        SimpleObject so = validateSimpleObject(id, 1L);
        so.setValue(2L);
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(so);
            tx.commit();
        }
        validateSimpleObject(id, 2L);
    }

    @Test
    public void testRefresh() {
        Long id;
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();

            SimpleObject simpleObject = new SimpleObject();

            simpleObject.setKey("testMerge");
            simpleObject.setValue(1L);

            session.save(simpleObject);
            id = simpleObject.getId();

            tx.commit();
        }

        SimpleObject so = validateSimpleObject(id, 1L);
        so.setValue(2L);

        try (Session session = SessionUtil.openSession()) {
            // note that refresh is a read,
            // so no TX is necessary unless an update occurs later
            session.refresh(so);
        }

        validateSimpleObject(id, 1L);
    }

    private SimpleObject validateSimpleObject(Long id, Long value) {
        SimpleObject so;
        try (Session session = SessionUtil.openSession()) {
            so = session.load(SimpleObject.class, id);
            assertEquals(so.getKey(), "testMerge");
            assertEquals(so.getValue(), value);
        }
        return so;
    }
}
