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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TrainersController implements Initializable {

    @FXML
    private Button add, delete, update;

    @FXML
    private TextField tname, tmobile, tservice, tcharges;

    @FXML
    private TableColumn<TrainerModel, String> idt, namet, mobt, servicet, chargest;

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
            idt.setCellValueFactory(f -> f.getValue().idProperty());
            namet.setCellValueFactory(f -> f.getValue().nameProperty());
            mobt.setCellValueFactory(f -> f.getValue().mobileProperty());
            servicet.setCellValueFactory(f -> f.getValue().serviceProperty());
            chargest.setCellValueFactory(f -> f.getValue().chargesProperty());
        } catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setTableRowSelection() {
        table.setRowFactory(tv -> {
            TableRow<TrainerModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    myIndex = table.getSelectionModel().getSelectedIndex();
                    TrainerModel selectedTrainer = table.getItems().get(myIndex);
                    id = Integer.parseInt(selectedTrainer.getId());
                    tname.setText(selectedTrainer.getName());
                    tmobile.setText(selectedTrainer.getMobile());
                    tservice.setText(selectedTrainer.getService());
                    tcharges.setText(selectedTrainer.getCharges());
                }
            });
            return row;
        });
    }

    @FXML
    private void addTrainer(ActionEvent event) {
        String trnname = tname.getText();
        String trnmobile = tmobile.getText();
        String trnservice = tservice.getText();
        String trncharges = tcharges.getText();

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
    private void deleteTrainer(ActionEvent event) {
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
    private void updateTrainer(ActionEvent event) {
        String trnname = tname.getText();
        String trnmobile = tmobile.getText();
        String trnservice = tservice.getText();
        String trncharges = tcharges.getText();

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
        tname.setText("");
        tmobile.setText("");
        tservice.setText("");
        tcharges.setText("");
        tname.requestFocus();
    }
}
