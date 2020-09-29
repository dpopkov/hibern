package learn.hibern.beghibern4.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PersonTest {

    private static SessionFactory factory;

    @BeforeClass
    public void setup() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Test
    public void testSavePerson() {
        Person person = new Person();
        person.setName("J. C. Smell");
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(person);
            tx.commit();
        }
        System.out.println("Saved person: " + person);
    }
}