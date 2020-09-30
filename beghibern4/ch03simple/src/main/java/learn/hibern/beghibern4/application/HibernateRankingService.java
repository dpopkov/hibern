package learn.hibern.beghibern4.application;

import learn.hibern.beghibern4.hibernate.Person;
import learn.hibern.beghibern4.hibernate.Ranking;
import learn.hibern.beghibern4.hibernate.Skill;
import learn.hibern.beghibern4.util.hibernate.SessionUtil;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.Session;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

public class HibernateRankingService implements RankingService {

    @Override
    public int getRankingFor(String subject, String skill) {
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            int average = getRankingFor(session, subject, skill);
            tx.commit();
            return average;
        }
    }

    public int getRankingFor(Session session, String subject, String skill) {
        Query<Ranking> query = session.createQuery("from Ranking r "
                + "where r.subject.name=:name "
                + "and r.skill.name=:skill", Ranking.class);
        query.setParameter("name", subject);
        query.setParameter("skill", skill);

        IntSummaryStatistics stats = query
                .list()
                .stream()
                .collect(Collectors.summarizingInt(Ranking::getRanking));
        return (int) stats.getAverage();
    }

    @Override
    public void addRanking(String subjectName, String observerName, String skillName, int rank) {
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            addRanking(session, subjectName, observerName, skillName, rank);
            tx.commit();
        }
    }

    private void addRanking(Session session, String subjectName, String observerName, String skillName, int rank) {
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
}
