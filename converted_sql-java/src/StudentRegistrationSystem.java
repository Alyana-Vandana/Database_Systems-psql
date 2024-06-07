import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "system";
        String password = "oracle";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            grep(conn, "HR", "%John%");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void grep(Connection conn, String username, String pattern) throws SQLException {
        String query = "SELECT table_name, column_name FROM all_tab_cols WHERE owner = ? AND data_type = 'VARCHAR2'";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String tableName = rs.getString(1);
            String columnName = rs.getString(2);

            String selectQuery = "SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName + " LIKE ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, pattern);
            ResultSet selectRs = selectStmt.executeQuery();

            while (selectRs.next()) {
                String result = selectRs.getString(1);
                System.out.println("\"" + pattern + "\",\"" + username + "\",\"" + tableName + "\",\"" + columnName + "\",\"" + result + "\"");
            }
        }
    }
}
