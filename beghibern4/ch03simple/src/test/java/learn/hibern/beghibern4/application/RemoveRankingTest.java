package learn.hibern.beghibern4.application;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RemoveRankingTest {
    private final RankingService service = new HibernateRankingService();

    @Test
    public void testRemoveExistingRanking() {
        service.addRanking("R1", "R2", "RS1", 6);
        assertEquals(service.getRankingFor("R1", "RS1"), 6);
        service.removeRanking("R1", "R2", "RS1");
        assertEquals(service.getRankingFor("R1", "RS1"), 0);
    }

    @Test
    public void testRemoveNonExistingRanking() {
        assertEquals(service.getRankingFor("R3", "RS2"), 0);
        service.removeRanking("R3", "R4", "RS2");
        assertEquals(service.getRankingFor("R3", "RS2"), 0);
    }
}
