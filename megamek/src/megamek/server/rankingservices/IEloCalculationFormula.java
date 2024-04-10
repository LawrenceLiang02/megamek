package megamek.server.rankingservices;

public interface IEloCalculationFormula {

    /**
     * Calculate the change in Elo rating for each player
     * @param ratings
     * @param winnersIndex
     * @param kFactor
     * @return
     */
    int[] calculateEloChange(int[] ratings, boolean[] winnersIndex, int kFactor);

    /**
     * Calculate the new Elo rating for each player
     * @param ratings
     * @param winnersIndex
     * @param kFactor
     * @return un array qui contient les nouveau ratings
     */
    int[] calculateNewRating(int[] ratings, boolean[] winnersIndex, int kFactor);
}
