package metadata;

public enum SubmitOperationCode {
    ACCEPT("Accept"),
    HOLD("Hold"),
    UNKNOWN("Unknown");

    private String status;

    SubmitOperationCode(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
