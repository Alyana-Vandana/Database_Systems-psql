
import java.sql.*;
import java.util.List;
import java.util.Map;

public class StudentRegistrationSystem {
    // Global Types
    public static class RefCursor {
        // Define the equivalent Java representation of REF_CURSOR
        // You can use ResultSet or List<Map<String, Object>> to represent the result set
    }

    // Custom Exceptions
    public static class InvalidBNumberException extends Exception {
        // Custom exception for EXCP_INVALID_B#
    }

    public static class InvalidClassIdException extends Exception {
        // Custom exception for EXCP_INVALID_CLASSID
    }

    public static class InvalidStudentEnrollException extends Exception {
        // Custom exception for EXCP_INVALID_STUDENT_ENROLL
    }

    public static class InvalidSemClassException extends Exception {
        // Custom exception for EXCP_INVALID_SEM_CLASS
    }

    public static class InvalidDropPrereqException extends Exception {
        // Custom exception for EXCP_INVALID_DROP_PREREQ
    }

    public static class InvalidDropLastClassException extends Exception {
        // Custom exception for EXCP_INVALID_DROP_LAST_CLASS
    }

    public static class InvalidDropLastStudentException extends Exception {
        // Custom exception for EXCP_INVALID_DROP_LAST_STUDENT
    }

    public static class ClassIsFullException extends Exception {
        // Custom exception for EXCP_CLASS_IS_FULL
    }

    public static class ExceededEnrollmentException extends Exception {
        // Custom exception for EXCP_EXCEEDED_ENROLLMENT
    }

    public static class PrerequisiteNotSatisfiedException extends Exception {
        // Custom exception for EXCP_PREREQ_NOT_SATISFIED
    }

    public static class InvalidDeptCodeCourseException extends Exception {
        // Custom exception for EXCP_INVALID_DEPTCODE_COURSE#
    }

    // Database Connectivity
    private Connection connection;

    public StudentRegistrationSystem() {
        // Initialize the database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "username", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        // Close the database connection
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Procedures to display the tuples in each of the seven tables for this project
    public RefCursor showStudents() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STUDENTS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showTAs() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showCourses() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM COURSES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showClasses() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM CLASSES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showEnrollments() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ENROLLMENTS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showPrerequisites() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PREREQUISITES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showLogs() {
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOGS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate refCursor
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    // Procedure to list B#, first name and last name of the TA of the class for a given class
    public void classTA(int classId, String taBNumber, String firstName, String lastName) throws InvalidClassIdException {
        try {
            CallableStatement statement = connection.prepareCall("{CALL CLASS_TA(?, ?, ?, ?)}");
            statement.setInt(1, classId);
            statement.registerOutParameter(2, Types.VARCHAR);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.registerOutParameter(4, Types.VARCHAR);
            statement.execute();
            taBNumber = statement.getString(2);
            firstName = statement.getString(3);
            lastName = statement.getString(4);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new InvalidClassIdException();
            }
            e.printStackTrace();
        }
    }

    // Procedure to list all prerequisite courses for given course (with dept_code and course#)
    // Including both direct and indirect prerequisite courses
    public String classPrereq(String deptCode, String courseNumber) throws InvalidDeptCodeCourseException {
        String prerequisites = "";
        try {
            CallableStatement statement = connection.prepareCall("{CALL CLASS_PREREQ(?, ?, ?)}");
            statement.setString(1, deptCode);
            statement.setString(2, courseNumber);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.execute();
            prerequisites = statement.getString(3);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new InvalidDeptCodeCourseException();
            }
            e.printStackTrace();
        }
        return prerequisites;
    }

    // Procedure to Enroll Student for given class
    public void enrollStudent(String bNumber, int classId) throws InvalidBNumberException, InvalidClassIdException, ExceededEnrollmentException {
        try {
            CallableStatement statement = connection.prepareCall("{CALL ENROLL_STUDENT(?, ?)}");
            statement.setString(1, bNumber);
            statement.setInt(2, classId);
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new InvalidBNumberException();
            } else if (e.getErrorCode() == 20002) {
                throw new InvalidClassIdException();
            } else if (e.getErrorCode() == 20003) {
                throw new ExceededEnrollmentException();
            }
            e.printStackTrace();
        }
    }

    // Procedure to drop a student from a class
    public void deleteStudentEnrollment(String bNumber, int classId) throws InvalidBNumberException, InvalidClassIdException {
        try {
            CallableStatement statement = connection.prepareCall("{CALL DELETE_STUDENT_ENROLLMENT(?, ?)}");
            statement.setString(1, bNumber);
            statement.setInt(2, classId);
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new InvalidBNumberException();
            } else if (e.getErrorCode() == 20002) {
                throw new InvalidClassIdException();
            }
            e.printStackTrace();
        }
    }

    // Procedure to delete Student
    public void deleteStudent(String bNumber) throws InvalidBNumberException {
        try {
            CallableStatement statement = connection.prepareCall("{CALL DELETE_STUDENT(?)}");
            statement.setString(1, bNumber);
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new InvalidBNumberException();
            }
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StudentRegistrationSystem system = new StudentRegistrationSystem();
        // Call the methods to perform the desired operations
        system.closeConnection();
    }
}
