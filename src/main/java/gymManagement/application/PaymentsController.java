package gymManagement.application;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaymentsController implements Initializable {

    @FXML
    private Label receivedFee;

    @FXML
    private TextField salaries, retail_sales, locker_rentals, penalties, outstanding_balance;

    @FXML
    private ComboBox<String> monthSelector;

    private Connection con;
    private int monthId;
    public String txtFxId, txtFxIdForGettingPaymentValues;
    private final ObservableList<Month> months = FXCollections.observableArrayList(
            new Month(1, "January"),
            new Month(2, "February"),
            new Month(3, "March"),
            new Month(4, "April"),
            new Month(5, "May"),
            new Month(6, "June"),
            new Month(7, "July"),
            new Month(8, "August"),
            new Month(9, "September"),
            new Month(10, "October"),
            new Month(11, "November"),
            new Month(12, "December")
    );

    public void setReceivedFeeText(String rfstr) {
        receivedFee.setText(rfstr);
    }

    public void setTextFieldText(@NotNull TextField textfield, String value) {
        textfield.setText(value);
    }

    public String getTextFieldText(@NotNull TextField textfield) {
        return textfield.getText();
    }

    private @Nullable Month getMonthByName(String name) {
        return months.stream().filter(month -> month.getName().equals(name)).findFirst().orElse(null);
    }

    public void getSelectedMonthId() {
        monthSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Month selectedMonth = getMonthByName(newVal);
                if (selectedMonth != null) {
                    monthId = selectedMonth.getId();
                    System.out.println("Selected month: " + newVal + ", ID: " + monthId);

                    callGetPaymentValues(salaries, retail_sales, locker_rentals, penalties, outstanding_balance);
                    getFeeReceivedValue();
                }
            }
        });
    }

    public void getDefaultSelectedMonthId() {
        String defaultSelectedMonth = monthSelector.getSelectionModel().getSelectedItem();
        Month selectedMonth = getMonthByName(defaultSelectedMonth);
        if (selectedMonth != null) {
            monthId = selectedMonth.getId();
        }
    }

    public void setTextFieldFocus(TextField @NotNull ...args) {
        for (TextField textField : args) {
            textField.setFocusTraversable(false);
            textField.setOnMouseClicked(event -> textField.requestFocus());
        }
    }

    private @NotNull ObservableList<String> getMonthNames() {
        ObservableList<String> monthNames = FXCollections.observableArrayList();
        for (Month month : months) {
            monthNames.add(month.getName());
        }
        return monthNames;
    }

    public void updatePaymentValues(@NotNull TextField mtxtf) {
        getSelectedMonthId();
        mtxtf.setOnAction(_ -> {
            String enteredText = getTextFieldText(mtxtf);
            if (!enteredText.isEmpty()) {
                txtFxId = mtxtf.getId();
            }
            try {
                con = Main.Connect();
                PreparedStatement pstmt = con.prepareStatement("UPDATE monthly_payments SET " + txtFxId + " = ? WHERE month = ?");
                pstmt.setInt(1, Integer.parseInt(enteredText));
                pstmt.setInt(2, monthId);

                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void callUpdatePaymentValues(TextField @NotNull ...cmtxtf) {
        for (TextField textField : cmtxtf) {
            updatePaymentValues(textField);
        }
    }

    public void getPaymentValues(@NotNull TextField mtxtfForGettingPaymentValues) {
        txtFxIdForGettingPaymentValues = mtxtfForGettingPaymentValues.getId();
        getDefaultSelectedMonthId();
        try {
            con = Main.Connect();
            PreparedStatement pstmt = con.prepareStatement("SELECT " + txtFxIdForGettingPaymentValues + " FROM monthly_payments WHERE month = ?");
            pstmt.setInt(1, monthId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int paymentValue = rs.getInt(1);
                setTextFieldText(mtxtfForGettingPaymentValues, String.valueOf(paymentValue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void callGetPaymentValues(TextField @NotNull ...cmtxtf) {
        for (TextField textField : cmtxtf) {
            getPaymentValues(textField);
        }
    }

    public void getFeeReceivedValue() {
        getDefaultSelectedMonthId();
        try {
            con = Main.Connect();
            PreparedStatement pstmt = con.prepareStatement("SELECT received_fee FROM monthly_payments WHERE month = ?");
            pstmt.setInt(1, monthId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int paymentValue = rs.getInt(1);
                setReceivedFeeText(String.valueOf(paymentValue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFeeReceived(int fee) {
        try {
            con = Main.Connect();
            PreparedStatement pstmt = con.prepareStatement("UPDATE monthly_payments SET received_fee = ? WHERE month = ?");
            pstmt.setInt(1, fee);
            pstmt.setInt(2, LocalDate.now().getMonthValue());

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        CheckPaidMembers cpm = new CheckPaidMembers();
        cpm.monitorStatusChanges();

        setTextFieldFocus(salaries, retail_sales, locker_rentals, penalties, outstanding_balance);

        monthSelector.setItems(getMonthNames());
        monthSelector.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        // Get and update payment values and fee received
        callUpdatePaymentValues(salaries, retail_sales, locker_rentals, penalties, outstanding_balance);
        callGetPaymentValues(salaries, retail_sales, locker_rentals, penalties, outstanding_balance);
        getFeeReceivedValue();
        updateFeeReceived(cpm.getHasBeenPaid() * 1500);
    }
}

class Month {
    private final int id;
    private final SimpleStringProperty name;

    public Month(int id, String name) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    @Override
    public String toString() {
        return name.get();
    }
}