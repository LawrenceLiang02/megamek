package megamek.server.victory;

import megamek.common.Player;
import megamek.common.Game;
import megamek.server.rankingservices.EloProcessor;
import megamek.server.rankingservices.IEloCalculationFormula;
import megamek.server.rankingservices.OtherEloStrategy;
import megamek.server.rankingservices.SimpleEloStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import megamek.common.Player;
import megamek.server.GameManager;
import megamek.server.victory.VictoryResult;
import megamek.utilities.RankingsDBAccessor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EloRankingTest {
//    private EloProcessor eloProcessor;
//    private GameManager gameManager;
    private VictoryResult victoryResult;
    private RankingsDBAccessor rankingsDBAccessor;

    Player player1 = new Player(1, "John");
    Player player2 = new Player(2, "Alice");
    GameManager gameManager = new GameManager();
    EloProcessor eloProcessor = new EloProcessor();
    IEloCalculationFormula strategy1 = new SimpleEloStrategy();
    IEloCalculationFormula strategy2 = new OtherEloStrategy();


    @BeforeEach
    void setUp() {
        eloProcessor = new EloProcessor();
        gameManager = Mockito.mock(GameManager.class);
        victoryResult = Mockito.mock(VictoryResult.class);
        rankingsDBAccessor = Mockito.mock(RankingsDBAccessor.class);
    }

    @Test
    public void testEloProcessorCreation() {

        EloProcessor eloProcessor = new EloProcessor();
        gameManager.getGame().addPlayer(0, player1);
        gameManager.getGame().addPlayer(1, player2);

        eloProcessor.setGameManager(gameManager);

        eloProcessor.setVictoryResults(new VictoryResult(true));

        eloProcessor.setEloCalculationFormula(new SimpleEloStrategy());

        eloProcessor.createLeaderBoard(gameManager); // Creating a list of the players in the game
        eloProcessor.setWinnersIndex(new boolean[]{false, true});

        eloProcessor.calculateElo();
    }

    @Test
    void testCreateLeaderBoard() {
        HashMap<Player, Integer> leaderBoard = new HashMap<>();
        Player player1 = new Player(1, "John");
        Player player2 = new Player(2, "Jane");
        leaderBoard.put(player1, 1500);
        leaderBoard.put(player2, 1500);

        // Mock GameManager and its methods
        GameManager gameManager = Mockito.mock(GameManager.class);
        Game game = Mockito.mock(Game.class);
        Mockito.when(gameManager.getGame()).thenReturn(game);
        Mockito.when(game.getPlayersList()).thenReturn(new ArrayList<>(leaderBoard.keySet()));

        eloProcessor.setGameManager(gameManager);
        eloProcessor.createLeaderBoard();

        // Check the actual leaderboard
        HashMap<Player, Integer> actualLeaderBoard = eloProcessor.getLeaderBoard();

        assertEquals(leaderBoard.size(), actualLeaderBoard.size());

        for (Map.Entry<Player, Integer> entry : leaderBoard.entrySet()) {
            Player expectedPlayer = entry.getKey();
            int expectedRating = entry.getValue();
            assertTrue(actualLeaderBoard.containsKey(expectedPlayer));
            assertEquals(expectedRating, actualLeaderBoard.get(expectedPlayer));
        }
    }
    @Test
    void testCalculateElo() {
        int[] ratings = {1500, 1500};
        boolean[] winnersIndex = {true, false};

        Mockito.when(gameManager.getGame()).thenReturn(Mockito.mock(Game.class));
        Mockito.when(gameManager.getGame().getPlayersList()).thenReturn(new ArrayList<>());
        Mockito.when(victoryResult.getWinningPlayer()).thenReturn(0);
        Mockito.when(victoryResult.getWinningTeam()).thenReturn(0);
        eloProcessor.setGameManager(gameManager);
        eloProcessor.setVictoryResults(victoryResult);
        eloProcessor.createLeaderBoard();

        eloProcessor.calculateElo();
        // Add assertion for expected outcome if applicable
    }

    @Test
    void testExpectedEloChange() {
        double expectedChange = 0.5; // Example expected value, change as necessary
        double calculatedChange = eloProcessor.calculateExpectedEloChange(1500, 1500);
        assertEquals(expectedChange, calculatedChange);
    }

    @Test
    void testUpdateRatingInDb() {
        Player player = new Player(1, "John");
        int newRating = 1600; // Example new rating
        assertTrue(eloProcessor.updateRatingInDb(player, newRating));
    }

    @Test
    void testGetPlayerRatingFromDb() {
        Player player = new Player(1, "John");
        int expectedRating = 1500; // Example expected rating
        Mockito.when(rankingsDBAccessor.getPlayerElementByName(player.getName())).thenReturn(null);
        assertEquals(expectedRating, eloProcessor.getPlayerRatingFromDb(player));
    }


}
