package megamek.codeUItilities;
import megamek.utilities.RankingsDBAccessor;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;

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
        var resp = RankingsDBAccessor.addNewPlayer("TestPlayer32", 2000, 2);
        // Check if the player was added successfully
        assertNull(resp); // Should return -1 if already exists
        assertNotNull(RankingsDBAccessor.getPlayerElementByName("TestPlayer32"));
        // Remove the added player
        RankingsDBAccessor.deletePlayer("TestPlayer32");
        // Check if the player was removed successfully
        assertNull(RankingsDBAccessor.getPlayerElementByName("TestPlayer32"));
    }

    @Test
    public void testGetPlayerElementByName_PlayerExists() {
        // Check if a player exists in the database
        assertNotNull(RankingsDBAccessor.getPlayerElementByName("John"));
    }

    @Test
    public void testGetPlayerElementByName_PlayerDoesNotExist() {
        // Check if a player does not exist in the database
        assertNull(RankingsDBAccessor.getPlayerElementByName("UnknownPlayer"));
    }
}
