package src.gui;

import src.database.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManagerDashboard extends JFrame {

    private JTextField createAccNum, createHolder;
    private JTextField lookupAccNum, depositAmount, withdrawAmount;
    private JLabel lookupInfo;
    private JTextField removeAccNum;

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // Add Customer Panel
        JPanel createPanel = new JPanel(new GridLayout(4, 2));
        createAccNum = new JTextField();
        createHolder = new JTextField();
        JButton createButton = new JButton("Create Customer");
        createButton.addActionListener(this::handleCreate);
        createPanel.add(new JLabel("Account Number:"));
        createPanel.add(createAccNum);
        createPanel.add(new JLabel("Account Holder:"));
        createPanel.add(createHolder);
        createPanel.add(new JLabel("Default password: testPassword123!"));
        createPanel.add(new JLabel(""));
        createPanel.add(new JLabel(""));
        createPanel.add(createButton);

        // Lookup/Edit Customer Panel
        JPanel lookupPanel = new JPanel(new GridLayout(6, 2));
        lookupAccNum = new JTextField();
        lookupInfo = new JLabel("Info will show here");
        depositAmount = new JTextField();
        withdrawAmount = new JTextField();
        JButton lookupButton = new JButton("Lookup");
        lookupButton.addActionListener(this::handleLookup);
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(this::handleDeposit);
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(this::handleWithdraw);

        lookupPanel.add(new JLabel("Account Number:"));
        lookupPanel.add(lookupAccNum);
        lookupPanel.add(new JLabel(""));
        lookupPanel.add(lookupButton);
        lookupPanel.add(new JLabel("Customer Info:"));
        lookupPanel.add(lookupInfo);
        lookupPanel.add(new JLabel("Deposit Amount:"));
        lookupPanel.add(depositAmount);
        lookupPanel.add(new JLabel("Withdraw Amount:"));
        lookupPanel.add(withdrawAmount);
        lookupPanel.add(depositButton);
        lookupPanel.add(withdrawButton);

        // Remove Customer Panel
        JPanel removePanel = new JPanel(new GridLayout(2, 2));
        removeAccNum = new JTextField();
        JButton removeButton = new JButton("Delete Customer");
        removeButton.addActionListener(this::handleRemove);
        removePanel.add(new JLabel("Account Number:"));
        removePanel.add(removeAccNum);
        removePanel.add(new JLabel(""));
        removePanel.add(removeButton);

        // Logout Panel
        JPanel logoutPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        logoutPanel.add(logoutButton);

        // Add Tabs
        tabs.add("Create", createPanel);
        tabs.add("Lookup/Edit", lookupPanel);
        tabs.add("Remove", removePanel);
        tabs.add("Logout", logoutPanel);

        add(tabs);
    }

    private void handleCreate(ActionEvent e) {
        String acc = createAccNum.getText().trim();
        String holder = createHolder.getText().trim();
        if (acc.isEmpty() || holder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields required.");
            return;
        }
        boolean success = Database.createAccount(acc, holder, "testPassword123!", 0.0, "customer");
        JOptionPane.showMessageDialog(this, success ? "Account created." : "Account creation failed.");
    }

    private void handleLookup(ActionEvent e) {
        String acc = lookupAccNum.getText().trim();
        try {
            String holder = Database.getHolder(acc);
            double bal = Database.getBalance(acc);
            lookupInfo.setText(holder + " | Balance: $" + String.format("%.2f", bal));
        } catch (Exception ex) {
            lookupInfo.setText("Customer not found.");
        }
    }

    private void handleDeposit(ActionEvent e) {
        try {
            String acc = lookupAccNum.getText().trim();
            double amt = Double.parseDouble(depositAmount.getText());
            double cur = Database.getBalance(acc);
            Database.updateBalance(acc, cur + amt);
            handleLookup(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error depositing.");
        }
    }

    private void handleWithdraw(ActionEvent e) {
        try {
            String acc = lookupAccNum.getText().trim();
            double amt = Double.parseDouble(withdrawAmount.getText());
            double cur = Database.getBalance(acc);
            if (amt > cur) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.");
                return;
            }
            Database.updateBalance(acc, cur - amt);
            handleLookup(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error withdrawing.");
        }
    }

    private void handleRemove(ActionEvent e) {
        String acc = removeAccNum.getText().trim();
        try {
            if (acc.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Cannot delete manager account.");
                return;
            }
            int deleted = Database.deleteAccount(acc);
            JOptionPane.showMessageDialog(this, deleted > 0 ? "Deleted." : "Account not found.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting account.");
        }
    }
}
