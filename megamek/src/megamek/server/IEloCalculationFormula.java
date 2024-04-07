package megamek.server;

public interface IEloCalculationFormula {
    double calculateEloChange(int ratingA, int ratingB, int kFactor);
    int calculateNewRating(int ratingA, int ratingB);
}
