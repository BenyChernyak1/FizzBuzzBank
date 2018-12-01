package metadata;

public enum TransactionStatus {
    APPROVED("Approved"),
    HOLD("Hold"),
    REJECTED("Rejected");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
