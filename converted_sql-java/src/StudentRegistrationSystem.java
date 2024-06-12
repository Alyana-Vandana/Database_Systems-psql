import java.sql.*;

public class
StudentRegistrationSystem {

 // Helper Functions

 /*
 * Function to validate B# of a student
 */
 public static
boolean validateStudentB(String bNumber) {
 int studentCount = 0;
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT COUNT(1) FROM STUDENTS WHERE UPPER(B#) = UPPER(?)");
 stmt.setString(1,
bNumber);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 studentCount = rs.getInt(1);
 }
 conn.close();

} catch (SQLException e) {
 e.printStackTrace();
 }
 return studentCount > 0;
 }

 /*
 * Function to validate
DEPTCODE of a COURSES
 */
 public static boolean validateDeptCodeCourse(String deptCode, String courseNumber) {
 int
validCount = 0;
 try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","username", "password");
 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM COURSES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?");
 stmt.setString(1, deptCode);
 stmt.setString(2, courseNumber);

ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 validCount = rs.getInt(1);
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 return validCount > 0;
 }

 /*
 * Function to validate classid of a
classes
 */
 public static boolean validateStudentClassId(String classId) {
 int classCount = 0;
 try {
 Connection
conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?)");

stmt.setString(1, classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 classCount = rs.getInt(1);
 }

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return classCount > 0;
 }

 /*
 * Function
to validate whether particular class is offered in current semester or not
 * Current sem - FALL2018
 */
 public
static boolean validateCurrentSemClass(String classId) {
 int classCount = 0;
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND UPPER(SEMESTER) = 'FALL'AND YEAR = 2018");
 stmt.setString(1, classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 classCount
= rs.getInt(1);
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return classCount > 0;

}

 /*
 * Procedure to get course# and dept_code from classes for given classid
 */
 public static void
getCourseInfo(String classId, String[] deptCodeOut, String[] courseNumberOut) {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT DEPT_CODE, COURSE# FROM CLASSES WHERE CLASSID = ?");
 stmt.setString(1,
classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 deptCodeOut[0] = rs.getString("DEPT_CODE");

courseNumberOut[0] = rs.getString("COURSE#");
 }
 conn.close();
 } catch (SQLException e) {

e.printStackTrace();
 }
 }

 /*
 * Function to validate whether particular student is enrolled or not
 */
 public
