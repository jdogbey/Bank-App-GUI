package src.database;

import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:db/bank.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number TEXT PRIMARY KEY,
                    holder TEXT NOT NULL,
                    password TEXT NOT NULL,
                    balance REAL DEFAULT 0,
                    role TEXT NOT NULL CHECK(role IN ('customer', 'manager'))
                );
            """);
            System.out.println("[DB] Initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean createAccount(String accNum, String holder, String password, double balance, String role) {
        String sql = "INSERT INTO accounts (account_number, holder, password, balance, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNum);
            ps.setString(2, holder);
            ps.setString(3, password);
            ps.setDouble(4, balance);
            ps.setString(5, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Account creation failed: " + e.getMessage());
            return false;
        }
    }

    public static String authenticate(String accNum, String password) {
        String sql = "SELECT role FROM accounts WHERE account_number = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNum);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getBalance(String accNum) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("balance") : 0.0;
        }
    }

    public static String getHolder(String accNum) throws SQLException {
        String sql = "SELECT holder FROM accounts WHERE account_number = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accNum);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("holder") : "";
        }
    }

    public static void updateBalance(String accNum, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accNum);
            ps.executeUpdate();
        }
    }
}
