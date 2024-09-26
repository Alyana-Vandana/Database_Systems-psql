import java.sql.*;

public class StudentRegistrationSystem {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "username", "password");

            // Create tables
            String createStudentsTable = "CREATE TABLE students (B_ char(4) primary key check (B_ like 'B%'), " +
                    "first_name varchar(15) not null, last_name varchar(15) not null, status varchar(10) " +
                    "check (status in ('freshman', 'sophomore', 'junior', 'senior', 'MS', 'PhD')), " +
                    "gpa decimal(3,2) check (gpa between 0 and 4.0), email varchar(20) unique, " +
                    "bdate date, deptname varchar(4) not null)";
            preparedStatement = connection.prepareStatement(createStudentsTable);
            preparedStatement.executeUpdate();

            // Insert data into students table
            String insertStudent = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertStudent);
            preparedStatement.setString(1, "B001");
            preparedStatement.setString(2, "Anne");
            preparedStatement.setString(3, "Broder");
            preparedStatement.setString(4, "junior");
            preparedStatement.setDouble(5, 3.17);
            preparedStatement.setString(6, "broder@bu.edu");
            preparedStatement.setDate(7, Date.valueOf("1990-01-17"));
            preparedStatement.setString(8, "CS");
            preparedStatement.executeUpdate();

            // Execute other SQL statements and handle result sets

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback the transaction in case of an exception
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Close the resources
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
