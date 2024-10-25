package gymManagement.application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MembersController implements Initializable {

    @FXML
    private TextField status, mobile, name;

    @FXML
    private TableColumn<MemberModel, String> id_column, mobile_column, name_column, status_column;

    @FXML
    private TableView<MemberModel> table;

    private int myIndex, id;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        loadTableData();
        if (LocalDate.now().getDayOfMonth() == 1) {
            CheckPaidMembers cpm = new CheckPaidMembers();
            cpm.setUpdate(true);
            cpm.setBeenUpdated(cpm.loadBeenUpdatedFromFile());
            cpm.callUpdateMemberStatusToUnpaid();
        }
        setupTableRowSelection();
    }

    private void loadTableData() {
        ObservableList<MemberModel> members = FXCollections.observableArrayList();

        String query = "SELECT Id, Name, Mobile, Status FROM members";

        try (Connection con = Main.Connect()) {
            assert con != null;

            try (PreparedStatement pst = con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        MemberModel member = new MemberModel();
                        member.setId(rs.getString("Id"));
                        member.setName(rs.getString("Name"));
                        member.setMobile(rs.getString("Mobile"));
                        member.setStatus(rs.getString("Status"));
                        members.add(member);
                    }
                table.setItems(members);
                id_column.setCellValueFactory(f -> f.getValue().idProperty());
                name_column.setCellValueFactory(f -> f.getValue().nameProperty());
                mobile_column.setCellValueFactory(f -> f.getValue().mobileProperty());
                status_column.setCellValueFactory(f -> f.getValue().statusProperty());
            }

        } catch (SQLException ex) {
            Logger.getLogger(MembersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setupTableRowSelection() {
        table.setRowFactory(_ -> {
            TableRow<MemberModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    myIndex = table.getSelectionModel().getSelectedIndex();
                    MemberModel selectedMember = table.getItems().get(myIndex);
                    id = Integer.parseInt(selectedMember.getId());
                    name.setText(selectedMember.getName());
                    mobile.setText(selectedMember.getMobile());
                    status.setText(selectedMember.getStatus());
                }
            });
            return row;
        });
    }

    @FXML
    private void addMember() {

        String query = "INSERT INTO members (Name, Mobile, Status) VALUES (?, ?, ?)";

        try (Connection con = Main.Connect()) {
            assert con != null;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, name.getText());
                pst.setString(2, mobile.getText());
                pst.setString(3, status.getText());
                pst.executeUpdate();

                Main.showAlert(Alert.AlertType.INFORMATION, "Member Added", null, name.getText() + " has been added as a member of this gym.");
                clearFormFields();
                loadTableData();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MembersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteMember() {
        if (myIndex < 0) return;

        String query = "DELETE FROM members WHERE Id = ?";

        try (Connection con = Main.Connect()) {
            assert con != null;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, id);
                pst.executeUpdate();

                Main.showAlert(Alert.AlertType.INFORMATION, "Member Deleted",  null, "The selected member has been deleted.");
                loadTableData();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MembersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void updateMember() {
        if (myIndex < 0) return;

        String query = "UPDATE members SET Name = ?, Mobile = ?, Status = ? WHERE Id = ?";

        try (Connection con = Main.Connect()) {
            assert con != null;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, name.getText());
                pst.setString(2, mobile.getText());
                pst.setString(3, status.getText());
                pst.setInt(4, id);
                pst.executeUpdate();

                Main.showAlert(Alert.AlertType.INFORMATION, "Member Updated", null, "Member record \"" + name.getText() + "\" has been updated.");
                clearFormFields();
                loadTableData();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MembersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearFormFields() {
        name.setText("");
        mobile.setText("");
        status.setText("");
        name.requestFocus();
    }
}