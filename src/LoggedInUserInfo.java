import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoggedInUserInfo {
    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static String getTeacherGroup(String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userId = getUserId(conn, userName);

            if (userId != -1 && isUserTeacher(conn, userId)) {
                return getUserGroup(conn, userId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static boolean isUserTeacher(Connection conn, int userId) throws SQLException {
        String sql = "SELECT Teacher FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() && resultSet.getInt("Teacher") == 1;
        }
    }

    private static String getUserGroup(Connection conn, int userId) throws SQLException {
        String sql = "SELECT groups.group_name FROM groups " +
                "INNER JOIN user_groups ON groups.group_id = user_groups.group_id " +
                "WHERE user_groups.user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? resultSet.getString("group_name") : null;
        }
    }

    public static UserInfo getLoggedInUserInfo(String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userId = getUserId(conn, userName);

            if (userId != -1) {
                List<String> subjects = getUserSubjects(conn, userId);
                List<String> groups = getUserGroups(conn, userId);

                return new UserInfo(userName, subjects, groups);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    private static List<String> getUserSubjects(Connection conn, int userId) throws SQLException {
        String sql = "SELECT subjects.subject_name FROM subjects " +
                "INNER JOIN user_subjects ON subjects.subject_id = user_subjects.subject_id " +
                "WHERE user_subjects.user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> subjects = new ArrayList<>();
            while (resultSet.next()) {
                subjects.add(resultSet.getString("subject_name"));
            }

            return subjects;
        }
    }

    private static List<String> getUserGroups(Connection conn, int userId) throws SQLException {
        String sql = "SELECT groups.group_name FROM groups " +
                "INNER JOIN user_groups ON groups.group_id = user_groups.group_id " +
                "WHERE user_groups.user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> groups = new ArrayList<>();
            while (resultSet.next()) {
                groups.add(resultSet.getString("group_name"));
            }

            return groups;
        }
    }

    private static int getUserId(Connection conn, String userName) throws SQLException {
        String getUserIdQuery = "SELECT user_id FROM users WHERE name = ?";
        try (PreparedStatement getUserIdStatement = conn.prepareStatement(getUserIdQuery)) {
            getUserIdStatement.setString(1, userName);
            var resultSet = getUserIdStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("user_id") : -1;
        }
    }
}
