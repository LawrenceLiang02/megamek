package megamek.server.rankingservices;

import megamek.common.Player;
import megamek.server.GameManager;
import megamek.server.victory.VictoryResult;

import java.util.HashMap;

public interface IEloCalculator {

    HashMap<Player, Integer> getLeaderBoard();

    //Returns the expected score between two ratings
    double calculateExpectedEloChange(int ratingA, int ratingB);

    boolean updateRatingInDb(Player a, Integer newRating);

    void setVictoryResults(VictoryResult result);

    void setGameManager(GameManager currentGame);

    int getPlayerRatingFromDb(Player player);

    void calculateElo();

    void setEloCalculationFormula(IEloCalculationFormula formula);










}
