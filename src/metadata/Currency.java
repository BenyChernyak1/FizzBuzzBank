package metadata;

public enum Currency {
    USD("Dollar"),
    NIS("Shekel"),
    EURO("Euro");

    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }
}
