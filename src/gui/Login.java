package src.gui;

import java.awt.*;
import javax.swing.*;
import src.database.Database;

public class Login extends JFrame {

    private JTextField accountField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Bank Login");
        setSize(300, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        accountField = new JTextField();
        passwordField = new JPasswordField();

        add(new JLabel("Account Number:"));
        add(accountField);
        add(new JLabel("Password:"));
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        add(loginButton);
    }

    private void handleLogin() {
        String accountNumber = accountField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (accountNumber.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = Database.authenticate(accountNumber, password);
        if (role == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            this.dispose();
            try {
                if (role.equals("customer")) {
                    String holder = Database.getHolder(accountNumber);
                    double balance = Database.getBalance(accountNumber);
                    new Account(accountNumber, holder, balance).setVisible(true);
                } else if (role.equals("manager")) {
                    new ManagerDashboard().setVisible(true); // to be implemented next
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
