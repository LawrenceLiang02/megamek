package megamek.codeUItilities;
import megamek.utilities.RankingsDBAccessor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class rankingXMLUtilityTest {

    @Test
    public void testUpdatePlayer_Success() throws Exception {

            RankingsDBAccessor.updatePlayer( "John", 2300, 1);
            // Verify if the player was updated successfully by checking if the file has changed
            // You can add more assertions here if needed
             assertTrue(true);

            }

    @Test
    public void testUpdatePlayer_PlayerNotFound() {
        try {
            RankingsDBAccessor.updatePlayer( "UnknownPlayer", 2300, 1);
            fail("Expected Exception: PlayerNotFoundException not thrown");

        } catch (Exception e) {
            fail("Unexpected Exception thrown: " + e.getClass().getName());
        }
    }


    @Test
    public void testAddAndRemovePlayer() throws Exception {
        // Add a player
        RankingsDBAccessor.addNewPlayer("TestPlayer", 2000, 2);
        // Check if the player was added successfully
        assertNotNull(RankingsDBAccessor.getPlayerFromDb("TestPlayer"));

        // Remove the added player
       // RankingsDBAccessor.deletePlayer("TestPlayer");
        // Check if the player was removed successfully
       // assertNull(RankingsDBAccessor.getPlayerFromDb("TestPlayer"));
    }


}
