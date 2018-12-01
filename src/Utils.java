import metadata.Currency;

public final class Utils {
    private static final double EURO_RATE = 1.13171;
    private static final double SHEKEL_RATE = 0.268904;

    private Utils() {
        throw new AssertionError();
    }

    public static double exchange(double amount, Currency currency) {
        switch (currency){
            case EURO:
                return exchangeEuro(amount);
            case NIS:
                return exchangeShekel(amount);
            case USD:
            default:
                return amount;
        }
    }

    private static double exchangeEuro(double amountInEuro) {
        return amountInEuro * EURO_RATE;
    }

    private static double exchangeShekel(double amountInShekel) {
        return amountInShekel * SHEKEL_RATE;
    }
}
