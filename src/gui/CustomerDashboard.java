package src.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CustomerDashboard extends JFrame {
    private JList<String> accountList;
    private DefaultListModel<String> accountListModel;
    private JTextField transferAmountField;
    private JComboBox<String> toAccountCombo;
    private JButton transferButton, openAccountButton;
    private ArrayList<Account> accounts;
    private String customerName;

    public CustomerDashboard(String customerName, ArrayList<Account> accounts) {
        this.customerName = customerName;
        this.accounts = accounts;
        setTitle("Customer Dashboard - " + customerName);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Account list panel
        accountListModel = new DefaultListModel<>();
        for (Account acc : accounts) {
            accountListModel.addElement(acc.getAccountNumber() + " | Balance: $" + String.format("%.2f", acc.getBalance()));
        }
        accountList = new JList<>(accountListModel);
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(accountList);
        listScroll.setPreferredSize(new Dimension(300, 300));

        // Open account button
        openAccountButton = new JButton("Open Selected Account");
        openAccountButton.addActionListener(e -> openSelectedAccount());
        accountList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openSelectedAccount();
                }
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Your Accounts:"), BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        leftPanel.add(openAccountButton, BorderLayout.SOUTH);

        // Transfer panel
        JPanel transferPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        transferPanel.setBorder(BorderFactory.createTitledBorder("Transfer Between Your Accounts"));
        transferPanel.add(new JLabel("From Account:"));
        JTextField fromAccountField = new JTextField();
        fromAccountField.setEditable(false);
        transferPanel.add(fromAccountField);
        transferPanel.add(new JLabel("To Account:"));
        toAccountCombo = new JComboBox<>();
        for (Account acc : accounts) {
            toAccountCombo.addItem(acc.getAccountNumber());
        }
        transferPanel.add(toAccountCombo);
        transferPanel.add(new JLabel("Amount:"));
        transferAmountField = new JTextField();
        transferPanel.add(transferAmountField);
        transferButton = new JButton("Transfer");
        transferPanel.add(new JLabel(""));
        transferPanel.add(transferButton);

        // When an account is selected, set as 'from' account
        accountList.addListSelectionListener(e -> {
            int idx = accountList.getSelectedIndex();
            if (idx >= 0) {
                fromAccountField.setText(accounts.get(idx).getAccountNumber());
            }
        });

        transferButton.addActionListener(e -> {
            int fromIdx = accountList.getSelectedIndex();
            String toAccNum = (String) toAccountCombo.getSelectedItem();
            String amtStr = transferAmountField.getText();
            if (fromIdx < 0 || toAccNum == null || amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select accounts and enter amount.");
                return;
            }
            if (accounts.get(fromIdx).getAccountNumber().equals(toAccNum)) {
                JOptionPane.showMessageDialog(this, "Cannot transfer to the same account.");
                return;
            }
            try {
                double amt = Double.parseDouble(amtStr);
                Account from = accounts.get(fromIdx);
                Account to = null;
                for (Account acc : accounts) {
                    if (acc.getAccountNumber().equals(toAccNum)) {
                        to = acc;
                        break;
                    }
                }
                if (to == null) {
                    JOptionPane.showMessageDialog(this, "To account not found.");
                    return;
                }
                if (amt <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter a positive amount.");
                    return;
                }
                if (from.getBalance() < amt) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                    return;
                }
                from.withdrawAmount(amt);
                to.depositAmount(amt);
                // Persist changes to database
                try {
                    src.database.Database.updateBalance(from.getAccountNumber(), from.getBalance());
                    src.database.Database.updateBalance(to.getAccountNumber(), to.getBalance());
                } catch (Exception dbEx) {
                    JOptionPane.showMessageDialog(this, "Error updating database.");
                }
                updateAccountList();
                JOptionPane.showMessageDialog(this, "Transfer successful.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(transferPanel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void openSelectedAccount() {
        int idx = accountList.getSelectedIndex();
        if (idx >= 0) {
            Account acc = accounts.get(idx);
            Account gui = new Account(acc.getAccountNumber(), acc.getAccountHolder(), acc.getBalance());
            gui.setVisible(true);
        }
    }

    private void updateAccountList() {
        accountListModel.clear();
        for (Account acc : accounts) {
            accountListModel.addElement(acc.getAccountNumber() + " | Balance: $" + String.format("%.2f", acc.getBalance()));
        }
    }
}
