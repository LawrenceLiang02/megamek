package megamek.server.rankingservices;

import megamek.common.Player;
import megamek.server.GameManager;
import megamek.server.victory.VictoryResult;
import java.util.Random;

import java.util.HashMap;


public class EloProcessor implements IEloCalculator {
    private static final int INITIAL_ELO_RATING = 1500;
    private static final int K_FACTOR = 32;
    private IEloCalculationFormula eloFormula;
    private HashMap<Player, Integer> leaderBoard;
    private VictoryResult result;
    private GameManager gameManager;

    public EloProcessor() {
        leaderBoard = new HashMap<>();
    }
    public EloProcessor(HashMap<Player, Integer> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }
    public EloProcessor(VictoryResult result) {
        this.result = result;
    }
    public EloProcessor(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    public EloProcessor(HashMap<Player, Integer> leaderBoard, VictoryResult result) {
        this.leaderBoard = leaderBoard;
        this.result = result;
    }


    public void calculateElo() {
        if (result != null && gameManager != null) {
            int winningPlayer = result.getWinningPlayer();
            int winningTeam = result.getWinningTeam();

            var listPlayer = gameManager.getGame().getPlayersList();

        }
    }

    @Override
    public void setEloCalculationFormula(IEloCalculationFormula formula) {
        this.eloFormula = formula;
    }

    public void createLeaderBoard() {
        if (result != null || gameManager != null) {

            var listPlayer = gameManager.getGame().getPlayersList();
            for (int i = 0; i < listPlayer.size(); i++) {
                leaderBoard.put(listPlayer.get(i), getPlayerRatingFromDb(listPlayer.get(i)));
            }
        }
    }

    @Override
    public HashMap<Player, Integer> getLeaderBoard() {
        return leaderBoard;
    }

    @Override
    public double calculateExpectedEloChange(int ratingA, int ratingB) {
        return 1 / (1 + Math.pow(10, (ratingB - ratingA) / 400.0));
    }

    @Override
    public boolean updateRatingInDb(Player player, Integer newRating) {
        boolean succcessOfTransaction = true;
        return succcessOfTransaction;
    }

    @Override
    public void setVictoryResults(VictoryResult result) {
        this.result = result;
    }

    @Override
    public void setGameManager(GameManager currentGame) {
        this.gameManager=   currentGame;

    }

    @Override
    public int getPlayerRatingFromDb(Player player) {
        Random rand = new Random();

        boolean isInDB = true;
        if (isInDB) {
            //Normally you would get the value from the DB
            return rand.nextInt(700) + 1200;
        }
        if(!isInDB)
        {
            return 1500;
        }
        return -1;

    }
}

