package megamek.server;

public class SimpleEloStrategy implements IEloCalculationFormula {
    @Override
    public double calculateEloChange(int ratingA, int ratingB, int kFactor) {
        double expectedScoreA = 1 / (1 + Math.pow(10, (ratingB - ratingA) / 400.0));
        return kFactor * (1 - expectedScoreA);
    }

    @Override
    public int calculateNewRating(int ratingA, int ratingB) {

       return  ratingA + (int) calculateEloChange(ratingA, ratingB, 32);
    }
}

 class AdvancedEloStrategy implements IEloCalculationFormula {
    @Override
    public double calculateEloChange(int ratingA, int ratingB, int kFactor) {
        // Custom implementation for advanced Elo calculation
        return 0; // Placeholder for demonstration
    }

     @Override
     public int calculateNewRating(int ratingA, int ratingB) {
         return 0;
     }
 }