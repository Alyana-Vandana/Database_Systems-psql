Here's the Java equivalent of the provided PL/SQL trigger code, along with explanations in comments:

```java
import java.sql.*;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class StudentDeletionLogger {

    private static DataSource dataSource;

    static {
        // Initialize connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:XE"); // Replace with your database URL
        config.setUsername("your_username");
        config.setPassword("your_password");
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public static void main(String[] args) {
        // This main method is just for demonstration
        // You would typically call logStudentDeletion from your application logic
        logStudentDeletion("B12345"); // Example student ID
    }

    /**
     * Logs the deletion of a student in the LOGS table.
     * This method simulates the functionality of the PL/SQL trigger.
     *
     * @param studentId The ID of the deleted student
     */
    public static void logStudentDeletion(String studentId) {
        String sql = "INSERT INTO LOGS (LOG_ID, USER_NAME, LOG_DATE, TABLE_NAME, OPERATION, RECORD_ID) " +
                     "VALUES (LOG_SEQ_GENERATOR.NEXTVAL, ?, SYSDATE, 'STUDENTS', 'DELETE', ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters
            pstmt.setString(1, System.getProperty("user.name")); // Get current user
            pstmt.setString(2, studentId);

            // Execute the insert
            pstmt.executeUpdate();

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            // In a real application, you might want to log this error or throw a custom exception
        }
    }
}
```

Explanatory notes:

1. **Data Type Mapping**: 
   - The PL/SQL trigger doesn't explicitly declare variables, so we don't need to map data types directly.
   - We use `String` for the student ID (B#) and username.

2. **Database Connectivity**:
   - We use JDBC for database interaction.
   - A connection pool (HikariCP) is used for efficient connection management.

3. **Control Structures**:
   - The PL/SQL trigger doesn't contain loops or conditionals, so we don't need to convert these.
   - Exception handling is done using try-catch in Java.

4. **Stored Procedures and Functions**:
   - We're not calling stored procedures here, but we're simulating the trigger's functionality in Java.

5. **Transaction Management**:
   - The connection from the pool is used, which handles transaction management.
   - Auto-commit is typically disabled in connection pools, so we explicitly call `conn.commit()`.

6. **Trigger Simulation**:
   - Instead of a database trigger, we have a Java method that performs the logging operation.
   - This method would need to be called whenever a student is deleted in your application logic.

7. **Sequence Generation**:
   - We use `LOG_SEQ_GENERATOR.NEXTVAL` directly in the SQL, assuming it exists in the database.

8. **Current User and Date**:
   - We use `System.getProperty("user.name")` to get the current user.
   - `SYSDATE` is used directly in the SQL for the current date.

9. **Identifier Changes**:
   - As requested, `B#` has been changed to `studentId` in the Java code to avoid illegal characters.

Note: This code assumes that you have the necessary JDBC driver and HikariCP in your classpath. You'll need to add these dependencies to your project. Also, remember to replace the database URL, username, and password with your actual database credentials.