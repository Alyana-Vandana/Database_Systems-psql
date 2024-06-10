
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRegistrationSystem {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        // Code to interact with the database using JDBC
    }

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

    // Common Exceptions
    public static class ExcpInvalidB extends Exception {}
    public static class ExcpInvalidClassid extends Exception {}
    public static class ExcpInvalidStudentEnroll extends Exception {}
    public static class ExcpInvalidSemClass extends Exception {}
    public static class ExcpInvalidDropPrereq extends Exception {}
    public static class ExcpInvalidDropLastClass extends Exception {}
    public static class ExcpInvalidDropLastStudent extends Exception {}
    public static class ExcpClassIsFull extends Exception {}
    public static class ExcpExceededEnrollment extends Exception {}
    public static class ExcpPrereqNotSatisfied extends Exception {}
    public static class ExcpInvalidDeptcodeCourse extends Exception {}

    // Procedures to display data from tables
    public static List<Student> showStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_STUDENTS(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                students.add(new Student(
                        refCursor.getResultSet().getString("B#"),
                        refCursor.getResultSet().getString("FIRST_NAME"),
                        refCursor.getResultSet().getString("LAST_NAME"),
                        refCursor.getResultSet().getString("DEPT_CODE"),
                        refCursor.getResultSet().getInt("YEAR")
                ));
            }
        }
        return students;
    }

    public static List<Ta> showTas() throws SQLException {
        List<Ta> tas = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_TAS(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                tas.add(new Ta(
                        refCursor.getResultSet().getString("B#"),
                        refCursor.getResultSet().getString("FIRST_NAME"),
                        refCursor.getResultSet().getString("LAST_NAME"),
                        refCursor.getResultSet().getString("DEPT_CODE")
                ));
            }
        }
        return tas;
    }

    public static List<Course> showCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_COURSES(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                courses.add(new Course(
                        refCursor.getResultSet().getString("DEPT_CODE"),
                        refCursor.getResultSet().getInt("COURSE#"),
                        refCursor.getResultSet().getString("TITLE"),
                        refCursor.getResultSet().getInt("CREDITS")
                ));
            }
        }
        return courses;
    }

    public static List<Class> showClasses() throws SQLException {
        List<Class> classes = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_CLASSES(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                classes.add(new Class(
                        refCursor.getResultSet().getString("CLASSID"),
                        refCursor.getResultSet().getString("DEPT_CODE"),
                        refCursor.getResultSet().getInt("COURSE#"),
                        refCursor.getResultSet().getString("SEMESTER"),
                        refCursor.getResultSet().getInt("YEAR"),
                        refCursor.getResultSet().getString("B#"),
                        refCursor.getResultSet().getInt("MAX_ENROLL"),
                        refCursor.getResultSet().getInt("CURRENT_ENROLL")
                ));
            }
        }
        return classes;
    }

    public static List<Enrollment> showEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_ENROLLMENTS(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                enrollments.add(new Enrollment(
                        refCursor.getResultSet().getString("B#"),
                        refCursor.getResultSet().getString("CLASSID"),
                        refCursor.getResultSet().getInt("GRADE")
                ));
            }
        }
        return enrollments;
    }

    public static List<Prerequisite> showPrerequisites() throws SQLException {
        List<Prerequisite> prerequisites = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_PREREQUISITES(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                prerequisites.add(new Prerequisite(
                        refCursor.getResultSet().getString("DEPT_CODE"),
                        refCursor.getResultSet().getInt("COURSE#"),
                        refCursor.getResultSet().getString("PRE_REQ_DEPT_CODE"),
                        refCursor.getResultSet().getInt("PRE_REQ_COURSE#")
                ));
            }
        }
        return prerequisites;
    }

    public static List<Log> showLogs() throws SQLException {
        List<Log> logs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.SHOW_LOGS(?)}");
             RefCursor refCursor = new RefCursor(callableStatement.executeQuery())) {
            while (refCursor.getResultSet().next()) {
                logs.add(new Log(
                        refCursor.getResultSet().getString("B#"),
                        refCursor.getResultSet().getString("CLASSID"),
                        refCursor.getResultSet().getTimestamp("TIMESTAMP"),
                        refCursor.getResultSet().getString("ACTION")
                ));
            }
        }
        return logs;
    }

    // Procedure to get TA information for a class
    public static Ta classTa(String classId) throws SQLException, ExcpInvalidClassid {
        Ta ta = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.CLASS_TA(?, ?, ?, ?)}")) {
            callableStatement.setString(1, classId);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.execute();
            String taB = callableStatement.getString(2);
            String firstName = callableStatement.getString(3);
            String lastName = callableStatement.getString(4);
            if (taB != null) {
                ta = new Ta(taB, firstName, lastName, null);
            }
        }
        if (ta == null) {
            throw new ExcpInvalidClassid("Invalid class ID: " + classId);
        }
        return ta;
    }

    // Procedure to get prerequisite courses for a given course
    public static String classPrereq(String deptCode, int courseNum) throws SQLException {
        String prereqString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.CLASS_PREREQ(?, ?, ?)}")) {
            callableStatement.setString(1, deptCode);
            callableStatement.setInt(2, courseNum);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.execute();
            prereqString = callableStatement.getString(3);
        }
        return prereqString;
    }

    // Procedure to enroll a student in a class
    public static void enrollStudent(String bNum, String classId) throws SQLException, ExcpInvalidB, ExcpInvalidClassid, ExcpClassIsFull, ExcpExceededEnrollment, ExcpPrereqNotSatisfied {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.ENROLL_STUDENT(?, ?)}")) {
            callableStatement.setString(1, bNum);
            callableStatement.setString(2, classId);
            callableStatement.execute();
        }
    }

    // Procedure to drop a student from a class
    public static void deleteStudentEnrollment(String bNum, String classId) throws SQLException, ExcpInvalidB, ExcpInvalidClassid, ExcpInvalidDropLastClass, ExcpInvalidDropLastStudent {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT_ENROLLMENT(?, ?)}")) {
            callableStatement.setString(1, bNum);
            callableStatement.setString(2, classId);
            callableStatement.execute();
        }
    }

    // Procedure to delete a student
    public static void deleteStudent(String bNum) throws SQLException, ExcpInvalidB, ExcpInvalidDropLastStudent {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall("{call STUDENT_REGISTRATION_SYSTEM.DELETE_STUDENT(?)}")) {
            callableStatement.setString(1, bNum);
            callableStatement.execute();
        }
    }

    // Data classes for tables
    public static class Student {
        private String bNum;
        private String firstName;
        private String lastName;
        private String deptCode;
        private int year;

        public Student(String bNum, String firstName, String lastName, String deptCode, int year) {
            this.bNum = bNum;
            this.firstName = firstName;
            this.lastName = lastName;
            this.deptCode = deptCode;
            this.year = year;
        }

        // Getters and setters
    }

    public static class Ta {
        private String bNum;
        private String firstName;
        private String lastName;
        private String deptCode;

        public Ta(String bNum, String firstName, String lastName, String deptCode) {
            this.bNum = bNum;
            this.firstName = firstName;
            this.lastName = lastName;
            this.deptCode = deptCode;
        }

        // Getters and setters
    }

    public static class Course {
        private String deptCode;
        private int courseNum;
        private String title;
        private int credits;

        public Course(String deptCode, int courseNum, String title, int credits) {
            this.deptCode = deptCode;
            this.courseNum = courseNum;
            this.title = title;
            this.credits = credits;
        }

        // Getters and setters
    }

    public static class Class {
        private String classId;
        private String deptCode;
        private int courseNum;
        private String semester;
        private int year;
        private String taBNum;
        private int maxEnroll;
        private int currentEnroll;

        public Class(String classId, String deptCode, int courseNum, String semester, int year, String taBNum, int maxEnroll, int currentEnroll) {
            this.classId = classId;
            this.deptCode = deptCode;
            this.courseNum = courseNum;
            this.semester = semester;
            this.year = year;
            this.taBNum = taBNum;
            this.maxEnroll = maxEnroll;
            this.currentEnroll = currentEnroll;
        }

        // Getters and setters
    }

    public static class Enrollment {
        private String bNum;
        private String classId;
        private Integer grade;

        public Enrollment(String bNum, String classId, Integer grade) {
            this.bNum = bNum;
            this.classId = classId;
            this.grade = grade;
        }

        // Getters and setters
    }

    public static class Prerequisite {
        private String deptCode;
        private int courseNum;
        private String preReqDeptCode;
        private int preReqCourseNum;

        public Prerequisite(String deptCode, int courseNum, String preReqDeptCode, int preReqCourseNum) {
            this.deptCode = deptCode;
            this.courseNum = courseNum;
            this.preReqDeptCode = preReqDeptCode;
            this.preReqCourseNum = preReqCourseNum;
        }

        // Getters and setters
    }

    public static class Log {
        private String bNum;
        private String classId;
        private Timestamp timestamp;
        private String action;

        public Log(String bNum, String classId, Timestamp timestamp, String action) {
            this.bNum = bNum;
            this.classId = classId;
            this.timestamp = timestamp;
            this.action = action;
        }

        // Getters and setters
    }
}
