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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TrainersController implements Initializable {

    @FXML
    private TextField name, mobile, service, charges;

    @FXML
    private TableColumn<TrainerModel, String> id_column, name_column, mobile_column, service_column, charges_column;

    @FXML
    private TableView<TrainerModel> table;

    private Connection con;
    private PreparedStatement pst;
    private int myIndex;
    private int id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con = Main.Connect();
        loadTableData();
        setTableRowSelection();
    }

    private void loadTableData() {
        ObservableList<TrainerModel> trainers = FXCollections.observableArrayList();

        try {
            pst = con.prepareStatement("SELECT Id, Name, Mobile, Service, Charges FROM trainers");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TrainerModel trn = new TrainerModel(
                        rs.getString("Id"),
                        rs.getString("Name"),
                        rs.getString("Mobile"),
                        rs.getString("Service"),
                        rs.getString("Charges")
                );
                trainers.add(trn);
            }

            table.setItems(trainers);
            id_column.setCellValueFactory(f -> f.getValue().idProperty());
            name_column.setCellValueFactory(f -> f.getValue().nameProperty());
            mobile_column.setCellValueFactory(f -> f.getValue().mobileProperty());
            service_column.setCellValueFactory(f -> f.getValue().serviceProperty());
            charges_column.setCellValueFactory(f -> f.getValue().chargesProperty());
        } catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setTableRowSelection() {
        table.setRowFactory(_ -> {
            TableRow<TrainerModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    myIndex = table.getSelectionModel().getSelectedIndex();
                    TrainerModel selectedTrainer = table.getItems().get(myIndex);
                    id = Integer.parseInt(selectedTrainer.getId());
                    name.setText(selectedTrainer.getName());
                    mobile.setText(selectedTrainer.getMobile());
                    service.setText(selectedTrainer.getService());
                    charges.setText(selectedTrainer.getCharges());
                }
            });
            return row;
        });
    }

    @FXML
    private void addTrainer() {
        String trnname = name.getText();
        String trnmobile = mobile.getText();
        String trnservice = service.getText();
        String trncharges = charges.getText();

        try {
            pst = con.prepareStatement("INSERT INTO trainers(Name, Mobile, Service, Charges) VALUES (?, ?, ?, ?)");
            pst.setString(1, trnname);
            pst.setString(2, trnmobile);
            pst.setString(3, trnservice);
            pst.setString(4, trncharges);
            pst.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD ADDED", "Record Added Successfully!", trnname + " has been added as a trainer at this gym.");
            loadTableData();
            clearFormFields();
        } catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteTrainer() {
        try {
            if (myIndex >= 0) {
                pst = con.prepareStatement("DELETE FROM trainers WHERE Id = ?");
                pst.setInt(1, id);
                pst.executeUpdate();

                Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD DELETED", "Record Deleted Successfully!", "The selected trainer record has been deleted successfully.");
                loadTableData();
                clearFormFields();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void updateTrainer() {
        String trnname = name.getText();
        String trnmobile = mobile.getText();
        String trnservice = service.getText();
        String trncharges = charges.getText();

        try {
            if (myIndex >= 0) {
                pst = con.prepareStatement("UPDATE trainers SET Name = ?, Mobile = ?, Service = ?, Charges = ? WHERE Id = ?");
                pst.setString(1, trnname);
                pst.setString(2, trnmobile);
                pst.setString(3, trnservice);
                pst.setString(4, trncharges);
                pst.setInt(5, id);
                pst.executeUpdate();

                Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD UPDATED", "Record Updated Successfully!", "Trainer record \"" + trnname + "\" has been updated successfully.");
                loadTableData();
                clearFormFields();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearFormFields() {
        name.setText("");
        mobile.setText("");
        service.setText("");
        charges.setText("");
        name.requestFocus();
    }
}
