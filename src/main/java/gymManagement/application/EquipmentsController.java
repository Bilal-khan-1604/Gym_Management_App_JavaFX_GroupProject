package gymManagement.application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class EquipmentsController implements Initializable {

    @FXML
    private TextField equipmentStatus, equipmentName, equipmentId;

    @FXML
    private TableColumn<EquipmentModel, String> idColumn, nameColumn, equipmentIdColumn, statusColumn;

    @FXML
    private TableView<EquipmentModel> equipmentTable;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private int selectedIndex, selectedEquipmentId;

    private void clearFields() {
        equipmentId.setText("");
        equipmentName.setText("");
        equipmentStatus.setText("");
        equipmentId.requestFocus();
    }

    @FXML
    void addEquipment() {
        String equipmentIdInput = equipmentId.getText();
        String equipmentNameInput = equipmentName.getText();
        String equipmentStatusInput = equipmentStatus.getText();

        try {
            preparedStatement = connection.prepareStatement("insert into equipments(Equipments_Id, Name, Status) values(?, ?, ?)");
            preparedStatement.setString(1, equipmentIdInput);
            preparedStatement.setString(2, equipmentNameInput);
            preparedStatement.setString(3, equipmentStatusInput);
            preparedStatement.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "Equipment Added", "Record Added Successfully", equipmentNameInput + " has been added to the gym.");
            populateTable();
            clearFields();
        } catch (SQLException ex) {
            Logger.getLogger(EquipmentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void deleteEquipment() {
        try {
            preparedStatement = connection.prepareStatement("delete from equipments where Id = ?");
            preparedStatement.setInt(1, selectedEquipmentId);
            preparedStatement.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "Equipment Deleted", "Record Deleted Successfully", "The selected equipment record has been deleted.");
            populateTable();
        } catch (SQLException ex) {
            Logger.getLogger(EquipmentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void updateEquipment() {
        String updatedEquipmentId = equipmentId.getText();
        String updatedEquipmentName = equipmentName.getText();
        String updatedEquipmentStatus = equipmentStatus.getText();

        try {
            preparedStatement = connection.prepareStatement("update equipments set Equipments_Id = ?, Name = ?, Status = ? where Id = ?");
            preparedStatement.setString(1, updatedEquipmentId);
            preparedStatement.setString(2, updatedEquipmentName);
            preparedStatement.setString(3, updatedEquipmentStatus);
            preparedStatement.setInt(4, selectedEquipmentId);
            preparedStatement.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "Equipment Updated", "Record Updated Successfully", "Equipment record for \"" + updatedEquipmentName + "\" has been updated.");
            populateTable();
        } catch (SQLException ex) {
            Logger.getLogger(EquipmentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableData() {
        connection = Main.Connect();
        ObservableList<EquipmentModel> equipmentList = FXCollections.observableArrayList();

        try {
            preparedStatement = connection.prepareStatement("SELECT Id, Equipments_Id, Name, Status FROM equipments");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                EquipmentModel equipment = new EquipmentModel();
                equipment.setId(resultSet.getString("Id"));
                equipment.setName(resultSet.getString("Name"));
                equipment.setEquipmentId(resultSet.getString("Equipments_Id"));
                equipment.setStatus(resultSet.getString("Status"));
                equipmentList.add(equipment);
            }

            equipmentTable.setItems(equipmentList);
            idColumn.setCellValueFactory(f -> f.getValue().idProperty());
            equipmentIdColumn.setCellValueFactory(f -> f.getValue().equipmentIdProperty());
            nameColumn.setCellValueFactory(f -> f.getValue().nameProperty());
            statusColumn.setCellValueFactory(f -> f.getValue().statusProperty());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setupTableRowSelection() {
        equipmentTable.setRowFactory(_ -> {
            TableRow<EquipmentModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    selectedIndex = equipmentTable.getSelectionModel().getSelectedIndex();
                    selectedEquipmentId = Integer.parseInt(equipmentTable.getItems().get(selectedIndex).getId());
                    equipmentId.setText(equipmentTable.getItems().get(selectedIndex).getEquipmentId());
                    equipmentName.setText(equipmentTable.getItems().get(selectedIndex).getName());
                    equipmentStatus.setText(equipmentTable.getItems().get(selectedIndex).getStatus());
                }
            });
            return row;
        });
    }

    public void populateTable() {
        loadTableData();
        setupTableRowSelection();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        connection = Main.Connect();
        populateTable();
    }
}
