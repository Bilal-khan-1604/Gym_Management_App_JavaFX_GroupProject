module GymManagementApp {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;
    requires org.jetbrains.annotations;

    opens gymManagement.application;
}