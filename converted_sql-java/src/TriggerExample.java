import java.sql.*;

public class TriggerExample {
    public static void main(String[] args) {
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "username", "password");

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the trigger
            statement.executeUpdate("CREATE TRIGGER TRIG_ON_DEL_ENROLL_UPD_CLASSES " +
                    "AFTER DELETE ON ENROLLMENTS " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "UPDATE CLASSES " +
                    "SET CLASS_SIZE = CLASS_SIZE - 1 " +
                    "WHERE CLASSID = OLD.CLASSID; " +
                    "END;");

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
