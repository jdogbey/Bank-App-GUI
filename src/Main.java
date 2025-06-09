package src;

import src.gui.Account;
import src.gui.Login;
import src.database.Database;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Database.initialize();

        // Optional: preload manager
        Database.createAccount("admin", "Bank Manager", "adminpass", 0.0, "manager");

        javax.swing.SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
