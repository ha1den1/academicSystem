import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserSubjectAssigner {
    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";


    public static void assignUserToSubjectFromUI(){
        List<String> userNames = getUserNames();
        if(userNames == null){
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch user names from the database",
                    "Error",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> subjectNames = getSubjectNames();
        if(subjectNames == null){
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch user names from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));
        JComboBox<String> subjectDropdown = new JComboBox<>(subjectNames.toArray(new String[0]));
        JButton addButton = new JButton("Add to subject");
        JButton removeButton = new JButton("Remove from subject");

        addButton.addActionListener(e -> handleAddButton(userDropdown, subjectDropdown));
        removeButton.addActionListener(e -> handleRemoveButton(userDropdown));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select user"));
        panel.add(userDropdown);
        panel.add(new JLabel("Select subject"));
        panel.add(subjectDropdown);
        panel.add(addButton);
        panel.add(removeButton);
        int result = JOptionPane.showConfirmDialog(null,panel,
                "Assign user to subject", JOptionPane.OK_CANCEL_OPTION);
        String selectedUser = userDropdown.getSelectedItem().toString();
        String selectedSubject = subjectDropdown.getSelectedItem().toString();
        if(result == JOptionPane.OK_OPTION){
            if(addButton.getModel().isPressed()){
                assignUserToSubject(selectedUser,selectedSubject);
            } else if (removeButton.getModel().isPressed()) {
                removeUserFromSubjects(selectedUser);

            }
        }
    }
    private static void handleAddButton (JComboBox<String> userDropdown, JComboBox<String> subjectDropdown){
        String selectedUser = userDropdown.getSelectedItem() != null ? userDropdown.getSelectedItem().toString():"";
        String selectedSubject = subjectDropdown.getSelectedItem() != null ? subjectDropdown.getSelectedItem().toString(): "";
        assignUserToSubject(selectedUser,selectedSubject);
    }
    private static void handleRemoveButton(JComboBox<String> userDropdown){
        String selectedUser = userDropdown.getSelectedItem() != null ? userDropdown.getSelectedItem().toString() : "";
        removeUserFromSubjects(selectedUser);
    }
    private static void assignUserToSubject(String userName, String subjectName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userID = getUserId(conn, userName);
            int subjectID = getSubjectId(conn, subjectName);

            if (userID != -1 && subjectID != -1) {
                if (!isUserInSubject(conn, userID, subjectID)) {
                    String assignToSubjectQuery = "INSERT INTO user_subjects (user_id, subject_id) VALUES (?,?)";
                    try (PreparedStatement assignToGroupStatement = conn.prepareStatement(assignToSubjectQuery)) {
                        assignToGroupStatement.setInt(1, userID);
                        assignToGroupStatement.setInt(2, subjectID);
                        assignToGroupStatement.executeUpdate();

                        JOptionPane.showMessageDialog(null,
                                "User assigned to subject successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "User is already in the subject",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "User or group does not exist",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void removeUserFromSubjectsFromUI() {
        List<String> userNames = getUserNames();
        if (userNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch users from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));

        JButton removeButton = new JButton("Remove from subjects");
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select a user:"));
        panel.add(userDropdown);
        panel.add(removeButton);
        int result = JOptionPane.showConfirmDialog(null, panel,
                "Remove User from Groups", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedUser = userDropdown.getSelectedItem().toString();
            removeUserFromSubjects(selectedUser);
        }
    }
    public static void removeUserFromSubjects(String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userId = getUserId(conn, userName);

            if (userId != -1) {
                String removeUserFromSubjectsQuery = "DELETE FROM user_subjects WHERE user_id = ?";
                try (PreparedStatement removeUserFromSubjectsStatement = conn.prepareStatement(removeUserFromSubjectsQuery)) {
                    removeUserFromSubjectsStatement.setInt(1, userId);
                    int rowsAffected = removeUserFromSubjectsStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null,
                                "User removed from subjects successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "User is not assigned to any subjects",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "User does not exist",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
    private static int getSubjectId(Connection conn, String subjectName) throws SQLException {
        String getSubjectIdQuery = "SELECT subject_id FROM subjects WHERE subject_name = ?";
        try (PreparedStatement getSubjectIdStatement = conn.prepareStatement(getSubjectIdQuery)) {
            getSubjectIdStatement.setString(1, subjectName);
            var resultSet = getSubjectIdStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("subject_id") : -1;
        }
    }
    private static boolean isUserInSubject(Connection conn, int userId, int subjectId) throws SQLException {
        String isUserInSubjectQuery = "SELECT * FROM user_subjects WHERE user_id = ? AND subject_id = ?";
        try (PreparedStatement isUserInSubjectStatement = conn.prepareStatement(isUserInSubjectQuery)) {
            isUserInSubjectStatement.setInt(1, userId);
            isUserInSubjectStatement.setInt(2, subjectId);
            var resultSet = isUserInSubjectStatement.executeQuery();

            return resultSet.next();
        }
    }
    private static List<String> getUserNames() {
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
    private static List<String> getSubjectNames() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
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