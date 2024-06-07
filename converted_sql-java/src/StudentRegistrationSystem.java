import java.sql.*;

public class StudentRegistrationSystem {
    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

            // Create tables
            createTables(connection);

            // Insert data into tables
            insertData(connection);

            // Commit the changes
            connection.commit();

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Create students table
        statement.executeUpdate("CREATE TABLE students (" +
                "B# CHAR(4) PRIMARY KEY CHECK (B# LIKE 'B%'), " +
                "first_name VARCHAR2(15) NOT NULL, " +
                "last_name VARCHAR2(15) NOT NULL, " +
                "status VARCHAR2(10) CHECK (status IN ('freshman', 'sophomore', 'junior', 'senior', 'MS', 'PhD')), " +
                "gpa NUMBER(3,2) CHECK (gpa BETWEEN 0 AND 4.0), " +
                "email VARCHAR2(20) UNIQUE, " +
                "bdate DATE, " +
                "deptname VARCHAR2(4) NOT NULL)");

        // Create tas table
        statement.executeUpdate("CREATE TABLE tas (" +
                "B# CHAR(4) PRIMARY KEY REFERENCES students, " +
                "ta_level VARCHAR2(3) NOT NULL CHECK (ta_level IN ('MS', 'PhD')), " +
                "office VARCHAR2(10))");

        // Create courses table
        statement.executeUpdate("CREATE TABLE courses (" +
                "dept_code VARCHAR2(4), " +
                "course# NUMBER(3) CHECK (course# BETWEEN 100 AND 799), " +
                "title VARCHAR2(20) NOT NULL, " +
                "PRIMARY KEY (dept_code, course#))");

        // Create classes table
        statement.executeUpdate("CREATE TABLE classes (" +
                "classid CHAR(5) PRIMARY KEY CHECK (classid LIKE 'c%'), " +
                "dept_code VARCHAR2(4) NOT NULL, " +
                "course# NUMBER(3) NOT NULL, " +
                "sect# NUMBER(2), " +
                "year NUMBER(4), " +
                "semester VARCHAR2(8) CHECK (semester IN ('Spring', 'Fall', 'Summer 1', 'Summer 2')), " +
                "limit NUMBER(3), " +
                "class_size NUMBER(3), " +
                "room VARCHAR2(10), " +
                "ta_B# CHAR(4) REFERENCES tas, " +
                "FOREIGN KEY (dept_code, course#) REFERENCES courses ON DELETE CASCADE, " +
                "UNIQUE (dept_code, course#, sect#, year, semester), " +
                "CHECK (class_size <= limit))");

        // Create enrollments table
        statement.executeUpdate("CREATE TABLE enrollments (" +
                "B# CHAR(4) REFERENCES students, " +
                "classid CHAR(5) REFERENCES classes, " +
                "lgrade VARCHAR2(2) CHECK (lgrade IN ('A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D', 'F', 'I')), " +
                "PRIMARY KEY (B#, classid))");

        // Create prerequisites table
        statement.executeUpdate("CREATE TABLE prerequisites (" +
                "dept_code VARCHAR2(4) NOT NULL, " +
                "course# NUMBER(3) NOT NULL, " +
                "pre_dept_code VARCHAR2(4) NOT NULL, " +
                "pre_course# NUMBER(3) NOT NULL, " +
                "PRIMARY KEY (dept_code, course#, pre_dept_code, pre_course#), " +
                "FOREIGN KEY (dept_code, course#) REFERENCES courses ON DELETE CASCADE, " +
                "FOREIGN KEY (pre_dept_code, pre_course#) REFERENCES courses ON DELETE CASCADE)");

        // Create logs table
        statement.executeUpdate("CREATE TABLE logs (" +
                "log_id NUMBER(5) PRIMARY KEY, " +
                "log_message VARCHAR2(100), " +
                "log_date DATE)");

        statement.close();
    }

    public static void insertData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Insert sample data into students table
        statement.executeUpdate("INSERT INTO students (B#, first_name, last_name, status, gpa, email, bdate, deptname) " +
                "VALUES ('B001', 'John', 'Doe', 'freshman', 3.5, 'john.doe@example.com', TO_DATE('2001-01-01', 'YYYY-MM-DD'), 'CS')");

        // Insert sample data into tas table
        statement.executeUpdate("INSERT INTO tas (B#, ta_level, office) " +
                "VALUES ('B001', 'MS', 'Room 101')");

        // Insert sample data into courses table
        statement.executeUpdate("INSERT INTO courses (dept_code, course#, title) " +
                "VALUES ('CS', 101, 'Introduction to Computer Science')");

        // Insert sample data into classes table
        statement.executeUpdate("INSERT INTO classes (classid, dept_code, course#, sect#, year, semester, limit, class_size, room, ta_B#) " +
                "VALUES ('c001', 'CS', 101, 1, 2024, 'Spring', 30, 25, 'Room 202', 'B001')");

        // Insert sample data into enrollments table
        statement.executeUpdate("INSERT INTO enrollments (B#, classid, lgrade) " +
                "VALUES ('B001', 'c001', 'A')");

        // Insert sample data into prerequisites table
        statement.executeUpdate("INSERT INTO prerequisites (dept_code, course#, pre_dept_code, pre_course#) " +
                "VALUES ('CS', 101, 'CS', 100)");

        // Insert sample data into logs table
        statement.executeUpdate("INSERT INTO logs (log_id, log_message, log_date) " +
                "VALUES (1, 'Student B001 enrolled in class c001', TO_DATE('2024-01-10', 'YYYY-MM-DD'))");

        statement.close();
    }
}
