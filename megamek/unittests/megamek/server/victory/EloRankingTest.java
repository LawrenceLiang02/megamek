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


public class EloRankingTest {

    Player player1 = new Player(1, "John");
    Player player2 = new Player(2, "Jane");
    EloProcessor eloProcessor = new EloProcessor();
    GameManager gameManager = new GameManager();

    // Test whether the server.victory() returns false when mocking VictoryResult as false;
    VictoryResult victoryResult = new VictoryResult(true);




}
