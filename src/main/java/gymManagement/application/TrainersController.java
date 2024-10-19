package gymManagement.application;


import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;

public class TrainersController implements Initializable{

    @FXML
    private Button add;

    @FXML
    private Button delete;
    
    @FXML
    private Button update;

    @FXML
    private TextField tservice;

    @FXML
    private TableColumn<TrainerModel, String> idt;

    @FXML
    private TextField tmobile;

    @FXML
    private TextField tname;
    
    @FXML
    private TextField tcharges;

    @FXML
    private TableColumn<TrainerModel, String> mobt;

    @FXML
    private TableColumn<TrainerModel, String> namet;

    @FXML
    private TableColumn<TrainerModel, String> servicet;
    
    @FXML
    private TableColumn<TrainerModel, String> chargest;

    @FXML
    private TableView<TrainerModel> table;

    Connection con;
    PreparedStatement pst;
    int myIndex;
    int id;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Connect();
		table();
		
	}
    
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/gymManagement","root","");
        }
        catch (ClassNotFoundException ex) {
          
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public void table(){
        Connect();
        ObservableList<TrainerModel> trainers = FXCollections.observableArrayList();

        try {
            pst = con.prepareStatement("select Id,Name,Mobile,Service,Charges from trainers");  
            ResultSet rs = pst.executeQuery();
            
            {
                while (rs.next()) {
                    TrainerModel trn = new TrainerModel();
                    trn.setId(rs.getString("id"));
                    trn.setName(rs.getString("name"));
                    trn.setMobile(rs.getString("mobile"));
                    trn.setService(rs.getString("service"));
                    trn.setCharges(rs.getString("charges"));
                    trainers.add(trn);
                }
            }
            
            table.setItems(trainers);
            idt.setCellValueFactory(f -> f.getValue().idProperty());
            namet.setCellValueFactory(f -> f.getValue().nameProperty());
            mobt.setCellValueFactory(f -> f.getValue().mobileProperty());
            servicet.setCellValueFactory(f -> f.getValue().serviceProperty());       
            chargest.setCellValueFactory(f -> f.getValue().chargesProperty());   
        }
        catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }

        table.setRowFactory( tv -> {
        	
            TableRow<TrainerModel> myRow = new TableRow<>();
            
            myRow.setOnMouseClicked (event -> {
                if (event.getClickCount() == 1 && (!myRow.isEmpty())) {
                    myIndex =  table.getSelectionModel().getSelectedIndex();
                    id = Integer.parseInt(String.valueOf(table.getItems().get(myIndex).getId()));
                    tname.setText(table.getItems().get(myIndex).getName());
                    tmobile.setText(table.getItems().get(myIndex).getMobile());
                    tservice.setText(table.getItems().get(myIndex).getService());
                    tcharges.setText(table.getItems().get(myIndex).getCharges());
                }
            });
            return myRow;
        });
    }


    @FXML
    void add(ActionEvent event) {
        String trnname,trnmobile,trnservice,trncharges;
        trnname = tname.getText();
        trnmobile = tmobile.getText();
        trnservice = tservice.getText();
        trncharges = tcharges.getText();

        try {
            pst = con.prepareStatement("insert into trainers(Name,Mobile,Service,Charges)values(?,?,?,?)");
            pst.setString(1, trnname);
            pst.setString(2, trnmobile);
            pst.setString(3, trnservice);
            pst.setString(4, trncharges);
            pst.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD ADDED", "Record Added Successfully!", trnname + " has been added as a trainer at this gym.");
            table();
            tname.setText("");
            tmobile.setText("");
            tservice.setText("");
            tcharges.setText("");
            tname.requestFocus();
        } 
        catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    void delete(ActionEvent event) {
    	Connect();
        myIndex = table.getSelectionModel().getSelectedIndex();
        id = Integer.parseInt(String.valueOf(table.getItems().get(myIndex).getId()));
                     
        try {
            pst = con.prepareStatement("delete from trainers where id = ? ");
            pst.setInt(1, id);
            pst.executeUpdate();
            
            Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD DELETED", "Record Deleted Successfully!", "The selected trainer record has been deleted successfully.");
            table();
        }
        catch (SQLException ex) {
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    void update(ActionEvent event) {
    	String trnname,trnmobile,trnservice,trncharges;
        myIndex = table.getSelectionModel().getSelectedIndex();
        id = Integer.parseInt(String.valueOf(table.getItems().get(myIndex).getId()));
           
        trnname = tname.getText();
        trnmobile = tmobile.getText();
        trnservice = tservice.getText();
        trncharges = tcharges.getText();

        try {
            pst = con.prepareStatement("update trainers set name = ?,mobile = ? ,service = ? ,charges = ? where id = ? ");
            pst.setString(1, trnname);
            pst.setString(2, trnmobile);
            pst.setString(3, trnservice);
            pst.setString(4, trncharges);
            pst.setInt(5, id);
            pst.executeUpdate();

            Main.showAlert(AlertType.INFORMATION, "TRAINER RECORD UPDATED", "Record Updated Successfully!", "Trainer record \"" + trnname + "\" has been updated successfully.");
            table();
        } 
        catch (SQLException ex){
            Logger.getLogger(TrainersController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
