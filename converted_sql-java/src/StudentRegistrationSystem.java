import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRegistrationSystem {

    // Global Types
    public static class RefCursor {
        private ResultSet resultSet;

        public RefCursor(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }
    }

    // COMMON EXCEPTION
    public static class InvalidBException extends Exception {
        public InvalidBException(String message) {
            super(message);
        }
    }

    public static class InvalidClassIdException extends Exception {
        public InvalidClassIdException(String message) {
            super(message);
        }
    }

    public static class InvalidStudentEnrollException extends Exception {
        public InvalidStudentEnrollException(String message) {
            super(message);
        }
    }

    public static class InvalidSemClassException extends Exception {
        public InvalidSemClassException(String message) {
            super(message);
        }
    }

    public static class InvalidDropPrereqException extends Exception {
        public InvalidDropPrereqException(String message) {
            super(message);
        }
    }

    public static class InvalidDropLastClassException extends Exception {
        public InvalidDropLastClassException(String message) {
            super(message);
        }
    }

    public static class InvalidDropLastStudentException extends Exception {
        public InvalidDropLastStudentException(String message) {
            super(message);
        }
    }

    public static class ClassIsFullException extends Exception {
        public ClassIsFullException(String message) {
            super(message);
        }
    }

    public static class ExceededEnrollmentException extends Exception {
        public ExceededEnrollmentException(String message) {
            super(message);
        }
    }

    public static class PrereqNotSatisfiedException extends Exception {
        public PrereqNotSatisfiedException(String message) {
            super(message);
        }
    }

    public static class InvalidDeptCodeCourseException extends Exception {
        public InvalidDeptCodeCourseException(String message) {
            super(message);
        }
    }

    // Procedures to display the tuples in each of the seven tables for this project.
    public static RefCursor showStudents(Connection connection) throws SQLException {
        String sql = "SELECT * FROM STUDENTS";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showTAs(Connection connection) throws SQLException {
        String sql = "SELECT * FROM TAS";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showCourses(Connection connection) throws SQLException {
        String sql = "SELECT * FROM COURSES";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showClasses(Connection connection) throws SQLException {
        String sql = "SELECT * FROM CLASSES";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showEnrollments(Connection connection) throws SQLException {
        String sql = "SELECT * FROM ENROLLMENTS";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showPrerequisites(Connection connection) throws SQLException {
        String sql = "SELECT * FROM PREREQUISITES";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }

    public static RefCursor showLogs(Connection connection) throws SQLException {
        String sql = "SELECT * FROM LOGS";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new RefCursor(resultSet);
    }
}
