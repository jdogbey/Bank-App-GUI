import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AccountGUI extends JFrame {
    private Account account;

    private JLabel balanceLabel;
    private JTextArea historyArea;
    private JTextField amountField;
    private JButton depositButton, withdrawButton;

    public AccountGUI(Account account) {
        this.account = account;

        setTitle("Bank Account - " + account.getAccountHolder());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for balance
        JPanel topPanel = new JPanel();
        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", account.getBalance()));
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

        updateHistory();
    }

    private void deposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (account.deposit(amount)) {
                updateBalance();
                updateHistory();
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
            if (account.withdraw(amount)) {
                updateBalance();
                updateHistory();
            } else {
                showError("Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException ex) {
            showError("Invalid amount.");
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", account.getBalance()));
    }

    private void updateHistory() {
        List<String> history = account.getTransactionHistory();
        historyArea.setText("");
        for (String entry : history) {
            historyArea.append(entry + "\n");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                Account account = new Account("123456", "John Doe", 1000.0);
            AccountGUI gui = new AccountGUI(account);
            gui.setVisible(true);
        });
    }
}
