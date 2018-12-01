import metadata.Currency;
import metadata.Direction;
import metadata.TransactionStatus;

import java.time.LocalDateTime;

public class Transaction {
    private long id;
    private LocalDateTime date;
    private int customerAccountId;
    private int counterpartAccountId;
    private Direction direction;
    private Currency currency;
    private double quantity;
    private TransactionStatus status;


    public Transaction(int customerAccountId, int counterpartAccountId,
                       Direction direction, Currency currency, double quantity) {
        this.date = LocalDateTime.now();
        this.customerAccountId = customerAccountId;
        this.counterpartAccountId = counterpartAccountId;
        this.direction = direction;
        this.currency = currency;
        this.quantity = quantity;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    void setStatus(TransactionStatus status) {
        this.status = status;
    }

    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    Direction getDirection() {
        return direction;
    }

    int getCounterpartAccountId() {
        return counterpartAccountId;
    }

    double getQuantity() {
        return quantity;
    }

    Currency getCurrency() {
        return currency;
    }

    int getCustomerAccountId() {
        return customerAccountId;
    }

    void print() {
        System.out.println("----------------------------------------------------------" +
        "\nTransaction ID: " + this.getId() +
        "\nDate: " + this.date +
        "\nCustomer Account: " + this.customerAccountId +
        "\nCounterPart Account: " + this.counterpartAccountId +
        "\nDirection: " + this.direction.getDirection() +
        "\nCurrency: " + this.getCurrency() +
        "\nQuantity: " + this.getQuantity() +
        "\nStatus:" + (this.getStatus() != null ? this.getStatus() : "No status") +
        "\n----------------------------------------------------------\n");
    }
}
