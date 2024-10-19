package gymManagement.application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;


public class Main extends Application {

	public Scene loadSceneFromFXML(String fxmlPath) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		return new Scene(loader.load());
	}

	@Override
	public void start(Stage primaryStage) {
        try {
			Scene scene = loadSceneFromFXML("/application/login.fxml");
			scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/base.css")).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Gym Management System");
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection Connect() {
		String url = "jdbc:mysql://localhost:3306/gymManagement?autoReconnect=true";
		String user = "root";
		String password = "";

		try {
            return DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			System.err.println("SQL error: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static void showAlert(AlertType alertType, String title, String header, String content) {
    	Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

	public static void main(String[] args) {
		launch(args);
	}
}