import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    // Fields for account details
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private ArrayList<String> transactionHistory;

    public Account(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with balance: $" + String.format("%.2f", balance));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposited: $" + String.format("%.2f", amount));
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrew: $" + String.format("%.2f", amount));
            return true;
        }
        return false;
    }

    private void addTransaction(String message) {
        transactionHistory.add(message);
    }
}
