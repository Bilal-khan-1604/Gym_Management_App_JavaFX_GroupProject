package gymManagement.application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class LoginAndNewAccountController {

    private static final String ADMIN_PASSWORD = "admin";

    @FXML
    private Label statusLabel;

    @FXML
    private PasswordField passwordField, adminPasswordField;

    @FXML
    private TextField usernameField;

    @FXML
    private DatePicker accountDatePicker;

    @FXML
    private Label createStatusLabel;

    @FXML
    private RadioButton maleRadioButton;

    @FXML
    private PasswordField accountPasswordField, confirmPasswordField;

    @FXML
    void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username or password cannot be empty.");
            return;
        }

        if (AccountDatabaseHandler.checkUserCredentials(username, password)) {
            loadScene(event, "/application/base.fxml");
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    void onCreateAccount(ActionEvent event) {
        loadScene(event, "/application/newAccount.fxml");
    }

    @FXML
    void onCreateAction(ActionEvent event) {
        String name = usernameField.getText();
        String date = accountDatePicker.getValue().toString();
        String gender = maleRadioButton.isSelected() ? "Male" : "Female";
        String password = accountPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (validatePassword(password) && password.equals(confirmPassword)) {
            createStatusLabel.setText("Password meets criteria.");
            if (AccountDatabaseHandler.isUsernameAvailable(name)) {
                if (adminPasswordField.getText().equals(ADMIN_PASSWORD)) {
                    AccountDatabaseHandler.insertAccountData(name, date, gender, password);
                    loadScene(event, "/application/login.fxml");
                } else {
                    Main.showAlert(Alert.AlertType.ERROR, "Incorrect Admin Password", "Admin Password Error", "The admin password you provided is incorrect.");
                }
            } else {
                Main.showAlert(Alert.AlertType.ERROR, "Duplicate Username", "Username Error", "The username you entered is already taken.");
            }
        } else {
            handlePasswordValidationErrors(password, confirmPassword);
        }
    }

    private boolean validatePassword(@NotNull String password) {
        return password.length() < 15 && password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
    }

    private void handlePasswordValidationErrors(String password, String confirmPassword) {
        if (!validatePassword(password)) {
            createStatusLabel.setText("Password must contain one special character and be less than 15 characters.");
        } else if (!password.equals(confirmPassword)) {
            createStatusLabel.setText("Passwords do not match.");
        }
    }

    private void loadScene(@NotNull Event event, String fxmlPath) {
        try {
            Scene scene = new Main().loadSceneFromFXML(fxmlPath);
            Stage stage = null;
            if (event instanceof ActionEvent) {
                stage = (Stage) ((Node) ((ActionEvent) event).getSource()).getScene().getWindow();
            } else if (event instanceof MouseEvent) {
                stage = (Stage) ((Node) ((MouseEvent) event).getSource()).getScene().getWindow();
            }
            if (stage != null) {
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack(MouseEvent mouseEvent) {
        System.out.println("Function called");
        loadScene(mouseEvent, "/application/login.fxml");
    }
}
