package learn.hibern.beghibern4.application;

public interface RankingService {

    int getRankingFor(String subject, String skill);

    void addRanking(String subject, String observer, String skill, int ranking);
}
