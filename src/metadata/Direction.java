package metadata;

public enum Direction {
    CREDIT("Credit"),
    DEBIT("Debit");

    private String direction;

    Direction(String direction) {
    }

    public String getDirection() {
        return this.direction;
    }
}
