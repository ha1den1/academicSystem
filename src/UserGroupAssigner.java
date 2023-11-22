import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserGroupAssigner {

    static final String DB_URL = "jdbc:mysql://localhost/mydata?serverTimezone=UTC";
    static final String USERNAME = "root";
    static final String PASSWORD = "";

    public static void assignUserToGroupFromUI() {
        List<String> userNames = getUserNames();
        if (userNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch users from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> groupNames = getGroupNames();
        if (groupNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch groups from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));
        JComboBox<String> groupDropdown = new JComboBox<>(groupNames.toArray(new String[0]));

        JButton addButton = new JButton("Add to Group");
        JButton removeButton = new JButton("Remove from Group");

        addButton.addActionListener(e -> handleAddButton(userDropdown, groupDropdown));
        removeButton.addActionListener(e -> handleRemoveButton(userDropdown));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select a user:"));
        panel.add(userDropdown);
        panel.add(new JLabel("Select a group:"));
        panel.add(groupDropdown);
        panel.add(addButton);
        panel.add(removeButton);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Assign User to Group", JOptionPane.OK_CANCEL_OPTION);

        String selectedUser = userDropdown.getSelectedItem().toString();
        String selectedGroup = groupDropdown.getSelectedItem().toString();

        if (result == JOptionPane.OK_OPTION) {
            if (addButton.getModel().isPressed()) {
                assignUserToGroup(selectedUser, selectedGroup);
            } else if (removeButton.getModel().isPressed()) {
                removeUserFromGroups(selectedUser);
            }
        }
    }
    private static void handleAddButton(JComboBox<String> userDropdown, JComboBox<String> groupDropdown) {
        String selectedUser = userDropdown.getSelectedItem() != null ? userDropdown.getSelectedItem().toString() : "";
        String selectedGroup = groupDropdown.getSelectedItem() != null ? groupDropdown.getSelectedItem().toString() : "";

        assignUserToGroup(selectedUser, selectedGroup);
    }

    private static void handleRemoveButton(JComboBox<String> userDropdown) {
        String selectedUser = userDropdown.getSelectedItem() != null ? userDropdown.getSelectedItem().toString() : "";
        removeUserFromGroups(selectedUser);
    }
    private static void assignUserToGroup(String userName, String groupName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userId = getUserId(conn, userName);
            int groupId = getGroupId(conn, groupName);



            if (userId != -1 && groupId != -1) {
                if (!isUserInGroup(conn, userId, groupId)) {
                    String assignToGroupQuery = "INSERT INTO user_groups (user_id, group_id) VALUES (?, ?)";


                    try (PreparedStatement assignToGroupStatement = conn.prepareStatement(assignToGroupQuery)) {
                        assignToGroupStatement.setInt(1, userId);
                        assignToGroupStatement.setInt(2, groupId);
                        assignToGroupStatement.executeUpdate();

                        JOptionPane.showMessageDialog(null,
                                "User assigned to group successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "User is already assigned to the group",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
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
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void removeUserFromGroupsFromUI() {
        List<String> userNames = getUserNames();
        if (userNames == null) {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch users from the database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> userDropdown = new JComboBox<>(userNames.toArray(new String[0]));

        JButton removeButton = new JButton("Remove from Groups");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select a user:"));
        panel.add(userDropdown);
        panel.add(removeButton);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Remove User from Groups", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedUser = userDropdown.getSelectedItem().toString();
            removeUserFromGroups(selectedUser);
        }
    }

    private static void removeUserFromGroups(String userName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            int userId = getUserId(conn, userName);

            if (userId != -1) {
                String removeUserFromGroupsQuery = "DELETE FROM user_groups WHERE user_id = ?";
                try (PreparedStatement removeUserFromGroupsStatement = conn.prepareStatement(removeUserFromGroupsQuery)) {
                    removeUserFromGroupsStatement.setInt(1, userId);
                    int rowsAffected = removeUserFromGroupsStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null,
                                "User removed from groups successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "User is not assigned to any groups",
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

    private static int getGroupId(Connection conn, String groupName) throws SQLException {
        String getGroupIdQuery = "SELECT group_id FROM groups WHERE group_name = ?";
        try (PreparedStatement getGroupIdStatement = conn.prepareStatement(getGroupIdQuery)) {
            getGroupIdStatement.setString(1, groupName);
            var resultSet = getGroupIdStatement.executeQuery();

            return resultSet.next() ? resultSet.getInt("group_id") : -1;
        }
    }

    private static boolean isUserInGroup(Connection conn, int userId, int groupId) throws SQLException {
        String isUserInGroupQuery = "SELECT * FROM user_groups WHERE user_id = ? AND group_id = ?";
        try (PreparedStatement isUserInGroupStatement = conn.prepareStatement(isUserInGroupQuery)) {
            isUserInGroupStatement.setInt(1, userId);
            isUserInGroupStatement.setInt(2, groupId);
            var resultSet = isUserInGroupStatement.executeQuery();

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

    private static List<String> getGroupNames() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT group_name FROM groups";

            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String> groupNames = new ArrayList<>();
                while (resultSet.next()) {
                    groupNames.add(resultSet.getString("group_name"));
                }

                return groupNames;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
