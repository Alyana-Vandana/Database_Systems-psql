
import java.sql.*;
import java.util.*;

public class StudentRegistrationSystem5 {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create a statement object
            Statement stmt = conn.createStatement();

            // Drop tables and triggers
            dropTablesAndTriggers(stmt);

            // Create tables
            createTables(stmt);

            // Insert data into tables
            insertData(stmt);

            // Start triggers and packages
            startTriggersAndPackages(stmt);

            // Close statement and connection
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to drop tables and triggers
    private static void dropTablesAndTriggers(Statement stmt) throws SQLException {
        stmt.executeUpdate("DROP TABLE enrollments");
        stmt.executeUpdate("DROP TABLE classes");
        stmt.executeUpdate("DROP TABLE tas");
        stmt.executeUpdate("DROP TABLE prerequisites");
        stmt.executeUpdate("DROP TABLE courses");
        stmt.executeUpdate("DROP TABLE students");
        stmt.executeUpdate("DROP TABLE logs");

        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_ENROLL_INS_LOGS");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_ENROLL_UPD_CLASSES");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_INS_ENROLL_INS_LOGS");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_INS_ENROLL_UPD_CLASSES");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_STUDENTS_INS_LOGS");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_STUDENTS_DEL_TA");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_STUD_DEL_ENROLL");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_TAS_INS_LOGS");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_DEL_TAS_UPD_CLASSES");
        stmt.executeUpdate("DROP TRIGGER TRIG_ON_UPD_CLASSES_INS_LOGS");

        stmt.executeUpdate("DROP SEQUENCE LOG_SEQ_GENERATOR");

        stmt.executeUpdate("DROP PACKAGE STUDENT_REGISTRATION_SYSTEM");
    }

    // Method to create tables
    private static void createTables(Statement stmt) throws SQLException {
        // Create students table
        stmt.executeUpdate("CREATE TABLE students (" +
                "B_ char(4) PRIMARY KEY CHECK (B_ LIKE 'B%')," +
                "first_name VARCHAR2(15) NOT NULL," +
                "last_name VARCHAR2(15) NOT NULL," +
                "status VARCHAR2(10) CHECK (status IN ('freshman', 'sophomore', 'junior', 'senior', 'MS', 'PhD'))," +
                "gpa NUMBER(3,2) CHECK (gpa BETWEEN 0 AND 4.0)," +
                "email VARCHAR2(20) UNIQUE," +
                "bdate DATE," +
                "deptname VARCHAR2(4) NOT NULL," +
                "CONSTRAINT FK_deptname FOREIGN KEY (deptname) REFERENCES departments(dept_name)" +
                ")");

        // Create tas table
        stmt.executeUpdate("CREATE TABLE tas (" +
                "B_ char(4) PRIMARY KEY REFERENCES students," +
                "ta_level VARCHAR2(3) NOT NULL CHECK (ta_level IN ('MS', 'PhD'))," +
                "office VARCHAR2(10)" +
                ")");

        // Create courses table
        stmt.executeUpdate("CREATE TABLE courses (" +
                "dept_code VARCHAR2(4) NOT NULL," +
                "course# NUMBER(3) CHECK (course# BETWEEN 100 AND 799)," +
                "title VARCHAR2(20) NOT NULL," +
                "PRIMARY KEY (dept_code, course#)" +
                ")");

        // Create classes table
        stmt.executeUpdate("CREATE TABLE classes (" +
                "classid char(5) PRIMARY KEY CHECK (classid LIKE 'c%')," +
                "dept_code VARCHAR2(4) NOT NULL," +
                "course# NUMBER(3) NOT NULL," +
                "sect# NUMBER(2)," +
                "year NUMBER(4)," +
                "semester VARCHAR2(8) CHECK (semester IN ('Spring', 'Fall', 'Summer 1', 'Summer 2'))," +
                "limit NUMBER(3)," +
                "class_size NUMBER(3)," +
                "room VARCHAR2(10)," +
                "ta_B_ char(4) REFERENCES tas," +
                "FOREIGN KEY (dept_code, course#) REFERENCES courses ON DELETE CASCADE," +
                "UNIQUE(dept_code, course#, sect#, year, semester)," +
                "CHECK (class_size <= limit)" +
                ")");

        // Create enrollments table
        stmt.executeUpdate("CREATE TABLE enrollments (" +
                "B_ char(4) REFERENCES students," +
                "classid char(5) REFERENCES classes," +
                "lgrade VARCHAR2(2) CHECK (lgrade IN ('A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-','D', 'F', 'I'))," +
                "PRIMARY KEY (B_, classid)" +
                ")");

        // Create prerequisites table
        stmt.executeUpdate("CREATE TABLE prerequisites (" +
                "dept_code VARCHAR2(4) NOT NULL," +
                "course# NUMBER(3) NOT NULL," +
                "pre_dept_code VARCHAR2(4) NOT NULL," +
                "pre_course# NUMBER(3) NOT NULL," +
                "PRIMARY KEY (dept_code, course#, pre_dept_code, pre_course#)," +
                "FOREIGN KEY (dept_code, course#) REFERENCES courses ON DELETE CASCADE," +
                "FOREIGN KEY (pre_dept_code, pre_course#) REFERENCES courses ON DELETE CASCADE" +
                ")");

        // Create logs table
        stmt.executeUpdate("CREATE TABLE logs (" +
                "log# NUMBER(4) PRIMARY KEY," +
                "op_name VARCHAR2(10) NOT NULL," +
                "op_time DATE NOT NULL," +
                "table_name VARCHAR2(12) NOT NULL," +
                "operation VARCHAR2(6) NOT NULL," +
                "key_value VARCHAR2(10)" +
                ")");
    }

    // Method to insert data into tables
    private static void insertData(Statement stmt) throws SQLException {
        // Insert data into students table
        stmt.executeUpdate("INSERT INTO students VALUES ('B001', 'Anne', 'Broder', 'junior', 3.17, 'broder@bu.edu', '17-JAN-90', 'CS')");
        // ... (insert remaining data)

        // Insert data into other tables
        // ...
    }

    // Method to start triggers and packages
    private static void startTriggersAndPackages(Statement stmt) throws SQLException {
        stmt.executeUpdate("START Log_Sequence");
        stmt.executeUpdate("START TRIG_ON_DEL_ENROLL_INS_LOGS");
        // ... (start remaining triggers and packages)
    }
}
