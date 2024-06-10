
import java.sql.*;
import java.util.*;

public class StudentRegistrationSystem2 {

    // Database connection variables
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    // Exception classes
    public static class ExcpInvalidB# extends Exception {}
    public static class ExcpInvalidClassid extends Exception {}
    public static class ExcpInvalidSemClass extends Exception {}
    public static class ExcpClassIsFull extends Exception {}
    public static class ExcpInvalidStudentEnroll extends Exception {}
    public static class ExcpExceededEnrollment extends Exception {}
    public static class ExcpPrereqNotSatisfied extends Exception {}
    public static class ExcpInvalidDropPrereq extends Exception {}
    public static class ExcpInvalidDropLastStudent extends Exception {}

    // Helper functions
    private static boolean validateStudentB#(String b#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM STUDENTS WHERE UPPER(B#) = UPPER(?)")) {
            stmt.setString(1, b#);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean validateDeptcodeCourse#(String deptCode, String course#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM COURSES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?")) {
            stmt.setString(1, deptCode);
            stmt.setString(2, course#);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean validateStudentClassid(String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?)")) {
            stmt.setString(1, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean validateCurrentSemClass(String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND UPPER(SEMESTER) = 'FALL' AND YEAR = 2018")) {
            stmt.setString(1, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static void getCourseInfo(String classid, String[] deptCode, String[] course#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT DEPT_CODE, COURSE# FROM CLASSES WHERE CLASSID = ?")) {
            stmt.setString(1, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    deptCode[0] = rs.getString("DEPT_CODE");
                    course#[0] = rs.getString("COURSE#");
                }
            }
        }
    }

    private static boolean validateStudentEnrollments(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?) AND UPPER(CLASSID) = ?")) {
            stmt.setString(1, b#);
            stmt.setString(2, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean validateStudentPrereq(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT CLASSID FROM ENROLLMENTS E, CLASSES C WHERE UPPER(E.B#) = UPPER(?) AND UPPER(E.CLASSID) != UPPER(?) AND E.CLASSID = C.CLASSID AND UPPER(C.SEMESTER) = 'FALL' AND C.YEAR = 2018")) {
            stmt.setString(1, b#);
            stmt.setString(2, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] deptCode = new String[1];
                    String[] course# = new String[1];
                    getCourseInfo(rs.getString("CLASSID"), deptCode, course#);
                    String prereq = getPrereq(deptCode[0], course#[0]);
                    if (prereq.contains(deptCode[0] + course#[0])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean validateLastEnrollment(String b#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?)")) {
            stmt.setString(1, b#);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private static boolean validateLastStudent(String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(CLASSID) = UPPER(?)")) {
            stmt.setString(1, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private static boolean validateClassFull(String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND LIMIT = CLASS_SIZE")) {
            stmt.setString(1, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static int getStudentEnrollCount(String b#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS EN, CLASSES CL WHERE UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(CL.CLASSID) AND UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018")) {
            stmt.setString(1, b#);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private static String getClassid(String courseInfo) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT CLASSID FROM CLASSES CL WHERE UPPER(CL.DEPT_CODE) || CL.COURSE# = UPPER(?) AND NOT (UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018)")) {
            stmt.setString(1, courseInfo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("CLASSID") : null;
            }
        }
    }

    private static String getGrade(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT LGRADE FROM ENROLLMENTS EN WHERE UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(?)")) {
            stmt.setString(1, b#);
            stmt.setString(2, classid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("LGRADE") : null;
            }
        }
    }

    private static boolean validateStudentPrereqGrade(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT REGEXP_SUBSTR(C_PREREQ, '[^,]+', 1, LEVEL) AS DATA FROM DUAL CONNECT BY REGEXP_SUBSTR(C_PREREQ, '[^,]+', 1, LEVEL) IS NOT NULL")) {
            stmt.setString(1, getPrereq(b#, classid));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String currPrereq = rs.getString("DATA");
                    String currClassid = getClassid(currPrereq);
                    if (currClassid == null) {
                        return false;
                    }
                    String grade = getGrade(b#, currClassid);
                    if (grade == null || (grade.substring(0, 1).compareTo("C") > 0 && !grade.equals("C-"))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Recursive helper function to get prerequisites
    private static String getPrereq(String deptCode, String course#) throws SQLException {
        StringBuilder prereq = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT PRE_DEPT_CODE, PRE_COURSE# FROM PREREQUISITES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?")) {
            stmt.setString(1, deptCode);
            stmt.setString(2, course#);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tempDeptCode = rs.getString("PRE_DEPT_CODE");
                    String tempCourse# = rs.getString("PRE_COURSE#");
                    prereq.append(tempDeptCode).append(tempCourse#).append(",");
                    prereq.append(getPrereq(tempDeptCode, tempCourse#));
                }
            }
        }
        return prereq.toString();
    }

    // Main procedures
    public static void showStudents(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_STUDENTS(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("B#: " + rs.getString("B#") + ", First Name: " + rs.getString("FIRST_NAME") + ", Last Name: " + rs.getString("LAST_NAME"));
            }
        }
    }

    public static void showTAs(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_TAS(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("B#: " + rs.getString("B#") + ", First Name: " + rs.getString("FIRST_NAME") + ", Last Name: " + rs.getString("LAST_NAME"));
            }
        }
    }

    public static void showCourses(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_COURSES(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("Dept Code: " + rs.getString("DEPT_CODE") + ", Course#: " + rs.getString("COURSE#") + ", Title: " + rs.getString("TITLE"));
            }
        }
    }

    public static void showClasses(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_CLASSES(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("Classid: " + rs.getString("CLASSID") + ", Dept Code: " + rs.getString("DEPT_CODE") + ", Course#: " + rs.getString("COURSE#") + ", Semester: " + rs.getString("SEMESTER") + ", Year: " + rs.getInt("YEAR") + ", Limit: " + rs.getInt("LIMIT") + ", Class Size: " + rs.getInt("CLASS_SIZE") + ", TA B#: " + rs.getString("TA_B#"));
            }
        }
    }

    public static void showEnrollments(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_ENROLLMENTS(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("B#: " + rs.getString("B#") + ", Classid: " + rs.getString("CLASSID") + ", Grade: " + rs.getString("LGRADE"));
            }
        }
    }

    public static void showPrerequisites(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_PREREQUISITES(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("Dept Code: " + rs.getString("DEPT_CODE") + ", Course#: " + rs.getString("COURSE#") + ", Pre Dept Code: " + rs.getString("PRE_DEPT_CODE") + ", Pre Course#: " + rs.getString("PRE_COURSE#"));
            }
        }
    }

    public static void showLogs(CallableStatement cstmt) throws SQLException {
        cstmt.execute("BEGIN STUDENT_REGISTRATION_SYSTEM.SHOW_LOGS(?); END;");
        try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
            while (rs.next()) {
                System.out.println("Timestamp: " + rs.getTimestamp("TIMESTAMP") + ", User: " + rs.getString("USER") + ", Action: " + rs.getString("ACTION") + ", Details: " + rs.getString("DETAILS"));
            }
        }
    }

    public static void classTA(String classid, String[] taB#, String[] firstName, String[] lastName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{CALL STUDENT_REGISTRATION_SYSTEM.CLASS_TA(?, ?, ?, ?)}")) {
            cstmt.setString(1, classid);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.execute();
            taB#[0] = cstmt.getString(2);
            firstName[0] = cstmt.getString(3);
            lastName[0] = cstmt.getString(4);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20008) {
                throw new ExcpInvalidB#();
            } else if (e.getErrorCode() == 20009) {
                throw new ExcpInvalidClassid();
            } else {
                throw e;
            }
        }
    }

    public static void classPrereq(String deptCode, String course#, String[] preReq) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{CALL STUDENT_REGISTRATION_SYSTEM.CLASS_PREREQ(?, ?, ?)}")) {
            cstmt.setString(1, deptCode);
            cstmt.setString(2, course#);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            preReq[0] = cstmt.getString(3);
        } catch (SQLException e) {
            if (e.getErrorCode() == 20010) {
                throw new ExcpInvalidDeptcodeCourse#();
            } else {
                throw e;
            }
        }
    }

    public static void enrollStudent(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{CALL STUDENT_REGISTRATION_SYSTEM.ENROLL_STUDENT(?, ?)}")) {
            cstmt.setString(1, b#);
            cstmt.setString(2, classid);
            cstmt.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20008) {
                throw new ExcpInvalidB#();
            } else if (e.getErrorCode() == 20009) {
                throw new ExcpInvalidClassid();
            } else if (e.getErrorCode() == 20010) {
                throw new ExcpInvalidSemClass();
            } else if (e.getErrorCode() == 20011) {
                throw new ExcpClassIsFull();
            } else if (e.getErrorCode() == 20012) {
                throw new ExcpInvalidStudentEnroll();
            } else if (e.getErrorCode() == 20013) {
                throw new ExcpExceededEnrollment();
            } else if (e.getErrorCode() == 20014) {
                throw new ExcpPrereqNotSatisfied();
            } else {
                throw e;
            }
        }
    }

    public static void deleteStudentEnrollment(String b#, String classid) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{CALL STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT_ENROLLMENT(?, ?)}")) {
            cstmt.setString(1, b#);
            cstmt.setString(2, classid);
            cstmt.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20002) {
                throw new ExcpInvalidB#();
            } else if (e.getErrorCode() == 20003) {
                throw new ExcpInvalidClassid();
            } else if (e.getErrorCode() == 20004) {
                throw new ExcpInvalidStudentEnroll();
            } else if (e.getErrorCode() == 20005) {
                throw new ExcpInvalidSemClass();
            } else if (e.getErrorCode() == 20006) {
                throw new ExcpInvalidDropPrereq();
            } else {
                throw e;
            }
        }
    }

    public static void deleteStudent(String b#) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{CALL STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT(?)}")) {
            cstmt.setString(1, b#);
            cstmt.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 20001) {
                throw new ExcpInvalidB#();
            } else {
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement cstmt = conn.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_STUDENTS(?)}")) {
            showStudents(cstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
