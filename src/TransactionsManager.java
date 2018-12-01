import metadata.Currency;
import metadata.Direction;
import metadata.SubmitOperationCode;
import metadata.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionsManager {
    private final AtomicLong counter = new AtomicLong(0L);
    private Map<Long, Transaction> debitTransactions;
    private Map<Long, Transaction> creditTransactions;
    private Map<Integer, TransactionStatus> destinationStatus;
    private Map<Integer, Integer> debitTransactionsApproved;
    private final Object creditSyncObj;
    private final Object debitSyncObj;

    public TransactionsManager() {
        debitTransactions = new ConcurrentHashMap<>();
        creditTransactions = new ConcurrentHashMap<>();
        destinationStatus = new ConcurrentHashMap<>();
        debitTransactionsApproved = new ConcurrentHashMap<>();
        creditSyncObj = new Object();
        debitSyncObj = new Object();
    }

    private long generateTransactionId() {
        return counter.incrementAndGet();
    }

    public SubmitOperationCode submitTransaction(Transaction transaction) {
        SubmitOperationCode code = SubmitOperationCode.UNKNOWN;
        transaction.setId(generateTransactionId());

        // check if Debit or Credit
        if (transaction.getDirection().equals(Direction.CREDIT)) {
            synchronized (creditSyncObj) {
                transaction.setStatus(TransactionStatus.APPROVED);
                destinationStatus.put(transaction.getCounterpartAccountId(), TransactionStatus.APPROVED);
                // add transaction to data storage
                creditTransactions.put(transaction.getId(), transaction);
                code = SubmitOperationCode.ACCEPT;
            }
        } else if (transaction.getDirection().equals(Direction.DEBIT)) {
            synchronized (debitSyncObj) {
                // check if exists transaction with the same dest on Rejected or Hold status
                TransactionStatus status = destinationStatus.get(transaction.getCounterpartAccountId());
                if (status != null &&
                        (status.equals(TransactionStatus.REJECTED) || status.equals(TransactionStatus.HOLD))) {
                    transaction.setStatus(TransactionStatus.HOLD);
                    code = SubmitOperationCode.HOLD;
                } else {
                    // check value is less then $1000
                    if (Utils.exchange(transaction.getQuantity(), transaction.getCurrency()) < 1000) {
                        debitTransactionsApproved.put(transaction.getCustomerAccountId(), transaction.getCounterpartAccountId());
                        transaction.setStatus(TransactionStatus.APPROVED);
                        code = SubmitOperationCode.ACCEPT;
                    } else {
                        // check if exists debit transaction with the same dest and status Approved
                        Integer counterpartAccountId = debitTransactionsApproved.get(transaction.getCustomerAccountId());
                        if (counterpartAccountId != null &&
                                counterpartAccountId == transaction.getCounterpartAccountId()) {
                            transaction.setStatus(TransactionStatus.APPROVED);
                            code = SubmitOperationCode.ACCEPT;
                        } else {
                            transaction.setStatus(TransactionStatus.HOLD);
                            code = SubmitOperationCode.HOLD;
                        }
                    }
                }
                // add transaction to data storage
                debitTransactions.put(transaction.getId(), transaction);
            }
        }
        return code;
    }

    public boolean UpdateTransactionStatus (long transactionId, TransactionStatus status) {
        boolean isUpdated = false;
        // check if transaction exists
        Transaction debit = debitTransactions.get(transactionId);
        Transaction credit = creditTransactions.get(transactionId);
        if (debit != null) {
            synchronized (debitTransactions.get(transactionId)) {
                debit.setStatus(status);
                isUpdated = true;
            }
        } else if (credit != null) {
            synchronized (creditTransactions.get(transactionId)) {
                credit.setStatus(status);
                isUpdated = true;
            }
        }
        return isUpdated;
    }

    public void ListAllTransactions() {
        for (Map.Entry<Long, Transaction> transaction : debitTransactions.entrySet()) {
            transaction.getValue().print();
        }
        for (Map.Entry<Long, Transaction> transaction : creditTransactions.entrySet()) {
            transaction.getValue().print();
        }
    }

    public static void main(String[] args) {
        TransactionsManager manager = new TransactionsManager();

        // create transactions
        List<Transaction> transactionsCollection = new ArrayList<Transaction>(){
            {
                add(new Transaction(111, 222, Direction.CREDIT, Currency.USD, 500));
                add(new Transaction(333, 444, Direction.DEBIT, Currency.EURO, 467));
                add(new Transaction(555, 666, Direction.DEBIT, Currency.USD, 1400));
                add(new Transaction(333, 222, Direction.DEBIT, Currency.EURO, 4000));
                add(new Transaction(111, 777, Direction.DEBIT, Currency.USD, 1000));
                add(new Transaction(555, 999, Direction.DEBIT, Currency.EURO, 1895));
                add(new Transaction(444, 333, Direction.CREDIT, Currency.USD, 750));
                add(new Transaction(777, 666, Direction.DEBIT, Currency.NIS, 2000));
                add(new Transaction(888, 222, Direction.DEBIT, Currency.NIS, 3400));
                add(new Transaction(999, 111, Direction.DEBIT, Currency.NIS, 5000));
            }
        };

        for (Transaction transaction: transactionsCollection) {
            SubmitOperationCode code = manager.submitTransaction(transaction);
            System.out.println("Transaction " + transaction.getId() + " submitted: " + code);
        }

        if(manager.UpdateTransactionStatus(6, TransactionStatus.APPROVED)) {
            System.out.println("\nUpdate transaction 6 has succeeded");
        } else {
            System.out.println("\nUpdate transaction 6 has failed");
        }

        manager.ListAllTransactions();
    }
}
