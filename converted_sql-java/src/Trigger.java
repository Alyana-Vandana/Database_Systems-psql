

import java.sql.*;

public class Trigger {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "username", "password");

            // Create trigger
            String triggerQuery = "CREATE TRIGGER TRIG_ON_DEL_ENROLL_INS_LOGS " +
                    "AFTER DELETE ON ENROLLMENTS " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "INSERT INTO LOGS " +
                    "VALUES (?, ?, ?, ?, ?, ?); " +
                    "END;";
            statement = connection.prepareStatement(triggerQuery);

            // Set trigger values
            statement.setInt(1, getNextLogSeqVal());
            statement.setString(2, System.getProperty("user.name"));
            statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(4, "ENROLLMENTS");
            statement.setString(5, "DELETE");
            statement.setString(6, oldB + "," + oldClassId);

            // Execute trigger
            statement.executeUpdate();

            System.out.println("Trigger created and executed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close statement and connection
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getNextLogSeqVal() {
        // Implement logic to get next value from LOG_SEQ_GENERATOR sequence
        return 0;
    }
}
