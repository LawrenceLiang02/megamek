package megamek.server.rankingservices;

import megamek.server.rankingservices.IEloCalculationFormula;

public class SimpleEloStrategy implements IEloCalculationFormula {

    @Override
    public int[] calculateEloChange(int[] ratings, boolean[] winnersIndex, int kFactor) {
        double[] expectedScores = calculateExpectedScores(ratings);
        int[] eloChanges = new int[ratings.length];

        for (int i = 0; i < ratings.length; i++) {
            double actualScore = winnersIndex[i] ? 1.0 : 0.0;
            double expectedScore = expectedScores[i];
            eloChanges[i] = (int) Math.round(kFactor * (actualScore - expectedScore));
        }

        return eloChanges;
    }

    @Override
    public int[] calculateNewRating(int[] ratings, boolean[] winnersIndex, int kFactor) {
        int[] eloChanges = calculateEloChange(ratings, winnersIndex, kFactor);
        int[] newRatings = new int[ratings.length];

        for (int i = 0; i < ratings.length; i++) {
            newRatings[i] = ratings[i] + eloChanges[i];
        }

        return newRatings;
    }

    private double[] calculateExpectedScores(int[] ratings) {
        double[] expectedScores = new double[ratings.length];
        double sumExp = 0;

        for (int rating : ratings) {
            sumExp += Math.pow(10, rating / 400.0);
        }

        for (int i = 0; i < ratings.length; i++) {
            expectedScores[i] = Math.pow(10, ratings[i] / 400.0) / sumExp;
        }

        return expectedScores;
    }
}

 class AdvancedEloStrategy implements IEloCalculationFormula {

     @Override
     public int[] calculateEloChange(int[] ratings, boolean[] winnersIndex, int kFactor) {
         return new int[0];
     }

     @Override
     public int[] calculateNewRating(int[] ratings, boolean[] winnersIndex, int kFactor) {
         return new int[0];
     }
 }