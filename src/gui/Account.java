package src.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import src.database.Database;

public class Account extends JFrame {

    // Fields for account details
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private ArrayList<String> transactionHistory;

    // GUI components
    private JLabel balanceLabel;
    private JTextArea historyArea;
    private JTextField amountField;
    private JButton depositButton, withdrawButton;

    public Account(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();

        // Setup GUI
        setTitle("Bank Account - " + accountHolder);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for balance
        JPanel topPanel = new JPanel();
        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", balance));
        topPanel.add(balanceLabel);

        // Center panel for transaction history
        historyArea = new JTextArea(10, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        // Bottom panel for actions
        JPanel bottomPanel = new JPanel();
        amountField = new JTextField(10);
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");

        bottomPanel.add(new JLabel("Amount:"));
        bottomPanel.add(amountField);
        bottomPanel.add(depositButton);
        bottomPanel.add(withdrawButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        depositButton.addActionListener(e -> deposit());
        withdrawButton.addActionListener(e -> withdraw());

        updateHistory("Account created with balance: $" + String.format("%.2f", balance));
    }

    private void deposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0) {
                balance += amount;
                updateBalance();
                updateHistory("Deposited: $" + String.format("%.2f", amount));
            } else {
                showError("Enter a positive amount.");
            }
        } catch (NumberFormatException ex) {
            showError("Invalid amount.");
        }
    }

    private void withdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                updateBalance();
                updateHistory("Withdrew: $" + String.format("%.2f", amount));
            } else {
                showError("Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException ex) {
            showError("Invalid amount.");
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", balance));
    }

    private void updateHistory(String message) {
        transactionHistory.add(message);
        historyArea.append(message + "\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Account account = new Account("123456", "John Doe", 1000.0);
            account.setVisible(true);
        });
    }
}
