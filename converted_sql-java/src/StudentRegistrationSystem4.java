
import java.sql.*;
import java.util.*;

public class StudentRegistrationSystem4 {

    private static final String EXCP_INVALID_B# = "EXCP_INVALID_B#";
    private static final String EXCP_INVALID_CLASSID = "EXCP_INVALID_CLASSID";
    private static final String EXCP_INVALID_SEM_CLASS = "EXCP_INVALID_SEM_CLASS";
    private static final String EXCP_CLASS_IS_FULL = "EXCP_CLASS_IS_FULL";
    private static final String EXCP_INVALID_STUDENT_ENROLL = "EXCP_INVALID_STUDENT_ENROLL";
    private static final String EXCP_EXCEEDED_ENROLLMENT = "EXCP_EXCEEDED_ENROLLMENT";
    private static final String EXCP_PREREQ_NOT_SATISFIED = "EXCP_PREREQ_NOT_SATISFIED";
    private static final String EXCP_INVALID_DROP_PREREQ = "EXCP_INVALID_DROP_PREREQ";

    private static Connection connection;

    public static void main(String[] args) throws SQLException {
        // Replace with your database credentials
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String username = "your_username";
        String password = "your_password";

        connection = DriverManager.getConnection(url, username, password);

        // Example usage of the procedures
        showStudents();
        showTAs();
        showCourses();
        showClasses();
        showEnrollments();
        showPrerequisites();
        showLogs();

        classTA("CS101");
        classPrereq("CS", 101);
        enrollStudent("B00123456", "CS101");
        deleteStudentEnrollment("B00123456", "CS101");
        deleteStudent("B00123456");

        connection.close();
    }

    // Helper functions

