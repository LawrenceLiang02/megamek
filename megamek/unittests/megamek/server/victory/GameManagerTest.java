package megamek.server.victory;

import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.Game;
import megamek.common.Player;
import megamek.common.force.Forces;
import megamek.common.options.GameOptions;
import megamek.server.rankingservices.EloProcessor;
import megamek.server.GameManager;
import megamek.server.rankingservices.IEloCalculationFormula;
import megamek.server.rankingservices.SimpleEloStrategy;
import megamek.server.rankingservices.OtherEloStrategy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameManagerTest {

    protected Game createMockedGame() {
        Game testGame = mock(Game.class);
        Forces testForces = new Forces(testGame);
        when(testGame.getGameListeners()).thenReturn(new Vector<>());
        when(testGame.getEntities()).thenReturn(Collections.emptyIterator());
        when(testGame.getEntitiesVector()).thenReturn(Collections.emptyList());
        when(testGame.getPlayers()).thenReturn(Collections.emptyEnumeration());
        when(testGame.getPlayersList()).thenReturn(Collections.emptyList());
        when(testGame.getAttacks()).thenReturn(Collections.emptyEnumeration());
        when(testGame.getAttacksVector()).thenReturn(new Vector<>());
        when(testGame.getForces()).thenReturn(testForces);
        when(testGame.getOptions()).thenReturn(new GameOptions());
        return testGame;
    }

    @Test
    public void testVictory() {
        GameManager gameManager = new GameManager();
        VictoryResult testVictoryResultFalse = new VictoryResult(false);
        VictoryResult testVictoryResultTrue = new VictoryResult(true);

        Game testGame = createMockedGame();

        // test whether the server.victory() returns false when mocking VictoryResult as false
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultFalse);
        gameManager.setGame(testGame);
        assertFalse(gameManager.victory());

        // test whether the server.victory() returns true when mocking VictoryResult as true
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultTrue);
        gameManager.setGame(testGame);
        assertTrue(gameManager.victory());
    }


    @Test
    public void testEloRatingLeaderBoard(){
        GameManager gameManager = new GameManager();
        Game testGame = createMockedGame();

        Player mockedPlayer = mock(Player.class);
        when(mockedPlayer.getName()).thenReturn("The champion");
        when(mockedPlayer.getColour()).thenReturn(PlayerColour.BLUE);
        Player mockedPlayer2 = mock(Player.class);
        when(mockedPlayer2.getName()).thenReturn("The champion");
        when(mockedPlayer2.getColour()).thenReturn(PlayerColour.BLUE);

        EloProcessor elo = new EloProcessor();
        gameManager.getGame().addPlayer(0, mockedPlayer);
        gameManager.getGame().addPlayer(1, mockedPlayer2);


        elo.setGameManager(gameManager);
        elo.createLeaderBoard(gameManager.getGame().getPlayersList());



        IEloCalculationFormula advancedEloStrategy = new OtherEloStrategy();
        IEloCalculationFormula simpleEloStrategy = new SimpleEloStrategy();

        elo.setEloCalculationFormula(advancedEloStrategy);
        elo.calculateElo();
        elo.setEloCalculationFormula(simpleEloStrategy);
        elo.calculateElo();

        gameManager.setGame(testGame);
        VictoryResult testVictoryResultTrue = new VictoryResult(true);
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultTrue);








    }

    @Test
    public void testVictoryDrawReport() {
        GameManager gameManager = new GameManager();
        VictoryResult testVictoryResultTrue = new VictoryResult(true);
        Game testGame = createMockedGame();
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultTrue);

        gameManager.setGame(testGame);
        gameManager.victory();
        verify(testGame, times(1)).setVictoryPlayerId(Player.PLAYER_NONE);
        verify(testGame, times(1)).setVictoryTeam(Player.TEAM_NONE);
    }

    @Test
    public void testVictoryFalseReport() {
        GameManager gameManager = new GameManager();
        VictoryResult testVictoryResultTrue = new VictoryResult(false);
        Game testGame = createMockedGame();
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultTrue);

        gameManager.setGame(testGame);
        gameManager.victory();
        verify(testGame, times(1)).cancelVictory();
    }

    @Test
    public void testCancelVictory() {
        GameManager gameManager = new GameManager();
        VictoryResult testVictoryResultTrue = new VictoryResult(false);
        Game testGame = createMockedGame();
        when(testGame.getVictoryResult()).thenReturn(testVictoryResultTrue);
        when(testGame.isForceVictory()).thenReturn(true);

        gameManager.setGame(testGame);
        gameManager.victory();
        verify(testGame, times(1)).cancelVictory();
    }

    @Test
    public void testVictoryWinReports() {
        GameManager gameManager = new GameManager();

        int winner = 1;

        // Mock a win victory result
        // Only 1 report should be generated as the team is set to TEAM_NONE
        Game testGame = createMockedGame();
        VictoryResult victoryResult = mock(VictoryResult.class);
        when(victoryResult.processVictory(testGame)).thenCallRealMethod();
        when(victoryResult.getReports()).thenReturn(new ArrayList<>());
        when(victoryResult.victory()).thenReturn(true);
        when(victoryResult.isDraw()).thenReturn(false);
        when(victoryResult.getWinningPlayer()).thenReturn(winner);
        when(victoryResult.getWinningTeam()).thenReturn(Player.TEAM_NONE);

        Player mockedPlayer = mock(Player.class);
        when(mockedPlayer.getName()).thenReturn("The champion");
        when(mockedPlayer.getColour()).thenReturn(PlayerColour.BLUE);

        when(testGame.getVictoryResult()).thenReturn(victoryResult);
        when(testGame.getPlayer(winner)).thenReturn(mockedPlayer);

        gameManager.setGame(testGame);
        gameManager.victory();

        assertSame(1, gameManager.getvPhaseReport().size());

        // Second test server tests with both a team != TEAM_NONE and a player != PLAYER_NONE
        // Two reports should be generated
        GameManager gameManager2 = new GameManager();

        when(victoryResult.getWinningTeam()).thenReturn(10);
        when(victoryResult.getReports()).thenReturn(new ArrayList<>());
        gameManager2.setGame(testGame);
        gameManager2.victory();

        assertSame(2, gameManager2.getvPhaseReport().size());
    }
}
