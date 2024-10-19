package gymManagement.application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController implements Initializable {

	@FXML
	private BarChart<String, Integer> barChart;

	@FXML
	private Label totalMembersLabel,trainersLabel, equipmentLabel;

	@FXML
	private PieChart pieChart;

	private Connection con;
	private int paidMembers, unpaidMembers, activeEquipment, inactiveEquipment;

	@SuppressWarnings("unchecked")
	private void setupBarChart() {
		XYChart.Series<String, Integer> paidSeries = new XYChart.Series<>();
		paidSeries.setName("Paid");
		paidSeries.getData().add(new XYChart.Data<>("Paid", paidMembers));

		XYChart.Series<String, Integer> unpaidSeries = new XYChart.Series<>();
		unpaidSeries.setName("Unpaid");
		unpaidSeries.getData().add(new XYChart.Data<>("Unpaid", unpaidMembers));

		barChart.getData().addAll( paidSeries, unpaidSeries);
	}

	private void setupPieChart() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Active", activeEquipment),
				new PieChart.Data("Inactive", inactiveEquipment)
		);

		pieChartData.forEach(data ->
				data.nameProperty().bind(
						Bindings.concat(
								data.getName(), " machines: ", data.pieValueProperty().intValue()
						)
				)
		);
		pieChart.getData().addAll(pieChartData);
	}

	private void loadMemberCounts() {
		String query = "SELECT " +
				"COUNT(*) AS total, " +
				"SUM(CASE WHEN status = 'paid' THEN 1 ELSE 0 END) AS paid, " +
				"SUM(CASE WHEN status = 'unpaid' THEN 1 ELSE 0 END) AS unpaid " +
				"FROM members";
		try (PreparedStatement pst = con.prepareStatement(query);
			 ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				totalMembersLabel.setText(rs.getString("total"));
				paidMembers = rs.getInt("paid");
				unpaidMembers = rs.getInt("unpaid");
			}
		} catch (SQLException e) {
			e.printStackTrace();  // Replace with logger if needed
		}
	}

	private void loadTrainerCounts() {
		String query = "SELECT COUNT(*) AS total FROM trainers";
		try (PreparedStatement pst = con.prepareStatement(query);
			 ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				trainersLabel.setText(rs.getString("total"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  // Replace with logger if needed
		}
	}

	private void loadEquipmentCounts() {
		String query = "SELECT " +
				"COUNT(*) AS total, " +
				"SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) AS active, " +
				"SUM(CASE WHEN status = 'inactive' THEN 1 ELSE 0 END) AS inactive " +
				"FROM equipments";
		try (PreparedStatement pst = con.prepareStatement(query);
			 ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				equipmentLabel.setText(rs.getString("total"));
				activeEquipment = rs.getInt("active");
				inactiveEquipment = rs.getInt("inactive");
			}
		} catch (SQLException e) {
			e.printStackTrace();  // Replace with logger if needed
		}
	}

	private void setupCharts() {
		setupBarChart();
		setupPieChart();
	}

	private void loadDashboardData() {
		loadMemberCounts();
		loadTrainerCounts();
		loadEquipmentCounts();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		con = Main.Connect();
		loadDashboardData();
		setupCharts();
	}
}