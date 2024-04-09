package megamek.server.rankingservices;

import megamek.common.Player;
import megamek.server.GameManager;
import megamek.server.victory.VictoryResult;
import megamek.utilities.RankingsDBAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.HashMap;


public class EloProcessor implements IEloCalculator {
    private static final int INITIAL_ELO_RATING = 1500;
    private static final int K_FACTOR = 50;
    private IEloCalculationFormula eloFormula = null;
    private HashMap<Player, Integer> leaderBoard;
    private int[] ratings;
    private boolean[] winnersIndex;
    private VictoryResult result;
    private GameManager gameManager;
    private RankingsDBAccessor rankingService = new RankingsDBAccessor();


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

        if(leaderBoard==null || leaderBoard.isEmpty()){
            createLeaderBoard();
        }

        if (result != null && gameManager != null) {
            int winningPlayer = result.getWinningPlayer();
            int winningTeam = result.getWinningTeam();

            var listPlayer = gameManager.getGame().getPlayersList();

            if(eloFormula==null){
                eloFormula = new SimpleEloStrategy();
            }
            int[] eloChanged = eloFormula.calculateEloChange(ratings, winnersIndex, K_FACTOR);
            for (int i = 0; i < listPlayer.size(); i++) {
                ratings[i] += eloChanged[i];
                leaderBoard.put(listPlayer.get(i), ratings[i]);
                updateRatingInDb(listPlayer.get(i), ratings[i]);
            }
        }
    }

    @Override
    public void setEloCalculationFormula(IEloCalculationFormula formula) {
        this.eloFormula = formula;
    }

    public void createLeaderBoard(List<Player> playersList ) {
            ratings = new int[playersList.size()];
            winnersIndex = new boolean[playersList.size()];

            for (int i = 0; i < playersList.size(); i++) {
                leaderBoard.put(playersList.get(i), getPlayerRatingFromDb(playersList.get(i)));
                ratings[i] = leaderBoard.get(playersList.get(i));
                winnersIndex[i] = false;
            }

    }

    public void createLeaderBoard(GameManager gameManager) {
        List<Player> playersList = gameManager.getGame().getPlayersList();
        ratings = new int[playersList.size()];
        winnersIndex = new boolean[playersList.size()];

        for (int i = 0; i < playersList.size(); i++) {
            leaderBoard.put(playersList.get(i), getPlayerRatingFromDb(playersList.get(i)));
            ratings[i] = leaderBoard.get(playersList.get(i));
            winnersIndex[i] = false;
        }

    }

    public void createLeaderBoard() {
        if (result != null && gameManager != null && gameManager.getGame() != null) {
            var listPlayer = gameManager.getGame().getPlayersList();
            if (listPlayer != null) {
                ratings = new int[listPlayer.size()]; // Initialize ratings array
                winnersIndex = new boolean[listPlayer.size()];

                for (int i = 0; i < listPlayer.size(); i++) {
                    leaderBoard.put(listPlayer.get(i), getPlayerRatingFromDb(listPlayer.get(i)));
                    ratings[i] = leaderBoard.get(listPlayer.get(i));
                    winnersIndex[i] = false;
                }
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
       player.setEloRanking(newRating);
        try {
            RankingsDBAccessor.updatePlayer(player.getName(), newRating, 0);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        int elo =0;
        var play = RankingsDBAccessor.getPlayerElementByName(player.getName());
        if (play != null) {
            //Normally you would get the value from the DB
            return Integer.parseInt(play.getElementsByTagName("elo").item(0).getTextContent());
        }
        else
        {
            return 1500;
        }
    }

    public void setWinnersIndex(boolean[] winnersIndex) {
        this.winnersIndex = winnersIndex;
    }

    public void setRatings(int[] ratings) {
        this.ratings = ratings;
    }

}