static boolean validateStudentEnrollments(String bNumber, String classId) {
 int enrollmentsCount = 0;
 String
lDeptCodeOut = "";
 String lCourseNumberOut = "";
 String tDeptCodeOut = "";
 String tCourseNumberOut = "";

try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username",
"password");
 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS E WHERE UPPER(E.B#) = UPPER(?)");
 stmt.setString(1, bNumber);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 enrollmentsCount =
rs.getInt(1);
 }
 if (enrollmentsCount > 0) {
 PreparedStatement stmt2 = conn.prepareStatement("SELECT CLASSID FROM ENROLLMENTS E WHERE UPPER(E.B#) = UPPER(?)");
 stmt2.setString(1, bNumber);
 ResultSet rs2 = stmt2.executeQuery();

while (rs2.next()) {
 String c1RecClassId = rs2.getString("CLASSID");
 getCourseInfo(c1RecClassId, new
String[]{tDeptCodeOut}, new String[]{tCourseNumberOut});
 if (tDeptCodeOut.equals(lDeptCodeOut) &&
tCourseNumberOut.equals(lCourseNumberOut)) {
 conn.close();
 return true;
 }
 }
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 return enrollmentsCount > 0;
 }

 /*
 * Function to validate pre-req
course condition for given classid
 */
 public static boolean validateStudentPrereq(String bNumber, String classId)
{
 String lDeptCode = "";
 String lCourseNumber = "";
 String lCurrDeptCode = "";
 String lCurrCourseNumber =
"";
 String lPrereq = "";
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

getCourseInfo(classId, new String[]{lDeptCode}, new String[]{lCourseNumber});
 PreparedStatement stmt =
conn.prepareStatement("SELECT E.CLASSID FROM ENROLLMENTS E, CLASSES C WHERE UPPER(E.B#) = UPPER(?) AND UPPER(E.CLASSID)!= UPPER(?) AND E.CLASSID = C.CLASSID AND UPPER(C.SEMESTER) = 'FALL' AND C.YEAR = 2018");
 stmt.setString(1,bNumber);
 stmt.setString(2, classId);
 ResultSet rs = stmt.executeQuery();
 while (rs.next()) {
 String
c1RecClassId = rs.getString("CLASSID");
 getCourseInfo(c1RecClassId, new String[]{lCurrDeptCode}, new
String[]{lCurrCourseNumber});
 classPrereq(lCurrDeptCode, lCurrCourseNumber, new String[]{lPrereq});
 if
(lPrereq.contains(lDeptCode + lCourseNumber)) {
 conn.close();
 return false;
 }
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 return true;
 }

 /*
 * Function to validate last enrolled class for
the student
 */
 public static boolean validateLastEnrollment(String bNumber) {
 int classCount = 0;
 try {

Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?)");

stmt.setString(1, bNumber);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 classCount = rs.getInt(1);
 }

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return classCount == 1;
 }

 /*
 * Function
to validate last enrolled student for the class
 */
 public static boolean validateLastStudent(String classId) {
 int
studentCount = 0;
 try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
"username", "password");
 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(CLASSID) = UPPER(?)");
 stmt.setString(1, classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {

studentCount = rs.getInt(1);
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return
studentCount == 1;
 }

 /*
 * Function to validate if class is full
 */
 public static boolean
validateClassFull(String classId) {
 int classCount = 0;
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND LIMIT = CLASS_SIZE");

stmt.setString(1, classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 classCount = rs.getInt(1);
 }

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return classCount > 0;
 }

 /*
 * Function
to get the count of student enrollments
 */
 public static int getStudentEnrollCount(String bNumber) {
 int
enrollCount = 0;
 try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
"username", "password");
 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS EN,CLASSES CL WHERE UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(CL.CLASSID) AND UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018");
 stmt.setString(1, bNumber);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 enrollCount
= rs.getInt(1);
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return enrollCount;

}

 /*
 * Function to get classid from courses from prev semester i.e not FALL 2018
 */
 public static String
