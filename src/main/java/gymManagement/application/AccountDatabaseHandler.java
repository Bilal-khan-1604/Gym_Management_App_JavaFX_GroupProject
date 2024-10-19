package gymManagement.application;

import javafx.scene.control.Alert;
import java.sql.*;

public class AccountDatabaseHandler {

    private static String adminUsername;

    public static void insertAccountData(String name, String date, String gender, String password) {
        String insertSQL = "INSERT INTO accountsInformation (name, date, gender, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = Main.Connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, name);
                pstmt.setString(2, date);
                pstmt.setString(3, gender);
                pstmt.setString(4, password);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    Main.showAlert(Alert.AlertType.CONFIRMATION, "ADMIN ACCOUNT CREATED", "Account Created Successfully!", "An admin account has been created. Your username is \"" + name + "\".");
                } else {
                    Main.showAlert(Alert.AlertType.ERROR, "ACCOUNT CREATION FAILED", "Account Can't Be Created", "Your admin account has not been created. Please try again later.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Main.showAlert(Alert.AlertType.ERROR, "DATABASE ERROR", "Database Connection Error", e.getMessage());
        }
    }

    public static boolean isUsernameAvailable(String name) {
        String checkSQL = "SELECT COUNT(*) FROM accountsInformation WHERE name = ?";

        try (Connection conn = Main.Connect()) {
            assert conn != null;
            try (PreparedStatement psCheckUserExists = conn.prepareStatement(checkSQL)) {
                psCheckUserExists.setString(1, name);
                try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Main.showAlert(Alert.AlertType.ERROR, "DATABASE ERROR", "Database Connection Error", e.getMessage());
        }
        return false;
    }

    public static boolean checkUserCredentials(String username, String password) {
        String query = "SELECT COUNT(*) FROM accountsInformation WHERE name = ? AND password = ?";

        try (Connection conn = Main.Connect()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        adminUsername = username;
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAdminUsername(){
        return adminUsername;
    }
}
