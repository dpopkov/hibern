package learn.hibern.beghibern4.application;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AddRankingTest {
    private final RankingService service = new HibernateRankingService();

    @Test
    public void testAddRanking() {
        service.addRanking("J. C. Smell", "Drew Lombardo", "Mule", 8);
        assertEquals(service.getRankingFor("J. C. Smell", "Mule"), 8);
    }
}
