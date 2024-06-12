
import java.sql.*;

public class StudentRegistrationSystem {
 private Connection connection;

 public StudentRegistrationSystem() {
 // Initialize database connection
 try {
 connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
 } catch(SQLException e) {
 e.printStackTrace();
 }
 }

 // Helper Functions

 /*
 * Function to validate B# of a student
 */
 public boolean validateStudentBNumber(String bNumber) {
 int studentCount = 0;
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT COUNT(1) FROM STUDENTS WHERE UPPER(B#) = UPPER(?)");
 statement.setString(1, bNumber);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {
 studentCount = resultSet.getInt(1);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return studentCount > 0;
 }

 /*
 * Function to validate DEPTCODE of a COURSES
 */
 public boolean
validateCourseDeptCode(String deptCode, String courseNumber) {
 int validCount = 0;
 try {
 PreparedStatement
statement = connection.prepareStatement("SELECT COUNT(1) FROM COURSES WHERE UPPER(DEPT_CODE) = UPPER(?) AND COURSE# =
?");
 statement.setString(1, deptCode);
 statement.setString(2, courseNumber);
 ResultSet resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 validCount = resultSet.getInt(1);
 }
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 return validCount > 0;
 }

 /*
 * Function to validate classid of a classes
 */

public boolean validateClassId(String classId) {
 int classCount = 0;
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?)");
 statement.setString(1,
classId);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {
 classCount =
resultSet.getInt(1);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return classCount > 0;
 }

 /*
 *
Function to validate whether particular class is offered in current semester or not
 * Current sem - FALL2018
 */

public boolean validateCurrentSemesterClass(String classId) {
 int classCount = 0;
 try {
 PreparedStatement
statement = connection.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE UPPER(CLASSID) = UPPER(?) AND
UPPER(SEMESTER) = 'FALL' AND YEAR = 2018");
 statement.setString(1, classId);
 ResultSet resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 classCount = resultSet.getInt(1);
 }
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 return classCount > 0;
 }

 /*
 * Procedure to get course# and dept_code from classes
for given classid
 */
 public void getCourseInfo(String classId, String[] deptCodeOut, String[] courseNumberOut) {

try {
 PreparedStatement statement = connection.prepareStatement("SELECT DEPT_CODE, COURSE# FROM CLASSES WHERE CLASSID
= ?");
 statement.setString(1, classId);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {

deptCodeOut[0] = resultSet.getString("DEPT_CODE");
 courseNumberOut[0] = resultSet.getString("COURSE#");
 }
 }
catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Function to validate whether particular student is
enrolled or not
 */
 public boolean validateStudentEnrollments(String bNumber, String classId) {
 int
enrollmentsCount = 0;
 String deptCodeOut = "";
 String courseNumberOut = "";
 String tempDeptCodeOut = "";

String tempCourseNumberOut = "";
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT COUNT(1)
FROM ENROLLMENTS E WHERE UPPER(E.B#) = UPPER(?)");
 statement.setString(1, bNumber);
 ResultSet resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 enrollmentsCount = resultSet.getInt(1);
 }


getCourseInfo(classId, new String[]{deptCodeOut}, new String[]{courseNumberOut});

 statement =
connection.prepareStatement("SELECT CLASSID FROM ENROLLMENTS E WHERE UPPER(E.B#) = UPPER(?)");

statement.setString(1, bNumber);
 resultSet = statement.executeQuery();
 while (resultSet.next()) {

getCourseInfo(resultSet.getString("CLASSID"), new String[]{tempDeptCodeOut}, new String[]{tempCourseNumberOut});
 if
(tempDeptCodeOut.equals(deptCodeOut) && tempCourseNumberOut.equals(courseNumberOut)) {
 return true;
 }
 }
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
 public boolean validateStudentPrerequisite(String bNumber, String classId) {

String deptCode = "";
 String courseNumber = "";
 String tempDeptCode = "";
 String tempCourseNumber = "";

String prerequisites = "";
 try {
 getCourseInfo(classId, new String[]{deptCode}, new String[]{courseNumber});


PreparedStatement statement = connection.prepareStatement("SELECT E.CLASSID FROM ENROLLMENTS E, CLASSES C WHERE
UPPER(E.B#) = UPPER(?) AND UPPER(E.CLASSID) != UPPER(?) AND E.CLASSID = C.CLASSID AND UPPER(C.SEMESTER) = 'FALL' AND
C.YEAR = 2018");
 statement.setString(1, bNumber);
 statement.setString(2, classId);
 ResultSet resultSet =
statement.executeQuery();
 while (resultSet.next()) {
 getCourseInfo(resultSet.getString("CLASSID"), new
String[]{tempDeptCode}, new String[]{tempCourseNumber});
 getClassPrerequisites(tempDeptCode, tempCourseNumber, new
String[]{prerequisites});
 if (prerequisites.contains(deptCode + courseNumber)) {
 return false;
 }
 }
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
 public boolean validateLastEnrollment(String bNumber) {
 int classCount = 0;
 try {

PreparedStatement statement = connection.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(B#) =
UPPER(?)");
 statement.setString(1, bNumber);
 ResultSet resultSet = statement.executeQuery();
 if
(resultSet.next()) {
 classCount = resultSet.getInt(1);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }

return classCount != 1;
 }

 /*
 * Function to validate last enrolled student for the class
 */
 public boolean
validateLastStudent(String classId) {
 int studentCount = 0;
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS WHERE UPPER(CLASSID) = UPPER(?)");

statement.setString(1, classId);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {

studentCount = resultSet.getInt(1);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return studentCount !=
1;
 }

 /*
 * Function to validate if class is full
 */
 public boolean validateClassFull(String classId) {
 int
classCount = 0;
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT COUNT(1) FROM CLASSES WHERE
UPPER(CLASSID) = UPPER(?) AND LIMIT = CLASS_SIZE");
 statement.setString(1, classId);
 ResultSet resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 classCount = resultSet.getInt(1);
 }
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 return classCount > 0;
 }

 /*
 * Function to get the count of student enrollments

*/
 public int getStudentEnrollmentCount(String bNumber) {
 int enrollmentCount = 0;
 try {
 PreparedStatement
statement = connection.prepareStatement("SELECT COUNT(1) FROM ENROLLMENTS EN, CLASSES CL WHERE UPPER(EN.B#) = UPPER(?)
AND UPPER(EN.CLASSID) = UPPER(CL.CLASSID) AND UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018");

statement.setString(1, bNumber);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {

enrollmentCount = resultSet.getInt(1);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return
enrollmentCount;
 }

 /*
 * Function to get classid from courses from prev semester i.e not FALL 2018
 */
 public
String getClassId(String courseInfo) {
 String classId = "";
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT CLASSID FROM CLASSES CL WHERE UPPER(CL.DEPT_CODE) || CL.COURSE# = UPPER(?) AND NOT
(UPPER(CL.SEMESTER) = 'FALL' AND CL.YEAR = 2018)");
 statement.setString(1, courseInfo);
 ResultSet resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 classId = resultSet.getString("CLASSID");
 }
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
 public String getGrade(String bNumber, String classId) {
 String grade =
"";
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT EN.LGRADE FROM ENROLLMENTS EN WHERE
UPPER(EN.B#) = UPPER(?) AND UPPER(EN.CLASSID) = UPPER(?)");
 statement.setString(1, bNumber);
 statement.setString(2,
classId);
 ResultSet resultSet = statement.executeQuery();
 if (resultSet.next()) {
 grade =
resultSet.getString("LGRADE");
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return grade;
 }


/*
 * Function to validate pre-req course condition for given classid
 */
 public boolean
validateStudentPrerequisiteGrade(String bNumber, String classId) {
 String deptCode = "";
 String courseNumber =
"";
 String prerequisites = "";
 try {
 getCourseInfo(classId, new String[]{deptCode}, new
String[]{courseNumber});

 PreparedStatement statement = connection.prepareStatement("SELECT REGEXP_SUBSTR(C_PREREQ,
'[^,]+', 1, LEVEL) AS DATA FROM DUAL CONNECT BY REGEXP_SUBSTR(C_PREREQ, '[^,]+', 1, LEVEL) IS NOT NULL");

statement.setString(1, prerequisites);
 ResultSet resultSet = statement.executeQuery();
 while (resultSet.next()) {

String currPrerequisite = resultSet.getString("DATA");
 String currClassId = getClassId(currPrerequisite);
 if
(currClassId == null) {
 return false;
 }
 String grade = getGrade(bNumber, currClassId);
 if (grade == null ||
(Character.toUpperCase(grade.charAt(0)) > 'C' || grade.toUpperCase().equals("C-"))) {
 return false;
 }
 }
 }
catch (SQLException e) {
 e.printStackTrace();
 }
 return true;
 }

 /*
 * Procedures to display the tuples in
each of the seven tables for this project.
 * Procedure "show_students"
 */
 public ResultSet showStudents() {

ResultSet resultSet = null;
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT * FROM
STUDENTS");
 resultSet = statement.executeQuery();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return
resultSet;
 }

 /*
 * Procedures to display the tuples in each of the seven tables for this project.
 * Procedure
"show_tas"
 */
 public ResultSet showTAs() {
 ResultSet resultSet = null;
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT * FROM TAS");
 resultSet = statement.executeQuery();
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 return resultSet;
 }

 /*
 * Procedures to display the tuples in each of the seven
tables for this project.
 * Procedure "show_courses"
 */
 public ResultSet showCourses() {
 ResultSet resultSet =
null;
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT * FROM COURSES");
 resultSet =
statement.executeQuery();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return resultSet;
 }

 /*
 *
Procedures to display the tuples in each of the seven tables for this project.
 * Procedure "show_classes"
 */

public ResultSet showClasses() {
 ResultSet resultSet = null;
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT * FROM CLASSES");
 resultSet = statement.executeQuery();
 } catch (SQLException
e) {
 e.printStackTrace();
 }
 return resultSet;
 }

 /*
 * Procedures to display the tuples in each of the seven
tables for this project.
 * Procedure "show_enrollments"
 */
 public ResultSet showEnrollments() {
 ResultSet
resultSet = null;
 try {
 PreparedStatement statement = connection.prepareStatement("SELECT * FROM ENROLLMENTS");

resultSet = statement.executeQuery();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return resultSet;

}

 /*
 * Procedures to display the tuples in each of the seven tables for this project.
 * Procedure
"show_prerequisites"
 */
 public ResultSet showPrerequisites() {
 ResultSet resultSet = null;
 try {

PreparedStatement statement = connection.prepareStatement("SELECT * FROM PREREQUISITES");
 resultSet =
statement.executeQuery();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 return resultSet;
 }

 /*
 *
Procedures to display the tuples in each of the seven tables for this project.
 * Procedure "show_logs"
 */
 public
ResultSet showLogs() {
 ResultSet resultSet = null;
 try {
 PreparedStatement statement =
connection.prepareStatement("SELECT * FROM LOGS");
 resultSet = statement.executeQuery();
 } catch (SQLException e)
{
 e.printStackTrace();
 }
 return resultSet;
 }

 /*
 * Procedure to list B#, first name and last name of the TA
of the class for a given class
 * If the class does not have a TA, report  The class has no TA. 
 * If the
provided classid is invalid (i.e., not in the Classes table), report  The classid is invalid. 
 */
 public
void getClassTA(String classId, String[] taBNumberOut, String[] firstNameOut, String[] lastNameOut) {
 try {

PreparedStatement statement = connection.prepareStatement("SELECT CL.CLASSID FROM CLASSES CL WHERE UPPER(CL.CLASSID) =
UPPER(?)");
 statement.setString(1, classId);
 ResultSet resultSet = statement.executeQuery();
 if
(!resultSet.next()) {
 System.out.println("The classid is invalid.");
 return;
 }

 statement =
connection.prepareStatement("SELECT ST.B#, ST.FIRST_NAME, ST.LAST_NAME FROM STUDENTS ST, CLASSES CL WHERE
UPPER(CL.CLASSID) = UPPER(?) AND UPPER(ST.B#) = UPPER(CL.TA_B#)");
 statement.setString(1, classId);
 resultSet =
statement.executeQuery();
 if (resultSet.next()) {
 taBNumberOut[0] = resultSet.getString("B#");
 firstNameOut[0] =
resultSet.getString("FIRST_NAME");
 lastNameOut[0] = resultSet.getString("LAST_NAME");
 } else {

System.out.println("The class has no TA.");
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 *
Recursive helper procedure to get direct and indirect prerequisites
 * If course C1 has course C2 as a prerequisite, C2
is a direct prerequisite.
 * If C2 has course C3 has a prerequisite, then C3 is an indirect prerequisite for C1.
 */

public void getPrerequisiteCourse(String deptCode, String courseNumber, String[] prerequisitesOut) {
 try {

PreparedStatement statement = connection.prepareStatement("SELECT PRE_DEPT_CODE, PRE_COURSE# FROM PREREQUISITES WHERE
UPPER(DEPT_CODE) = UPPER(?) AND COURSE# = ?");
 statement.setString(1, deptCode);
 statement.setString(2,
courseNumber);
 ResultSet resultSet = statement.executeQuery();
 while (resultSet.next()) {
 String preDeptCode =
resultSet.getString("PRE_DEPT_CODE");
 String preCourseNumber = resultSet.getString("PRE_COURSE#");

prerequisitesOut[0] += preDeptCode + preCourseNumber + ",";
 getPrerequisiteCourse(preDeptCode, preCourseNumber,
prerequisitesOut);
 }
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to list all
prerequisite courses for given course (with dept_code and course#)
 * Including both direct and indirect prerequisite
courses
 */
 public void getClassPrerequisites(String deptCode, String courseNumber, String[] prerequisitesOut) {

try {
 if (!validateCourseDeptCode(deptCode, courseNumber)) {
 System.out.println(deptCode + courseNumber + " does
not exist.");
 return;
 }
 getPrerequisiteCourse(deptCode, courseNumber, prerequisitesOut);
 if
(prerequisitesOut[0].length() > 0 && prerequisitesOut[0].endsWith(",")) {
 prerequisitesOut[0] =
prerequisitesOut[0].substring(0, prerequisitesOut[0].length() - 1);
 }
 } catch (SQLException e) {

e.printStackTrace();
 }
 }

 /*
 * Procedure to Enroll Student for given class (insert a tuple into Enrollments
table)
 * If the student is not in the Students table, report  The B# is invalid. 
 * If the classid is not
in the classes table, report  The classid is invalid. 
 * If the class is not offered in the current semester
(i.e., Fall 2018), reject the enrollment:
 *  Cannot enroll into a class from a previous semester. 
 * If
the class is already full before the enrollment request, reject the enrollment request:
 *  The class is already
full. 
 * If the student is already in the class, report  The student is already in the class. 
 * If
the student is already enrolled in four other classes in the same semester and the same year, report:
 *  The
student will be overloaded with the new enrollment.  but still allow the student to be enrolled.
 * If the student
is already enrolled in five other classes in the same semester and the same year, report:
 *  Students cannot be
enrolled in more than five classes in the same semester.  and reject the enrollment.
 * If the student has not
completed the required prerequisite courses with at least a grade C, reject the enrollment:
 *  Prerequisite not
satisfied. 
 * For all the other cases, the requested enrollment should be carried out successfully.
 */
 public
void enrollStudent(String bNumber, String classId) {
 try {
 if (!validateStudentBNumber(bNumber)) {

System.out.println("The B# is invalid.");
 return;
 } else if (!validateClassId(classId)) {

System.out.println("The classid is invalid.");
 return;
 } else if (!validateCurrentSemesterClass(classId)) {

System.out.println("Cannot enroll into a class from a previous semester.");
 return;
 } else if
(validateClassFull(classId)) {
 System.out.println("The class is already full.");
 return;
 } else if
(validateStudentEnrollments(bNumber, classId)) {
 System.out.println("The student is already in the class.");

return;
 } else if (getStudentEnrollmentCount(bNumber) >= 5) {
 System.out.println("Students cannot be enrolled in
more than five classes in the same semester.");
 return;
 } else if (getStudentEnrollmentCount(bNumber) == 4) {

System.out.println("The student will be overloaded with the new enrollment.");
 } else if
(!validateStudentPrerequisiteGrade(bNumber, classId)) {
 System.out.println("Prerequisite not satisfied.");

return;
 }

 PreparedStatement statement = connection.prepareStatement("INSERT INTO ENROLLMENTS VALUES (?, ?,
NULL)");
 statement.setString(1, bNumber);
 statement.setString(2, classId);
 statement.executeUpdate();

System.out.println("Successfully Enrolled student with B# --> " + bNumber + " and Classid --> " + classId);

connection.commit();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to drop a student
from a class (delete a tuple from Enrollments table)
 * If the student is not in the Students table, report  The
B# is invalid. 
 * If the classid is not in the Classes table, report  The classid is invalid. 
 * If
the student is not enrolled in the class, report  The student is not enrolled in the class. 
 * If the class
is not offered in Fall 2018, reject the drop attempt and report:
 *  Only enrollment in the current semester can
be dropped. 
 * If dropping the student from class would cause a violation of the prerequisite requirement, reject
the drop attempt:
 *  The drop is not permitted because another class the student registered uses it as a
prerequisite. 
 * In all the other cases, the student will be dropped from the class.
 * If the class is the last
class for the student, delete and report  This student is not enrolled in any classes. 
 * If the student is
the last student in the class, delete and report  The class now has no students. 
 */
 public void
deleteStudentEnrollment(String bNumber, String classId) {
 try {
 if (!validateStudentBNumber(bNumber)) {

System.out.println("The B# is invalid.");
 return;
 } else if (!validateClassId(classId)) {

System.out.println("The classid is invalid.");
 return;
 } else if (!validateStudentEnrollments(bNumber, classId))
{
 System.out.println("The student is not enrolled in the class.");
 return;
 } else if
(!validateCurrentSemesterClass(classId)) {
 System.out.println("Only enrollment in the current semester can be
dropped.");
 return;
 } else if (!validateStudentPrerequisite(bNumber, classId)) {
 System.out.println("The drop is
not permitted because another class the student registered uses it as a prerequisite.");
 return;
 }

 if
(validateLastEnrollment(bNumber)) {
 System.out.println("This student is no more enrolled in any classes.");
 }


if (validateLastStudent(classId)) {
 System.out.println("The class now has no students.");
 }

 PreparedStatement
statement = connection.prepareStatement("DELETE FROM ENROLLMENTS WHERE UPPER(B#) = UPPER(?) AND UPPER(CLASSID) =
UPPER(?)");
 statement.setString(1, bNumber);
 statement.setString(2, classId);
 statement.executeUpdate();

System.out.println("Successfully Deleted Student Enrollment with B# --> " + bNumber + " and Classid --> " +
classId);
 connection.commit();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 /*
 * Procedure to
delete Student (delete a tuple from Students table)
 * If the student is not in the Students table, report  The B#
is invalid. 
 */
 public void deleteStudent(String bNumber) {
 try {
 if (!validateStudentBNumber(bNumber)) {

System.out.println("The B# is invalid.");
 return;
 }

 PreparedStatement statement =
connection.prepareStatement("DELETE FROM STUDENTS WHERE UPPER(B#) = UPPER(?)");
 statement.setString(1, bNumber);

statement.executeUpdate();
 System.out.println("Successfully Deleted Student with B# --> " + bNumber);

connection.commit();
 } catch (SQLException e) {
 e.printStackTrace();
 }
 }

 public static void main(String[]
args) {
 StudentRegistrationSystem system = new StudentRegistrationSystem();
 // Call the required methods

}
}
