package learn.hibern.beghibern4.application;

import learn.hibern.beghibern4.hibernate.Person;
import learn.hibern.beghibern4.hibernate.Ranking;
import learn.hibern.beghibern4.hibernate.Skill;
import learn.hibern.beghibern4.util.hibernate.SessionUtil;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
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

    @Override
    public void updateRanking(String subjectName, String observerName, String skillName, int rank) {
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            Ranking ranking = findRanking(session, subjectName, observerName, skillName);
            if (ranking == null) {
                addRanking(session, subjectName, observerName, skillName, rank);
            } else {
                ranking.setRanking(rank);
            }
            tx.commit();
        }
    }

    @Override
    public void removeRanking(String subject, String observer, String skill) {
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            removeRanking(session, subject, observer, skill);
            tx.commit();
        }
    }

    @Override
    public Map<String, Integer> findRankingsFor(String subject) {
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            Map<String, Integer> result = findRankingsFor(session, subject);
            tx.commit();
            return result;
        }
    }

    @Override
    public Person findBestPersonFor(String skill) {
        Person person;
        try (Session session = SessionUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            person = findBestPersonFor(session, skill);
            tx.commit();
        }
        return person;
    }

    private Person findBestPersonFor(Session session, String skill) {
        Query<Object[]> query = session.createQuery("select r.subject.name, avg(r.ranking)"
                + " from Ranking r where "
                + "r.skill.name=:skill "
                + "group by r.subject.name "
                + "order by avg(r.ranking) desc", Object[].class);
        query.setParameter("skill", skill);
        List<Object[]> result = query.list();
        if (result.size() > 0) {
            String subjectName = (String) result.get(0)[0];
            return findPerson(session, subjectName);
        }
        return null;
    }

    private Map<String, Integer> findRankingsFor(Session session, String subject) {
        Map<String, Integer> resultMap = new HashMap<>();
        Query<Ranking> query = session.createQuery("from Ranking r "
                        + "where r.subject.name=:name order by r.skill.name", Ranking.class);
        query.setParameter("name", subject);
        List<Ranking> list = query.list();
        String lastSkill = "";
        int count = 0;
        int sum = 0;
        for (Ranking r : list) {
            if (!lastSkill.equals(r.getSkill().getName())) {
                count = 0;
                sum = 0;
                lastSkill = r.getSkill().getName();
            }
            sum += r.getRanking();
            count++;
            resultMap.put(r.getSkill().getName(), sum / count);
        }
        return resultMap;
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

    private Ranking findRanking(Session session, String subjectName, String observerName, String skillName) {
        Query<Ranking> query = session.createQuery("from Ranking r "
                + "where r.subject.name=:subject and "
                + "r.observer.name=:observer and "
                + "r.skill.name=:skill", Ranking.class);
        query.setParameter("subject", subjectName);
        query.setParameter("observer", observerName);
        query.setParameter("skill", skillName);
        return query.uniqueResult();
    }

    private void removeRanking(Session session, String subject, String observer, String skill) {
        Ranking ranking = findRanking(session, subject, observer, skill);
        if (ranking != null) {
            session.delete(ranking);
        }
    }
}
