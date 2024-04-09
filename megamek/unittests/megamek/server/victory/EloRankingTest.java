package megamek.server.victory;
import megamek.common.Player;
import megamek.server.rankingservices.EloProcessor;
import megamek.server.GameManager;
import megamek.server.rankingservices.IEloCalculationFormula;
import megamek.server.rankingservices.SimpleEloStrategy;
import megamek.server.rankingservices.OtherEloStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class EloRankingTest {



    Player player1 = new Player(1, "John");
    Player player2 = new Player(2, "Alice");
    GameManager gameManager = new GameManager();
    EloProcessor eloProcessor = new EloProcessor();
    IEloCalculationFormula strategy1 = new SimpleEloStrategy();
    IEloCalculationFormula strategy2 = new OtherEloStrategy();

    @Test
    public void testEloProcessorCreation(){

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




}
