
import java.sql.*;
import java.util.*;

public class StudentRegistrationSystem {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Example usage of procedures and functions
            String studentBNumber = "B00123456";
            String classId = "CS-101";

            // Validate student B#
            if (!validateStudentBNumber(conn, studentBNumber)) {
                System.out.println("Invalid student B#");
                return;
            }

            // Validate class ID
            if (!validateClassId(conn, classId)) {
                System.out.println("Invalid class ID");
                return;
            }

            // Enroll student in class
            enrollStudent(conn, studentBNumber, classId);

            // Get TA information for a class
            String taBNumber, firstName, lastName;
            getTaInformation(conn, classId, taBNumber, firstName, lastName);
            System.out.println("TA Information:");
            System.out.println("B#: " + taBNumber);
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);

            // Get prerequisites for a course
            String prerequisites;
            getPrerequisites(conn, "CS", "101", prerequisites);
            System.out.println("Prerequisites: " + prerequisites);

            // Delete student enrollment
            deleteStudentEnrollment(conn, studentBNumber, classId);

            // Delete student
            deleteStudent(conn, studentBNumber);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to validate student B#
    private static boolean validateStudentBNumber(Connection conn, String bNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM STUDENTS WHERE B# = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Function to validate class ID
    private static boolean validateClassId(Connection conn, String classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CLASSES WHERE CLASSID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Procedure to enroll student in class
    private static void enrollStudent(Connection conn, String bNumber, String classId) throws SQLException {
        String sql = "{call STUDENT_REGISTRATION_SYSTEM.ENROLL_STUDENT(?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.execute();
        }
    }

    // Procedure to get TA information for a class
    private static void getTaInformation(Connection conn, String classId, String taBNumber, String firstName, String lastName) throws SQLException {
        String sql = "{call STUDENT_REGISTRATION_SYSTEM.CLASS_TA(?, ?, ?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, classId);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.execute();
            taBNumber = cstmt.getString(2);
            firstName = cstmt.getString(3);
            lastName = cstmt.getString(4);
        }
    }

    // Function to get prerequisites for a course
    private static void getPrerequisites(Connection conn, String deptCode, String courseNumber, String prerequisites) throws SQLException {
        String sql = "{call STUDENT_REGISTRATION_SYSTEM.CLASS_PREREQ(?, ?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, deptCode);
            cstmt.setString(2, courseNumber);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            prerequisites = cstmt.getString(3);
        }
    }

    // Procedure to delete student enrollment
    private static void deleteStudentEnrollment(Connection conn, String bNumber, String classId) throws SQLException {
        String sql = "{call STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT_ENROLLMENT(?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.execute();
        }
    }

    // Procedure to delete student
    private static void deleteStudent(Connection conn, String bNumber) throws SQLException {
        String sql = "{call STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT(?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, bNumber);
            cstmt.execute();
        }
    }
}

