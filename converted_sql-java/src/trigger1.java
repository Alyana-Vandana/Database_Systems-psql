
import java.sql.*;
import java.util.List;
import java.util.Map;

public class trigger1 {
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
        // Execute the query to show students and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM STUDENTS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showTAs() {
        // Execute the query to show TAs and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TAS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showCourses() {
        // Execute the query to show courses and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM COURSES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showClasses() {
        // Execute the query to show classes and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM CLASSES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showEnrollments() {
        // Execute the query to show enrollments and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ENROLLMENTS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showPrerequisites() {
        // Execute the query to show prerequisites and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM PREREQUISITES");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    public RefCursor showLogs() {
        // Execute the query to show logs and return the result set
        RefCursor refCursor = new RefCursor();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOGS");
            ResultSet resultSet = statement.executeQuery();
            // Process the result set and populate the refCursor object
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return refCursor;
    }

    // Procedure to list B#, first name and last name of the TA of the class for a given class
    public void classTA(String classId, String taBNumber, String firstName, String lastName) throws InvalidClassIdException {
        // Execute the query to get the TA details for the given classId
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT TA_B#, FIRST_NAME, LAST_NAME FROM CLASSES JOIN STUDENTS ON CLASSES.TA_B# = STUDENTS.B# WHERE CLASSID = ?");
            statement.setString(1, classId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                taBNumber = resultSet.getString("TA_B#");
                firstName = resultSet.getString("FIRST_NAME");
                lastName = resultSet.getString("LAST_NAME");
            } else {
                throw new InvalidClassIdException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Procedure to list all prerequisite courses for given course (with dept_code and course#)
    // Including both direct and indirect prerequisite courses
    public void classPrerequisite(String deptCode, String courseNumber, String prerequisiteCourses) throws InvalidDeptCodeCourseException {
        // Execute the query to get the prerequisite courses for the given deptCode and courseNumber
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT PRE_REQ FROM PREREQUISITES WHERE DEPT_CODE = ? AND COURSE# = ?");
            statement.setString(1, deptCode);
            statement.setString(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String prerequisite = resultSet.getString("PRE_REQ");
                prerequisiteCourses += prerequisite + ", ";
            }
            if (prerequisiteCourses.isEmpty()) {
                throw new InvalidDeptCodeCourseException();
            } else {
                prerequisiteCourses = prerequisiteCourses.substring(0, prerequisiteCourses.length() - 2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Procedure to Enroll Student for given class
    public void enrollStudent(String bNumber, String classId) throws InvalidBNumberException, InvalidClassIdException, ClassIsFullException, ExceededEnrollmentException, PrerequisiteNotSatisfiedException {
        // Execute the query to enroll the student for the given class
        try {
            // Check if the student exists
            PreparedStatement studentStatement = connection.prepareStatement("SELECT * FROM STUDENTS WHERE B# = ?");
            studentStatement.setString(1, bNumber);
            ResultSet studentResultSet = studentStatement.executeQuery();
            if (!studentResultSet.next()) {
                throw new InvalidBNumberException();
            }

            // Check if the class exists
            PreparedStatement classStatement = connection.prepareStatement("SELECT * FROM CLASSES WHERE CLASSID = ?");
            classStatement.setString(1, classId);
            ResultSet classResultSet = classStatement.executeQuery();
            if (!classResultSet.next()) {
                throw new InvalidClassIdException();
            }

            // Check if the class is full
            int currentEnrollment = classResultSet.getInt("ENROLLMENT");
            int maxEnrollment = classResultSet.getInt("MAX_ENROLLMENT");
            if (currentEnrollment >= maxEnrollment) {
                throw new ClassIsFullException();
            }

            // Check if the student has already enrolled in the class
            PreparedStatement enrollmentStatement = connection.prepareStatement("SELECT * FROM ENROLLMENTS WHERE B# = ? AND CLASSID = ?");
            enrollmentStatement.setString(1, bNumber);
            enrollmentStatement.setString(2, classId);
            ResultSet enrollmentResultSet = enrollmentStatement.executeQuery();
            if (enrollmentResultSet.next()) {
                throw new ExceededEnrollmentException();
            }

            // Check if the student satisfies the prerequisites for the class
            PreparedStatement prerequisiteStatement = connection.prepareStatement("SELECT * FROM PREREQUISITES WHERE DEPT_CODE = ? AND COURSE# = ?");
            prerequisiteStatement.setString(1, classResultSet.getString("DEPT_CODE"));
            prerequisiteStatement.setString(2, classResultSet.getString("COURSE#"));
            ResultSet prerequisiteResultSet = prerequisiteStatement.executeQuery();
            while (prerequisiteResultSet.next()) {
                String prerequisite = prerequisiteResultSet.getString("PRE_REQ");
                PreparedStatement completedStatement = connection.prepareStatement("SELECT * FROM ENROLLMENTS WHERE B# = ? AND CLASSID = ?");
                completedStatement.setString(1, bNumber);
                completedStatement.setString(2, prerequisite);
                ResultSet completedResultSet = completedStatement.executeQuery();
                if (!completedResultSet.next()) {
                    throw new PrerequisiteNotSatisfiedException();
                }
            }

            // Enroll the student in the class
            PreparedStatement enrollStatement = connection.prepareStatement("INSERT INTO ENROLLMENTS (B#, CLASSID) VALUES (?, ?)");
            enrollStatement.setString(1, bNumber);
            enrollStatement.setString(2, classId);
            enrollStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Procedure to drop a student from a class
    public void deleteStudentEnrollment(String bNumber, String classId) throws InvalidBNumberException, InvalidClassIdException {
        // Execute the query to delete the student enrollment for the given bNumber and classId
        try {
            // Check if the student exists
            PreparedStatement studentStatement = connection.prepareStatement("SELECT * FROM STUDENTS WHERE B# = ?");
            studentStatement.setString(1, bNumber);
            ResultSet studentResultSet = studentStatement.executeQuery();
            if (!studentResultSet.next()) {
                throw new InvalidBNumberException();
            }

            // Check if the class exists
            PreparedStatement classStatement = connection.prepareStatement("SELECT * FROM CLASSES WHERE CLASSID = ?");
            classStatement.setString(1, classId);
            ResultSet classResultSet = classStatement.executeQuery();
            if (!classResultSet.next()) {
                throw new InvalidClassIdException();
            }

            // Delete the student enrollment
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM ENROLLMENTS WHERE B# = ? AND CLASSID = ?");
            deleteStatement.setString(1, bNumber);
            deleteStatement.setString(2, classId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Procedure to delete Student
    public void deleteStudent(String bNumber) throws InvalidBNumberException {
        // Execute the query to delete the student for the given bNumber
        try {
            // Check if the student exists
            PreparedStatement studentStatement = connection.prepareStatement("SELECT * FROM STUDENTS WHERE B# = ?");
            studentStatement.setString(1, bNumber);
            ResultSet studentResultSet = studentStatement.executeQuery();
            if (!studentResultSet.next()) {
                throw new InvalidBNumberException();
            }

            // Delete the student
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM STUDENTS WHERE B# = ?");
            deleteStatement.setString(1, bNumber);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StudentRegistrationSystem system = new StudentRegistrationSystem();
        // Call the methods to perform the required operations
        system.closeConnection();
    }
}
```

Please note that this is a basic conversion of the provided PL/SQL code to Java. You may need to modify the code according to your specific requirements and database schema.
