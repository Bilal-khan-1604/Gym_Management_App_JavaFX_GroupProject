package gymManagement.application;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.*;

public class CheckPaidMembers {

	private static int hasBeenPaid = 0;
	private static boolean beenUpdated;
	private static boolean update = false;
	private static final String FILENAME = "@../images_and_icons/paidMembers.txt";

	public void setHasBeenPaid(int newUpdate) {
		hasBeenPaid = newUpdate;
	}

	public int getHasBeenPaid() {
		return hasBeenPaid;
	}

	public void setBeenUpdated(boolean newUpdate) {
		beenUpdated = newUpdate;
	}

	public void setUpdate(boolean newUpdate) {
		update = newUpdate;
	}

	public boolean getUpdate() {
		return update;
	}

	// Load state of 'beenUpdated' from file
	public boolean loadBeenUpdatedFromFile() {
		char statusChar = 'F';  // Default to 'F'
		try (FileReader fr = new FileReader(FILENAME)) {
			statusChar = (char) fr.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statusChar == 'T';  // True if status was 'T'
	}

	// Save state of 'beenUpdated' to file
	private void saveBeenUpdatedToFile(String value) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, false))) {
			writer.write(value);
		} catch (IOException e) {
			System.err.println("Error saving 'beenUpdated' to file: " + e.getMessage());
		}
	}

	// Update the timestamp of the last check
	private void updateLastCheckedTimestamp(LocalDateTime timestamp) {
		String query = "UPDATE members SET timestamp = ?";

		try (Connection connection = Main.Connect()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, Timestamp.valueOf(timestamp));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Retrieve the last checked timestamp from the database
	private LocalDateTime getLastCheckedTimestamp() {
		LocalDateTime lastCheckedTimestamp = null;
		String query = "SELECT timestamp FROM members";

		try (Connection connection = Main.Connect()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet rs = preparedStatement.executeQuery()) {
					if (rs.next()) {
						Timestamp timestamp = rs.getTimestamp("timestamp");
						if (timestamp != null) {
							lastCheckedTimestamp = timestamp.toLocalDateTime();
						}
                	}
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}

		if (lastCheckedTimestamp == null) {
			lastCheckedTimestamp = LocalDateTime.now().minusDays(1);
		}
		return lastCheckedTimestamp;
	}

	// Monitor status changes of paid members
	public void monitorStatusChanges() {
		String query = "SELECT COUNT(*) FROM members WHERE status = 'paid' AND timestamp > ?";

		try (Connection connection = Main.Connect()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                LocalDateTime lastCheckedTimestamp = this.getLastCheckedTimestamp();
                this.updateLastCheckedTimestamp(LocalDateTime.now());
                preparedStatement.setTimestamp(1, Timestamp.valueOf(lastCheckedTimestamp));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int countPaidChanges = resultSet.getInt(1);
                        this.setHasBeenPaid(countPaidChanges);
                    }
                }
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Update all members' status to 'Unpaid'
	private void updateMemberStatusToUnpaid() {
		String query = "UPDATE members SET status = 'Unpaid'";

		try (Connection connection = Main.Connect()) {
            assert connection != null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void callUpdateMemberStatusToUnpaid() {
		try {
			if (this.getUpdate() && !beenUpdated) {
				this.setUpdate(false);
				this.saveBeenUpdatedToFile("T");
				this.updateMemberStatusToUnpaid();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.setUpdate(false);
			if (LocalDate.now().getDayOfMonth() > 1) {
				this.saveBeenUpdatedToFile("F");
			}
		}
	}
}