package megamek.server.rankingservices;


import megamek.server.rankingservices.IEloCalculationFormula;
public class OtherEloStrategy implements IEloCalculationFormula {

     @Override
     public int[] calculateEloChange(int[] ratings, boolean[] winnersIndex, int kFactor) {
         return new int[0];
     }

     @Override
     public int[] calculateNewRating(int[] ratings, boolean[] winnersIndex, int kFactor) {
         return new int[0];
     }
 }