    private static void validateDeptcodeCourse#(String deptCode, int course#) throws SQLException {
        String sql = "SELECT COUNT(*) FROM COURSES WHERE UPPER(DEPT_CODE) = ? AND COURSE# = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, deptCode.toUpperCase());
            stmt.setInt(2, course#);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new Exception("Invalid deptcode and course# combination.");
                }
            }
        }
    }

    private static String getGrade(String b#, String classid) throws SQLException {
        String sql = "SELECT EN.LGRADE FROM ENROLLMENTS EN WHERE UPPER(EN.B#) = ? AND UPPER(EN.CLASSID) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, b#.toUpperCase());
            stmt.setString(2, classid.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("LGRADE");
                }
            }
        }
        return null;
    }

    // Procedures

    public static void showStudents() throws SQLException {
        String sql = "SELECT * FROM STUDENTS";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("B#") + ", " + rs.getString("FIRST_NAME") + ", " + rs.getString("LAST_NAME"));
                }
            }
        }
    }

    public static void showTAs() throws SQLException {
        String sql = "SELECT * FROM TAS";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("B#") + ", " + rs.getString("FIRST_NAME") + ", " + rs.getString("LAST_NAME"));
                }
            }
        }
    }

    public static void showCourses() throws SQLException {
        String sql = "SELECT * FROM COURSES";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("DEPT_CODE") + ", " + rs.getInt("COURSE#") + ", " + rs.getString("TITLE"));
                }
            }
        }
    }

    public static void showClasses() throws SQLException {
        String sql = "SELECT * FROM CLASSES";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("CLASSID") + ", " + rs.getString("DEPT_CODE") + ", " + rs.getInt("COURSE#") + ", " + rs.getString("SEMESTER") + ", " + rs.getInt("YEAR") + ", " + rs.getInt("LIMIT") + ", " + rs.getInt("CLASS_SIZE") + ", " + rs.getString("TA_B#"));
                }
            }
        }
    }

    public static void showEnrollments() throws SQLException {
        String sql = "SELECT * FROM ENROLLMENTS";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("B#") + ", " + rs.getString("CLASSID") + ", " + rs.getString("LGRADE"));
                }
            }
        }
    }

    public static void showPrerequisites() throws SQLException {
        String sql = "SELECT * FROM PREREQUISITES";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("DEPT_CODE") + ", " + rs.getInt("COURSE#") + ", " + rs.getString("PRE_DEPT_CODE") + ", " + rs.getInt("PRE_COURSE#"));
                }
            }
        }
    }

    public static void showLogs() throws SQLException {
        String sql = "SELECT * FROM LOGS";
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString("B#") + ", " + rs.getString("CLASSID") + ", " + rs.getString("ACTION") + ", " + rs.getTimestamp("TIMESTAMP"));
                }
            }
        }
    }

    public static void classTA(String classid) throws SQLException {
        String sql = "SELECT B#, FIRST_NAME, LAST_NAME FROM STUDENTS ST, CLASSES CL WHERE UPPER(CLASSID) = ? AND UPPER(ST.B#) = UPPER(CL.TA_B#)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, classid.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("TA for class " + classid + ": " + rs.getString("B#") + ", " + rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
                } else {
                    System.out.println("The class has no TA.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 20004) {
                System.out.println("The classid is invalid.");
            } else {
                throw e;
            }
        }
    }

    public static void classPrereq(String deptCode, int course#) throws SQLException {
        String sql = "SELECT PRE_DEPT_CODE, PRE_COURSE# FROM PREREQUISITES WHERE UPPER(DEPT_CODE) = ? AND COURSE# = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, deptCode.toUpperCase());
            stmt.setInt(2, course#);
            try (ResultSet rs = stmt.executeQuery()) {
                StringBuilder preReq = new StringBuilder();
                while (rs.next()) {
                    preReq.append(rs.getString("PRE_DEPT_CODE")).append(rs.getInt("PRE_COURSE#")).append(", ");
                }
                if (preReq.length() > 0) {
                    preReq.delete(preReq.length() - 2, preReq.length());
                }
                System.out.println("Prerequisites for " + deptCode + " " + course# + ": " + preReq);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 20008) {
                System.out.println(deptCode + " " + course# + " does not exist.");
            } else {
                throw e;
            }
        }
    }

    public static void enrollStudent(String b#, String classid) throws SQLException {
        String sql = "INSERT INTO ENROLLMENTS (B#, CLASSID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, b#.toUpperCase());
            stmt.setString(2, classid.toUpperCase());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Successfully enrolled student with B# " + b# + " and classid " + classid);
            } else {
                throw new Exception("Enrollment failed.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 20008) {
                System.out.println("The B# is invalid.");
            } else if (e.getErrorCode() == 20009) {
                System.out.println("The classid is invalid.");
            } else if (e.getErrorCode() == 20010) {
                System.out.println("Cannot enroll into a class from a previous semester.");
            } else if (e.getErrorCode() == 20011) {
                System.out.println("The class is already full.");
            } else if (e.getErrorCode() == 20012) {
                System.out.println("The student is already in the class.");
            } else if (e.getErrorCode() == 20013) {
                System.out.println("Students cannot be enrolled in more than five classes in the same semester.");
            } else if (e.getErrorCode() == 20014) {
                System.out.println("Prerequisite not satisfied.");
            } else {
                throw e;
            }
        }
    }

    public static void deleteStudentEnrollment(String b#, String classid) throws SQLException {
        String sql = "DELETE FROM ENROLLMENTS WHERE UPPER(B#) = ? AND UPPER(CLASSID) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, b#.toUpperCase());
            stmt.setString(2, classid.toUpperCase());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Successfully deleted student enrollment with B# " + b# + " and classid " + classid);
            } else {
                throw new Exception("Enrollment deletion failed.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                System.out.println("The B# is invalid.");
            } else if (e.getErrorCode() == 20003) {
                System.out.println("The classid is invalid.");
            } else if (e.getErrorCode() == 20004) {
                System.out.println("The student is not enrolled in the class.");
            } else if (e.getErrorCode() == 20005) {
                System.out.println("Only enrollment in the current semester can be dropped.");
            } else if (e.getErrorCode() == 20006) {
                System.out.println("The drop is not permitted because another class the student registered uses it as a prerequisite.");
            } else {
                throw e;
            }
        }
    }

    public static void deleteStudent(String b#) throws SQLException {
        String sql = "DELETE FROM STUDENTS WHERE UPPER(B#) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, b#.toUpperCase());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Successfully deleted student with B# " + b#);
            } else {
                throw new Exception("Student deletion failed.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                System.out.println("The B# is invalid.");
            } else {
                throw e;
            }
        }
    }
}
