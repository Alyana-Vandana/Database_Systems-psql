
import java.sql.*;
import java.util.*;

public class StudentRegistrationSystem3 {

    // Helper Functions

    // Function to validate B# of a student
    public static boolean validateStudentB#(String bNumber) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM STUDENTS WHERE UPPER(B#) = UPPER(?)")) {
            stmt.setString(1, bNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating student B#: " + e.getMessage());
        }
        return false;
    }

    // Function to validate DEPTCODE of a COURSES
    public static boolean validateDeptcodeCourse#(String deptCode, String courseNumber) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM COURSES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?")) {
            stmt.setString(1, deptCode);
            stmt.setString(2, courseNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating deptcode and course#: " + e.getMessage());
        }
        return false;
    }

    // Function to validate classid of a classes
    public static boolean validateStudentClassid(String classId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?)")) {
            stmt.setString(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating classid: " + e.getMessage());
        }
        return false;
    }

    // Function to validate whether particular class is offered in current semester or not
    // Current sem - FALL2018
    public static boolean validateCurrentSemClass(String classId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND UPPER(SEMESTER) = 'FALL' AND YEAR = 2018")) {
            stmt.setString(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating current semester class: " + e.getMessage());
        }
        return false;
    }

    // Procedure to get course# and dept_code from classes for given classid
    public static void getCourseInfo(String classId, String[] deptCodeOut, String[] courseNumberOut) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GET_COURSE_INFO(?, ?, ?)}")) {
            cstmt.setString(1, classId);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            deptCodeOut[0] = cstmt.getString(2);
            courseNumberOut[0] = cstmt.getString(3);
        } catch (SQLException e) {
            System.err.println("Error getting course info: " + e.getMessage());
        }
    }

    // Function to validate whether particular student is enrolled or not
    public static boolean validateStudentEnrollments(String bNumber, String classId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?) AND UPPER(CLASSID) = UPPER(?)")) {
            stmt.setString(1, bNumber);
            stmt.setString(2, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating student enrollments: " + e.getMessage());
        }
        return false;
    }

    // Function to validate pre-req course condition for given classid
    public static boolean validateStudentPrereq(String bNumber, String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL VALIDATE_STUDENT_PREREQ(?, ?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.registerOutParameter(3, Types.BOOLEAN);
            cstmt.execute();
            return cstmt.getBoolean(3);
        } catch (SQLException e) {
            System.err.println("Error validating student prereq: " + e.getMessage());
        }
        return false;
    }

    // Function to validate last enrolled class for the student
    public static boolean validateLastEnrollment(String bNumber) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL VALIDATE_LAST_ENROLLMENT(?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.registerOutParameter(2, Types.BOOLEAN);
            cstmt.execute();
            return cstmt.getBoolean(2);
        } catch (SQLException e) {
            System.err.println("Error validating last enrollment: " + e.getMessage());
        }
        return false;
    }

    // Function to validate last enrolled student for the class
    public static boolean validateLastStudent(String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL VALIDATE_LAST_STUDENT(?, ?)}")) {
            cstmt.setString(1, classId);
            cstmt.registerOutParameter(2, Types.BOOLEAN);
            cstmt.execute();
            return cstmt.getBoolean(2);
        } catch (SQLException e) {
            System.err.println("Error validating last student: " + e.getMessage());
        }
        return false;
    }

    // Function to validate if class is full
    public static boolean validateClassFull(String classId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND LIMIT = CLASS_SIZE")) {
            stmt.setString(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating class full: " + e.getMessage());
        }
        return false;
    }

    // Function to get the count of student enrollments
    public static int getStudentEnrollCount(String bNumber) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS EN, CLASSES CL WHERE UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(CL.CLASSID) AND UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018")) {
            stmt.setString(1, bNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student enroll count: " + e.getMessage());
        }
        return 0;
    }

    // Function to get classid from courses from prev semester i.e not FALL 2018
    public static String getClassId(String courseInfo) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GET_CLASSID(?, ?)}")) {
            cstmt.setString(1, courseInfo);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.execute();
            return cstmt.getString(2);
        } catch (SQLException e) {
            System.err.println("Error getting classid: " + e.getMessage());
        }
        return null;
    }

    // Function to get grade of student for particular class - from enrollments
    public static String getGrade(String bNumber, String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GET_GRADE(?, ?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            return cstmt.getString(3);
        } catch (SQLException e) {
            System.err.println("Error getting grade: " + e.getMessage());
        }
        return null;
    }

    // Function to validate pre-req course condition for given classid
    public static boolean validateStudentPrereqGrade(String bNumber, String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL VALIDATE_STUDENT_PREREQ_GRADE(?, ?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.registerOutParameter(3, Types.BOOLEAN);
            cstmt.execute();
            return cstmt.getBoolean(3);
        } catch (SQLException e) {
            System.err.println("Error validating student prereq grade: " + e.getMessage());
        }
        return false;
    }

    // Procedures to display the tuples in each of the seven tables for this project.
    // Procedure "show_students"
    public static void showStudents(ResultSet[] refCursorStudents) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_STUDENTS(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorStudents[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing students: " + e.getMessage());
        }
    }

    // Procedure "show_tas"
    public static void showTAs(ResultSet[] refCursorTAs) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_TAS(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorTAs[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing TAs: " + e.getMessage());
        }
    }

    // Procedure "show_courses"
    public static void showCourses(ResultSet[] refCursorCourses) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_COURSES(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorCourses[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing courses: " + e.getMessage());
        }
    }

    // Procedure "show_classes"
    public static void showClasses(ResultSet[] refCursorClasses) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_CLASSES(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorClasses[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing classes: " + e.getMessage());
        }
    }

    // Procedure "show_enrollments"
    public static void showEnrollments(ResultSet[] refCursorEnrollments) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_ENROLLMENTS(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorEnrollments[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing enrollments: " + e.getMessage());
        }
    }

    // Procedure "show_prerequisites"
    public static void showPrerequisites(ResultSet[] refCursorPrerequisites) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_PREREQUISITES(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorPrerequisites[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing prerequisites: " + e.getMessage());
        }
    }

    // Procedure "show_logs"
    public static void showLogs(ResultSet[] refCursorLogs) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL SHOW_LOGS(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            refCursorLogs[0] = (ResultSet) cstmt.getObject(1);
        } catch (SQLException e) {
            System.err.println("Error showing logs: " + e.getMessage());
        }
    }

    // Procedure to list B#, first name and last name of the TA of the class for a given class
    // If the class does not have a TA, report The class has no TA.
    // If the provided classid is invalid (i.e., not in the Classes table), report The classid is invalid.
    public static void classTA(String classId, String[] taBNumberOut, String[] firstNameOut, String[] lastNameOut) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL CLASS_TA(?, ?, ?, ?)}")) {
            cstmt.setString(1, classId);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.execute();
            taBNumberOut[0] = cstmt.getString(2);
            firstNameOut[0] = cstmt.getString(3);
            lastNameOut[0] = cstmt.getString(4);
        } catch (SQLException e) {
            System.err.println("Error getting class TA: " + e.getMessage());
        }
    }

    // Recursive helper procedure to get direct and indirect prerequisites
    // If course C1 has course C2 as a prerequisite, C2 is a direct prerequisite.
    // If C2 has course C3 has a prerequisite, then C3 is an indirect prerequisite for C1.
    public static void getPrereqCourse(String deptCode, String courseNumber, String preReqOut) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL GET_PREREQ_COURSE(?, ?, ?)}")) {
            cstmt.setString(1, deptCode);
            cstmt.setString(2, courseNumber);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            preReqOut = cstmt.getString(3);
        } catch (SQLException e) {
            System.err.println("Error getting prereq course: " + e.getMessage());
        }
    }

    // Procedure to list all prerequisite courses for given course (with dept_code and course#)
    // Including both direct and indirect prerequisite courses
    public static void classPrereq(String deptCode, String courseNumber, String preReqOut) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL CLASS_PREREQ(?, ?, ?)}")) {
            cstmt.setString(1, deptCode);
            cstmt.setString(2, courseNumber);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.execute();
            preReqOut = cstmt.getString(3);
        } catch (SQLException e) {
            System.err.println("Error getting class prereq: " + e.getMessage());
        }
    }

    // Procedure to Enroll Student for given class (insert a tuple into Enrollments table)
    // If the student is not in the Students table, report The B# is invalid.
    // If the classid is not in the classes table, report The classid is invalid.
    // If the class is not offered in the current semester (i.e., Fall 2018), reject the enrollment:
    // Cannot enroll into a class from a previous semester.
    // If the class is already full before the enrollment request, reject the enrollment request:
    // The class is already full.
    // If the student is already in the class, report The student is already in the class.
    // If the student is already enrolled in four other classes in the same semester and the same year, report:
    // The student will be overloaded with the new enrollment. but still allow the student to be enrolled.
    // If the student is already enrolled in five other classes in the same semester and the same year, report:
    // Students cannot be enrolled in more than five classes in the same semester. and reject the enrollment.
    // If the student has not completed the required prerequisite courses with at least a grade C, reject the enrollment:
    // Prerequisite not satisfied.
    // For all the other cases, the requested enrollment should be carried out successfully.
    public static void enrollStudent(String bNumber, String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL ENROLL_STUDENT(?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.execute();
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
        }
    }

    // Procedure to drop a student from a class (delete a tuple from Enrollments table)
    // If the student is not in the Students table, report The B# is invalid.
    // If the classid is not in the Classes table, report The classid is invalid.
    // If the student is not enrolled in the class, report The student is not enrolled in the class.
    // If the class is not offered in Fall 2018, reject the drop attempt and report:
    // Only enrollment in the current semester can be dropped.
    // If dropping the student from class would cause a violation of the prerequisite requirement, reject the drop attempt:
    // The drop is not permitted because another class the student registered uses it as a prerequisite.
    // In all the other cases, the student will be dropped from the class.
    // If the class is the last class for the student, delete and report This student is not enrolled in any classes.
    // If the student is the last student in the class, delete and report The class now has no students.
    public static void deleteStudentEnrollment(String bNumber, String classId) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL DELETE_STUDENT_ENROLLMENT(?, ?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.setString(2, classId);
            cstmt.execute();
        } catch (SQLException e) {
            System.err.println("Error deleting student enrollment: " + e.getMessage());
        }
    }

    // Procedure to delete Student (delete a tuple from Students table)
    // If the student is not in the Students table, report The B# is invalid.
    public static void deleteStudent(String bNumber) {
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall("{CALL DELETE_STUDENT(?)}")) {
            cstmt.setString(1, bNumber);
            cstmt.execute();
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        }
    }

    // Helper function to get a database connection
    private static Connection getConnection() throws SQLException {
        // Replace with your database credentials
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
    }
}
