
import java.math.BigDecimal;
import java.sql.*;

public class Main1 {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "username", "password");

            // Create sequence for Log table
            String createSequenceQuery = "CREATE SEQUENCE LOG_SEQ_GENERATOR "
                    + "MINVALUE 1 "
                    + "MAXVALUE 9999999999999999999999999999 "
                    + "START WITH 100 "
                    + "INCREMENT BY 1 "
                    + "CACHE 20";
            preparedStatement = connection.prepareStatement(createSequenceQuery);
            preparedStatement.executeUpdate();

            // Perform other database operations...

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