getClassId(String courseInfo) {
 String classId = "";
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT CLASSID FROM CLASSES CL WHERE UPPER(CL.DEPT_CODE) || CL.COURSE# = UPPER(?) AND NOT (UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018)");
 stmt.setString(1, courseInfo);
 ResultSet rs =
stmt.executeQuery();
 if (rs.next()) {
 classId = rs.getString("CLASSID");
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 return classId;
 }

 /*
 * Function to get grade of student for
particular class - from enrollments
 */
 public static String getGrade(String bNumber, String classId) {
 String
grade = "";
 try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
"username", "password");
 PreparedStatement stmt = conn.prepareStatement("SELECT EN.LGRADE FROM ENROLLMENTS EN WHERE UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(?)");
 stmt.setString(1, bNumber);
 stmt.setString(2,
classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {
 grade = rs.getString("LGRADE");
 }

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return grade;
 }

 /*
 * Function to
validate pre-req course condition for given classid
 */
 public static boolean validateStudentPrereqGrade(String
bNumber, String classId) {
 String lDeptCode = "";
 String lCourseNumber = "";
 String lPrereq = "";
 String
lClassId = "";
 String lCurrPrereq = "";
 String lGrade = "";
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

getCourseInfo(classId, new String[]{lDeptCode}, new String[]{lCourseNumber});
 PreparedStatement stmt =
conn.prepareStatement("SELECT E.CLASSID FROM ENROLLMENTS E, CLASSES C WHERE UPPER(E.B#) = UPPER(?) AND UPPER(E.CLASSID)!= UPPER(?) AND E.CLASSID = C.CLASSID AND UPPER(C.SEMESTER) = 'FALL' AND C.YEAR = 2018");
 stmt.setString(1,
bNumber);
 stmt.setString(2, classId);
 ResultSet rs = stmt.executeQuery();
 while (rs.next()) {
 lClassId =
rs.getString("CLASSID");
 getCourseInfo(lClassId, new String[]{CurrDeptCode}, new String[]{CurrCourseNumber});

classPrereq(CurrDeptCode, CurrCourseNumber, new String[]{Prereq});
 if (Prereq.contains(DeptCode + CourseNumber))
{
 conn.close();
 return false;
 }
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }

return true;
 }

 /*
 * Procedures to display the tuples in each of the seven tables for this project.
 * Procedure
"show_students"
 */
 public static void showStudents() {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 Statement stmt =
conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM STUDENTS");
 while (rs.next()) {

System.out.println(rs.getString("B#") + " " + rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));

}
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedures to display the
tuples in each of the seven tables for this project.
 * Procedure "show_tas"
 */
 public static void showTAs() {

try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username",
"password");
 Statement stmt = conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM TAS");

while (rs.next()) {
 System.out.println(rs.getString("B#") + " " + rs.getString("FIRST_NAME") + " " +
rs.getString("LAST_NAME"));
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 *
Procedures to display the tuples in each of the seven tables for this project.
 * Procedure "show_courses"
 */

public static void showCourses() {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 Statement stmt =
conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM COURSES");
 while (rs.next()) {

System.out.println(rs.getString("DEPT_CODE") + " " + rs.getString("COURSE#"));
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedures to display the tuples in each of the seven
tables for this project.
 * Procedure "show_classes"
 */
 public static void showClasses() {
 try {
 Connection
conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 Statement
stmt = conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM CLASSES");
 while (rs.next()) {

System.out.println(rs.getString("CLASSID") + " " + rs.getString("DEPT_CODE") + " " +
rs.getString("COURSE#"));
 }
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 *
Procedures to display the tuples in each of the seven tables for this project.
 * Procedure "show_enrollments"
 */

public static void showEnrollments() {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 Statement stmt =
conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM ENROLLMENTS");
 while (rs.next()) {

System.out.println(rs.getString("B#") + " " + rs.getString("CLASSID"));
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedures to display the tuples in each of the seven
tables for this project.
 * Procedure "show_prerequisites"
 */
 public static void showPrerequisites() {
 try {

Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");

Statement stmt = conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM PREREQUISITES");
 while
(rs.next()) {
 System.out.println(rs.getString("DEPT_CODE") + " " + rs.getString("COURSE#") + " " +
rs.getString("PRE_DEPT_CODE") + " " + rs.getString("PRE_COURSE#"));
 }
 conn.close();
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 }

 /*
 * Procedures to display the tuples in each of the seven tables for this
project.
 * Procedure "show_logs"
 */
 public static void showLogs() {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 Statement stmt =
conn.createStatement();
 ResultSet rs = stmt.executeQuery("SELECT * FROM LOGS");
 while (rs.next()) {

System.out.println(rs.getString("LOG_ID") + " " + rs.getString("LOG_MESSAGE"));
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to list B#, first name and last name of the TA of
the class for a given class
 * If the class does not have a TA, report   The class has no TA.  
 * If the
provided classid is invalid (i.e., not in the Classes table), report   The classid is invalid.  
 */
 public
static void classTA(String classId, String[] taBNumberOut, String[] firstNameOut, String[] lastNameOut) {
 String
lClassId = "";
 try {
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
"username", "password");
 PreparedStatement stmt = conn.prepareStatement("SELECT CLASSID FROM CLASSES WHEREUPPER(CLASSID) = UPPER(?)");
stmt.setString(1, classId);
 ResultSet rs = stmt.executeQuery();
 if (rs.next()) {

lClassId = rs.getString("CLASSID");
 }
 if (lClassId.isEmpty()) {
 System.out.println("The classid is invalid.");
 conn.close();
 return;
 }
 PreparedStatement stmt2 = conn.prepareStatement("SELECT B#, FIRST_NAME,LAST_NAME FROM STUDENTS ST, CLASSES CL WHERE UPPER(CL.CLASSID) = UPPER(?) AND UPPER(ST.B#) = UPPER(CL.TA_B#)");

stmt2.setString(1, classId);
 ResultSet rs2 = stmt2.executeQuery();
 if (rs2.next()) {
 taBNumberOut[0] =
rs2.getString("B#");
 firstNameOut[0] = rs2.getString("FIRST_NAME");
 lastNameOut[0] =
rs2.getString("LAST_NAME");
 } else {
 System.out.println("The class has no TA.");
 }
 conn.close();
 } catch
(SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Recursive helper procedure to get direct and indirect
prerequisites
 * If course C1 has course C2 as a prerequisite, C2 is a direct prerequisite.
 * If C2 has course C3 has
a prerequisite, then C3 is an indirect prerequisite for C1.
 */
 public static void getPrereqCourse(String deptCode,
String courseNumber, String[] prereqOut) {
 try {
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("SELECT PRE_DEPT_CODE, PRE_COURSE# FROM PREREQUISITES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?");
 stmt.setString(1, deptCode);
 stmt.setString(2, courseNumber);
 ResultSet rs =
stmt.executeQuery();
 while (rs.next()) {
 String tempDeptCode = rs.getString("PRE_DEPT_CODE");
 String
tempCourseNumber = rs.getString("PRE_COURSE#");
 prereqOut[0] += tempDeptCode + tempCourseNumber + ",";

getPrereqCourse(tempDeptCode, tempCourseNumber, prereqOut);
 }
 conn.close();
 } catch (SQLException e) {

e.printStackTrace();
 }
 }

 /*
 * Procedure to list all prerequisite courses for given course (with dept_code and
course#)
 * Including both direct and indirect prerequisite courses
 */
 public static void classPrereq(String
deptCode, String courseNumber, String[] prereqOut) {
 try {
 if (!validateDeptCodeCourse(deptCode, courseNumber)) {

System.out.println(deptCode + courseNumber + " does not exist.");
 return;
 }
 getPrereqCourse(deptCode,
courseNumber, prereqOut);
 if (prereqOut[0].length() > 0 && prereqOut[0].endsWith(",")) {
 prereqOut[0] =
prereqOut[0].substring(0, prereqOut[0].length() - 1);
 }
 } catch (Exception e) {
 e.printStackTrace();
 }
 }


/*
 * Procedure to Enroll Student for given class (insert a tuple into Enrollments table)
 * If the student is not in
the Students table, report   The B# is invalid.  
 * If the classid is not in the classes table, report
  The classid is invalid.  
 * If the class is not offered in the current semester (i.e., Fall 2018), reject
the enrollment:
 *   Cannot enroll into a class from a previous semester.  
 * If the class is already full
before the enrollment request, reject the enrollment request:
 *   The class is already full.  
 * If the
student is already in the class, report   The student is already in the class.  
 * If the student is already
enrolled in four other classes in the same semester and the same year, report:
 *   The student will be overloaded
with the new enrollment.   but still allow the student to be enrolled.
 * If the student is already enrolled in
five other classes in the same semester and the same year, report:
 *   Students cannot be enrolled in more than
five classes in the same semester.   and reject the enrollment.
 * If the student has not completed the required
prerequisite courses with at least a grade C, reject the enrollment:
 *   Prerequisite not satisfied.  
 * For
all the other cases, the requested enrollment should be carried out successfully.
 */
 public static void
enrollStudent(String bNumber, String classId) {
 try {
 if (!validateStudentB(bNumber)) {
 System.out.println("The B# is invalid.");
 return;
 } else if (!validateStudentClassId(classId)) {
 System.out.println("The classid is invalid.");
 return;
 } else if (!validateCurrentSemClass(classId)) {
 System.out.println("Cannot enroll into a class from a previous semester.");
 return;
 } else if (validateClassFull(classId)) {
 System.out.println("The class is already full.");
 return;
 } else if (validateStudentEnrollments(bNumber, classId)) {

System.out.println("The student is already in the class.");
 return;
 } else if (getStudentEnrollCount(bNumber) >=
5) {
 System.out.println("Students cannot be enrolled in more than five classes in the same semester.");
 return;

} else if (!validateStudentPrereqGrade(bNumber, classId)) {
 System.out.println("Prerequisite not satisfied.");

return;
 }
 if (getStudentEnrollCount(bNumber) == 4) {
 System.out.println("The student will be overloaded with the new enrollment.");
 }
 Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
"username", "password");
 PreparedStatement stmt = conn.prepareStatement("INSERT INTO ENROLLMENTS VALUES (?, ?,NULL)");
 stmt.setString(1, bNumber);
 stmt.setString(2, classId);
 stmt.executeUpdate();

System.out.println("Successfully Enrolled student with B# --> " + bNumber + " and Classid --> " + classId);

conn.commit();
 conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to drop
a student from a class (delete a tuple from Enrollments table)
 * If the student is not in the Students table, report
  The B# is invalid.  
 * If the classid is not in the Classes table, report   The classid is
invalid.  
 * If the student is not enrolled in the class, report   The student is not enrolled in the
class.  
 * If the class is not offered in Fall 2018, reject the drop attempt and report:
 *   Only enrollment
in the current semester can be dropped.  
 * If dropping the student from class would cause a violation of the
prerequisite requirement, reject the drop attempt:
 *   The drop is not permitted because another class the student
registered uses it as a prerequisite.  
 * In all the other cases, the student will be dropped from the class.
 *
If the class is the last class for the student, delete and report   This student is not enrolled in any
classes.  
 * If the student is the last student in the class, delete and report   The class now has no
students.  
 */
 public static void deleteStudentEnrollment(String bNumber, String classId) {
 try {
 if
(!validateStudentB(bNumber)) {
 System.out.println("The B# is invalid.");
 return;
 } else if
(!validateStudentClassId(classId)) {
 System.out.println("The classid is invalid.");
 return;
 } else if
(!validateStudentEnrollments(bNumber, classId)) {
 System.out.println("The student is not enrolled in the class.");

return;
 } else if (!validateCurrentSemClass(classId)) {
 System.out.println("Only enrollment in the current semester can be dropped.");
 return;
 } else if (!validateStudentPrereq(bNumber, classId)) {
 System.out.println("The dropis not permitted because another class the student registered uses it as a prerequisite.");
 return;
 }
 if
(validateLastEnrollment(bNumber)) {
 System.out.println("This student is not enrolled in any classes.");
 }
 if
(validateLastStudent(classId)) {
 System.out.println("The class now has no students.");
 }
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("DELETE FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?) AND UPPER(CLASSID) = UPPER(?)");

stmt.setString(1, bNumber);
 stmt.setString(2, classId);
 stmt.executeUpdate();
 System.out.println("SuccessfullyDeleted Student Enrollment with B# --> " + bNumber + " and Classid --> " + classId);
 conn.commit();

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to delete Student (delete
a tuple from Students table)
 * If the student is not in the Students table, report   The B# is invalid.  

*/
 public static void deleteStudent(String bNumber) {
 try {
 if (!validateStudentB(bNumber)) {

System.out.println("The B# is invalid.");
 return;
 }
 Connection conn =
DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 PreparedStatement
stmt = conn.prepareStatement("DELETE FROM STUDENTS WHERE UPPER(B#) = UPPER(?)");
 stmt.setString(1, bNumber);

stmt.executeUpdate();
 System.out.println("Successfully Deleted Student with B# --> " + bNumber);
 conn.commit();

conn.close();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 public static void main(String[] args) {

// Test the functions and procedures
 showStudents();
 showTAs();
 showCourses();
 showClasses();

showEnrollments();
 showPrerequisites();
 showLogs();

 String bNumber = "B12345";
 String classId = "C123";

enrollStudent(bNumber, classId);
 deleteStudentEnrollment(bNumber, classId);
 deleteStudent(bNumber);

}
}
