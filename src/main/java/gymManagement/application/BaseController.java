package gymManagement.application;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class BaseController {

    @FXML
    private AnchorPane sidebar, menuListPane, contentPane;
    
    @FXML
    private Label adminNameLabel, dashboard, members, equipments, trainers, payments, logout;

    private boolean isMenuVisible = false;

    private void loadFXML(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Pane newLoadedPane = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newLoadedPane);
            AnchorPane.setTopAnchor(newLoadedPane, 0.0);
            AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
            AnchorPane.setRightAnchor(newLoadedPane, 0.0);
            AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // called within the base.fxml
    public void switchFxmlScene(@NotNull MouseEvent event) {
        String fxId = "dashboard";
        Node targetNode = (Node) event.getTarget();
        if (targetNode instanceof Label clickedLabel) {
            // Clicked directly on the label
            fxId = clickedLabel.getId();
        } else if (targetNode.getParent() instanceof Label parentLabel) {
            // Clicked on the text inside the label
            fxId = parentLabel.getId();
        }
        loadFXML("/application/"+fxId+".fxml");
        hideMenu();
    }

    private void showMenu() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), menuListPane);
        transition.setToX(0); // Move to the visible position
        transition.setOnFinished(_ -> isMenuVisible = true);
        transition.play();
    }

    @FXML
    private void highlightLabelsOnHover() {
        applyHoverEffects(dashboard, members, equipments, trainers, payments, logout);
    }

    private void applyHoverEffects(Label... labels) {
        for (Label label : labels) {
            label.setOnMouseEntered(_ -> {
                label.getStyleClass().add("highlightLabelsOnHover");
                label.setCursor(Cursor.HAND);
            });

            label.setOnMouseExited(_ -> {
                label.getStyleClass().remove("highlightLabelsOnHover");
                label.setCursor(Cursor.DEFAULT);
            });
        }
    }

    private void handleMouseEntered(MouseEvent event) {
        // Show the menu when the mouse enters either sidebar or menuListPane
        if (!isMenuVisible) {
            showMenu();
            highlightLabelsOnHover();
        }
    }

    private boolean isMouseInsideSidebarOrMenuListPane(MouseEvent event) {
        return event.getSceneX() >= 0 && event.getSceneY() >= 0 &&
                event.getSceneX() <= sidebar.getWidth() && event.getSceneY() <= sidebar.getHeight() ||
                event.getSceneX() >= 0 && event.getSceneY() >= 0 &&
                        event.getSceneX() <= menuListPane.getWidth() && event.getSceneY() <= menuListPane.getHeight();
    }

    private void hideMenu() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), menuListPane);
        transition.setToX(-400); // Move to the hidden position
        transition.setOnFinished(_ -> isMenuVisible = false);
        transition.play();
    }

    private void handleMouseExited(MouseEvent event) {
        // Hide the menu only when the mouse exits both sidebar and menuListPane
        if (isMenuVisible && !isMouseInsideSidebarOrMenuListPane(event)) {
            hideMenu();
        }
    }

    private void setAdminUserName(String username) {
        adminNameLabel.setText(username);
    }

    @FXML
    private void handleLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) logout.getScene().getWindow(); // closing current window
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();  // Replace with proper exception handling
        }
    }

    @FXML
    private void initialize() {
        // Add event handlers to both sidebar and menuListPane
        sidebar.setOnMouseEntered(this::handleMouseEntered);
        sidebar.setOnMouseExited(this::handleMouseExited);
        menuListPane.setOnMouseEntered(this::handleMouseEntered);
        menuListPane.setOnMouseExited(this::handleMouseExited);
        loadFXML("/application/dashboard.fxml");
        setAdminUserName(AccountDatabaseHandler.getAdminUsername());
    }
}

