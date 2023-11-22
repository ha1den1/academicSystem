import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserSubjectViewer {
    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Subject Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);

            JComboBox<String> userDropdown = new JComboBox<>(getUserNames().toArray(new String[0]));
            JButton viewButton = new JButton("View Subjects and Groups");

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Select a user:"));
            panel.add(userDropdown);
            panel.add(new JLabel(""));
            panel.add(viewButton);

            viewButton.addActionListener(e -> viewUserSubjectsAndGroups(getSelectedUser()));

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    public static void viewUserSubjectsAndGroups(String userName) {
        if (userName != null) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                int userId = getUserId(conn, userName);

                if (userId != -1) {
                    List<String> subjects = getUserSubjects(conn, userId);
                    List<String> groups = getUserGroups(conn, userId);

                    StringBuilder message = new StringBuilder("User: " + userName + "\n\n");
                    message.append("Subjects:\n");

                    for (String subject : subjects) {
                        int subjectId = getSubjectId(conn, subject);
                        List<String> grades = getGrades(conn, userId, subjectId);

                        message.append("- ").append(subject);
                        if (!grades.isEmpty()) {
                            message.append(" (Grades: ").append(String.join(", ", grades)).append(")");
                        }
                        message.append("\n");
                    }

                    message.append("\nGroups:\n");
                    for (String group : groups) {
                        message.append("- ").append(group).append("\n");
                    }

                    JOptionPane.showMessageDialog(null, message.toString(), "User Subjects and Groups", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static List<String> getGrades(Connection conn, int userId, int subjectId) throws SQLException {
        List<String> grades = new ArrayList<>();
        String sql = "SELECT grade FROM grades WHERE user_id = ? AND subject_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, subjectId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                grades.add(resultSet.getString("grade"));
            }
        }
        return grades;
    }

    private static String getSelectedUser() {
        return (String) JOptionPane.showInputDialog(
                null,
                "Select a user:",
                "User Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                getUserNames().toArray(),
                null);
    }

    public static List<String> getUserSubjects(Connection conn, int userId) throws SQLException {
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

    public static int getUserId(Connection conn, String userName) throws SQLException {
        String getUserIdQuery = "SELECT user_id FROM users WHERE name = ?";
        try (PreparedStatement getUserIdStatement = conn.prepareStatement(getUserIdQuery)) {
            getUserIdStatement.setString(1, userName);
            var resultSet = getUserIdStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("user_id") : -1;
        }
    }

    public static List<String> getUserNames() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT name FROM users";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String> userNames = new ArrayList<>();
                while (resultSet.next()) {
                    userNames.add(resultSet.getString("name"));
                }

                return userNames;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static int getSubjectId(Connection conn, String subjectName) throws SQLException {
        String getSubjectIdQuery = "SELECT subject_id FROM subjects WHERE subject_name = ?";
        try (PreparedStatement getSubjectIdStatement = conn.prepareStatement(getSubjectIdQuery)) {
            getSubjectIdStatement.setString(1, subjectName);
            ResultSet resultSet = getSubjectIdStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("subject_id") : -1;
        }
    }

    public static List<String> getSubjectNames(Connection conn) {
        try {
            String sql = "SELECT subject_name FROM subjects";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String> subjectNames = new ArrayList<>();
                while (resultSet.next()) {
                    subjectNames.add(resultSet.getString("subject_name"));
                }

                return subjectNames;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
