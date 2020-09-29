package learn.hibern.beghibern4.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class RankingTest {
    private SessionFactory factory;

    @BeforeMethod
    public void setup() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Test
    public void testSaveRanking() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Person subject = savePerson(session, "J. C. Smell");
            Person observer = savePerson(session, "Drew Lombardo");
            Skill skill = saveSkill(session, "Java");

            Ranking ranking = new Ranking();
            ranking.setSubject(subject);
            ranking.setObserver(observer);
            ranking.setSkill(skill);
            ranking.setRanking(8);
            session.save(ranking);

            tx.commit();
        }
    }

    @Test
    public void testRankings() {
        populateRankingData();
        long count;
        int average;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:name "
                    + "and r.skill.name=:skill", Ranking.class);
            query.setParameter("name", "J. C. Smell");
            query.setParameter("skill", "Java");
            IntSummaryStatistics stats = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));
            count = stats.getCount();
            average = (int) stats.getAverage();
            tx.commit();
        }
        assertEquals(count, 3);
        assertEquals(average, 7);
    }

    @Test
    public void changeRanking() {
        populateRankingData();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:subject and "
                    + "r.observer.name=:observer and "
                    + "r.skill.name=:skill", Ranking.class);
            query.setParameter("subject", "J. C. Smell");
            query.setParameter("observer", "Gene Showrama");
            query.setParameter("skill", "Java");
            Ranking ranking = query.uniqueResult();
            assertNotNull(ranking, "Could not find matching ranking");
            ranking.setRanking(9);
            tx.commit();
        }
        assertEquals(getAverage("J. C. Smell", "Java"), 8);
    }

    private Person findPerson(Session session, String name) {
        Query<Person> query = session.createQuery("from Person p where p.name=:name", Person.class);
        query.setParameter("name", name);
        return query.uniqueResult();
    }

    private Skill findSkill(Session session, String name) {
        Query<Skill> query = session.createQuery("from Skill s where s.name=:name", Skill.class);
        query.setParameter("name", name);
        return query.uniqueResult();
    }

    private Person savePerson(Session session, String name) {
        Person person = findPerson(session, name);
        if (person == null) {
            person = new Person();
            person.setName(name);
            session.save(person);
        }
        return person;
    }

    private Skill saveSkill(Session session, String name) {
        Skill skill = findSkill(session, name);
        if (skill == null) {
            skill = new Skill();
            skill.setName(name);
            session.save(skill);
        }
        return skill;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void populateRankingData() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            createData(session, "J. C. Smell", "Gene Showrama", "Java", 6);
            createData(session, "J. C. Smell", "Scottball Most", "Java", 7);
            createData(session, "J. C. Smell", "Drew Lombardo", "Java", 8);
            tx.commit();
        }
    }

    private void createData(Session session, String subjectName, String observerName, String skillName, int rank) {
        Person subject = savePerson(session, subjectName);
        Person observer = savePerson(session, observerName);
        Skill skill = saveSkill(session, skillName);

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(rank);
        session.save(ranking);
    }

    private int getAverage(String subjectName, String skillName) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<Ranking> query = session.createQuery("from Ranking r "
                    + "where r.subject.name=:subject and "
                    + "r.skill.name=:skill", Ranking.class);
            query.setParameter("subject", subjectName);
            query.setParameter("skill", skillName);
            IntSummaryStatistics stats = query.list()
                    .stream()
                    .collect(Collectors.summarizingInt(Ranking::getRanking));
            int average = (int) stats.getAverage();
            tx.commit();
            return average;
        }
    }
}
