package learn.hibern.beghibern4.application;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UpdateRankingTest {
    private final RankingService service = new HibernateRankingService();

    @Test
    public void testUpdateExistingRanking() {
        service.addRanking("Gene Showrama", "Scottball Most", "Ceylon", 6);
        assertEquals(service.getRankingFor("Gene Showrama", "Ceylon"), 6);
        service.updateRanking("Gene Showrama", "Scottball Most", "Ceylon", 8);
        assertEquals(service.getRankingFor("Gene Showrama", "Ceylon"), 8);
    }

    @Test
    public void testUpdateNonExistingRanking() {
        assertEquals(service.getRankingFor("Scottball Most", "Ceylon"), 0);
        service.updateRanking("Scottball Most", "Gene Showrama", "Ceylon", 9);
        assertEquals(service.getRankingFor("Scottball Most", "Ceylon"), 9);
    }
}
